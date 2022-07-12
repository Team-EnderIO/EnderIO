package com.enderio.core.data.model;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.*;

import java.util.function.Consumer;

public class EIOModel {

    public static BlockModelBuilder getExistingParent(BlockModelProvider prov, ResourceLocation model) {
        return new BlockModelBuilder(null, prov.existingFileHelper)
            .parent(prov.getExistingFile(model));
    }

    // region General

    /**
     * @deprecated Use forge {@link net.minecraftforge.client.model.generators.loaders.CompositeModelBuilder} instead.
     */
    @Deprecated(forRemoval = true)
    public static <T extends ModelProvider<B>, B extends ModelBuilder<B>> ModelFile compositeModel(T prov, String name, Consumer<CompositeModelBuilder<B>> compositeBuilder) {
        var builder = prov.getBuilder(name)
            .customLoader(CompositeModelBuilder::begin);
        compositeBuilder.accept(builder);
        return builder.end();
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
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(MissingTextureAtlasSprite.getLocation());
    }

    // endregion
}
