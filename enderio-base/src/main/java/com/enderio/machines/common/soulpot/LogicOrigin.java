package com.enderio.machines.common.soulpot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;

public record LogicOrigin(Logic logic, Origin<?> first, Origin<?> second) implements Origin<LogicOrigin> {
    public static final MapCodec<LogicOrigin> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.STRING.xmap(str -> Objects.requireNonNull(Logic.valueOf(str)), Logic::name).fieldOf("logic").forGetter(LogicOrigin::logic),
            Origin.CODEC.fieldOf("first").forGetter(LogicOrigin::first),
            Origin.CODEC.fieldOf("second").forGetter(LogicOrigin::second))
        .apply(inst, LogicOrigin::new));

    public static LogicOrigin or(Origin<?> first, Origin<?> second) {
        return new LogicOrigin(Logic.OR, first, second);
    }

    public static LogicOrigin and(Origin<?> first, Origin<?> second) {
        return new LogicOrigin(Logic.AND, first, second);
    }

    @Override
    public OriginType<LogicOrigin> type() {
        return OriginType.LOGIC;
    }

    @Override
    public boolean matches(OriginContext ctx) {
        return switch (logic()) {
                case AND -> first().matches(ctx) && second().matches(ctx);
                case OR  -> first().matches(ctx) || second().matches(ctx);
        };
    }

    public enum Logic {
        AND,
        OR;
    }
}
