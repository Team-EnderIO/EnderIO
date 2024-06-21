package com.enderio.conduits.common.conduit.type.energy;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitGraph;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.ConduitType;
import com.enderio.conduits.common.conduit.ConduitGraphObject;
import com.enderio.api.conduit.ticker.CapabilityAwareConduitTicker;
import com.enderio.conduits.common.tag.ConduitTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class EnergyConduitTicker extends CapabilityAwareConduitTicker<EnergyConduitData, IEnergyStorage> {

    public EnergyConduitTicker() {
    }

    @Override
    public void tickGraph(
        ServerLevel level,
        ConduitType<EnergyConduitData> type,
        List<ConduitNode<EnergyConduitData>> loadedNodes,
        ConduitGraph<EnergyConduitData> graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        super.tickGraph(level, type, loadedNodes, graph, coloredRedstoneProvider);

        for (ConduitNode<EnergyConduitData> node : loadedNodes) {
            EnergyConduitData energyConduitData = node.getConduitData();
            IEnergyStorage energy = energyConduitData.getSelfCap()
                .resolve()
                .orElseThrow();
            if (energy.getEnergyStored() == 0) {
                energyConduitData.setCapacity(500);
                continue;
            }

            int previousStored = energy.getEnergyStored();
            for (ConduitNode<?> otherNode : loadedNodes) {
                for (Direction dir: Direction.values()) {
                    if (otherNode.getIOState(dir).map(ConduitGraphObject.IOState::isInsert).orElse(false)) {
                        BlockEntity be = level.getBlockEntity(otherNode.getPos().relative(dir));
                        if (be == null) {
                            continue;
                        }
                        Optional<IEnergyStorage> capability = be.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).resolve();
                        if (capability.isPresent()) {
                            IEnergyStorage insert = capability.get();
                            extractEnergy(energy, List.of(insert), 0, i -> {});
                        }
                    }
                }
            }

            if (energy.getEnergyStored() == 0) {
                if (previousStored == energy.getMaxEnergyStored()) {
                    energyConduitData.setCapacity(Math.min(1_000_000_000, 2 * energyConduitData.getCapacity()));
                } else if (previousStored < energyConduitData.getCapacity() / 2) {
                    energyConduitData.setCapacity(Math.max(500, energyConduitData.getCapacity() / 2));
                }
            } else if (energy.getEnergyStored() > 0) {
                energyConduitData.setCapacity(Math.max(500, energy.getEnergyStored()));
            }
        }
    }

    @Override
    public void tickCapabilityGraph(
        ServerLevel level,
        ConduitType<EnergyConduitData> type,
        List<CapabilityConnection> inserts,
        List<CapabilityConnection> extracts,
        ConduitGraph<EnergyConduitData> graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        for (CapabilityConnection extract : extracts) {
            IEnergyStorage extractHandler = extract.capability;

            EnergyConduitData.EnergySidedData sidedExtractData = extract.data.castTo(EnergyConduitData.class).compute(extract.direction);
            extractEnergy(extractHandler, inserts.stream().map(
                con -> con.capability).toList(),
                sidedExtractData.rotatingIndex, i -> sidedExtractData.rotatingIndex = i);
        }
    }

    private void extractEnergy(IEnergyStorage extractHandler, List<IEnergyStorage> inserts, int startingIndex, Consumer<Integer> rotationIndexSetter) {

        int availableForExtraction = extractHandler.extractEnergy(Integer.MAX_VALUE, true);
        if (availableForExtraction <= 0) {
            return;
        }

        if (inserts.size() <= startingIndex) {
            startingIndex = 0;
            rotationIndexSetter.accept(0);
        }

        for (int j = startingIndex; j < startingIndex + inserts.size(); j++) {
            int insertIndex = j % inserts.size();
            IEnergyStorage insert = inserts.get(insertIndex);

            int inserted = insert.receiveEnergy(availableForExtraction, false);
            extractHandler.extractEnergy(inserted, false);

            if (inserted == availableForExtraction) {
                rotationIndexSetter.accept(startingIndex + (insertIndex) + 1);
                return;
            }

            availableForExtraction -= inserted;
        }
    }

    /**
     * This ensures consistent behaviour for FE/t caps and more.
     * @return how often the conduit should tick. 1 is every tick, 5 is every 5th tick, so 4 times a second
     */
    @Override
    public int getTickRate() {
        return 1;
    }

    @Override
    public Capability<IEnergyStorage> getCapability() {
        return ForgeCapabilities.ENERGY;
    }

    @Override
    public boolean canConnectTo(Level level, BlockPos conduitPos, Direction direction) {
        return super.canConnectTo(level, conduitPos, direction) && !level.getBlockState(conduitPos.relative(direction)).is(ConduitTags.Blocks.ENERGY_CABLE);
    }
}
