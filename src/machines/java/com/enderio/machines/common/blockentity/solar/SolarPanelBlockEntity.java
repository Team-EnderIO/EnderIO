package com.enderio.machines.common.blockentity.solar;

import com.enderio.api.capacitor.FixedScalable;
import com.enderio.api.io.IIOConfig;
import com.enderio.api.io.IOMode;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.blockentity.multienergy.MultiEnergyNode;
import com.enderio.machines.common.blockentity.multienergy.MultiEnergyStorageWrapper;
import com.enderio.machines.common.io.SidedFixedIOConfig;
import com.enderio.machines.common.io.energy.MachineEnergyStorage;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SolarPanelBlockEntity extends PoweredMachineBlockEntity {

    private final ISolarPanelTier tier;

    private final MultiEnergyNode node;

    public SolarPanelBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState, ISolarPanelTier tier) {
        super(EnergyIOMode.Output, new FixedScalable(tier::getStorageCapacity), new FixedScalable(tier::getStorageCapacity), type, worldPosition, blockState);
        this.tier = tier;
        this.node = new MultiEnergyNode(() -> energyStorage, () -> (MultiEnergyStorageWrapper) getExposedEnergyStorage(), worldPosition);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return null;
    }

    @Override
    public @Nullable MachineEnergyStorage createExposedEnergyStorage() {
        return new MultiEnergyStorageWrapper(createIOConfig(), EnergyIOMode.Output, () -> tier);
    }

    @Override
    public void serverTick() {
        if (isGenerating()) {
            getEnergyStorage().addEnergy(getGenerationRate());
        }

        super.serverTick();
    }

    @Override
    protected boolean isActive() {
        return canAct() && hasEnergy() && isGenerating();
    }

    public boolean isGenerating() {
        if (level == null || !this.level.canSeeSky(getBlockPos().above()))
            return false;

        return getGenerationRate() > 0;
    }

    public int getGenerationRate() {
        int minuteInTicks = 20 * 60;
        if (level == null)
            return 0;
        int dayTime = (int) (level.getDayTime() % (minuteInTicks * 20));
        if (dayTime > minuteInTicks * 9)
            return 0;
        if (dayTime < minuteInTicks)
            return 0;
        float progress = dayTime > minuteInTicks * 5 ? 10 * minuteInTicks - dayTime : dayTime;
        progress = (progress - minuteInTicks) / (4 * minuteInTicks);
        double easing = easing(progress);
        if (level.isRaining() && !level.isThundering())
            easing -= 0.3f;
        if (level.isThundering())
            easing -= 0.7f;
        if (easing < 0)
            return 0;
        return (int) (easing * tier.getProductionRate());
    }

    @Override
    protected boolean shouldPushEnergyTo(Direction direction) {
        if (node.getGraph() == null)
            return true;
        for (GraphObject<Mergeable.Dummy> neighbour : node.getGraph().getNeighbours(node)) {
            if (neighbour instanceof MultiEnergyNode node) {
                if (node.pos.equals(worldPosition.relative(direction))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void setRemoved() {
        if (node.getGraph() != null)
            node.getGraph().remove(node);
        super.setRemoved();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (node.getGraph() == null)
            Graph.integrate(node, List.of());
        for (Direction direction: new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}) {
            if (level.getBlockEntity(worldPosition.relative(direction)) instanceof SolarPanelBlockEntity panel && panel.tier == tier) {
                Graph.connect(node, panel.node);
            }
        }
    }

    //Reference: EaseInOutQuad Function
    private static double easing(float progress) {
        if (progress > 0.5f)
            return 1 - Math.pow(-2*progress + 2, 2)/2;
        return 2 * progress * progress;
    }

    @Override
    protected IIOConfig createIOConfig() {
        return new SidedFixedIOConfig(dir -> dir == Direction.UP ? IOMode.NONE : IOMode.PUSH);
    }

    @Override
    public boolean canOpenMenu() {
        return false;
    }
}