package com.enderio.enderio.content.travel.travel_anchor;

import com.enderio.core.common.network.menu.BoolSyncSlot;
import com.enderio.core.common.network.menu.StringSyncSlot;
import com.enderio.enderio.api.travel.TravelTargetApi;
import com.enderio.enderio.foundation.menu.GhostMachineSlot;
import com.enderio.enderio.foundation.menu.MachineMenu;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TravelAnchorMenu extends MachineMenu<TravelAnchorBlockEntity> {

    private final BoolSyncSlot visibility;
    private final StringSyncSlot name;

    public TravelAnchorMenu(int containerId, Inventory inventory, TravelAnchorBlockEntity blockEntity) {
        super(EIOMenus.TRAVEL_ANCHOR.get(), containerId, inventory, blockEntity);
        addSlots();
        visibility = addUpdatableSyncSlot(BoolSyncSlot.simple(() -> this.isVisible(blockEntity),
            aBoolean -> this.setVisible(blockEntity, aBoolean)));
        name = addUpdatableSyncSlot(StringSyncSlot.simple(() -> getName(blockEntity), b -> setName(blockEntity, b)));
    }

    public TravelAnchorMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(EIOMenus.TRAVEL_ANCHOR.get(), containerId, playerInventory, buf,
                EIOBlockEntities.TRAVEL_ANCHOR.get(), EIOBlockEntities.PAINTED_TRAVEL_ANCHOR.get());
        addSlots();
        visibility = addUpdatableSyncSlot(BoolSyncSlot.standalone());
        name = addUpdatableSyncSlot(StringSyncSlot.standalone());
    }

    private void addSlots() {
        addSlot(new GhostMachineSlot(getMachineInventory(), TravelAnchorBlockEntity.GHOST, 125, 10));
        addPlayerInventorySlots(8, 103);
    }

    public boolean isVisible() {
        return visibility.get();
    }

    public void setVisible(Boolean visible) {
        visibility.set(visible);
        updateSlot(visibility);
    }

    public String getName() {
        String name = this.name.get();
        return name == null ? "" : name;
    }

    public void setName(String newName) {
        name.set(newName);
        updateSlot(name);
    }

    //TODO we don't create the target if it's missing, but that should be fine?
    private boolean isVisible(BlockEntity blockEntity) {
        var target = TravelTargetApi.INSTANCE.get(blockEntity.getLevel(), blockEntity.getBlockPos());
        if (target.isPresent() && target.get() instanceof AnchorTravelTarget anchorTravelTarget) {
            return anchorTravelTarget.isVisible();
        }
        return false;
    }

    //TODO we don't create the target if it's missing, but that should be fine?
    private void setVisible(BlockEntity blockEntity, boolean visible) {
        var target = TravelTargetApi.INSTANCE.get(blockEntity.getLevel(), blockEntity.getBlockPos());
        if (target.isPresent() && target.get() instanceof AnchorTravelTarget anchorTravelTarget) {
            TravelTargetApi.INSTANCE.set(blockEntity.getLevel(), anchorTravelTarget.withVisible(visible));
        }
    }

    //TODO we don't create the target if it's missing, but that should be fine?
    private String getName(BlockEntity blockEntity) {
        var target = TravelTargetApi.INSTANCE.get(blockEntity.getLevel(), blockEntity.getBlockPos());
        if (target.isPresent() && target.get() instanceof AnchorTravelTarget anchorTravelTarget) {
            return anchorTravelTarget.name();
        }
        return "";
    }

    //TODO we don't create the target if it's missing, but that should be fine?
    private void setName(BlockEntity blockEntity, String newName) {
        var target = TravelTargetApi.INSTANCE.get(blockEntity.getLevel(), blockEntity.getBlockPos());
        if (target.isPresent() && target.get() instanceof AnchorTravelTarget anchorTravelTarget) {
            TravelTargetApi.INSTANCE.set(blockEntity.getLevel(), anchorTravelTarget.withName(newName));
        }
    }
}
