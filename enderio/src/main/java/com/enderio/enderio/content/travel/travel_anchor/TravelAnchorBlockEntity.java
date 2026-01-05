package com.enderio.enderio.content.travel.travel_anchor;

import com.enderio.enderio.api.travel.TravelTarget;
import com.enderio.enderio.api.travel.TravelTargetApi;
import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import com.enderio.enderio.foundation.inventory.MachineInventoryLayout;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import com.enderio.enderio.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

// TODO: Shouldn't be a machine block entity...
public class TravelAnchorBlockEntity extends MachineBlockEntity {

    public static final SingleSlotAccess GHOST = new SingleSlotAccess();

    public TravelAnchorBlockEntity(BlockPos worldPosition, BlockState blockState) {
        this(EIOBlockEntities.TRAVEL_ANCHOR.get(), worldPosition, blockState);
    }

    public TravelAnchorBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState, false);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        TravelTargetApi.INSTANCE.removeAt(level, pos);
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
        ItemStack stack = GHOST.getStack(getInventory());
        setIcon(stack.getItem());
    }

    //TODO items should be synced by default?
    public void setIcon(Item icon) {
        var newTravelTarget = getOrCreateTravelTarget().withIcon(icon);
        if (level != null && level.isClientSide()) {
//            clientUpdateSlot(travelTargetDataSlot, newTravelTarget);
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

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        getOrCreateTravelTarget(); //Make or load the target when the level is present
    }
}
