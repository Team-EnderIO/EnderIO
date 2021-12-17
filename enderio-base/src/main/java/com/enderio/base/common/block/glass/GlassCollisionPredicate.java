package com.enderio.base.common.block.glass;

import com.enderio.base.common.lang.EIOLang;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.shapes.EntityCollisionContext;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Glass collision predicate wrapper.
 * Contains the predicate, the description id for the tooltip and the icon for the itemstack.
 */
public enum GlassCollisionPredicate {
    NONE(ctx -> false, null),

    PLAYERS_PASS(ctx -> ctx.getEntity() instanceof Player, EIOLang.GLASS_COLLISION_PLAYERS_PASS),

    PLAYERS_BLOCK(ctx -> !(ctx.getEntity() instanceof Player), EIOLang.GLASS_COLLISION_PLAYERS_BLOCK),

    MOBS_PASS(ctx -> ctx.getEntity() instanceof Mob, EIOLang.GLASS_COLLISION_MOBS_PASS),

    MOBS_BLOCK(ctx -> !(ctx.getEntity() instanceof Mob), EIOLang.GLASS_COLLISION_MOBS_BLOCK),

    ANIMALS_PASS(ctx -> ctx.getEntity() instanceof Animal, EIOLang.GLASS_COLLISION_ANIMALS_PASS),

    ANIMALS_BLOCK(ctx -> !(ctx.getEntity() instanceof Animal), EIOLang.GLASS_COLLISION_ANIMALS_BLOCK);

    private final Predicate<EntityCollisionContext> predicate;
    private final @Nullable Component description;

    GlassCollisionPredicate(Predicate<EntityCollisionContext> predicate, @Nullable Component description) {
        this.predicate = predicate;
        this.description = description;
    }

    public boolean canPass(EntityCollisionContext context) {
        return predicate.test(context);
    }

    public Optional<Component> getDescription() {
        if (description != null)
            return Optional.of(description);
        return Optional.empty();
    }

    // TODO: Get icon for overlay
}
