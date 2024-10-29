package com.enderio.core.data.model;

import com.enderio.regilite.data.DataGenContext;
import com.enderio.regilite.data.RegiliteItemModelProvider;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;

public class ModelHelper {

    public static BlockModelBuilder getExistingAsBuilder(BlockModelProvider prov, ResourceLocation model) {
        return new BlockModelBuilder(null, prov.existingFileHelper).parent(prov.getExistingFile(model));
    }

    public static ItemModelBuilder fakeBlockModel(RegiliteItemModelProvider prov,
            DataGenContext<Item, ? extends Item> ctx) {
        return prov.withExistingParent(ctx.getName(), prov.mcLoc("block/cube_all"))
                .texture("all", prov.itemTexture(ctx.get()));
    }

    public static ItemModelBuilder mimicItem(RegiliteItemModelProvider prov, DataGenContext<Item, ? extends Item> ctx,
            Supplier<? extends Item> mimic) {
        return prov.basicItem(ctx.get(), prov.itemTexture(mimic.get()));
    }

    public static TextureAtlasSprite getMissingTexture() {
        return Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(MissingTextureAtlasSprite.getLocation());
    }
}
