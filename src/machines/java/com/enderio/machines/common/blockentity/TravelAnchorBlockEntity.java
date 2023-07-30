package com.enderio.machines.common.blockentity;

import com.enderio.base.common.travel.TravelSavedData;
import com.enderio.core.common.network.slot.ResourceLocationNetworkDataSlot;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;

public class TravelAnchorBlockEntity extends MachineBlockEntity {

    private String name = "";
    private Item icon = Items.AIR;
    public static final SingleSlotAccess GHOST = new SingleSlotAccess();

    public TravelAnchorBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
        addDataSlot(new StringNetworkDataSlot(() -> getName(), name -> setName(name)));
        addDataSlot(new ResourceLocationNetworkDataSlot(() -> ForgeRegistries.ITEMS.getKey(getIcon()), (loc) -> setIcon(ForgeRegistries.ITEMS.getValue(loc))));
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

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        Optional<AnchorTravelTarget> target = getTravelTarget();
        if (target.isPresent()) {
            target.get().setName(name);
        } else {
            getTravelData().addTravelTarget(createTravelTarget());
        }
    }

    public Item getIcon() {
        setChanged();
        return icon;
    }

    public void setIcon(Item icon) {
        setChanged();
        this.icon = icon;
        Optional<AnchorTravelTarget> target = getTravelTarget();
        if (target.isPresent()) {
            target.get().setIcon(icon != Items.AIR ? icon : null);
        } else {
            getTravelData().addTravelTarget(createTravelTarget());
        }
    }

    @Override
    public void onLoad() {
        getTravelData().addTravelTarget(createTravelTarget());
        super.onLoad();
    }

    @Override
    public void setRemoved() {
        getTravelData().removeTravelTargetAt(worldPosition);
        super.setRemoved();
    }

    private AnchorTravelTarget createTravelTarget() {
        return new AnchorTravelTarget(worldPosition, getName(), getIcon() == Items.AIR ? null: getIcon());
    }

    private Optional<AnchorTravelTarget> getTravelTarget() {
        return getTravelData().getTravelTarget(worldPosition).filter(target -> target instanceof AnchorTravelTarget).map(target -> (AnchorTravelTarget)target);
    }

    private TravelSavedData getTravelData() {
        return TravelSavedData.getTravelData(level);
    }

}
