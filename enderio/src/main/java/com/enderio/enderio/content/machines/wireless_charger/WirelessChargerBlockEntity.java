package com.enderio.enderio.content.machines.wireless_charger;

import com.enderio.core.annotations.UseOnly;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.QuadraticScalable;
import com.enderio.enderio.api.io.IOMode;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.attachment.ActionRange;
import com.enderio.enderio.foundation.attachment.RangedActor;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.datamap.RangeExtender;
import com.enderio.enderio.foundation.energy.PoweredMachineEnergyStorage;
import com.enderio.enderio.foundation.inventory.MachineInventoryLayout;
import com.enderio.enderio.foundation.io.IOConfig;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class WirelessChargerBlockEntity extends PoweredMachineBlockEntity implements RangedActor {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.WIRELESS_CHARGER_CAPACITY);

    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.WIRELESS_CHARGER_USAGE);

    private final ModConfigSpec.ConfigValue<Integer> energyUpkeep;

    private ActionRange actionRange;
    private int maxRange;

    private @Nullable AABB bounds;

    public WirelessChargerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.WIRELESS_CHARGER.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED,
                EnergyIOMode.Input, CAPACITY, USAGE);
        actionRange = new ActionRange(MachinesConfig.COMMON.WIRELESS_CHARGER_RANGE.get(), false);
        energyUpkeep = MachinesConfig.COMMON.ENERGY.WIRELESS_CHARGER_UPKEEP;
    }

    @Override
    public IOConfig getDefaultIOConfig() {
        return IOConfig.of(IOMode.PULL);
    }

    public void chargeItem() {
        if (level == null || bounds == null) {
            return;
        }
        PoweredMachineEnergyStorage energyStorage = getEnergyStorage();
        int toDistribute = Math.min(energyStorage.getAmountAsInt(), getMaxEnergyUse());

        if (toDistribute <= 0) {
            return;
        }

        // TODO: It would be ideal if we split the amount of energy we have to distribute evenly across nearby players.
        List<Player> players = level.getEntitiesOfClass(Player.class, bounds);
        for (Player player : players) {
            Inventory inventory = player.getInventory();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                if (stack.isEmpty()) {
                    continue;
                }
                @Nullable EnergyHandler cap = stack.getCapability(Capabilities.Energy.ITEM, ItemAccess.forStack(stack));
                if (cap != null) {
                    try (Transaction transaction = Transaction.openRoot()) {
                        int maxConsumed;
                        try (Transaction simulatedConsume = Transaction.open(transaction)) {
                            maxConsumed = energyStorage.consume(toDistribute, simulatedConsume);
                        }

                        int inserted = cap.insert(maxConsumed, transaction);
                        if (inserted != energyStorage.consume(inserted, transaction)) {
                            continue;
                        }

                        toDistribute -= inserted;
                        transaction.commit();

                        if (toDistribute <= 0) {
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onNeighbourBlockChanged(Block neighborBlock, BlockPos neighborPos) {
        super.onNeighbourBlockChanged(neighborBlock, neighborPos);
        if (level != null && !level.isClientSide() && getBlockPos().above().equals(neighborPos)) {
            calculateMaxRange();
            if (getRangeExtension() > 0 || actionRange.range() > maxRange) {
                // Antenna placed or removed from the top so update range
                setActionRange(new ActionRange(maxRange, actionRange.isVisible()));
            }
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (isActive()) {
            getEnergyStorage().consume(energyUpkeep.get(), null);
            chargeItem();
        }
    }

    @Override
    public void clientTick() {
        if (level instanceof ClientLevel clientLevel) {
            getActionRange().addClientParticle(clientLevel, getBlockPos(),
                    MachinesConfig.CLIENT.BLOCKS.WIRELESS_CHARGER_RANGE_COLOR.get());
        }
        super.clientTick();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        calculateMaxRange();
        if (level != null && !level.isClientSide() && actionRange.range() > this.maxRange) {
            setActionRange(new ActionRange(maxRange, actionRange.isVisible()));
        }
        updateBounds();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store(MachineNBTKeys.ACTION_RANGE, ActionRange.CODEC, actionRange);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        actionRange = input.read(MachineNBTKeys.ACTION_RANGE, ActionRange.CODEC)
            .orElse(new ActionRange(maxRange, false));
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        var actionRange = components.get(EIODataComponents.ACTION_RANGE);
        if (actionRange != null) {
            this.actionRange = actionRange;
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(EIODataComponents.ACTION_RANGE, actionRange);
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        super.removeComponentsFromTag(output);
        output.discard(MachineNBTKeys.ACTION_RANGE);
    }

    @Override
    public boolean isActive() {
        return hasEnergy() && canAct();
    }

    private void calculateMaxRange() {
        this.maxRange = MachinesConfig.COMMON.WIRELESS_CHARGER_RANGE.get() + getRangeExtension();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    private void updateBounds() {
        bounds = new AABB(getBlockPos()).inflate(getRange());
    }

    @Override
    public int getMaxRange() {
        return maxRange;
    }

    private int getRangeExtension() {
        int rangeExtension = 0;
        if (level != null) {
            BlockState bs = level.getBlockState(getBlockPos().above());
            if (bs.is(EIOTags.Blocks.RANGE_EXTENDER)) {
                Map<TagKey<Block>, Integer> map = bs.typeHolder().getData(RangeExtender.DATA_MAP);
                if (map != null) {
                    rangeExtension = map.getOrDefault(EIOTags.Blocks.RANGE_EXTENDER, 0);
                }
            }
        }
        return rangeExtension;
    }

    @Override
    public ActionRange getActionRange() {
        return actionRange;
    }

    @Override
    @UseOnly(LogicalSide.SERVER)
    public void setActionRange(ActionRange actionRange) {
        this.actionRange = actionRange.clamp(0, getMaxRange());
        updateBounds();
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder().capacitor().build();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new WirelessChargerMenu(containerId, playerInventory, this);
    }
}
