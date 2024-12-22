package com.enderio.machines.common.blocks.travel_anchor;

import com.enderio.base.api.travel.TravelTarget;
import com.enderio.base.api.travel.TravelTargetApi;
import com.enderio.core.common.network.NetworkDataSlot;
import com.enderio.machines.common.blocks.base.blockentity.MachineBlockEntity;
import com.enderio.machines.common.blocks.base.inventory.MachineInventoryLayout;
import com.enderio.machines.common.blocks.base.inventory.SingleSlotAccess;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.travel.AnchorTravelTarget;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

// TODO: Shouldn't be a machine block entity...
public class TravelAnchorBlockEntity extends MachineBlockEntity {

    public static final SingleSlotAccess GHOST = new SingleSlotAccess();

    private final NetworkDataSlot<AnchorTravelTarget> travelTargetDataSlot;

    public TravelAnchorBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        this(MachineBlockEntities.TRAVEL_ANCHOR.get(), pWorldPosition, pBlockState);
    }

    public TravelAnchorBlockEntity(BlockEntityType<?> type, BlockPos pWorldPosition, BlockState pBlockState) {
        super(type, pWorldPosition, pBlockState, false);

        travelTargetDataSlot = addDataSlot(
                AnchorTravelTarget.DATA_SLOT_TYPE.create(this::getOrCreateTravelTarget, this::setTravelTarget));
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new TravelAnchorMenu(containerId, inventory, this);
    }

    @Override
    public @Nullable MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder().setStackLimit(1).ghostSlot().slotAccess(GHOST).build();
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        ItemStack stack = GHOST.getItemStack(getInventory());
        setIcon(stack.getItem());
    }

    @Nullable
    public String getName() {
        return getOrCreateTravelTarget().name();
    }

    public void setName(String name) {
        var newTravelTarget = getOrCreateTravelTarget().withName(name);
        if (level != null && level.isClientSide()) {
            clientUpdateSlot(travelTargetDataSlot, newTravelTarget);
        } else {
            setTravelTarget(newTravelTarget);
        }
    }

    public Item getIcon() {
        return getOrCreateTravelTarget().icon();
    }

    public void setIcon(Item icon) {
        var newTravelTarget = getOrCreateTravelTarget().withIcon(icon);
        if (level != null && level.isClientSide()) {
            clientUpdateSlot(travelTargetDataSlot, newTravelTarget);
        } else {
            setTravelTarget(newTravelTarget);
        }
    }

    public boolean isVisible() {
        return getOrCreateTravelTarget().isVisible();
    }

    public void setIsVisible(boolean isVisible) {
        var newTravelTarget = getOrCreateTravelTarget().withVisible(isVisible);
        if (level != null && level.isClientSide()) {
            clientUpdateSlot(travelTargetDataSlot, newTravelTarget);
        } else {
            setTravelTarget(newTravelTarget);
        }
    }

    private AnchorTravelTarget getOrCreateTravelTarget() {
        Optional<TravelTarget> travelTarget = TravelTargetApi.INSTANCE.get(level, worldPosition);
        if (travelTarget.isPresent() && travelTarget.get() instanceof AnchorTravelTarget anchorTravelTarget) {
            return anchorTravelTarget;
        }

        AnchorTravelTarget anchorTravelTarget = new AnchorTravelTarget(worldPosition, "", Items.AIR, true);
        setTravelTarget(anchorTravelTarget);
        return anchorTravelTarget;
    }

    private void setTravelTarget(AnchorTravelTarget target) {
        TravelTargetApi.INSTANCE.set(level, target);
    }

}
