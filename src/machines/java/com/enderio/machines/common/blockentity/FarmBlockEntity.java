package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.core.common.network.slot.BooleanNetworkDataSlot;
import com.enderio.core.common.network.slot.IntegerNetworkDataSlot;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ticket.AABBTicket;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FarmBlockEntity extends PoweredMachineBlockEntity {
    private static final QuadraticScalable ENERGY_CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.DRAIN_CAPACITY);
    private static final QuadraticScalable ENERGY_USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, MachinesConfig.COMMON.ENERGY.DRAIN_USAGE);
    public static final SingleSlotAccess AXE = new SingleSlotAccess();
    public static final SingleSlotAccess HOE = new SingleSlotAccess();
    public static final SingleSlotAccess SHEAR = new SingleSlotAccess();
    public static final SingleSlotAccess NE = new SingleSlotAccess();
    public static final SingleSlotAccess SE = new SingleSlotAccess();
    public static final SingleSlotAccess SW = new SingleSlotAccess();
    public static final SingleSlotAccess NW = new SingleSlotAccess();
    public static final MultiSlotAccess BONEMEAL = new MultiSlotAccess();
    public static final MultiSlotAccess OUTPUT = new MultiSlotAccess();
    public static final FakePlayer FARM_PLAYER = new FakePlayer(ServerLifecycleHooks.getCurrentServer().overworld(), new GameProfile(UUID.fromString(""), "enderio:farm"));
    private List<BlockPos> positions;
    private int currentIndex = 0;
    private AABBTicket ticket;

    public FarmBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_USAGE, type, worldPosition, blockState);

        this.range = 5;

        rangeDataSlot = new IntegerNetworkDataSlot(this::getRange, r -> this.range = r) {
            @Override
            public void updateServerCallback() {
                updateLocations();
            }
        };
        addDataSlot(rangeDataSlot);

        rangeVisibleDataSlot = new BooleanNetworkDataSlot(this::isRangeVisible, b -> this.rangeVisible = b);
        addDataSlot(rangeVisibleDataSlot);
    }

    @Override
    public @Nullable MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .capacitor()
            .inputSlot((i,s) -> s.is(ItemTags.AXES))
            .slotAccess(AXE)
            .inputSlot((i,s) -> s.is(ItemTags.HOES))
            .slotAccess(HOE)
            .inputSlot((i,s) -> s.is(Tags.Items.SHEARS))
            .slotAccess(SHEAR)
            .inputSlot()
            .slotAccess(NE)
            .inputSlot()
            .slotAccess(SE)
            .inputSlot()
            .slotAccess(SW)
            .inputSlot()
            .slotAccess(NW)
            .inputSlot(2)
            .slotAccess(BONEMEAL)
            .outputSlot(6)
            .slotAccess(OUTPUT)
            .build();
    }

    @Override
    public void serverTick() {
        if (isActive()) {
            //Hydrate
            this.ticket = FarmlandWaterManager.addAABBTicket(getLevel(), new AABB(getBlockPos()).expandTowards(range, -1, range));
            doFarmTask();
        }

        super.serverTick();
    }

    private void doFarmTask() {
        int stop = Math.min(currentIndex + range, positions.size());
        while (currentIndex < stop) {
            BlockPos soil = positions.get(currentIndex);
            for (FarmTask task: FarmTask.TASKS) {
                if (task.farm(soil, this) == FarmInteraction.USED) {
                    break;
                }
            }
        }
        if (stop == positions.size()) {
            currentIndex = 0;
        }
    }

    public ItemStack getSeedForPos(BlockPos soil) {
        //TODO
        return NW.getItemStack(getInventoryNN());
    }

    @Override
    protected boolean isActive() {
        if (!canAct()) {
            return false;
        }
        //Check tool
        return true;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return null;
    }

    @Override
    public void setRange(int range) {
        super.setRange(range);
        updateLocations();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateLocations();
    }

    private void updateLocations() {
        positions = new ArrayList<>();
        currentIndex = 0;
        for (BlockPos pos : BlockPos.betweenClosed(worldPosition.offset(-range,-1, -range), worldPosition.offset(range,-1,range))) {
            positions.add(pos.immutable()); //Need to make it immutable
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        ticket.invalidate();
    }

    public enum FarmInteraction {
        USED,
        BLOCKED,
        IGNORED;
    }
}
