package com.enderio.machines.client.model;

import com.enderio.machines.EIOMachines;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MachineBakedModel implements IDynamicBakedModel {

    private final List<BakedModel> components;

    private final ItemTransforms transforms = getAlTransforms();

    public MachineBakedModel(List<BakedModel> components) {
        this.components = components;
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull Random rand, @NotNull IModelData extraData) {
        // Get all component quads
        List<BakedQuad> quads = new ArrayList<>();
        for (BakedModel model : components) {
            quads.addAll(model.getQuads(state, side, rand, extraData));
        }

        // TODO: add overlay quads
        return quads;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(EIOMachines.loc("block/machine_side"));
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public ItemTransforms getTransforms() {
        return transforms;
    }

    private ItemTransforms getAlTransforms() {
        ItemTransform tpLeft = this.getTransform(ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
        ItemTransform tpRight = this.getTransform(ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
        ItemTransform fpLeft = this.getTransform(ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND);
        ItemTransform fpRight = this.getTransform(ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
        ItemTransform head = this.getTransform(ItemTransforms.TransformType.HEAD);
        ItemTransform gui = this.getTransform(ItemTransforms.TransformType.GUI);
        ItemTransform ground = this.getTransform(ItemTransforms.TransformType.GROUND);
        ItemTransform fixed = this.getTransform(ItemTransforms.TransformType.FIXED);
        return new ItemTransforms(tpLeft, tpRight, fpLeft, fpRight, head, gui, ground, fixed);
    }

    private ItemTransform getTransform(ItemTransforms.TransformType type) {
        switch (type) {
        case GUI:
            return new ItemTransform(new Vector3f(30, 225, 0), Vector3f.ZERO, new Vector3f(0.625f, 0.625f, 0.625f));
        case GROUND:
            return new ItemTransform(Vector3f.ZERO, Vector3f.ZERO, new Vector3f(0.25f, 0.25f, 0.25f));
        case FIXED:
            return new ItemTransform(Vector3f.ZERO, Vector3f.ZERO, new Vector3f(0.5f, 0.5f, 0.5f));
        case THIRD_PERSON_RIGHT_HAND:
        case THIRD_PERSON_LEFT_HAND:
            return new ItemTransform(new Vector3f(75, 45, 0), new Vector3f(0, 0, 0), new Vector3f(0.375f, 0.375f, 0.375f));
        case FIRST_PERSON_RIGHT_HAND:
            return new ItemTransform(new Vector3f(0, 45, 0), Vector3f.ZERO, new Vector3f(0.4f, 0.4f, 0.4f));
        case FIRST_PERSON_LEFT_HAND:
            return new ItemTransform(new Vector3f(0, 225, 0), Vector3f.ZERO, new Vector3f(0.4f, 0.4f, 0.4f));
        case NONE:
        case HEAD:
        default:
            return ItemTransform.NO_TRANSFORM;
        }
    }
}
