package com.enderio.enderio.datagen.client.models;

import com.enderio.core.data.athena.AthenaBlockStateProvider;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class AthenaCapacitorBankBlockStateProvider extends AthenaBlockStateProvider {
    public AthenaCapacitorBankBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, EnderIO.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (var capacitorBank : EIOBlocks.CAPACITOR_BANKS.values()) {
            String name = capacitorBank.getId().getPath();
            var builder = getAthenaVariantBuilder(capacitorBank.get())
                .renderType("solid")
                .texture("particle", blockTexture(capacitorBank.get()));
            for (String part : new String[] { "center", "empty", "horizontal", "vertical" }) {
                builder.texture(part, EnderIO.rl("block/ctm/" + name + "_" + part));
            }

            var model = models().getExistingFile(EnderIO.rl("block/" + name));
            builder.variants().partialState().setModels(new ConfiguredModel(model));
        }
    }
}
