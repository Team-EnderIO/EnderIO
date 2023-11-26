package com.enderio.machines.common.blockentity;

import com.enderio.api.travel.ITravelTarget;
import com.enderio.base.common.travel.TravelSavedData;
import com.enderio.core.common.network.slot.BooleanNetworkDataSlot;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TravelAnchorBlockEntity extends MachineBlockEntity {

    public static final SingleSlotAccess GHOST = new SingleSlotAccess();
    private final StringNetworkDataSlot nameDataSlot;
    private final BooleanNetworkDataSlot visibilityDataSlot;
    private final ResourceLocationNetworkDataSlot iconDataSlot;
    public TravelAnchorBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
        nameDataSlot = new StringNetworkDataSlot(this::getName, name -> getOrCreateTravelTarget().setName(name));
        visibilityDataSlot = new BooleanNetworkDataSlot(this::getVisibility, vis -> getOrCreateTravelTarget().setVisibility(vis));
        iconDataSlot = new ResourceLocationNetworkDataSlot(() -> ForgeRegistries.ITEMS.getKey(getIcon()),
            loc -> getOrCreateTravelTarget().setIcon(ForgeRegistries.ITEMS.getValue(loc)));
        addDataSlot(nameDataSlot);
        addDataSlot(visibilityDataSlot);
        addDataSlot(iconDataSlot);
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
        setIcon(stack.getItem());
    }

    @Nullable
    public String getName() {
        return getOrCreateTravelTarget().getName();
    }

    public void setName(String name) {
        if (level != null && level.isClientSide()) {
            clientUpdateSlot(nameDataSlot, name);
        } else {
            getOrCreateTravelTarget().setName(name);
        }
    }

    public Item getIcon() {
        return getOrCreateTravelTarget().getIcon();
    }

    public void setIcon(Item icon) {
        if (level != null && level.isClientSide()) {
            clientUpdateSlot(iconDataSlot, ForgeRegistries.ITEMS.getKey(icon));
        } else {
            getOrCreateTravelTarget().setIcon(icon);
        }
    }

    public boolean getVisibility() {
        return getOrCreateTravelTarget().getVisibility();
    }

    public void setVisibility(boolean visible) {
        if (level != null && level.isClientSide()) {
            clientUpdateSlot(visibilityDataSlot, visible);
        } else {
            getOrCreateTravelTarget().setVisibility(visible);
        }
    }

    private AnchorTravelTarget getOrCreateTravelTarget() {
        Optional<ITravelTarget> travelTarget = getTravelData().getTravelTarget(worldPosition);
        if (travelTarget.isPresent() && travelTarget.get() instanceof AnchorTravelTarget anchorTravelTarget) {
            return anchorTravelTarget;
        }
        AnchorTravelTarget anchorTravelTarget = new AnchorTravelTarget(worldPosition, "", Items.AIR, true);
        getTravelData().addTravelTarget(level, anchorTravelTarget);
        return anchorTravelTarget;
    }

    private TravelSavedData getTravelData() {
        return TravelSavedData.getTravelData(level);
    }

}
