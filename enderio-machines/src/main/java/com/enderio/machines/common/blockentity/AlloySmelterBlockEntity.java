package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.base.common.blockentity.sync.EnumDataSlot;
import com.enderio.base.common.blockentity.sync.SyncMode;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.PoweredCraftingMachineEntity;
import com.enderio.machines.common.init.MachineCapacitorKeys;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.menu.AlloySmelterMenu;
import com.enderio.api.recipe.AlloySmeltingRecipe;
import com.enderio.machines.common.init.MachineRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.Nullable;

// Using the base Recipe<Container> class so we can support both smelting recipe and alloy smelting recipe.
public abstract class AlloySmelterBlockEntity extends PoweredCraftingMachineEntity<Recipe<Container>> {

    public static class Simple extends AlloySmelterBlockEntity {
        public Simple(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
            super(MachineCapacitorKeys.SIMPLE_ALLOY_SMELTER_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.SIMPLE_ALLOY_SMELTER_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.SIMPLE_ALLOY_SMELTER_ENERGY_CONSUME.get(),
                AlloySmelterMode.ALLOYS, pType, pWorldPosition, pBlockState);
        }

        @Override
        public MachineTier getTier() {
            return MachineTier.Simple;
        }

        @Override
        public int getEnergyLeakRate() {
            return 5; // TODO: Config
        }
    }

    public static class Furnace extends AlloySmelterBlockEntity {
        public Furnace(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
            super(MachineCapacitorKeys.SIMPLE_ALLOY_SMELTER_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.SIMPLE_ALLOY_SMELTER_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.SIMPLE_ALLOY_SMELTER_ENERGY_CONSUME.get(),
                AlloySmelterMode.FURNACE, pType, pWorldPosition, pBlockState);
        }

        @Override
        public MachineTier getTier() {
            return MachineTier.Simple;
        }
    }

    public static class Standard extends AlloySmelterBlockEntity {
        public Standard(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
            super(MachineCapacitorKeys.ALLOY_SMELTER_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.ALLOY_SMELTER_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.ALLOY_SMELTER_ENERGY_CONSUME.get(),
                AlloySmelterMode.ALL, pType, pWorldPosition, pBlockState);
        }

        @Override
        public MachineTier getTier() {
            return MachineTier.Standard;
        }
    }

    public static class Enhanced extends AlloySmelterBlockEntity {
        public Enhanced(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
            super(MachineCapacitorKeys.ENHANCED_ALLOY_SMELTER_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.ENHANCED_ALLOY_SMELTER_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.ENHANCED_ALLOY_SMELTER_ENERGY_CONSUME.get(),
                AlloySmelterMode.ALL, pType, pWorldPosition, pBlockState);
        }

        @Override
        public MachineTier getTier() {
            return MachineTier.Enhanced;
        }
    }

    private final AlloySmelterMode defaultMode;
    private AlloySmelterMode mode;

    private int resultModifier = 1;

    public AlloySmelterBlockEntity(CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey consumptionKey, AlloySmelterMode defaultMode, BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(capacityKey, transferKey, consumptionKey, pType, pWorldPosition, pBlockState);
        this.defaultMode = defaultMode;
        this.mode = defaultMode;

        // This can be changed by the gui for the normal and enhanced machines.
        if (getTier() != MachineTier.Simple) {
            add2WayDataSlot(new EnumDataSlot<>(this::getMode, this::setMode, SyncMode.GUI));
        }
    }

    public AlloySmelterMode getMode() {
        // Lock to default mode if this is a simple machine.
        return getTier() == MachineTier.Simple ? defaultMode : mode;
    }

    public void setMode(AlloySmelterMode mode) {
        this.mode = mode;
    }

    @Override
    protected RecipeType<AlloySmeltingRecipe> getRecipeType() {
        return MachineRecipes.Types.ALLOY_SMELTING;
    }

    @Override
    protected void consumeIngredients(Recipe<Container> recipe) {
        MachineInventory itemHandler = getInventory();
        if (recipe instanceof AlloySmeltingRecipe alloySmeltingRecipe) {
            for (int i = 0; i < 3; i++) {
                alloySmeltingRecipe.consumeInput(itemHandler.getStackInSlot(i));
            }

            // We only craft 1x when alloying
            resultModifier = 1;
        } else if (recipe instanceof SmeltingRecipe) {
            NonNullList<Ingredient> ingredients = getCurrentRecipe().getIngredients();
            // Remove the cooked item.
            int smelting = 0;
            for (int i = 0; i < 3; i++) {
                ItemStack stack = itemHandler.getStackInSlot(i);
                if (ingredients.get(0).test(stack)) {
                    stack.shrink(1);
                    itemHandler.setStackInSlot(i, stack);
                    smelting++;
                }
            }

            // Save the amount we are smelting, it allows multiple smelts in one go
            resultModifier = smelting;
        }
    }

    // TODO: Ensure this is in line with the stirling generator.
    // TODO: Someplace to keep constants like this common.
    public static final int RF_PER_ITEM = ForgeHooks.getBurnTime(new ItemStack(Items.COAL, 1), RecipeType.SMELTING) * 10 / 8;

    @Override
    protected int getEnergyCost(Recipe<Container> recipe) {
        if (recipe instanceof SmeltingRecipe) {
            return RF_PER_ITEM * resultModifier;
        }
        return super.getEnergyCost(recipe);
    }

    @Override
    protected void selectNextRecipe() {
        // Select an alloy smelting recipe if we support it.
        if (getMode().canAlloy()) {
            super.selectNextRecipe();
        }

        // Find a smelting recipe if we aren't alloy smelting.
        if (getMode().canSmelt() && getCurrentRecipe() == null) {
            level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, getRecipeWrapper(), level).ifPresent(this::setCurrentRecipe);
        }
    }

    // TODO: A way to fold this back into PoweredCraftingMachineEntity:
    @Override
    protected void assembleRecipe(Recipe<Container> recipe) {
        ItemStack result = recipe.assemble(getRecipeWrapper());
        result.setCount(result.getCount() * resultModifier);
        getInventory().insertItem(3, result, false);
    }

    @Override
    protected boolean canTakeOutput(Recipe<Container> recipe) {
        ItemStack result = recipe.assemble(getRecipeWrapper());
        result.setCount(result.getCount() * resultModifier);
        return getInventory().insertItem(3, result, true).isEmpty();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new AlloySmelterMenu(this, pInventory, pContainerId);
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        // Setup item slots
        return MachineInventoryLayout.builder(getTier() != MachineTier.Simple)
            .inputSlot(3, this::acceptSlotInput)
            .outputSlot()
            .build();
    }

    private boolean acceptSlotInput(int slot, ItemStack stack) {
        if (getCurrentRecipe() == null)
            return true;

        if (getInventory().getStackInSlot(slot).is(stack.getItem()))
            return true;

        // TODO: Deal with the case that the slot has become empty. Maybe we need to store in PoweredCraftingMachineEntity the item types in all the slots before a recipe started?

        return false;
    }

    @Override
    protected boolean canCraft() {
        return (getTier() == MachineTier.Simple || isCapacitorInstalled()) && super.canCraft();
    }

    @Override
    protected boolean canSelectRecipe() {
        return (getTier() == MachineTier.Simple || isCapacitorInstalled()) && super.canSelectRecipe();
    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        if (getTier() != MachineTier.Simple) {
            pTag.putInt("mode", this.mode.ordinal());
        }
        pTag.putInt("result_modifier", resultModifier);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        if (getTier() != MachineTier.Simple) {
            try {
                mode = AlloySmelterMode.values()[pTag.getInt("mode")];
            } catch (IndexOutOfBoundsException ex) { // In case something happens in the future.
                mode = AlloySmelterMode.ALL;
            }
        }
        resultModifier = pTag.getInt("result_modifier");
        super.load(pTag);
    }

    @Override
    protected void processLoadedRecipe() {
        super.processLoadedRecipe();

        // Reset the result modifier if the recipe load failed.
        if (getCurrentRecipe() == null)
            resultModifier = 1;
    }
}
