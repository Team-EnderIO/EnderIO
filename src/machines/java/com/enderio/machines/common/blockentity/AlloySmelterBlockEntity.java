package com.enderio.machines.common.blockentity;

import com.enderio.EnderIO;
import com.enderio.api.UseOnly;
import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.core.common.recipes.CountedIngredient;
import com.enderio.core.common.sync.EnumDataSlot;
import com.enderio.core.common.sync.FloatDataSlot;
import com.enderio.core.common.sync.SyncMode;
import com.enderio.machines.common.blockentity.base.PoweredCraftingMachine;
import com.enderio.machines.common.blockentity.task.PoweredCraftingTask;
import com.enderio.machines.common.integrations.vanilla.VanillaAlloySmeltingRecipe;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.energy.MachineEnergyStorage;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.AlloySmelterMenu;
import com.enderio.machines.common.menu.PrimitiveAlloySmelterMenu;
import com.enderio.machines.common.recipe.AlloySmeltingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

// TODO: Award XP

public class AlloySmelterBlockEntity extends PoweredCraftingMachine<AlloySmeltingRecipe, AlloySmeltingRecipe.Container> {

    public static final MultiSlotAccess INPUTS = new MultiSlotAccess();
    public static final SingleSlotAccess OUTPUT = new SingleSlotAccess();

    /**
     * The primitive variant of the alloy smelter burns coal instead of using an energy buffer.
     * In order to keep implementation logic together, we do some kinda hacky stuff to emulate an internal buffer.
     * This buffer cannot be accessed via external means however.
     */
    public static class Primitive extends AlloySmelterBlockEntity {
        // TODO: Currently smelts really slowly. Needs addressed when we deal with burn -> FE rates.
        private int burnTime;
        private int burnDuration;
        public static final SingleSlotAccess FUEL = new SingleSlotAccess();
        @UseOnly(LogicalSide.CLIENT)
        private float clientBurnProgress;

        public Primitive(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
            super(pType, pWorldPosition, pBlockState);
            addDataSlot(new FloatDataSlot(this::getBurnProgress, p -> clientBurnProgress = p, SyncMode.GUI));
        }

        @Override
        protected boolean restrictedMode() {
            return true;
        }
        @Override
        public MachineInventoryLayout getInventoryLayout() {
            return MachineInventoryLayout.builder()
                .inputSlot(3, this::acceptSlotInput)
                .slotAccess(INPUTS)
                .outputSlot()
                .slotAccess(OUTPUT)
                .inputSlot(this::acceptSlotInput)
                .slotAccess(FUEL)
                .build();
        }

        @Override
        public AlloySmelterMode getMode() {
            // Force alloys only
            return AlloySmelterMode.ALLOYS;
        }

        @Override
        public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
            return new PrimitiveAlloySmelterMenu(this, inventory, containerId);
        }

        @Override
        public void serverTick() {
            super.serverTick();

            // Tick burn time even if redstone activation has stopped.
            if (isBurning()) {
                burnTime--;
            }

            // Only continue burning if redstone is enabled and the internal buffer has space.
            if (canAct() && !isBurning() && hasTask() && !getCurrentTask().isComplete()) {
                // Get the fuel
                ItemStack fuel = FUEL.getItemStack(this);
                if (!fuel.isEmpty()) {
                    // Get the burn time.
                    int burningTime = ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING);

                    // If this item can burn, burn it.
                    if (burningTime > 0) {
                        burnTime = burningTime;
                        burnDuration = burnTime;

                        // Remove the fuel
                        fuel.shrink(1);
                    }
                }
            }
        }

        @Override
        protected MachineEnergyStorage createEnergyStorage(EnergyIOMode energyIOMode, Supplier<Integer> capacity, Supplier<Integer> transferRate,
            Supplier<Integer> usageRate) {
            return new MachineEnergyStorage(getIOConfig(), energyIOMode, this::getBurnToFE, () -> 0, () -> 0) {
                @Override
                public int getEnergyStored() {
                    return getBurnToFE();
                }

                @Override
                public int consumeEnergy(int energy, boolean simulate) {
                    // We burn fuel, this energy storage is merely a wrapper now.
                    if (isBurning()) {
                        return getBurnToFE();
                    }
                    return 0;
                }

                // Stop things from connecting to the block.
                @Override
                public LazyOptional<IEnergyStorage> getCapability(@Nullable Direction side) {
                    return LazyOptional.empty();
                }
            };
        }

        public boolean isBurning() {
            return burnTime > 0;
        }

        public float getBurnProgress() {
            if (level.isClientSide)
                return clientBurnProgress;
            if (burnDuration == 0)
                return 0;
            return burnTime / (float) burnDuration;
        }

        public int getBurnToFE() {
            // TODO: TEMP, needs better solution.
            // Stirling generator produces 10 RF per tick of burn time.
            // https://github.com/SleepyTrousers/EnderIO/blob/d6dfb9d3964946ceb9fd72a66a3cff197a51a1fe/enderio-base/src/main/java/crazypants/enderio/base/recipe/alloysmelter/VanillaSmeltingRecipe.java#L50
            return 10;
        }
    }

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, () -> 100000f);
    public static final QuadraticScalable TRANSFER = new QuadraticScalable(CapacitorModifier.ENERGY_TRANSFER, () -> 120f);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, () -> 30f);

    /**
     * The alloying mode for the machine.
     * Determines which recipes it can craft.
     */
    private AlloySmelterMode mode = AlloySmelterMode.ALL;

    /**
     * The container used for crafting context.
     */
    private final AlloySmeltingRecipe.Container container;

    public AlloySmelterBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(MachineRecipes.ALLOY_SMELTING.type().get(), CAPACITY, TRANSFER, USAGE, pType, pWorldPosition, pBlockState);

        // Create the crafting inventory. Used for context in the vanilla recipe wrapper.
        this.container = new AlloySmeltingRecipe.Container(getInventory());

        // This can be changed by the gui for the normal and enhanced machines.
        if (restrictedMode()) {
            add2WayDataSlot(new EnumDataSlot<>(this::getMode, this::setMode, SyncMode.GUI));
        }
    }

    /**
     * Get the alloy smelting mode.
     */
    public AlloySmelterMode getMode() {
        return mode;
    }

    /**
     * Set the alloy smelting mode.
     * Calling on a simple tier machine does nothing.
     */
    public void setMode(AlloySmelterMode mode) {
        this.mode = mode;
        newTaskAvailable();
    }

    /**
     * Whether the mode is restricted.
     * Used to disable serialization of the mode and sync of the slot when this is the primitive variant.
     */
    protected boolean restrictedMode() {
        return false;
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .inputSlot(3, this::acceptSlotInput)
            .slotAccess(INPUTS)
            .outputSlot().slotAccess(OUTPUT)
            .capacitor().build();
    }

    protected boolean acceptSlotInput(int slot, ItemStack stack) {
        // Ensure we don't break automation by inserting items that'll break the current recipe.
        var currentTask = getCurrentTask();
        if (currentTask != null) {
            var currentRecipe = currentTask.getRecipe();
            if (currentRecipe != null) {
                MachineInventory inventory = getInventory();
                ItemStack currentContents = inventory.getStackInSlot(slot);
                inventory.setStackInSlot(slot, stack);
                boolean accept = currentRecipe.matches(container, level);
                inventory.setStackInSlot(slot, currentContents);
                return accept;
            }
        }
        return true;
    }

    @Override
    protected Optional<AlloySmeltingRecipe> findRecipe() {
        // Get alloy smelting recipe (Default)
        if (getMode().canAlloy()) {
            var recipe = super.findRecipe();
            if (recipe.isPresent())
                return recipe;
        }

        // Get vanilla smelting recipe.
        if (getMode().canSmelt()) {
            var recipe = level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, getContainer(), level);
            if (recipe.isPresent())
                return Optional.of(new VanillaAlloySmeltingRecipe(recipe.get()));
        }
        return Optional.empty();
    }

    @Override
    protected PoweredCraftingTask<AlloySmeltingRecipe, AlloySmeltingRecipe.Container> createTask(@Nullable AlloySmeltingRecipe recipe) {
        return createTask(recipe, OUTPUT);
    }

    protected PoweredCraftingTask<AlloySmeltingRecipe, AlloySmeltingRecipe.Container> createTask(@Nullable AlloySmeltingRecipe recipe, SingleSlotAccess output) {
        return new PoweredCraftingTask<>(this, container, output, recipe) {
            @Override
            protected void takeInputs(AlloySmeltingRecipe recipe) {
                MachineInventory inv = getInventory();

                if (recipe instanceof VanillaAlloySmeltingRecipe) {
                    CountedIngredient input = recipe.getInputs().get(0);

                    // Iterate over the slots

                    int consumeCount = 0;
                    for (int i = 0; i < 3; i++) {
                        ItemStack stack = inv.getStackInSlot(i);

                        if (input.test(stack)) {
                            stack.shrink(input.count());
                            consumeCount++;
                        }
                    }

                    container.setInputsTaken(consumeCount);
                } else {
                    // Track which ingredients have been consumed
                    List<CountedIngredient> inputs = recipe.getInputs();
                    boolean[] consumed = new boolean[3];

                    // Iterate over the slots
                    for (int i = 0; i < 3; i++) {
                        ItemStack stack = INPUTS.get(i).getItemStack(inv);

                        // Iterate over the inputs
                        for (int j = 0; j < 3; j++) {

                            // If this ingredient has been matched already, continue
                            if (consumed[j])
                                continue;

                            if (j < inputs.size()) {
                                // If we expect an input, test we have a match for it.
                                CountedIngredient input = inputs.get(j);

                                if (input.test(stack)) {
                                    consumed[j] = true;
                                    stack.shrink(input.count());
                                }
                            } else if (stack.isEmpty()) {
                                // If we don't expect an input, make sure we have a blank for it.
                                consumed[j] = true;
                            }
                        }
                    }

                    // Only accepted *1* times inputs.
                    container.setInputsTaken(1);
                }
            }

            @Nullable
            @Override
            protected AlloySmeltingRecipe loadRecipe(ResourceLocation id) {
                return level.getRecipeManager().byKey(id).map(recipe -> {
                    if (recipe.getType() == MachineRecipes.ALLOY_SMELTING.type().get()) {
                        return (AlloySmeltingRecipe) recipe;
                    } else if (recipe.getType() == RecipeType.SMELTING) {
                        return new VanillaAlloySmeltingRecipe((SmeltingRecipe) recipe);
                    }
                    return null;
                }).orElse(null);
            }
        };
    }

    @Override
    protected AlloySmeltingRecipe.Container getContainer() {
        return container;
    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        if (restrictedMode()) {
            pTag.putInt("Mode", this.mode.ordinal());
        }
        pTag.putInt("InputsTaken", container.getInputsTaken());
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        if (restrictedMode()) {
            try {
                mode = AlloySmelterMode.values()[pTag.getInt("Mode")];
            } catch (IndexOutOfBoundsException ex) { // In case something happens in the future.
                EnderIO.LOGGER.error("Invalid alloy smelter mode loaded from NBT. Ignoring.");
            }
        }
        container.setInputsTaken(pTag.getInt("InputsTaken"));
        super.load(pTag);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new AlloySmelterMenu(this, inventory, containerId);
    }
}
