package com.enderio.enderio.content.glass;

import com.enderio.core.common.lang.EnumLangMap;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;
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
public enum GlassCollisionPredicate implements StringRepresentable {

    NONE("none", ctx -> false),
    PLAYERS_PASS("players_pass", ctx -> ctx.getEntity() instanceof Player),
    PLAYERS_BLOCK("players_block", ctx -> !(ctx.getEntity() instanceof Player)),
    MOBS_PASS("mobs_pass", ctx -> ctx.getEntity() instanceof Mob),
    MOBS_BLOCK("mobs_block", ctx -> !(ctx.getEntity() instanceof Mob)),
    ANIMALS_PASS("animals_pass", ctx -> ctx.getEntity() instanceof Animal),
    ANIMALS_BLOCK("animals_block", ctx -> !(ctx.getEntity() instanceof Animal));

    private static final EnumLangMap<GlassCollisionPredicate> LANG_MAP = new EnumLangMap<>(GlassCollisionPredicate.class, EnderIO.MOD_ID,
        "glass_collision", GlassCollisionPredicate.NONE);

    private final String name;
    private final Predicate<EntityCollisionContext> predicate;

    GlassCollisionPredicate(String name, Predicate<EntityCollisionContext> predicate) {
        this.name = name;
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

    @Nullable
    public MutableComponent getComponent() {
        return LANG_MAP.get(this);
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
