package com.enderio.conduits.client.model;

import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Vec3i;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.QuadTransformers;
import org.joml.Vector3f;

public class BoxTextureQuadTransformer implements IQuadTransformer {

    private final Vec3i toSize;
    private final IQuadTransformer scaling;
    private final IQuadTransformer moveToCenter;

    public BoxTextureQuadTransformer(Vec3i toSize) {
        this.toSize = toSize;
        scaling = QuadTransformers.applying(new Transformation(null, null, new Vector3f(toSize.getX(), toSize.getY(), toSize.getZ()), null));
        moveToCenter = QuadTransformers.applying(new Transformation(new Vector3f(6.5f / 16, 6.5f / 16, 6.5f / 16), null, null, null));
    }

    @Override
    public void processInPlace(BakedQuad quad) {
        scaling.processInPlace(quad);
        moveToCenter.processInPlace(quad);

    }

    private static TextureAtlas blockAtlas() {
        return Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS);
    }

}
