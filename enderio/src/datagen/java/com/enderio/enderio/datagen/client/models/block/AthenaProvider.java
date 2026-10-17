package com.enderio.enderio.datagen.client.models.block;

import com.enderio.core.data.athena.AthenaDefinitionProvider;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.data.PackOutput;

public class AthenaProvider extends AthenaDefinitionProvider {
    public AthenaProvider(PackOutput output) {
        this(output, EnderIO.MOD_ID);
    }

    public AthenaProvider(PackOutput output, String modId) {
        super(output, modId);
    }

    @Override
    public void registerDefinitions() {
        for (var glassBlocks : EIOBlocks.GLASS_BLOCKS.values()) {
            for (var block : glassBlocks.getAllBlocks().toList()) {
                String texturePath = "block/ctm/" + (block.get().glassIdentifier().explosionResistance() ? "fused_quartz/" : "clear_glass/");
                registerSimple(block.get(), EnderIO.rl(texturePath + "center"), EnderIO.rl(texturePath + "empty"),
                    EnderIO.rl(texturePath + "horizontal"), EnderIO.rl(texturePath + "vertical"),
                    EnderIO.rl(texturePath + "particle"), 0, "cutout");
            }
        }
    }
}
