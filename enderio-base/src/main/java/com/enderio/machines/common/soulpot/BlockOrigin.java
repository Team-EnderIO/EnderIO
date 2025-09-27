package com.enderio.machines.common.soulpot;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public record BlockOrigin(Either<Block, TagKey<Block>> blocks, int range, int height) implements Origin<BlockOrigin> {
    public static final MapCodec<BlockOrigin> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(Codec.either(BuiltInRegistries.BLOCK.byNameCodec(), TagKey.codec(Registries.BLOCK)).fieldOf("blocks").forGetter(BlockOrigin::blocks),
            Codec.INT.fieldOf("range").forGetter(BlockOrigin::range), Codec.INT.fieldOf("height").forGetter(BlockOrigin::height))
        .apply(inst, BlockOrigin::new));

    public BlockOrigin(Block block, int range, int height) {
        this(Either.left(block), range, height);
    }
    public BlockOrigin(TagKey<Block> blocks, int range, int height) {
        this(Either.right(blocks), range, height);
    }

    @Override
    public OriginType<BlockOrigin> type() {
        return OriginType.BLOCK;
    }

    @Override
    public boolean matches(OriginContext ctx) {
        return ctx.level().getBlockStatesIfLoaded(AABB.ofSize(Vec3.atLowerCornerOf(ctx.pos()), range*2, height, range*2)).anyMatch(this::matches);
    }

    private boolean matches(BlockState state) {
        if (blocks().left().isPresent()) {
            return state.is(blocks().left().get());
        }
        return state.is(blocks.right().get());
    }
}
