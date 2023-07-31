package com.enderio.machines.common.blockentity;

import com.enderio.base.common.travel.TravelSavedData;
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
import java.util.Random;

public class TravelAnchorBlockEntity extends MachineBlockEntity {

    private String name = "";
    private Item icon = Items.AIR;
    private boolean visible = true;
    public static final SingleSlotAccess GHOST = new SingleSlotAccess();

    public TravelAnchorBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
        //        addDataSlot(new StringNetworkDataSlot(() -> getName(), name -> setName(name)));
        //        addDataSlot(new ResourceLocationNetworkDataSlot(() -> ForgeRegistries.ITEMS.getKey(getIcon()), (loc) -> setIcon(ForgeRegistries.ITEMS.getValue(loc))));
        name = ('a' + new Random().nextInt(26)) + "";
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
        return name;
    }

    public void setName(String name) {
        this.name = name;
        AnchorTravelTarget target = getOrCreateTravelTarget();
        target.setName(name);
    }

    public Item getIcon() {
        setChanged();
        return icon;
    }

    public void setIcon(Item icon) {
        setChanged();
        this.icon = icon;
        AnchorTravelTarget target = getOrCreateTravelTarget();
        target.setIcon(icon != Items.AIR ? icon : null);
    }

    public boolean getVisibility() {
        return visible;
    }

    public void setVisibility(boolean visible) {
        setChanged();
        this.visible = visible;
        AnchorTravelTarget target = getOrCreateTravelTarget();
        target.setVisibility(visible);
    }

    @Override
    public void onLoad() {
        AnchorTravelTarget target = getOrCreateTravelTarget();
        setName(target.getName());
        setIcon(target.getIcon());
        setVisibility(target.getVisibility());
        super.onLoad();
    }

    @Override
    public void setRemoved() {
        getTravelData().removeTravelTargetAt(worldPosition);
        super.setRemoved();
    }

    private AnchorTravelTarget createTravelTarget() {
        return new AnchorTravelTarget(worldPosition, getName(), getIcon() == Items.AIR ? null : getIcon(), getVisibility());
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
