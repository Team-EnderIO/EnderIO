package com.enderio.base.common.block.glass;

import com.enderio.base.common.init.EIOItems;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Glass collision predicate wrapper.
 * Contains the predicate, the description id for the tooltip and the icon for the itemstack.
 */
public enum GlassCollisionPredicate {

    NONE(ctx -> false),
    PLAYERS_PASS(ctx -> ctx.getEntity() instanceof Player),
    PLAYERS_BLOCK(ctx -> !(ctx.getEntity() instanceof Player)),
    MOBS_PASS(ctx -> ctx.getEntity() instanceof Mob),
    MOBS_BLOCK(ctx -> !(ctx.getEntity() instanceof Mob)),
    ANIMALS_PASS(ctx -> ctx.getEntity() instanceof Animal),
    ANIMALS_BLOCK(ctx -> !(ctx.getEntity() instanceof Animal));

    private final Predicate<EntityCollisionContext> predicate;

    GlassCollisionPredicate(Predicate<EntityCollisionContext> predicate) {
        this.predicate = predicate;
    }

    public boolean canPass(EntityCollisionContext context) {
        return predicate.test(context);
    }

    public String shortName() {
        return switch (this) {
            case NONE -> "";
            case PLAYERS_PASS -> "p";
            case PLAYERS_BLOCK -> "np";
            case MOBS_PASS -> "m";
            case MOBS_BLOCK -> "nm";
            case ANIMALS_PASS -> "a";
            case ANIMALS_BLOCK -> "na";
        };
    }

    /**
     * @return the predicate for the token or null if none found. Used for datagen
     */
    @Nullable
    public static GlassCollisionPredicate fromToken(Item token) {
        if (token == EIOItems.PLAYER_TOKEN.get()) {
            return PLAYERS_PASS;
        }

        if (token == EIOItems.ANIMAL_TOKEN.get()) {
            return ANIMALS_PASS;
        }

        if (token == EIOItems.MONSTER_TOKEN.get()) {
            return MOBS_PASS;
        }

        return null;
    }

    /**
     * @param predicate to invert
     * @return the inverted predicate. Used for datagen
     */
    public static GlassCollisionPredicate invert(GlassCollisionPredicate predicate) {
        return switch (predicate) {
            case NONE -> NONE;
            case MOBS_PASS -> MOBS_BLOCK;
            case MOBS_BLOCK -> MOBS_PASS;
            case ANIMALS_BLOCK -> ANIMALS_PASS;
            case ANIMALS_PASS -> ANIMALS_BLOCK;
            case PLAYERS_BLOCK -> PLAYERS_PASS;
            case PLAYERS_PASS -> PLAYERS_BLOCK;
        };
    }
}
