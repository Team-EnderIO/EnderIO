package com.enderio.base.common.filter.item.general;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.Function;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;

public enum DamageFilterMode implements StringRepresentable {
    IGNORE(0, "ignore", (stack) -> true),
    UP_TO_25(1, "up_to_25", stack -> stack.getDamageValue() <= stack.getMaxDamage() * 0.25f),
    MORE_THAN_25(2, "more_than_25", stack -> stack.getDamageValue() > stack.getMaxDamage() * 0.25f),
    UP_TO_50(3, "up_to_50", stack -> stack.getDamageValue() <= stack.getMaxDamage() * 0.5f),
    MORE_THAN_50(4, "more_than_50", stack -> stack.getDamageValue() > stack.getMaxDamage() * 0.5f),
    UP_TO_75(5, "up_to_75", stack -> stack.getDamageValue() <= stack.getMaxDamage() * 0.75f),
    MORE_THAN_75(6, "more_than_75", stack -> stack.getDamageValue() > stack.getMaxDamage() * 0.75f),
    NOT_DAMAGED(7, "not_damaged", stack -> !stack.isDamaged()), ONLY_DAMAGED(8, "only_damaged", ItemStack::isDamaged),
    IS_DAMAGEABLE(9, "is_damageable", ItemStack::isDamageableItem),
    NOT_DAMAGEABLE(10, "not_damageable", stack -> !stack.isDamageableItem());

    public static final Codec<DamageFilterMode> CODEC = StringRepresentable.fromEnum(DamageFilterMode::values);
    public static final IntFunction<DamageFilterMode> BY_ID = ByIdMap.continuous(key -> key.id, values(),
            ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, DamageFilterMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, v -> v.id);

    private final int id;
    private final String name;

    private final Function<ItemStack, Boolean> predicate;

    DamageFilterMode(int id, String name, Function<ItemStack, Boolean> predicate) {
        this.id = id;
        this.name = name;
        this.predicate = predicate;
    }

    public boolean test(ItemStack stack) {
        return predicate.apply(stack);
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
