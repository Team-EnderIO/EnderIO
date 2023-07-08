package com.enderio.core.data.model;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateItemModelProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ItemModelBuilder;

public class EIOModel {

    public static BlockModelBuilder getExistingParent(BlockModelProvider prov, ResourceLocation model) {
        return new BlockModelBuilder(null, prov.existingFileHelper)
            .parent(prov.getExistingFile(model));
    }

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
