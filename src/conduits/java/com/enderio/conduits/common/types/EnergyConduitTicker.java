package com.enderio.conduits.common.types;

import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.NodeIdentifier;
import com.enderio.api.conduit.ticker.CapabilityAwareConduitTicker;
import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.common.tag.ConduitTags;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class EnergyConduitTicker extends CapabilityAwareConduitTicker<IEnergyStorage> {

    public EnergyConduitTicker() {
    }

    @Override
    public void tickGraph(ConduitType<?> type, List<NodeIdentifier<?>> loadedNodes, ServerLevel level, Graph<Mergeable.Dummy> graph, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {
        super.tickGraph(type, loadedNodes, level, graph, isRedstoneActive);
        for (NodeIdentifier<?> node : loadedNodes) {
            EnergyExtendedData energyExtendedData = node.getExtendedConduitData().castTo(EnergyExtendedData.class);
            IEnergyStorage energy = energyExtendedData.getSelfCap()
                .resolve()
                .orElseThrow();
            if (energy.getEnergyStored() == 0) {
                energyExtendedData.setCapacity(500);
                continue;
            }
            int previousStored = energy.getEnergyStored();
            for (NodeIdentifier<?> otherNode : loadedNodes) {
               for (Direction dir: Direction.values()) {
                   if (otherNode.getIOState(dir).map(NodeIdentifier.IOState::isInsert).orElse(false)) {
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
                    energyExtendedData.setCapacity(Math.min(1_000_000_000, 2 * energyExtendedData.getCapacity()));
                } else if (previousStored < energyExtendedData.getCapacity() / 2) {
                    energyExtendedData.setCapacity(Math.max(500, energyExtendedData.getCapacity() / 2));
                }
            } else if (energy.getEnergyStored() > 0) {
                energyExtendedData.setCapacity(Math.max(500, energy.getEnergyStored()));
            }
        }
    }
        @Override
    public void tickCapabilityGraph(ConduitType<?> type, List<CapabilityConnection> inserts, List<CapabilityConnection> extracts, ServerLevel level,
                                    Graph<Mergeable.Dummy> graph, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {


        for (CapabilityConnection extract : extracts) {
            IEnergyStorage extractHandler = extract.cap;

            EnergyExtendedData.EnergySidedData sidedExtractData = extract.data.castTo(EnergyExtendedData.class).compute(extract.direction);
            extractEnergy(extractHandler, inserts.stream().map(con -> con.cap).toList(), sidedExtractData.rotatingIndex, i -> sidedExtractData.rotatingIndex = i);
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
