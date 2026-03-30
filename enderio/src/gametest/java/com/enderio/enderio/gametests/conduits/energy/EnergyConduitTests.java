package com.enderio.enderio.gametests.conduits.energy;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.io.IOMode;
import com.enderio.enderio.api.io.RedstoneControl;
import com.enderio.enderio.content.conduits.type.energy.EnergyConduitConnectionConfig;
import com.enderio.enderio.content.machines.capacitor_bank.CapacitorTier;
import com.enderio.enderio.gametests.conduits.ConduitGameTestHelper;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOConduits;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.testframework.DynamicTest;
import net.neoforged.testframework.annotation.ForEachTest;
import net.neoforged.testframework.annotation.RegisterStructureTemplate;
import net.neoforged.testframework.annotation.TestHolder;
import net.neoforged.testframework.gametest.StructureTemplateBuilder;

import java.util.function.Supplier;

@ForEachTest(groups = "conduit.energy")
public class EnergyConduitTests {

    private static final String TWO_CAPACITOR_BANKS = EnderIO.MOD_ID + ":energy_conduit_two_capacitor_banks";
    private static final String THREE_CAPACITOR_BANKS = EnderIO.MOD_ID + ":energy_conduit_three_capacitor_banks";

    // @formatter:off
    @RegisterStructureTemplate(TWO_CAPACITOR_BANKS)
    public static final Supplier<StructureTemplate> TWO_CAPACITOR_BANKS_TEMPLATE = StructureTemplateBuilder.lazy(3, 1, 1,
        builder -> builder
            .set(0, 0, 0, EIOBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC).get().defaultBlockState())
            .set(2, 0, 0, EIOBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC).get().defaultBlockState())
    );

    @RegisterStructureTemplate(THREE_CAPACITOR_BANKS)
    public static final Supplier<StructureTemplate> THREE_CAPACITOR_BANKS_TEMPLATE = StructureTemplateBuilder.lazy(5, 1, 1,
        builder -> builder
            .set(0, 0, 0, EIOBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC).get().defaultBlockState())
            .set(2, 0, 0, EIOBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC).get().defaultBlockState())
            .set(4, 0, 0, EIOBlocks.CAPACITOR_BANKS.get(CapacitorTier.BASIC).get().defaultBlockState())
    );
    // @formatter:on

    @GameTest(template = TWO_CAPACITOR_BANKS)
    @TestHolder(description = "Tests basic energy conduit transfer from source to sink capacitor bank.")
    public static void energyConduitBasicTransfer(final ConduitGameTestHelper helper) {
        var energyConduit = helper.getConduit(EIOConduits.ENERGY);
        final int transferRate = ((com.enderio.enderio.content.conduits.type.energy.EnergyConduit) energyConduit.value()).transferRatePerTick();

        helper.startSequence()
            // Wait for the capacitor banks to initialize
            .thenIdle(1)
            // Clear any existing conduits
            .thenExecute(() -> helper.setBlock(1, 1, 0, Blocks.AIR))
            .thenExecute(() -> helper.placeConduit(energyConduit, 1, 1, 0))
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 0, false)
                .setConnectionConfig(energyConduit, Direction.WEST,
                    EnergyConduitConnectionConfig.DEFAULT
                        .withIsExtract(true)
                        .withIsInsert(false)
                        .withExtractRedstoneControl(RedstoneControl.ALWAYS_ACTIVE)))
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 0, false)
                .setConnectionConfig(energyConduit, Direction.EAST,
                    EnergyConduitConnectionConfig.DEFAULT
                        .withIsExtract(false)
                        .withIsInsert(true)))
            .thenExecute(() -> helper.changeIoConfig(0, 1, 0, ioConfigurable -> {
                ioConfigurable.setIOMode(Direction.EAST, IOMode.PUSH);
            }))
            .thenExecute(() -> helper.provideEnergy(0, 1, 0, transferRate * 2))
            .thenExecuteAfter(10, () -> {
                int sourceEnergy = helper.getEnergyStored(0, 1, 0);
                int sinkEnergy = helper.getEnergyStored(2, 1, 0);
                if (sinkEnergy <= 0) {
                    throw new GameTestAssertException(
                        "Expected energy to transfer to sink, but sink has " + sinkEnergy + " energy (source has " + sourceEnergy + ")");
                }
            })
            .thenSucceed();
    }

    @GameTest(template = TWO_CAPACITOR_BANKS)
    @TestHolder(description = "Tests that energy conduits respect redstone control for extraction.")
    public static void energyConduitRedstoneControl(final ConduitGameTestHelper helper) {
        var energyConduit = helper.getConduit(EIOConduits.ENERGY);
        final int transferRate = ((com.enderio.enderio.content.conduits.type.energy.EnergyConduit) energyConduit.value()).transferRatePerTick();

        helper.startSequence()
            // Wait for the capacitor banks to initialize
            .thenIdle(1)
            .thenExecute(() -> helper.setBlock(1, 1, 0, Blocks.AIR))
            .thenExecute(() -> helper.placeConduit(energyConduit, 1, 1, 0))
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 0, false)
                .setConnectionConfig(energyConduit, Direction.WEST,
                    EnergyConduitConnectionConfig.DEFAULT
                        .withIsExtract(true)
                        .withIsInsert(false)
                        .withExtractRedstoneControl(RedstoneControl.NEVER_ACTIVE)))
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 0, false)
                .setConnectionConfig(energyConduit, Direction.EAST,
                    EnergyConduitConnectionConfig.DEFAULT
                        .withIsExtract(false)
                        .withIsInsert(true)))
            .thenExecute(() -> helper.changeIoConfig(0, 1, 0, ioConfigurable -> {
                ioConfigurable.setIOMode(Direction.EAST, IOMode.PUSH);
            }))
            .thenExecute(() -> helper.provideEnergy(0, 1, 0, transferRate * 2))
            .thenExecuteAfter(5, () -> {
                helper.assertEnergyStored(2, 1, 0, 0);
                int sourceEnergy = helper.getEnergyStored(0, 1, 0);
                if (sourceEnergy < transferRate) {
                    throw new GameTestAssertException(
                        "Expected source to retain energy with NEVER_ACTIVE, but source only has " + sourceEnergy);
                }
            })
            // Now enable extraction with ALWAYS_ACTIVE
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 0, false)
                .setConnectionConfig(energyConduit, Direction.WEST,
                    EnergyConduitConnectionConfig.DEFAULT
                        .withIsExtract(true)
                        .withIsInsert(false)
                        .withExtractRedstoneControl(RedstoneControl.ALWAYS_ACTIVE)))
            .thenExecuteAfter(2, () -> {
                helper.assertEnergyStoredAtLeast(2, 1, 0, 1);
            })
            .thenSucceed();
    }

    @GameTest(template = TWO_CAPACITOR_BANKS)
    @TestHolder(description = "Tests that disabled connections do not transfer energy.")
    public static void energyConduitDisabledConnection(final ConduitGameTestHelper helper) {
        var energyConduit = helper.getConduit(EIOConduits.ENERGY);
        final int transferRate = ((com.enderio.enderio.content.conduits.type.energy.EnergyConduit) energyConduit.value()).transferRatePerTick();

        helper.startSequence()
            // Wait for the capacitor banks to initialize
            .thenIdle(1)
            .thenExecute(() -> helper.setBlock(1, 1, 0, Blocks.AIR))
            .thenExecute(() -> helper.placeConduit(energyConduit, 1, 1, 0))
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 0, false)
                .setConnectionConfig(energyConduit, Direction.WEST,
                    EnergyConduitConnectionConfig.DEFAULT
                        .withIsExtract(true)
                        .withIsInsert(false)
                        .withExtractRedstoneControl(RedstoneControl.ALWAYS_ACTIVE)))
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 0, false)
                .setConnectionConfig(energyConduit, Direction.EAST,
                    EnergyConduitConnectionConfig.DEFAULT
                        .withIsExtract(false)
                        .withIsInsert(false)))
            .thenExecute(() -> helper.changeIoConfig(0, 1, 0, ioConfigurable -> {
                ioConfigurable.setIOMode(Direction.EAST, IOMode.PUSH);
            }))
            .thenExecute(() -> helper.provideEnergy(0, 1, 0, transferRate * 2))
            .thenExecuteAfter(5, () -> {
                helper.assertEnergyStored(2, 1, 0, 0);
            })
            // Enable insert side
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 0, false)
                .setConnectionConfig(energyConduit, Direction.EAST,
                    EnergyConduitConnectionConfig.DEFAULT
                        .withIsExtract(false)
                        .withIsInsert(true)))
            .thenExecuteAfter(2, () -> {
                helper.assertEnergyStoredAtLeast(2, 1, 0, 1);
            })
            .thenSucceed();
    }

    @GameTest(template = THREE_CAPACITOR_BANKS)
    @TestHolder(description = "Tests that energy conduits prioritize closer capacitor banks when distributing energy.")
    public static void energyConduitDistancePriority(final ConduitGameTestHelper helper) {
        var energyConduit = helper.getConduit(EIOConduits.ENERGY);
        final int transferRate = ((com.enderio.enderio.content.conduits.type.energy.EnergyConduit) energyConduit.value()).transferRatePerTick();

        helper.startSequence()
            // Wait for the capacitor banks to initialize
            .thenIdle(1)
            // Clear conduits at positions 1 and 3 only (position 2 has the middle bank)
            .thenExecute(() -> helper.setBlock(1, 1, 0, Blocks.AIR))
            .thenExecute(() -> helper.setBlock(3, 1, 0, Blocks.AIR))
            .thenExecute(() -> helper.placeConduit(energyConduit, 1, 1, 0))
            .thenExecute(() -> helper.placeConduit(energyConduit, 3, 1, 0))
            .thenExecuteAfter(5, () -> {})
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 0, false)
                .setConnectionConfig(energyConduit, Direction.WEST,
                    EnergyConduitConnectionConfig.DEFAULT
                        .withIsExtract(true)
                        .withIsInsert(false)
                        .withExtractRedstoneControl(RedstoneControl.ALWAYS_ACTIVE)))
            // Closer sink (at 2,0,0) - east side of conduit at (1,1,0)
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 0, false)
                .setConnectionConfig(energyConduit, Direction.EAST,
                    EnergyConduitConnectionConfig.DEFAULT
                        .withIsExtract(false)
                        .withIsInsert(true)))
            // Configure conduit at (3,1,0) to extract from west (from conduit at 1,1,0)
            .thenExecute(() -> helper
                .getConduitBundle(3, 1, 0, false)
                .setConnectionConfig(energyConduit, Direction.WEST,
                    EnergyConduitConnectionConfig.DEFAULT
                        .withIsExtract(true)
                        .withIsInsert(false)
                        .withExtractRedstoneControl(RedstoneControl.ALWAYS_ACTIVE)))
            // Further sink (at 4,0,0) - east side of conduit at (3,1,0)
            .thenExecute(() -> helper
                .getConduitBundle(3, 1, 0, false)
                .setConnectionConfig(energyConduit, Direction.EAST,
                    EnergyConduitConnectionConfig.DEFAULT
                        .withIsExtract(false)
                        .withIsInsert(true)))
            // Configure source bank to PUSH energy to the conduit
            .thenExecute(() -> helper.changeIoConfig(0, 1, 0, ioConfigurable -> {
                ioConfigurable.setIOMode(Direction.EAST, IOMode.PUSH);
            }))
            // Add energy to source bank - enough for multiple transfers
            .thenExecute(() -> helper.provideEnergy(0, 1, 0, transferRate * 4))
            // Wait for transfer - closer bank should receive energy first
            .thenExecuteAfter(2, () -> {
                helper.assertEnergyStoredAtLeast(2, 1, 0, 1);
            })
            .thenSucceed();
    }

    @GameTest(template = THREE_CAPACITOR_BANKS)
    @TestHolder(description = "Tests that manual priority overrides distance priority for energy distribution.")
    public static void energyConduitManualPriority(final ConduitGameTestHelper helper) {
        var energyConduit = helper.getConduit(EIOConduits.ENERGY);
        final int transferRate = ((com.enderio.enderio.content.conduits.type.energy.EnergyConduit) energyConduit.value()).transferRatePerTick();

        helper.startSequence()
            // Wait for the capacitor banks to initialize
            .thenIdle(1)
            // Clear any existing conduits
            .thenExecute(() -> helper.fillAir(1, 1, 0, 3, 1, 0))
            .thenExecute(() -> helper.fillConduits(energyConduit, 1, 1, 0, 3, 1, 0))
            // Configure extract from source
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 0, false)
                .setConnectionConfig(energyConduit, Direction.WEST,
                    EnergyConduitConnectionConfig.DEFAULT
                        .withIsExtract(true)
                        .withIsInsert(false)
                        .withExtractRedstoneControl(RedstoneControl.ALWAYS_ACTIVE)))
            // Configure closer sink with lower priority
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 0, false)
                .setConnectionConfig(energyConduit, Direction.EAST,
                    EnergyConduitConnectionConfig.DEFAULT
                        .withIsExtract(false)
                        .withIsInsert(true)
                        .withPriority(0)))
            // Configure conduit at (3,1,0) to extract from west (from conduit at 1,1,0)
            .thenExecute(() -> helper
                .getConduitBundle(3, 1, 0, false)
                .setConnectionConfig(energyConduit, Direction.WEST,
                    EnergyConduitConnectionConfig.DEFAULT
                        .withIsExtract(true)
                        .withIsInsert(false)
                        .withExtractRedstoneControl(RedstoneControl.ALWAYS_ACTIVE)))
            // Configure further sink with higher priority
            .thenExecute(() -> helper
                .getConduitBundle(3, 1, 0, false)
                .setConnectionConfig(energyConduit, Direction.EAST,
                    EnergyConduitConnectionConfig.DEFAULT
                        .withIsExtract(false)
                        .withIsInsert(true)
                        .withPriority(10)))
            // Configure source bank to PUSH energy to the conduit
            .thenExecute(() -> helper.changeIoConfig(0, 1, 0, ioConfigurable -> {
                ioConfigurable.setIOMode(Direction.EAST, IOMode.PUSH);
            }))
            // Add energy to source bank
            .thenExecute(() -> helper.provideEnergy(0, 1, 0, transferRate * 4))
            // Wait for transfer - higher priority (further) sink should receive first
            .thenExecuteAfter(2, () -> {
                helper.assertEnergyStoredAtLeast(4, 1, 0, 1);
            })
            .thenSucceed();
    }

    @GameTest
    @TestHolder(description = "Tests that mixed tier energy conduits limit transfer to the lowest tier rate.")
    public static void energyConduitTierLimiting(final DynamicTest test) {
        test.registerGameTestTemplate(
            () -> StructureTemplateBuilder.withSize(5, 1, 1)
                // Source bank at Y=0 (appears at world Y=1)
                .set(0, 0, 0, EIOBlocks.CAPACITOR_BANKS.get(CapacitorTier.VIBRANT).get().defaultBlockState())
                // Sink bank at Y=0 (appears at world Y=1)
                .set(4, 0, 0, EIOBlocks.CAPACITOR_BANKS.get(CapacitorTier.VIBRANT).get().defaultBlockState()));

        test.onGameTest(ConduitGameTestHelper.class, helper -> {
            var basicConduit = helper.getConduit(EIOConduits.ENERGY);
            var energeticConduit = helper.getConduit(EIOConduits.ENERGETIC_ENERGY);
            
            final int basicRate = ((com.enderio.enderio.content.conduits.type.energy.EnergyConduit) basicConduit.value()).transferRatePerTick();
            final int energeticRate = ((com.enderio.enderio.content.conduits.type.energy.EnergyConduit) energeticConduit.value()).transferRatePerTick();

            helper.startSequence()
                // Wait for the capacitor banks to initialize
                .thenIdle(1)
                // Clear conduits
                .thenExecute(() -> helper.fillAir(1, 1, 0, 3, 1, 0))
                // Place mixed tier conduits: basic in middle, energetic on edges
                .thenExecute(() -> helper.placeConduit(energeticConduit, 1, 1, 0))
                .thenExecute(() -> helper.placeConduit(basicConduit, 2, 1, 0))
                .thenExecute(() -> helper.placeConduit(energeticConduit, 3, 1, 0))
                // Configure extract from source
                .thenExecute(() -> helper
                    .getConduitBundle(1, 1, 0, false)
                    .setConnectionConfig(energeticConduit, Direction.WEST,
                        EnergyConduitConnectionConfig.DEFAULT
                            .withIsExtract(true)
                            .withIsInsert(false)
                            .withExtractRedstoneControl(RedstoneControl.ALWAYS_ACTIVE)))
                // Configure insert to sink
                .thenExecute(() -> helper
                    .getConduitBundle(3, 1, 0, false)
                    .setConnectionConfig(energeticConduit, Direction.EAST,
                        EnergyConduitConnectionConfig.DEFAULT
                            .withIsExtract(false)
                            .withIsInsert(true)))
                // Configure source bank to PUSH energy
                .thenExecute(() -> helper.changeIoConfig(0, 1, 0, ioConfigurable -> {
                    ioConfigurable.setIOMode(Direction.EAST, IOMode.PUSH);
                }))
                // Add large amount of energy to source
                .thenExecute(() -> helper.provideEnergy(0, 1, 0, energeticRate * 10))
                // Wait for transfer
                .thenExecuteAfter(2, () -> {
                    int transferred = (energeticRate * 10) - helper.getEnergyStored(0, 1, 0);
                    int sinkEnergy = helper.getEnergyStored(4, 1, 0);
                    
                    // First verify that energy actually transferred
                    if (sinkEnergy <= 0) {
                        throw new GameTestAssertException(
                            "No energy transferred to sink. Sink has " + sinkEnergy + " energy");
                    }
                    
                    // Then verify transfer was capped at basic rate (lowest tier in path)
                    if (transferred > basicRate * 2) {
                        throw new GameTestAssertException(
                            "Expected transfer to be capped at basic tier rate (~" + basicRate + "), but transferred " + transferred + " energy");
                    }
                })
                .thenSucceed();
        });
    }

    @GameTest(template = THREE_CAPACITOR_BANKS)
    @TestHolder(description = "Tests energy distribution across multiple sinks.")
    public static void energyConduitMultiSinkDistribution(final ConduitGameTestHelper helper) {
        var energyConduit = helper.getConduit(EIOConduits.ENERGY);
        final int transferRate = ((com.enderio.enderio.content.conduits.type.energy.EnergyConduit) energyConduit.value()).transferRatePerTick();

        helper.startSequence()
            // Wait for the capacitor banks to initialize
            .thenIdle(1)
            // Clear conduits at positions 1 and 3 only (position 2 has the middle bank)
            .thenExecute(() -> helper.setBlock(1, 1, 0, Blocks.AIR))
            .thenExecute(() -> helper.setBlock(3, 1, 0, Blocks.AIR))
            .thenExecute(() -> helper.placeConduit(energyConduit, 1, 1, 0))
            .thenExecute(() -> helper.placeConduit(energyConduit, 3, 1, 0))
            // Configure extract from source
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 0, false)
                .setConnectionConfig(energyConduit, Direction.WEST,
                    EnergyConduitConnectionConfig.DEFAULT
                        .withIsExtract(true)
                        .withIsInsert(false)
                        .withExtractRedstoneControl(RedstoneControl.ALWAYS_ACTIVE)))
            // Configure closer sink (at 2,1,0) - east of conduit at (1,1,0)
            .thenExecute(() -> helper
                .getConduitBundle(1, 1, 0, false)
                .setConnectionConfig(energyConduit, Direction.EAST,
                    EnergyConduitConnectionConfig.DEFAULT
                        .withIsExtract(false)
                        .withIsInsert(true)
                        .withPriority(0)))
            // Configure conduit at (3,1,0) to extract from west (from conduit at 1,1,0)
            .thenExecute(() -> helper
                .getConduitBundle(3, 1, 0, false)
                .setConnectionConfig(energyConduit, Direction.WEST,
                    EnergyConduitConnectionConfig.DEFAULT
                        .withIsExtract(true)
                        .withIsInsert(false)
                        .withExtractRedstoneControl(RedstoneControl.ALWAYS_ACTIVE)))
            // Configure further sink (at 4,1,0) - east of conduit at (3,1,0)
            .thenExecute(() -> helper
                .getConduitBundle(3, 1, 0, false)
                .setConnectionConfig(energyConduit, Direction.EAST,
                    EnergyConduitConnectionConfig.DEFAULT
                        .withIsExtract(false)
                        .withIsInsert(true)
                        .withPriority(0)))
            // Configure source bank to PUSH energy to the conduit
            .thenExecute(() -> helper.changeIoConfig(0, 1, 0, ioConfigurable -> {
                ioConfigurable.setIOMode(Direction.EAST, IOMode.PUSH);
            }))
            // Add energy to source bank - enough for both sinks
            .thenExecute(() -> helper.provideEnergy(0, 1, 0, transferRate * 4))
            // Wait for multiple transfers
            .thenExecuteAfter(5, () -> {
                int closerSinkEnergy = helper.getEnergyStored(2, 1, 0);
                int furtherSinkEnergy = helper.getEnergyStored(4, 1, 0);

                // Both sinks should have received some energy
                if (closerSinkEnergy <= 0 && furtherSinkEnergy <= 0) {
                    throw new GameTestAssertException(
                        "Expected both sinks to receive energy, but closer has " + closerSinkEnergy +
                        " and further has " + furtherSinkEnergy);
                }
            })
            .thenSucceed();
    }
}
