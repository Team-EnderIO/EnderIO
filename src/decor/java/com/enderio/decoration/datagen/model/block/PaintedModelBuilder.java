package com.enderio.decoration.datagen.model.block;

import com.enderio.EnderIO;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class PaintedModelBuilder extends BlockModelBuilder {

    private final Block reference;

    @Nullable
    Direction itemTextureRotation;

    public PaintedModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper, Block reference) {
        this(outputLocation, existingFileHelper, reference, null);
    }

    public PaintedModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper, Block reference, Direction itemTextureRotation) {
        super(outputLocation, existingFileHelper);
        this.reference = reference;
        transform();
        this.itemTextureRotation = itemTextureRotation;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        JsonObject root = new JsonObject();
        root.addProperty("loader", reference instanceof SlabBlock ? EnderIO.MODID + ":painted_slab" : EnderIO.MODID + ":painted_model");
        root.addProperty("reference", ForgeRegistries.BLOCKS.getKey(reference).toString());
        if (itemTextureRotation != null && itemTextureRotation != Direction.NORTH) {
            root.addProperty("item_texture_rotation", itemTextureRotation.toString());
        }
        if (json.has("display")) {
            root.add("display", json.get("display"));
        }
        return root;
    }

    private void transform() {
        if (reference instanceof StairBlock) {
            // @formatter:off
            transforms
                .transform(TransformType.GUI).rotation(30, 135, 0).scale(0.625f).end()
                .transform(TransformType.HEAD).rotation(0, -90, 0).end()
                .transform(TransformType.THIRD_PERSON_LEFT_HAND).rotation(75, -135, 0).translation(0, 2.5f, 0).scale(0.375f).end();
            // @formatter:on
        }
        if (reference instanceof FenceGateBlock) {
            // @formatter:off
            transforms
                .transform(TransformType.GUI).rotation(30, 45, 0).translation(0, -1, 0).scale(0.8f).end()
                .transform(TransformType.HEAD).translation(0, -3, -6).end();
            // @formatter:on
        }
        if (reference instanceof FenceBlock) {
            // @formatter:off
            transforms
                .transform(TransformType.GUI).rotation(30, 135, 0).scale(0.625f).end()
                .transform(TransformType.FIXED).rotation(0, 90, 0).scale(0.5f).end();
            // @formatter:on
        }
    }
}
