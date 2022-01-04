package com.enderio.machines.common.blockentity;

import com.enderio.base.common.blockentity.sync.EnumDataSlot;
import com.enderio.base.common.blockentity.sync.SyncMode;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.PoweredCraftingMachineEntity;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemHandlerMaster;
import com.enderio.machines.common.menu.AlloySmelterMenu;
import com.enderio.machines.common.recipe.AlloySmeltingRecipe;
import com.enderio.machines.common.recipe.MachineRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// Using the base Recipe<Container> class so we can support both smelting recipe and alloy smelting recipe.
public class AlloySmelterBlockEntity extends PoweredCraftingMachineEntity<Recipe<Container>> {

    public static class SimpleAlloySmelter extends AlloySmelterBlockEntity {
        public SimpleAlloySmelter(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
            super(SmeltingMode.Alloys, MachineTier.Simple, pType, pWorldPosition, pBlockState);
        }
    }

    public static class SimpleSmelter extends AlloySmelterBlockEntity {
        public SimpleSmelter(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
            super(SmeltingMode.Furnace, MachineTier.Simple, pType, pWorldPosition, pBlockState);
        }
    }

    public static class AlloySmelter extends AlloySmelterBlockEntity {
        public AlloySmelter(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
            super(SmeltingMode.All, MachineTier.Standard, pType, pWorldPosition, pBlockState);
        }
    }


    private final ItemHandlerMaster itemHandlerMaster = new ItemHandlerMaster(getConfig(), 4, List.of(0,1,2), List.of(3)) {
        @NotNull
        @Override
        public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            // If we already have a recipe, disallow use of an empty slot.
            // TODO: This doesn't work too great. Need a better solution.
//            if (getOnlyInputs().contains(slot) && getCurrentRecipe() != null && getStackInSlot(slot).isEmpty())
//                return stack;
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    public enum SmeltingMode {
        All(true, true),
        Alloys(true, false),
        Furnace(false, true);

        private final boolean alloy;
        private final boolean smelt;

        SmeltingMode(boolean alloy, boolean smelt) {
            this.alloy = alloy;
            this.smelt = smelt;
        }
    }

    private final SmeltingMode defaultMode;
    private SmeltingMode mode = SmeltingMode.All; // TODO: Set based on block and lock for two of them...

    public AlloySmelterBlockEntity(SmeltingMode defaultMode, MachineTier tier, BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(tier, pType, pWorldPosition, pBlockState);
        this.defaultMode = defaultMode;

        // This can be changed by the gui for the normal and enhanced machines.
        add2WayDataSlot(new EnumDataSlot<>(() -> mode, newMode -> mode = newMode, SyncMode.GUI));
    }

    public SmeltingMode getMode() {
        // Lock to default mode if this is a simple machine.
        return getTier() == MachineTier.Simple ? defaultMode : mode;
    }

    @Override
    public ItemHandlerMaster getItemHandlerMaster() {
        return itemHandlerMaster;
    }

    @Override
    protected RecipeType<AlloySmeltingRecipe> getRecipeType() {
        return MachineRecipes.Types.ALLOY_SMELTING;
    }

    @Override
    protected void deductIngredients(Recipe<Container> recipe) {
        NonNullList<Ingredient> ingredients = getCurrentRecipe().getIngredients();
        if (recipe instanceof AlloySmeltingRecipe) {
            for (int i = 0; i < 3; i++) {
                for (Ingredient ingredient : ingredients) {
                    ItemStack stack = getItemHandlerMaster().getStackInSlot(i);
                    if (ingredient.test(stack)) {
                        stack.shrink(1); // TODO: Get from recipe.
                        getItemHandlerMaster().setStackInSlot(i, stack);
                    }
                }
            }
        } else if (recipe instanceof SmeltingRecipe) {
            // Remove the cooked item.
            for (int i = 0; i < 3; i++) {
                ItemStack stack = getItemHandlerMaster().getStackInSlot(i);
                if (ingredients.get(0).test(stack)) {
                    stack.shrink(1);
                    getItemHandlerMaster().setStackInSlot(i, stack);
                    break;
                }
            }
        }
    }

    @Override
    protected int getEnergyCost(Recipe<Container> recipe) {
        // TODO
        return 64;
    }

    @Override
    protected void selectNextRecipe() {
        // Select an alloy smelting recipe if we support it.
        if (getMode().alloy) {
            super.selectNextRecipe();
        }

        // Find a smelting recipe if we aren't alloy smelting.
        if (getMode().smelt && getCurrentRecipe() == null) {
            // TODO: Support adding 3 ingredients at once to get 3 smelted outputs.
            level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new RecipeWrapper(getItemHandlerMaster()), level).ifPresent(this::setCurrentRecipe);
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new AlloySmelterMenu(this, pInventory, pContainerId);
    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        pTag.put("Inventory", itemHandlerMaster.serializeNBT());
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandlerMaster.deserializeNBT(pTag.getCompound("Inventory"));
    }
}
