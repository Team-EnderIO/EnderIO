package com.enderio.enderio.api.soul;

import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.transfer.resource.Resource;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.function.Predicate;

@ApiStatus.Experimental
@ApiStatus.AvailableSince("10.0.0")
public final class SoulResource implements Resource {

    public static final SoulResource EMPTY = new SoulResource(Soul.EMPTY);

    private final Soul innerSoul;

    private SoulResource(Soul innerSoul) {
        this.innerSoul = innerSoul;
    }

    @Nullable
    public EntityType<?> getEntityType() {
        return innerSoul.entityType();
    }

    @Override
    public boolean isEmpty() {
        return innerSoul.isEmpty();
    }

    public boolean matches(Soul soul) {
        return Soul.isSameEntitySameTag(innerSoul, soul);
    }

    public boolean is(EntityType<?> entityType) {
        return innerSoul.entityType() == entityType;
    }

    public boolean test(Predicate<Soul> predicate) {
        return predicate.test(innerSoul);
    }

    public Soul toSoul() {
        return innerSoul.copy();
    }

    @Override
    public int hashCode() {
        return Soul.hashEntityAndTag(innerSoul);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SoulResource other = (SoulResource) obj;
        return Soul.isSameEntitySameTag(innerSoul, other.innerSoul);
    }

    @Override
    public String toString() {
        return innerSoul.isEmpty() ? "empty" : innerSoul.entityType().toString();
    }
}
