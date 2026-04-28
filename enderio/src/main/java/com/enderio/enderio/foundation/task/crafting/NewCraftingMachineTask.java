package com.enderio.enderio.foundation.task.crafting;

import com.enderio.enderio.foundation.MachineRecipe;
import com.enderio.enderio.foundation.task.MachineTask;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

public class NewCraftingMachineTask<T extends MachineRecipe<U>, U extends RecipeInput> extends MachineTask<CraftingMachineTaskContext<T, U>> {
    private final RandomSource randomSource;

    @Nullable
    private ResourceKey<Recipe<?>> recipeKey;
    private int totalTicksToCraft;
    private int craftingTicks;
    private long randomSeed;

    @Nullable
    private RecipeHolder<T> cachedRecipe;

    private CraftingMachineTaskState currentState = CraftingMachineTaskState.ACTIVE;

    public NewCraftingMachineTask(CraftingMachineTaskContext<T, U> context, RecipeHolder<T> recipe) {
        this(context);
        this.recipeKey = recipe.id();
        this.cachedRecipe = recipe;
    }

    /**
     * Create an empty crafting machine task, intended for deserialization.
     * @param context the task context
     */
    public NewCraftingMachineTask(CraftingMachineTaskContext<T, U> context) {
        super(context);

        // Create a consistent random seed for determining recipe outputs
        this.randomSeed = RandomSupport.generateUniqueSeed();
        this.randomSource = RandomSource.create(randomSeed);
    }

    // TODO: Will be a good point to determine if outputs of a machine are blocked for machine state.
    public CraftingMachineTaskState currentState() {
        return currentState;
    }

    @Nullable
    public RecipeHolder<T> getRecipe() {
        ServerLevel level = context.getLevel();
        if (level == null) {
            throw new IllegalStateException("Cannot get recipe before level is available.");
        }

        if (recipeKey == null) {
            return null;
        }

        RecipeManager recipeManager = level.recipeAccess();

        // TODO: Do we want to do what RecipeCache does and keep a weak ref to the recipemanager to detect changes?
        if (cachedRecipe == null) {
            // TODO: Catch class cast exceptions
            //noinspection unchecked
            recipeManager.byKey(recipeKey).ifPresent(recipe ->
                this.cachedRecipe = (RecipeHolder<T>)recipe);
        }

        return cachedRecipe;
    }

    @Override
    public void tick() {
        if (currentState.shouldStop()) {
            return;
        }

        ServerLevel level = context.getLevel();
        if (level == null) {
            // Do not tick when there is no level available.
            return;
        }

        RecipeHolder<T> recipe = getRecipe();
        if (recipe == null) {
            currentState = CraftingMachineTaskState.CANCELLED;
            return;
        }

        // If the recipe fails to match, abort.
        U recipeInput = context.recipeInput();
        if (!recipe.value().matches(recipeInput, level)) {
            currentState = CraftingMachineTaskState.CANCELLED;
            return;
        }

        if (totalTicksToCraft <= 0) {
            totalTicksToCraft = Math.round(recipe.value().getOperationTime(recipeInput) * context.getOperationTimeMultiplier());
        }

        // Try and make some progress (i.e. consume something to do the craft, or just let it through)
        if (craftingTicks < totalTicksToCraft && context.onCraftingTick()) {
            craftingTicks++;
        }

        if (craftingTicks >= totalTicksToCraft) {
            // Attempt to place the outputs
            try (Transaction transaction = Transaction.openRoot()) {
                // Ensure random source is consistent
                randomSource.setSeed(randomSeed);

                boolean didInsertOutputs = context.insertRecipeOutputs(recipe.value(), recipeInput, randomSource, transaction);
                if (!didInsertOutputs) {
                    return;
                }

                // Now that we've managed to insert the outputs - make sure we can consume the inputs
                boolean didConsumeInputs = context.consumeRecipeInputs(recipe.value(), transaction);
                if (!didConsumeInputs) {
                    // If we cannot retrieve the inputs at this stage, we'll not cancel - wait for matches() to fail.
                    // This will hopefully make it more clear theres a consume logic issue if we don't just cancel and resume.
                    // TODO: We could add a boolean to see if this has happened once, if it happens again we could then log a message/cancel the task.
                    return;
                }

                // Complete task
                transaction.commit();
                currentState = CraftingMachineTaskState.COMPLETED;
            }
        }
    }

    @Override
    public float getProgress() {
        if (totalTicksToCraft <= 0) {
            return 0;
        }

        return craftingTicks / (float) totalTicksToCraft;
    }

    @Override
    public boolean isCompleted() {
        return currentState.shouldStop();
    }

    @Override
    public void serialize(ValueOutput output) {
        if (recipeKey != null) {
            output.store("RecipeId", ResourceKey.codec(Registries.RECIPE), recipeKey);
        }

        output.putInt("CraftingTicks", craftingTicks);
        output.putLong("RandomSeed", randomSeed);
    }

    @Override
    public void deserialize(ValueInput input) {
        recipeKey = input.read("RecipeId", ResourceKey.codec(Registries.RECIPE)).orElse(null);
        craftingTicks = input.getIntOr("CraftingTicks", 0);
        randomSeed = input.getLongOr("RandomSeed", RandomSupport.generateUniqueSeed());

        // Always load as active as complete/cancelled will never be saved.
        currentState = CraftingMachineTaskState.ACTIVE;
    }
}
