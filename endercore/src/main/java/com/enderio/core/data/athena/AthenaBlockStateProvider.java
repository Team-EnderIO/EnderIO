package com.enderio.core.data.athena;

import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.ApiStatus;

/**
 * Extended BlockStateProvider which supports generating blockstates with Athena CTM properties.
 */
// TODO: Experimental as I'm not yet super happy with how this works, but it'll do for now :)
@ApiStatus.Experimental
public abstract class AthenaBlockStateProvider extends BlockStateProvider {
    public AthenaBlockStateProvider(PackOutput output, String modId, ExistingFileHelper existingFileHelper) {
        super(output, modId, existingFileHelper);
    }

    public AthenaVariantBlockStateBuilder getAthenaVariantBuilder(Block block) {
        var existing = registeredBlocks.get(block);
        if (existing instanceof AthenaVariantBlockStateBuilder builder) {
            return builder;
        }

        var builder = new AthenaVariantBlockStateBuilder(getVariantBuilder(block));
        registeredBlocks.put(block, builder);
        return builder;
    }

    @Override
    public VariantBlockStateBuilder getVariantBuilder(Block block) {
        if (registeredBlocks.get(block) instanceof AthenaVariantBlockStateBuilder builder) {
            return builder.variants();
        }
        return super.getVariantBuilder(block);
    }
}
