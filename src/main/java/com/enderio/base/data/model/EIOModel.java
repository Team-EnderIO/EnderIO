package com.enderio.base.data.model;

import com.enderio.base.data.model.builder.CompositeModelBuilder;
import com.enderio.base.data.model.builder.DummyCustomModelBuilder;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;

import java.util.function.Consumer;

public class EIOModel {

    // region General

    public static <T extends ModelProvider<B>, B extends ModelBuilder<B>> ModelFile compositeModel(T prov, String name, Consumer<CompositeModelBuilder<B>> compositeBuilder) {
        var builder = prov.getBuilder(name)
            .customLoader(CompositeModelBuilder::begin);
        compositeBuilder.accept(builder);
        return builder.end();
    }

    public static <T extends ModelProvider<B>, B extends ModelBuilder<B>> ModelFile dummyModel(T prov, String name) {
        return prov.getBuilder(name).customLoader(DummyCustomModelBuilder::begin).end();
    }

    // endregion

    // region Item

    public static ItemModelBuilder fakeBlockModel(DataGenContext<Item, ? extends Item> ctx, RegistrateItemModelProvider prov) {
        return prov.withExistingParent(prov.name(ctx), prov.mcLoc("block/cube_all")).texture("all", prov.itemTexture(ctx));
    }

    public static ItemModelBuilder mimicItem(DataGenContext<Item, ? extends Item> ctx, ItemEntry<? extends Item> item, RegistrateItemModelProvider prov) {
        return prov.generated(ctx, prov.itemTexture(item));
    }

    // endregion

    // region Textures

    public static TextureAtlasSprite getMissingTexture() {
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(MissingTextureAtlasSprite.getLocation());
    }

    // endregion
}
