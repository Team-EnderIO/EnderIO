package com.enderio.machines.datagen.model.block;

import com.enderio.machines.common.block.EnhancedMachineBlock;
import com.enderio.machines.common.block.ProgressMachineBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;

public class MachinesBlockState {
    public static void machineBlock(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov, ModelFile unpowered, ModelFile powered) {
        prov
            .getVariantBuilder(ctx.get())
            .forAllStates(state -> ConfiguredModel
                .builder()
                .modelFile(state.getValue(ProgressMachineBlock.POWERED) ? powered : unpowered)
                .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                .build());
    }

    public static void enhancedMachineBlock(DataGenContext<Block, ? extends Block> ctx, RegistrateBlockstateProvider prov, ModelFile bottomUnpowered, ModelFile bottomPowered, ModelFile topUnpowered, ModelFile topPowered) {
        prov
            .getVariantBuilder(ctx.get())
            .forAllStates(state -> {
                boolean on = state.getValue(ProgressMachineBlock.POWERED);
                boolean bottom = state.getValue(EnhancedMachineBlock.HALF) == DoubleBlockHalf.LOWER;
                return ConfiguredModel
                    .builder()
                    .modelFile(bottom ? (on ? bottomPowered : bottomUnpowered) : (on ? topPowered : topUnpowered))
                    .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                    .build();
            });
    }
}
