package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.filter.EntityFilter;
import com.enderio.api.io.IOMode;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.core.common.network.NetworkDataSlot;
import com.enderio.machines.common.attachment.ActionRange;
import com.enderio.machines.common.attachment.RangedActor;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineAttachments;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineDataComponents;
import com.enderio.machines.common.io.IOConfig;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.AversionObeliskMenu;
import com.enderio.machines.common.menu.InhibitorObeliskMenu;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import org.jetbrains.annotations.Nullable;

public class AversionObeliskBlockEntity extends PoweredMachineBlockEntity implements RangedActor {

    private static final QuadraticScalable ENERGY_CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.AVERSION_CAPACITY);
    private static final QuadraticScalable ENERGY_USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, MachinesConfig.COMMON.ENERGY.AVERSION_USAGE);
    private AABB aabb;
    private final NetworkDataSlot<ActionRange> actionRangeDataSlot;
    public static SingleSlotAccess FILTER = new SingleSlotAccess();

    public AversionObeliskBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_USAGE, MachineBlockEntities.AVERSION_OBELISK.get(), worldPosition, blockState);

        actionRangeDataSlot = addDataSlot(ActionRange.DATA_SLOT_TYPE.create(this::getActionRange, this::internalSetActionRange));

        // TODO: rubbish way of having a default. use an interface instead?
        if (!hasData(MachineAttachments.ACTION_RANGE)) {
            setData(MachineAttachments.ACTION_RANGE, new ActionRange(5, false));
        }

        NeoForge.EVENT_BUS.addListener(this::spawnEvent);
    }

    @Override
    protected boolean isActive() {
        return canAct();
    }

    @Override
    public @Nullable MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .inputSlot((integer, itemStack) -> itemStack.getCapability(EIOCapabilities.Filter.ITEM) instanceof EntityFilter)
            .slotAccess(FILTER)
            .capacitor()
            .build();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new AversionObeliskMenu(this, pPlayerInventory, pContainerId);
    }

    @Override
    public int getMaxRange() {
        return 255;
    }

    @Override
    public ActionRange getActionRange() {
        return getData(MachineAttachments.ACTION_RANGE);
    }

    @Override
    public void setActionRange(ActionRange actionRange) {
        if (level != null && level.isClientSide) {
            clientUpdateSlot(actionRangeDataSlot, actionRange);
        } else {
            internalSetActionRange(actionRange);
        }
    }

    private void internalSetActionRange(ActionRange actionRange) {
        setData(MachineAttachments.ACTION_RANGE, actionRange);
        updateLocations();
        setChanged();
    }

    @Override
    public void serverTick() {
        updateMachineState(MachineState.ACTIVE, isActive()); //No powered model state, so it needs to be done manually
        super.serverTick();
    }

    @Override
    public void clientTick() {
        if (level instanceof ClientLevel clientLevel) {
            getActionRange().addClientParticle(clientLevel, getBlockPos(), MachinesConfig.CLIENT.BLOCKS.INHIBITOR_RANGE_COLOR.get());
        }

        super.clientTick();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateLocations();
    }

    private void updateLocations() {
        aabb = new AABB(getBlockPos()).inflate(getRange());
    }

    @Override
    public IOConfig getDefaultIOConfig() {
        return IOConfig.of(IOMode.PULL);
    }

    @Override
    public boolean isIOConfigMutable() {
        return false;
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput components) {
        super.applyImplicitComponents(components);

        var actionRange = components.get(MachineDataComponents.ACTION_RANGE);
        if (actionRange != null) {
            setData(MachineAttachments.ACTION_RANGE, actionRange);
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(MachineDataComponents.ACTION_RANGE, getData(MachineAttachments.ACTION_RANGE));
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        removeData(MachineAttachments.ACTION_RANGE);
    }

    @SubscribeEvent
    public void spawnEvent(FinalizeSpawnEvent event) {
        if (level.isClientSide || event.getSpawnType() != MobSpawnType.NATURAL) {
            return;
        }
        if (FILTER.getItemStack(this).getCapability(EIOCapabilities.Filter.ITEM) instanceof EntityFilter entityFilter) {
            if (!entityFilter.test(event.getEntity())) {
                return;
            }
        }
        if (isActive() && aabb.contains(event.getX(), event.getY(), event.getZ())) {
            int cost = ENERGY_USAGE.base().get(); //TODO scale on entity and range? The issue is that it needs the energy "now" and can't wait for it like other machines
            int energy = getEnergyStorage().consumeEnergy(cost, false);
            if (energy == cost) {
                event.setSpawnCancelled(true);
            }
        }
    }
}
