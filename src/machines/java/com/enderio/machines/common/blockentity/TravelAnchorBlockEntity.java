package com.enderio.machines.common.blockentity;

import com.enderio.api.travel.ITravelTarget;
import com.enderio.base.common.travel.TravelSavedData;
import com.enderio.core.common.network.NetworkDataSlot;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.TravelAnchorMenu;
import com.enderio.machines.common.travel.AnchorTravelTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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

    public static final SingleSlotAccess GHOST = new SingleSlotAccess();
    private final NetworkDataSlot<String> nameDataSlot;
    private final NetworkDataSlot<Boolean> visibilityDataSlot;
    private final NetworkDataSlot<ResourceLocation> iconDataSlot;
    public TravelAnchorBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        this(MachineBlockEntities.TRAVEL_ANCHOR.get(), pWorldPosition, pBlockState);
    }

    public TravelAnchorBlockEntity(BlockEntityType<?> type, BlockPos pWorldPosition, BlockState pBlockState) {
        super(type, pWorldPosition, pBlockState);
        nameDataSlot = addDataSlot(NetworkDataSlot.STRING.create(this::getName, name -> getOrCreateTravelTarget().setName(name)));
        visibilityDataSlot = addDataSlot(NetworkDataSlot.BOOL.create(this::getVisibility, vis -> getOrCreateTravelTarget().setVisibility(vis)));
        iconDataSlot = addDataSlot(NetworkDataSlot.RESOURCE_LOCATION.create(() -> BuiltInRegistries.ITEM.getKey(getIcon()),
            loc -> getOrCreateTravelTarget().setIcon(BuiltInRegistries.ITEM.get(loc))));
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
        ItemStack stack = GHOST.getItemStack(getInventoryNN());
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
            clientUpdateSlot(iconDataSlot, BuiltInRegistries.ITEM.getKey(icon));
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
