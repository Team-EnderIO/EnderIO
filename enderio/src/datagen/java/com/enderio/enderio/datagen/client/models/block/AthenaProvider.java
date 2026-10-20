package com.enderio.enderio.datagen.client.models.block;

import com.enderio.core.data.athena.AthenaDefinitionProvider;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

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
                registerSimple(block.get(), EnderIO.id(texturePath + "center"), EnderIO.id(texturePath + "empty"),
                    EnderIO.id(texturePath + "horizontal"), EnderIO.id(texturePath + "vertical"),
                    EnderIO.id(texturePath + "particle"), 0, "cutout");
            }
        }

        for (var capBank : EIOBlocks.CAPACITOR_BANKS.values()) {
            String texturePath = "block/ctm/" + Identifier.parse(capBank.getRegisteredName()).getPath() + "_";
            registerSimple(capBank.get(), EnderIO.id(texturePath + "center"), EnderIO.id(texturePath + "empty"),
                EnderIO.id(texturePath + "horizontal"), EnderIO.id(texturePath + "vertical"),
                EnderIO.id(texturePath + "particle"), 0, "solid");
        }
    }
}
