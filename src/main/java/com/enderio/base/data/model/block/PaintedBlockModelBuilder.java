package com.enderio.base.data.model.block;

import com.enderio.EnderIO;
import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PaintedBlockModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {

    @Nullable
    private Block referenceBlock = null;
    private Direction itemTextureRotation = Direction.NORTH;

    public static <T extends ModelBuilder<T>> PaintedBlockModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
        return new PaintedBlockModelBuilder<>(parent, existingFileHelper);
    }

    protected PaintedBlockModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(EnderIO.loc("painted_block"), parent, existingFileHelper);
    }

    public PaintedBlockModelBuilder<T> reference(Block referenceBlock) {
        this.referenceBlock = referenceBlock;
        return this;
    }

    public PaintedBlockModelBuilder<T> itemTextureRotation(Direction direction) {
        this.itemTextureRotation = direction;
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        json = super.toJson(json);
        json.addProperty("reference", Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(
            Objects.requireNonNull(referenceBlock, "Reference block was null!")
        ), "Reference block resource location was null!").toString());
        if (itemTextureRotation != null && itemTextureRotation != Direction.NORTH) {
            json.addProperty("item_texture_rotation", itemTextureRotation.toString());
        }
        return json;
    }
}