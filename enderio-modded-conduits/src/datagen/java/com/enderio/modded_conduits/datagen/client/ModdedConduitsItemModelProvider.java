package com.enderio.modded_conduits.datagen.client;

import com.enderio.enderio.EnderIO;
import com.enderio.modded_conduits.common.ModdedConduits;
import com.enderio.modded_conduits.common.ModuleModIds;
import com.enderio.modded_conduits.common.modules.mekanism.MekanismModule;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.data.PackOutput;
import net.neoforged.fml.ModList;

public class ModdedConduitsItemModelProvider extends ModelProvider {
    public ModdedConduitsItemModelProvider(PackOutput output) {
        super(output, ModdedConduits.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        if (ModList.get().isLoaded(ModuleModIds.MEKANISM)) {
            itemModels.itemModelOutput.accept(MekanismModule.BASIC_CHEMICAL_FILTER.get(),
                ItemModelUtils.plainModel(EnderIO.id("item/basic_fluid_filter")));
        }
    }
}
