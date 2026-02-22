package com.enderio.endergy.client;

import com.enderio.endergy.common.EnderIOEndergy;
import com.enderio.endergy.common.init.EndergyBlocks;
import com.enderio.endergy.common.init.EndergyItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion;

public class EndergyItemModelProvider extends ItemModelProvider {
    public EndergyItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, EnderIOEndergy.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // Alloys
        basicItem(EndergyItems.CRUDE_STEEL_INGOT.get());
        basicItem(EndergyItems.CRYSTALLINE_ALLOY_INGOT.get());
        basicItem(EndergyItems.MELODIC_ALLOY_INGOT.get());
        basicItem(EndergyItems.STELLAR_ALLOY_INGOT.get());
        basicItem(EndergyItems.VIVID_ALLOY_INGOT.get());

        basicItem(EndergyItems.CRUDE_STEEL_NUGGET.get());
        basicItem(EndergyItems.CRYSTALLINE_ALLOY_NUGGET.get());
        basicItem(EndergyItems.MELODIC_ALLOY_NUGGET.get());
        basicItem(EndergyItems.STELLAR_ALLOY_NUGGET.get());
        basicItem(EndergyItems.VIVID_ALLOY_NUGGET.get());

        // Grinding Balls
//        basicItem(EndergyItems.CONDUCTIVE_ALLOY_BALL.get());
//        basicItem(EndergyItems.ENERGETIC_ALLOY_BALL.get());
//        basicItem(EndergyItems.VIBRANT_ALLOY_BALL.get());
//        basicItem(EndergyItems.REDSTONE_ALLOY_BALL.get());
//        basicItem(EndergyItems.PULSATING_ALLOY_BALL.get());
//        basicItem(EndergyItems.DARK_STEEL_BALL.get());
        
        // Capacitors
        basicItem(EndergyItems.GRAINY_CAPACITOR.get());
        basicItem(EndergyItems.VIVID_CAPACITOR.get());
        basicItem(EndergyItems.CRYSTALLINE_CAPACITOR.get());
        basicItem(EndergyItems.MELODIC_CAPACITOR.get());
        basicItem(EndergyItems.STELLAR_CAPACITOR.get());
        basicItem(EndergyItems.TOTEMIC_CAPACITOR.get());

        // region Blocks

        // Alloys
        simpleBlockItem(EndergyBlocks.CRUDE_STEEL_BLOCK.get());
        simpleBlockItem(EndergyBlocks.CRYSTALLINE_ALLOY_BLOCK.get());
        simpleBlockItem(EndergyBlocks.MELODIC_ALLOY_BLOCK.get());
        simpleBlockItem(EndergyBlocks.STELLAR_ALLOY_BLOCK.get());
        simpleBlockItem(EndergyBlocks.VIVID_ALLOY_BLOCK.get());

        // endregion
    }
    
    public ItemModelBuilder flatBlockItem(ResourceLocation block) {
        return this.getBuilder(block.toString()).parent(new ModelFile.UncheckedModelFile("item/generated"))
            .texture("layer0", ResourceLocation.fromNamespaceAndPath(block.getNamespace(), "block/" + block.getPath()));
    }

    public ItemModelBuilder bucketItem(BucketItem item) {
        return withExistingParent(BuiltInRegistries.ITEM.getKey(item).toString(), ResourceLocation.fromNamespaceAndPath(NeoForgeVersion.MOD_ID, "item/bucket"))
            .customLoader(DynamicFluidContainerModelBuilder::begin)
            .fluid(item.content)
            .end();
    }
}
