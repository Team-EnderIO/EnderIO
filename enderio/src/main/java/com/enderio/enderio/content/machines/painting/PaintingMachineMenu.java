package com.enderio.enderio.content.machines.painting;

import com.enderio.core.common.network.menu.FloatSyncSlot;
import com.enderio.enderio.foundation.menu.MachineSlot;
import com.enderio.enderio.foundation.menu.PoweredMachineMenu;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.Nullable;

public class PaintingMachineMenu extends PoweredMachineMenu<PaintingMachineBlockEntity> {

    private final FloatSyncSlot craftingProgressSlot;

    public PaintingMachineMenu(Inventory inventory, int containerId, PaintingMachineBlockEntity blockEntity) {
        super(EIOMenus.PAINTING_MACHINE.get(), containerId, inventory, blockEntity);
        addSlots();

        craftingProgressSlot = addSyncSlot(FloatSyncSlot.readOnly(blockEntity::getCraftingProgress));
    }

    public PaintingMachineMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(EIOMenus.PAINTING_MACHINE.get(), containerId, playerInventory, buf,
            EIOBlockEntities.PAINTING_MACHINE.get());
        addSlots();

        craftingProgressSlot = addSyncSlot(FloatSyncSlot.standalone());
    }

    private void addSlots() {
        addCapacitorSlot(12, 60);

        addSlot(new MachineSlot(getMachineInventory(), PaintingMachineBlockEntity.INPUT, 67, 34));
        addSlot(new MachineSlot(getMachineInventory(), PaintingMachineBlockEntity.PAINT, 38, 34));
        addSlot(new MachineSlot(getMachineInventory(), PaintingMachineBlockEntity.OUTPUT, 120, 34));

        addPlayerInventorySlots(8, 84);
    }

    public float getCraftingProgress() {
        return craftingProgressSlot.get();
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.valueOf("ENDERIO_PAINTING");
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedItemContents stackedContents) {
        stackedContents.accountSimpleStack(getSlot(1).getItem());
        stackedContents.accountSimpleStack(getSlot(2).getItem());
    }

    @Override
    public PostPlaceAction handlePlacement(boolean useMaxItems, boolean allowDroppingItemsToClear, RecipeHolder<?> recipe, ServerLevel level,
        Inventory inventory) {
        return handlePlacement(false, allowDroppingItemsToClear, recipe, level, inventory, 1, 3);
    }

    @Nullable
    protected Recipe<?> getRecipe() {
        var currentTask = this.getBlockEntity().craftingTaskHost.getCurrentTask();
        if (currentTask == null) {
            return null;
        }
        return currentTask.getRecipe();
    }
}
