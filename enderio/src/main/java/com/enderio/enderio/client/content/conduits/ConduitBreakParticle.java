package com.enderio.enderio.client.content.conduits;

import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.content.conduits.bundle.ConduitShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class ConduitBreakParticle extends SingleQuadParticle {
    private final BlockPos pos;
    private final float uo;
    private final float vo;

    public ConduitBreakParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, BlockPos pos, Identifier sprite) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(sprite));

        this.pos = pos;
        this.quadSize /= 2.0F;
        this.uo = this.random.nextFloat() * 3.0F;
        this.vo = this.random.nextFloat() * 3.0F;
    }

    @Override
    protected Layer getLayer() {
        return Layer.TERRAIN;
    }

    @Override
    protected float getU0() {
        return this.sprite.getU((this.uo + 1.0F) / 4.0F);
    }

    @Override
    protected float getU1() {
        return this.sprite.getU(this.uo / 4.0F);
    }

    @Override
    protected float getV0() {
        return this.sprite.getV(this.vo / 4.0F);
    }

    @Override
    protected float getV1() {
        return this.sprite.getV((this.vo + 1.0F) / 4.0F);
    }

    @Override
    public int getLightColor(float partialTick) {
        int i = super.getLightColor(partialTick);
        return i == 0 && this.level.hasChunkAt(this.pos) ? LevelRenderer.getLightColor(this.level, this.pos) : i;
    }

    public static void addDestroyEffects(BlockPos pos, BlockState state, Conduit<?, ?> conduit) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        ParticleEngine engine = Minecraft.getInstance().particleEngine;
//        List<AABB> boxes = ConduitShape.CONNECTION.toAabbs();
        VoxelShape shape = state.getShape(level, pos);

        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double sizeX = Math.min(1D, maxX - minX);
            double sizeY = Math.min(1D, maxY - minY);
            double sizeZ = Math.min(1D, maxZ - minZ);
            int xCount = Math.max(2, Mth.ceil(sizeX / 0.25D));
            int yCount = Math.max(2, Mth.ceil(sizeY / 0.25D));
            int zCount = Math.max(2, Mth.ceil(sizeZ / 0.25D));

            for (int iX = 0; iX < xCount; ++iX) {
                for (int iY = 0; iY < yCount; ++iY) {
                    for (int iZ = 0; iZ < zCount; ++iZ) {
                        double offX = ((double) iX + 0.5D) / (double) xCount;
                        double offY = ((double) iY + 0.5D) / (double) yCount;
                        double offZ = ((double) iZ + 0.5D) / (double) zCount;
                        double x = pos.getX() + offX * sizeX + minX;
                        double y = pos.getY() + offY * sizeY + minY;
                        double z = pos.getZ() + offZ * sizeZ + minZ;
                        engine.add(new ConduitBreakParticle(level, x, y, z, offX - 0.5D, offY - 0.5D, offZ - 0.5D, pos, conduit.texture()));
                    }
                }
            }
        });
    }

    public static void addCrackEffects(BlockPos pos, BlockState state, Conduit<?, ?> conduit, Direction side) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        ParticleEngine engine = Minecraft.getInstance().particleEngine;
        List<AABB> boxes = ConduitShape.CONNECTION.toAabbs();
        double countMult = 1D / boxes.size();

        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        float f = 0.1F;
        AABB aabb = state.getShape(level, pos).bounds();
        double x = (double) i + level.getRandom().nextDouble() * (aabb.maxX - aabb.minX - 0.2F) + 0.1F + aabb.minX;
        double y = (double) j + level.getRandom().nextDouble() * (aabb.maxY - aabb.minY - 0.2F) + 0.1F + aabb.minY;
        double z = (double) k + level.getRandom().nextDouble() * (aabb.maxZ - aabb.minZ - 0.2F) + 0.1F + aabb.minZ;
        if (side == Direction.DOWN) {
            y = (double) j + aabb.minY - 0.1F;
        }

        if (side == Direction.UP) {
            y = (double) j + aabb.maxY + 0.1F;
        }

        if (side == Direction.NORTH) {
            z = (double) k + aabb.minZ - 0.1F;
        }

        if (side == Direction.SOUTH) {
            z = (double) k + aabb.maxZ + 0.1F;
        }

        if (side == Direction.WEST) {
            x = (double) i + aabb.minX - 0.1F;
        }

        if (side == Direction.EAST) {
            x = (double) i + aabb.maxX + 0.1F;
        }

        engine.add(new ConduitBreakParticle(level, x, y, z, 0.0D, 0.0D, 0.0D, pos, conduit.texture()).setPower(0.2F)
                .scale(0.6F));
    }
}
