package com.enderio.machines.common.blockentity;

import com.enderio.base.common.blockentity.sync.EnumDataSlot;
import com.enderio.base.common.blockentity.sync.SyncMode;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.common.util.CapacitorUtil;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.PoweredCraftingMachineEntity;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemHandlerMaster;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemSlotLayout;
import com.enderio.machines.common.machine.AlloySmelterMode;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

// Using the base Recipe<Container> class so we can support both smelting recipe and alloy smelting recipe.
public class AlloySmelterBlockEntity extends PoweredCraftingMachineEntity<Recipe<Container>> {

    public static class Simple extends AlloySmelterBlockEntity {
        public Simple(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
            super(AlloySmelterMode.Alloys, MachineTier.Simple, pType, pWorldPosition, pBlockState);
        }
    }

    public static class Furnace extends AlloySmelterBlockEntity {
        public Furnace(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
            super(AlloySmelterMode.Furnace, MachineTier.Simple, pType, pWorldPosition, pBlockState);
        }
    }

    public static class Standard extends AlloySmelterBlockEntity {
        public Standard(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
            super(AlloySmelterMode.All, MachineTier.Standard, pType, pWorldPosition, pBlockState);
        }
    }

    public static class Enhanced extends AlloySmelterBlockEntity {
        // TODO: Make it enhanced
        public Enhanced(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
            super(AlloySmelterMode.All, MachineTier.Enhanced, pType, pWorldPosition, pBlockState);
        }
    }

    private final AlloySmelterMode defaultMode;
    private AlloySmelterMode mode;

    public AlloySmelterBlockEntity(AlloySmelterMode defaultMode, MachineTier tier, BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(tier, pType, pWorldPosition, pBlockState);
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
        NonNullList<Ingredient> ingredients = getCurrentRecipe().getIngredients();
        ItemHandlerMaster itemHandler = getItemHandler();
        if (recipe instanceof AlloySmeltingRecipe) {
            for (int i = 0; i < 3; i++) {
                for (Ingredient ingredient : ingredients) {
                    ItemStack stack = itemHandler.getStackInSlot(i);
                    if (ingredient.test(stack)) {
                        stack.shrink(1); // TODO: Get from recipe.
                        itemHandler.setStackInSlot(i, stack);
                    }
                }
            }
        } else if (recipe instanceof SmeltingRecipe) {
            // Remove the cooked item.
            for (int i = 0; i < 3; i++) {
                ItemStack stack = itemHandler.getStackInSlot(i);
                if (ingredients.get(0).test(stack)) {
                    stack.shrink(1);
                    itemHandler.setStackInSlot(i, stack);
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
        if (getMode().canAlloy) {
            super.selectNextRecipe();
        }

        // Find a smelting recipe if we aren't alloy smelting.
        if (getMode().canSmelt && getCurrentRecipe() == null) {
            // TODO: Support adding 3 ingredients at once to get 3 smelted outputs.
            level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, getRecipeWrapper(), level).ifPresent(this::setCurrentRecipe);
        }
    }

    // TODO: A way to fold this back into PoweredCraftingMachineEntity:
    @Override
    protected void assembleRecipe(Recipe<Container> recipe) {
        getItemHandler().forceInsertItem(3, recipe.assemble(getRecipeWrapper()), false);
    }

    @Override
    protected boolean canTakeOutput(Recipe<Container> recipe) {
        return getItemHandler().forceInsertItem(3, recipe.assemble(getRecipeWrapper()), true).isEmpty();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new AlloySmelterMenu(this, pInventory, pContainerId);
    }

    @Override
    public Optional<ItemSlotLayout> getSlotLayout() {
        if (getTier() == MachineTier.Simple) {
            return Optional.of(ItemSlotLayout.basic(3, 1));
        }
        return Optional.of(ItemSlotLayout.withCapacitor(3, 1));
    }

    @Override
    protected boolean canCraft() {
        return (getTier() == MachineTier.Simple || hasCapacitor()) && super.canCraft();
    }

    @Override
    protected ItemHandlerMaster createItemHandler(ItemSlotLayout layout) {
        return new ItemHandlerMaster(getConfig(), layout) {
            @NotNull
            @Override
            public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                // If we already have a recipe, disallow use of an empty slot.
                // TODO: This doesn't work too great. Need a better solution.
                //            if (getOnlyInputs().contains(slot) && getCurrentRecipe() != null && getStackInSlot(slot).isEmpty())
                //                return stack;
                if (slot == 4 && !CapacitorUtil.isCapacitor(stack))
                    return stack;
                return super.insertItem(slot, stack, simulate);
            }

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
    }
}
