package com.enderio.machines.common.blocks.vat;

import com.enderio.core.common.network.menu.FloatSyncSlot;
import com.enderio.core.common.network.menu.RecipeSyncSlot;
import com.enderio.machines.common.blocks.base.fluid.FluidStorageInfo;
import com.enderio.machines.common.blocks.base.fluid.FluidStorageSyncSlot;
import com.enderio.machines.common.blocks.base.menu.MachineMenu;
import com.enderio.machines.common.blocks.base.menu.MachineSlot;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.init.MachineRecipes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class VatMenu extends MachineMenu<VatBlockEntity> {
    public static final int INPUTS_INDEX = 0;
    public static final int INPUT_COUNT = 2;
    public static final int LAST_INDEX = 1;

    public static final int MOVE_TO_OUTPUT_TANK_BUTTON_ID = 0;
    public static final int DUMP_OUTPUT_TANK_BUTTON_ID = 1;

    private final FloatSyncSlot craftingProgressSlot;
    private final FluidStorageSyncSlot inputTankSlot;
    private final FluidStorageSyncSlot outputTankSlot;
    private final RecipeSyncSlot<FermentingRecipe> recipeSlot;

    public VatMenu(int pContainerId, Inventory inventory, VatBlockEntity blockEntity) {
        super(MachineMenus.VAT.get(), pContainerId, inventory, blockEntity);
        addSlots();

        craftingProgressSlot = addSyncSlot(FloatSyncSlot.readOnly(blockEntity::getCraftingProgress));
        inputTankSlot = addSyncSlot(
                FluidStorageSyncSlot.readOnly(() -> FluidStorageInfo.of(blockEntity.getInputTank())));
        outputTankSlot = addSyncSlot(
                FluidStorageSyncSlot.readOnly(() -> FluidStorageInfo.of(blockEntity.getOutputTank())));
        recipeSlot = addSyncSlot(
                RecipeSyncSlot.readOnly(MachineRecipes.VAT_FERMENTING.type().get(), blockEntity::getRecipe));
    }

    public VatMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.VAT.get(), MachineBlockEntities.VAT.get(), containerId, playerInventory, buf);
        addSlots();

        craftingProgressSlot = addSyncSlot(FloatSyncSlot.standalone());
        inputTankSlot = addSyncSlot(FluidStorageSyncSlot.standalone());
        outputTankSlot = addSyncSlot(FluidStorageSyncSlot.standalone());
        recipeSlot = addSyncSlot(RecipeSyncSlot.standalone(MachineRecipes.VAT_FERMENTING.type().get()));
    }

    private void addSlots() {
        addSlot(new MachineSlot(getMachineInventory(), VatBlockEntity.REAGENTS.get(0), 56, 12));
        addSlot(new MachineSlot(getMachineInventory(), VatBlockEntity.REAGENTS.get(1), 105, 12));

        addPlayerInventorySlots(8, 84);
    }

    public float getCraftingProgress() {
        return craftingProgressSlot.get();
    }

    public FluidStorageInfo getInputTank() {
        return inputTankSlot.get();
    }

    public FluidStorageInfo getOutputTank() {
        return outputTankSlot.get();
    }

    @Nullable
    public RecipeHolder<FermentingRecipe> getRecipe() {
        return recipeSlot.get();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        var vat = getBlockEntity();
        switch (id) {
        case MOVE_TO_OUTPUT_TANK_BUTTON_ID:
            vat.moveFluidToOutputTank();
            return true;
        case DUMP_OUTPUT_TANK_BUTTON_ID:
            vat.dumpOutputTank();
            return true;
        default:
            return false;
        }
    }
}
