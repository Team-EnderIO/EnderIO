package com.enderio.enderio.datagen.client.models;

import com.enderio.enderio.EnderIO;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.data.PackOutput;

public class EIOModelProvider extends ModelProvider {

    private final EIOBlockStateProvider block;
    private final EIOItemModelProvider item;

    public EIOModelProvider(PackOutput output) {
        super(output, EnderIO.MOD_ID);
        this.block = new EIOBlockStateProvider(output);
        this.item = new EIOItemModelProvider(output);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        block.registerModels(blockModels, itemModels);
        item.registerModels(blockModels, itemModels);
    }

    @Override
    public String getName() {
        return "EnderIO Model Provider";
    }
}
