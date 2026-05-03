package com.enderio.enderio.content.machines.vat;

import com.enderio.core.common.network.menu.FloatSyncSlot;
import com.enderio.core.common.network.menu.IntSyncSlot;
import com.enderio.core.common.network.menu.StringSyncSlot;
import com.enderio.enderio.foundation.fluid.FluidStorageInfo;
import com.enderio.enderio.foundation.fluid.FluidStorageSyncSlot;
import com.enderio.enderio.foundation.menu.MachineMenu;
import com.enderio.enderio.foundation.menu.MachineSlot;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.fluids.FluidStack;

public class VatMenu extends MachineMenu<VatBlockEntity> {
    public static final int INPUTS_INDEX = 0;
    public static final int INPUT_COUNT = 2 + 1; //items + fluid
    public static final int LAST_INDEX = 1;

    public static final int MOVE_TO_OUTPUT_TANK_BUTTON_ID = 0;
    public static final int DUMP_OUTPUT_TANK_BUTTON_ID = 1;

    private final FloatSyncSlot craftingProgressSlot;
    private final FluidStorageSyncSlot inputTankSlot;
    private final FluidStorageSyncSlot outputTankSlot;

    private final IntSyncSlot inputAmount;
    private final FluidStorageSyncSlot result;
    private final FloatSyncSlot firstReagent;
    private final FloatSyncSlot secondReagent;

    public VatMenu(int containerId, Inventory inventory, VatBlockEntity blockEntity) {
        super(EIOMenus.VAT.get(), containerId, inventory, blockEntity);
        addSlots();

        craftingProgressSlot = addSyncSlot(FloatSyncSlot.readOnly(blockEntity::getCraftingProgress));
        inputTankSlot = addSyncSlot(
                FluidStorageSyncSlot.readOnly(() -> new FluidStorageInfo(blockEntity.getInputFluid(), VatBlockEntity.TANK_CAPACITY)));
        outputTankSlot = addSyncSlot(
                FluidStorageSyncSlot.readOnly(() -> new FluidStorageInfo(blockEntity.getOutputFluid(), VatBlockEntity.TANK_CAPACITY)));

        inputAmount = addSyncSlot(IntSyncSlot.readOnly(() -> {
            if (blockEntity.getRecipe() == null) {
                return 0;
            }
            return blockEntity.getRecipe().value().input().amount();
        }));

        result = addSyncSlot(FluidStorageSyncSlot.readOnly(() -> {
            if (blockEntity.getRecipe() == null) {
                return new FluidStorageInfo(FluidStack.EMPTY, 0);
            }
            return new FluidStorageInfo(blockEntity.getRecipe().value().output().create(),  VatBlockEntity.TANK_CAPACITY);
        }));

        firstReagent = addSyncSlot(FloatSyncSlot.readOnly(() -> {
            if (blockEntity.getRecipe() == null) {
                return 0f;
            }
            return (float) FermentingRecipe.getModifier(getSlot(0).getItem(), blockEntity.getRecipe().value().firstReagent());
        }));

        secondReagent = addSyncSlot(FloatSyncSlot.readOnly(() -> {
            if (blockEntity.getRecipe() == null) {
                return 0f;
            }
            return (float) FermentingRecipe.getModifier(getSlot(0).getItem(), blockEntity.getRecipe().value().secondReagent());
        }));

    }

    public VatMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(EIOMenus.VAT.get(), containerId, playerInventory, buf, EIOBlockEntities.VAT.get());
        addSlots();

        craftingProgressSlot = addSyncSlot(FloatSyncSlot.standalone());
        inputTankSlot = addSyncSlot(FluidStorageSyncSlot.standalone());
        outputTankSlot = addSyncSlot(FluidStorageSyncSlot.standalone());

        inputAmount = addSyncSlot(IntSyncSlot.standalone());
        result = addSyncSlot(FluidStorageSyncSlot.standalone());
        firstReagent = addSyncSlot(FloatSyncSlot.standalone());
        secondReagent = addSyncSlot(FloatSyncSlot.standalone());
    }

    private void addSlots() {
        addSlot(new MachineSlot(getMachineInventory(), VatBlockEntity.REAGENTS.slot(0), 56, 12));
        addSlot(new MachineSlot(getMachineInventory(), VatBlockEntity.REAGENTS.slot(1), 105, 12));

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


    public int getInputAmount() {
        return inputAmount.get();
    }

    public FluidStorageInfo getResult() {
        return result.get();
    }

    public float getFirstReagent() {
        return firstReagent.get();
    }

    public float getSecondReagent() {
        return secondReagent.get();
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
