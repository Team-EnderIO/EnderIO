package com.enderio.machines.common.blockentity;

import com.enderio.core.common.blockentity.EnderBlockEntity;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.DumbIOConfigurable;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.EnchanterMenu;
import com.enderio.machines.common.recipe.EnchanterRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

public class EnchanterBlockEntity extends EnderBlockEntity implements MenuProvider {

    private final RecipeWrapper container;
    @Nullable
    private RecipeHolder<EnchanterRecipe> currentRecipe;
    public static final SingleSlotAccess BOOK = new SingleSlotAccess();
    public static final SingleSlotAccess CATALYST = new SingleSlotAccess();
    public static final SingleSlotAccess LAPIS = new SingleSlotAccess();
    public static final SingleSlotAccess OUTPUT = new SingleSlotAccess();

    private final MachineInventory inventory;

    public EnchanterBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MachineBlockEntities.ENCHANTER.get(), worldPosition, blockState);

        inventory = createInventory();
        container = new RecipeWrapper(inventory);
    }

    public RecipeWrapper getContainer() {
        return container;
    }

    // region MenuProvider

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new EnchanterMenu(this, pInventory, pContainerId);
    }

    // endregion

    // region Inventory & Recipe

    public MachineInventory getInventory() {
        return inventory;
    }

    private MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .inputSlot((slot, stack) -> stack.getItem() == Items.WRITABLE_BOOK)
            .slotAccess(BOOK)
            .inputSlot()
            .slotAccess(CATALYST)
            .inputSlot((slot, stack) -> stack.is(Tags.Items.GEMS_LAPIS))
            .slotAccess(LAPIS)
            .outputSlot()
            .slotAccess(OUTPUT)
            .build();
    }

    private MachineInventory createInventory() {
        // Custom behaviour as this works more like a crafting table than a machine.
        return new MachineInventory(DumbIOConfigurable.DISABLED, getInventoryLayout()) {

            protected void onContentsChanged(int slot) {
                if (level == null) {
                    return;
                }
                currentRecipe = level.getRecipeManager().getRecipeFor(MachineRecipes.ENCHANTING.type().get(), container, level).orElse(null);
                if (!OUTPUT.isSlot(slot)) {
                    if (currentRecipe != null) {
                        OUTPUT.setStackInSlot(this, currentRecipe.value().assemble(container, level.registryAccess()));
                    } else {
                        OUTPUT.setStackInSlot(this, ItemStack.EMPTY);
                    }
                }

                setChanged();
            }

            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (level == null) {
                    return ItemStack.EMPTY;
                }

                if (OUTPUT.isSlot(slot) && level.isClientSide()) {
                    return ItemStack.EMPTY;
                }
                return super.extractItem(slot, amount, simulate);
            }
        };
    }

    @Nullable
    public EnchanterRecipe getCurrentRecipe() {
        if (currentRecipe == null) {
            return null;
        }

        return currentRecipe.value();
    }

    // endregion

    // region Serialization

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(tag, lookupProvider);
        inventory.deserializeNBT(lookupProvider, tag.getCompound(MachineNBTKeys.ITEMS));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(tag, lookupProvider);
        tag.put(MachineNBTKeys.ITEMS, inventory.serializeNBT(lookupProvider));
    }

    // endregion
}
