package com.enderio.machines.common.blockentity;

import com.enderio.base.common.travel.TravelSavedData;
import com.enderio.core.common.network.slot.BooleanNetworkDataSlot;
import com.enderio.core.common.network.slot.StringNetworkDataSlot;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.TravelAnchorMenu;
import com.enderio.machines.common.travel.AnchorTravelTarget;
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

import java.util.Optional;

public class TravelAnchorBlockEntity extends MachineBlockEntity {

    AnchorTravelTarget target;
    public static final SingleSlotAccess GHOST = new SingleSlotAccess();

    private final StringNetworkDataSlot nameDataSlot;
    private final BooleanNetworkDataSlot visibilityDataSlot;

    public TravelAnchorBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
        nameDataSlot = new StringNetworkDataSlot(() -> getName(), name -> setName(name));
        visibilityDataSlot = new BooleanNetworkDataSlot(() -> getVisibility(), visible -> setVisibility(visible));
        addDataSlot(nameDataSlot);
        addDataSlot(visibilityDataSlot);
        target = getOrCreateTravelTarget();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new TravelAnchorMenu(this, inventory, containerId);
    }

    @Override
    public @Nullable MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder().setStackLimit(1).ghostSlot().slotAccess(GHOST).build();
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        ItemStack stack = GHOST.getItemStack(getInventory());
        if (!stack.isEmpty()) {
            setIcon(stack.getItem());
        }
    }

    @Nullable
    public String getName() {
        return target.getName();
    }

    public void setName(String name) {
        setChanged();
        if (level != null && level.isClientSide()) {
            clientUpdateSlot(nameDataSlot, name);
        } else {
            target.setName(name);
        }
    }

    public Item getIcon() {
        return target.getIcon();
    }

    public void setIcon(Item icon) {
        setChanged();
        target.setIcon(icon != Items.AIR ? icon : null);
    }

    public boolean getVisibility() {
        return target.getVisibility();
    }

    public void setVisibility(boolean visible) {
        setChanged();
        if (level != null && level.isClientSide()) {
            clientUpdateSlot(visibilityDataSlot, visible);
        } else {
            target.setVisibility(visible);
        }
    }

    @Override
    public void onLoad() {
        target = getOrCreateTravelTarget();
        super.onLoad();
    }

    @Override
    public void setRemoved() {
        getTravelData().removeTravelTargetAt(worldPosition);
        super.setRemoved();
    }

    private AnchorTravelTarget createTravelTarget() {
        return new AnchorTravelTarget(worldPosition, "", Items.AIR, true);
    }

    private Optional<AnchorTravelTarget> getTravelTarget() {
        return getTravelData().getTravelTarget(worldPosition).filter(target -> target instanceof AnchorTravelTarget).map(target -> (AnchorTravelTarget) target);
    }

    private AnchorTravelTarget getOrCreateTravelTarget() {
        return getTravelTarget().orElse(createTravelTarget());
    }

    private TravelSavedData getTravelData() {
        return TravelSavedData.getTravelData(level);
    }

}
