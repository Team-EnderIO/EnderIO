package com.enderio.machines.common.blockentity.task;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.blockentity.base.PoweredCraftingMachine;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
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
     * The outputSlots - null if no items are produced, i.e. a vat recipe.
     */
    private final @Nullable MultiSlotAccess outputSlots;
    /**
     * The output tank in case there is one, i.e vat recipes.
     */
    private final @Nullable MachineFluidTank outputTank;

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
     * Whether inputs have been collected.
     */
    private boolean collectedInputs;

    /**
     * Whether the outputs have been determined yet.
     */
    private boolean determinedOutputs;

    /**
     * The outputs of the recipe.
     * These are determined after we take ingredients, as we don't want the outputs to change later.
     */
    private List<OutputStack> outputs = List.of(OutputStack.EMPTY);

    /**
     * Whether the recipe craft is complete.
     * Will not be true until the inventory has the result item.
     */
    private boolean complete;

    public PoweredCraftingTask(PoweredCraftingMachine<R, C> blockEntity, C container, MultiSlotAccess output, @Nullable R recipe) {
        super(blockEntity.getEnergyStorage());
        this.outputSlots = output;
        this.outputTank = null;
        this.recipe = recipe;
        this.container = container;
        this.blockEntity = blockEntity;
    }
    public PoweredCraftingTask(PoweredCraftingMachine<R, C> blockEntity, C container, SingleSlotAccess output, @Nullable R recipe) {
        this(blockEntity, container, output.wrapToMulti(), recipe);
    }

    public PoweredCraftingTask(PoweredCraftingMachine<R,C> blockEntity, C container,  MachineFluidTank outputTank, @Nullable R recipe) {
        super(blockEntity.getEnergyStorage());
        this.outputSlots = null;
        this.outputTank = outputTank;
        this.recipe = recipe;
        this.container = container;
        this.blockEntity = blockEntity;
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
        // Get outputs
        MachineInventory inv = blockEntity.getInventory();

        // See that we can add all the outputs
        for (OutputStack output : outputs) {
            if(output.isItem()){
                ItemStack item = output.getItem();
                assert outputSlots != null : "No output item slot to place item into";
                for (SingleSlotAccess outputAccess: outputSlots.getAccesses()) {
                    item = outputAccess.insertItem(inv, item, true);
                }
                // If we fail, say we can't accept these outputs
                if (!item.isEmpty())
                    return false;
            } else if (output.isFluid()) {
                FluidStack stack = output.getFluid().copy();
                assert outputTank != null: "output tank must not be null in recipes with fluids as result";
                int filledAmount = outputTank.fill(stack, IFluidHandler.FluidAction.SIMULATE, true);//since output tanks are
                            // generally set to disallow fill, we force it in there
                if (filledAmount != stack.getAmount())//we can't fit it all in the tank.
                    return false;
            }
        }

        // If we're not simulating, go for it
        if (!simulate) {
            for (OutputStack output : outputs) {
                if (output.isItem()){
                    ItemStack item = output.getItem();
                    assert outputSlots != null;
                    for (SingleSlotAccess outputAccess: outputSlots.getAccesses()) {
                        item = outputAccess.insertItem(inv, item, false);
                    }
                }
                else if(output.isFluid()){
                    FluidStack stack = output.getFluid();
                    assert outputTank != null;
                    outputTank.fill(stack, IFluidHandler.FluidAction.EXECUTE, true);
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

        // If we haven't done so already, consume inputs for the recipe.
        if (!collectedInputs) {
            // Consume inputs for the recipe.
            takeInputs(recipe);
            collectedInputs = true;

            // Store the recipe energy cost.
            // This is run after takeInputs() as it allows energy cost to be determined by the inputs that are taken
            energyCost = recipe.getEnergyCost(container);
        }

        // Get the outputs list.
        if (!determinedOutputs) {
            determinedOutputs = true;
            outputs = recipe.craft(container);
            // TODO: Compact any items that are the same into singular stacks?
        }

        // Try to consume as much energy as possible to finish the craft as soon as possible.
        if (energyConsumed <= energyCost) {
            energyConsumed += consumeEnergy(energyCost - energyConsumed);
        }

        // If enough time has passed, attempt to finish through placing the outputs.
        if (energyConsumed >= energyCost) {
            // Attempt to complete the craft
            if (placeOutputs(outputs, false)) {
                // The receiver was able to take the outputs, task complete. If not, try next tick.
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

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("RecipeId", recipe.getId().toString());
        tag.putInt("EnergyConsumed", energyConsumed);
        tag.putInt("EnergyCost", energyConsumed);
        tag.putBoolean("CollectedInputs", collectedInputs);
        tag.putBoolean("Complete", complete);

        tag.putBoolean("DeterminedOutputs", determinedOutputs);
        if (determinedOutputs) {
            ListTag outputsNbt = new ListTag();
            for (OutputStack stack : outputs) {
                outputsNbt.add(stack.serializeNBT());
            }
            tag.put("Outputs", outputsNbt);
        }

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        recipe = loadRecipe(new ResourceLocation(nbt.getString("RecipeId")));
        energyConsumed = nbt.getInt("EnergyConsumed");
        energyCost = nbt.getInt("EnergyCost");
        collectedInputs = nbt.getBoolean("CollectedInputs");
        complete = nbt.getBoolean("Complete");

        determinedOutputs = nbt.getBoolean("DeterminedOutputs");
        if (determinedOutputs) {
            ListTag outputsNbt = nbt.getList("Outputs", Tag.TAG_COMPOUND);
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
