package com.enderio.machines.common.blockentity.task;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.blockentity.base.PoweredCraftingMachine;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.recipe.MachineRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A recipe crafting task that consumes energy.
 *
 * @param <C> The container type used by the recipes. Mostly useful for {@link net.minecraft.world.item.crafting.CraftingRecipe}'s.
 */
public abstract class PoweredCraftingTask<R extends MachineRecipe<C>, C extends Container> extends PoweredTask {
    /**
     * The attached machine.
     */
    private final PoweredCraftingMachine<R, C> blockEntity;

    /**
     * The container we are crafting in.
     */
    private final C container;

    /**
     * The recipe being crafted.
     */
    @Nullable private R recipe;

    /**
     * The outputslots
     */
    private final MultiSlotAccess outputSlots;

    /**
     * Amount of energy consumed to craft so far.
     */
    private int energyConsumed;

    /**
     * Amount of energy needed to be consumed.
     * Stored because the recipe needs access to the container to determine energy cost. Once inputs are consumed, we can't query again.
     */
    private int energyCost;

    /**
     * Whether or not inputs have been collected.
     */
    private boolean collectedInputs;

    /**
     * Whether the outputs have been determined yet.
     */
    private boolean hasDeterminedOutputs;

    /**
     * The outputs of the recipe.
     * These are determined after we take ingredients, as we don't want the outputs to change later.
     */
    private List<OutputStack> outputs;

    /**
     * Whether the recipe craft is complete.
     * Will not be true until the inventory has the result item.
     */
    private boolean complete;

    public PoweredCraftingTask(PoweredCraftingMachine<R, C> blockEntity, C container, MultiSlotAccess output, @Nullable R recipe) {
        super(blockEntity.getEnergyStorage());
        this.outputSlots = output;
        this.recipe = recipe;
        this.container = container;
        this.blockEntity = blockEntity;
    }
    public PoweredCraftingTask(PoweredCraftingMachine<R, C> blockEntity, C container, SingleSlotAccess output, @Nullable R recipe) {
        this(blockEntity, container, output.wrapToMulti(), recipe);
    }

    /**
     * Get the recipe being crafted.
     * May be null if an error has occurred or the level isn't loaded yet.
     */
    @Nullable
    public final R getRecipe() {
        return recipe;
    }

    /**
     * Take inputs from the machine.
     */
    protected abstract void takeInputs(R recipe);

    /**
     * Consume energy from the buffer.
     * Only really exposed so the SAG mill can take durability from grinding balls.
     */
    protected int consumeEnergy(int maxConsume) {
        return energyStorage.consumeEnergy(maxConsume, false);
    }

    /**
     * Place outputs into the machine.
     */
    protected boolean placeOutputs(List<OutputStack> outputs, boolean simulate) {
        // TODO: Handle fluids too.

        // Get outputs
        MachineInventory inv = blockEntity.getInventory();

        // See that we can add all of the outputs
        for (OutputStack output : outputs) {
            ItemStack item = output.getItem();

            for (SingleSlotAccess outputAccess: outputSlots.getAccesses()) {
                item = outputAccess.insertItem(inv, item, true);
            }

            // If we fail, say we can't accept these outputs
            if (!item.isEmpty())
                return false;
        }

        // If we're not simulating, go for it
        if (!simulate) {
            for (OutputStack output : outputs) {
                ItemStack item = output.getItem();

                for (SingleSlotAccess outputAccess: outputSlots.getAccesses()) {
                    item = outputAccess.insertItem(inv, item, false);
                }
            }
        }

        return true;
    }

    @Override
    public void tick() {
        // If the recipe is done, don't let it tick.
        if (complete)
            return;

        // If the recipe failed to load somehow, cancel
        if (recipe == null) {
            complete = true;
            return;
        }

        // Get the outputs list.
        if (!hasDeterminedOutputs) {
            hasDeterminedOutputs = true;
            outputs = recipe.craft(container, blockEntity.getLevel().registryAccess());

            // TODO: Compact any items that are the same into singular stacks?
        }

        // If we can't inputs or outputs, cancel the task. However if for some reason we can't output after the inputs are collected, don't.
        if (!collectedInputs && (!placeOutputs(outputs, true) || !recipe.matches(container, blockEntity.getLevel()))) {
            complete = true;
            // This means if a sagmill recipe outputs 2 it cancels the recipe, and the determined outputs are cleared. Its a weird behaviour but not necessarily a bug.
            // We might want to review how this works in future, as right now we wait for an inventory change rather than the machine tick repeatedly.
            return;
        }

        // If we haven't done so already, consume inputs for the recipe.
        if (!collectedInputs) {
            // Consume inputs for the recipe.
            takeInputs(recipe);
            collectedInputs = true;

            // Store the recipe energy cost.
            // This is run afterwards as it allows container context changes after takeInputs()
            energyCost = recipe.getEnergyCost(container);
        }

        // Try to consume as much energy as possible to finish the craft.
        if (energyConsumed <= energyCost) {
            energyConsumed += consumeEnergy(energyCost - energyConsumed);
        }

        // If the recipe has been crafted, attempt to put it into storage
        if (energyConsumed >= energyCost) {
            // Attempt to complete the craft
            if (placeOutputs(outputs, false)) {
                // The receiver was able to take the outputs, task complete.
                complete = true;
            }
        }
    }

    @Override
    public float getProgress() {
        if (recipe == null)
            return 0.0f;
        return energyConsumed / (float) recipe.getEnergyCost(container);
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    // region Serialization

    private static final String KEY_RECIPE_ID = "RecipeId";
    private static final String KEY_COLLECTED_INPUTS = "CollectedInputs";
    private static final String KEY_COMPLETE = "Complete";
    private static final String KEY_HAS_DETERMINED_OUTPUTS = "HasDeterminedOutputs";
    private static final String KEY_OUTPUTS = "Outputs";

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString(KEY_RECIPE_ID, recipe.getId().toString());
        tag.putInt(KEY_ENERGY_CONSUMED, energyConsumed);
        tag.putInt(KEY_ENERGY_COST, energyConsumed);
        tag.putBoolean(KEY_COLLECTED_INPUTS, collectedInputs);
        tag.putBoolean(KEY_COMPLETE, complete);

        tag.putBoolean(KEY_HAS_DETERMINED_OUTPUTS, hasDeterminedOutputs);
        if (hasDeterminedOutputs) {
            ListTag outputsNbt = new ListTag();
            for (OutputStack stack : outputs) {
                outputsNbt.add(stack.serializeNBT());
            }
            tag.put(KEY_OUTPUTS, outputsNbt);
        }

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        recipe = loadRecipe(new ResourceLocation(nbt.getString(KEY_RECIPE_ID)));
        energyConsumed = nbt.getInt(KEY_ENERGY_CONSUMED);
        energyCost = nbt.getInt(KEY_ENERGY_COST);
        collectedInputs = nbt.getBoolean(KEY_COLLECTED_INPUTS);
        complete = nbt.getBoolean(KEY_COMPLETE);

        hasDeterminedOutputs = nbt.getBoolean(KEY_HAS_DETERMINED_OUTPUTS);
        if (hasDeterminedOutputs) {
            ListTag outputsNbt = nbt.getList(KEY_OUTPUTS, Tag.TAG_COMPOUND);
            outputs = new ArrayList<>();
            for (Tag tag : outputsNbt) {
                outputs.add(OutputStack.fromNBT((CompoundTag) tag));
            }
        }
    }

    @Nullable
    protected R loadRecipe(ResourceLocation id) {
        return (R) blockEntity.getLevel().getRecipeManager().byKey(id).orElse(null);
    }

    // endregion
}
