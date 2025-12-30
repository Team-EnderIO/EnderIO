package com.enderio.enderio.content.enchanter;

import com.enderio.core.common.blockentity.EnderBlockEntity;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.inventory.MachineInventory;
import com.enderio.enderio.foundation.inventory.MachineInventoryLayout;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import com.enderio.enderio.foundation.io.DumbIOConfigurable;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIORecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

public class EnchanterBlockEntity extends EnderBlockEntity implements MenuProvider {

    @Nullable
    private RecipeHolder<EnchanterRecipe> currentRecipe;
    public static final SingleSlotAccess BOOK = new SingleSlotAccess();
    public static final SingleSlotAccess CATALYST = new SingleSlotAccess();
    public static final SingleSlotAccess LAPIS = new SingleSlotAccess();
    public static final SingleSlotAccess OUTPUT = new SingleSlotAccess();

    private final MachineInventory inventory;

    public EnchanterBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.ENCHANTER.get(), worldPosition, blockState);

        inventory = createInventory();
    }

    public EnchanterRecipe.Input createRecipeInput() {
        return new EnchanterRecipe.Input(BOOK.getItemStack(getInventory()), CATALYST.getItemStack(getInventory()),
                LAPIS.getItemStack(getInventory()));
    }

    // region MenuProvider

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player pPlayer) {
        return new EnchanterMenu(containerId, playerInventory, this);
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

            @Override
            protected void onContentsChanged(int slot, ItemStack previousContents) {
                if (level == null) {
                    return;
                }

                EnchanterRecipe.Input recipeInput = createRecipeInput();

                if (level instanceof ServerLevel serverLevel) {
                    currentRecipe = serverLevel.recipeAccess()
                        .getRecipeFor(EIORecipes.ENCHANTING.type().get(), recipeInput, level)
                        .orElse(null);
                }
                if (!OUTPUT.isSlot(slot)) {
                    if (currentRecipe != null) {
                        OUTPUT.setStackInSlot(this,
                                currentRecipe.value().assemble(recipeInput, level.registryAccess()));
                    } else {
                        OUTPUT.setStackInSlot(this, ItemStack.EMPTY);
                    }
                }

                setChanged();
            }

            @Override
            public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
                if (level == null) {
                    return 0;
                }

                if (OUTPUT.isSlot(index) && level.isClientSide()) {
                    return 0;
                }
                return super.extract(index, resource, amount, transaction);
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
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putChild(MachineNBTKeys.ITEMS, inventory);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.child(MachineNBTKeys.ITEMS).ifPresent(inventory::deserialize);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        inventory.copyFromItem(components.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CONTAINER, inventory.toItemContents());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        super.removeComponentsFromTag(output);
        output.discard(MachineNBTKeys.ITEMS);
    }

    // endregion
}
