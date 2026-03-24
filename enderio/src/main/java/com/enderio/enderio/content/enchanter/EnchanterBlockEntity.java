package com.enderio.enderio.content.enchanter;

import com.enderio.core.common.blockentity.EnderBlockEntity;
import com.enderio.core.common.storage.ItemStorage;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.io.DumbIOConfigurable;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIORecipeTypes;
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
import org.jspecify.annotations.Nullable;

public class EnchanterBlockEntity extends EnderBlockEntity implements MenuProvider {

    @Nullable
    private RecipeHolder<EnchanterRecipe> currentRecipe;
    public static final SingleResourceSlotKey<ItemResource> BOOK = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<ItemResource> CATALYST = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<ItemResource> LAPIS = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<ItemResource> OUTPUT = new SingleResourceSlotKey<>();

    private final ItemStorage inventory;

    public EnchanterBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.ENCHANTER.get(), worldPosition, blockState);

        inventory = createInventory();
    }

    public EnchanterRecipe.Input createRecipeInput() {
        return new EnchanterRecipe.Input(getInventory().getStack(BOOK), getInventory().getStack(CATALYST),
            getInventory().getStack(CATALYST));
    }

    // region MenuProvider

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new EnchanterMenu(containerId, playerInventory, this);
    }

    // endregion

    // region Inventory & Recipe

    public ItemStorage getInventory() {
        return inventory;
    }

    private ItemStorageLayout getInventoryLayout() {
        return ItemStorageLayout.builder()
            .slot(BOOK, SlotTemplates.input(), b -> b
                .filter((_, itemResource) -> itemResource.is(Items.WRITABLE_BOOK)))
            .slot(CATALYST, SlotTemplates.input())
            .slot(LAPIS, SlotTemplates.input(), b -> b
                .filter((_, itemResource) -> itemResource.is(Tags.Items.GEMS_LAPIS)))
            .slot(OUTPUT, SlotTemplates.output())
            .build();
    }

    private ItemStorage createInventory() {
        // Custom behaviour as this works more like a crafting table than a machine.
        return new ItemStorage(getInventoryLayout()) {

            @Override
            protected void onContentsChanged(int slot, ItemStack previousContents) {
                if (level == null) {
                    return;
                }

                EnchanterRecipe.Input recipeInput = createRecipeInput();

                if (level instanceof ServerLevel serverLevel) {
                    currentRecipe = serverLevel.recipeAccess()
                        .getRecipeFor(EIORecipeTypes.ENCHANTING.get(), recipeInput, level)
                        .orElse(null);
                }
                if (!OUTPUT.isSlot(slot)) {
                    if (currentRecipe != null) {
                        OUTPUT.setStackInSlot(this,
                                currentRecipe.value().assemble(recipeInput));
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
