package com.enderio.base.common.block.glass;

import com.enderio.base.EnderIO;
import com.enderio.base.client.gui.IIcon;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.util.Vector2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.shapes.EntityCollisionContext;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Glass collision predicate wrapper.
 * Contains the predicate, the description id for the tooltip and the icon for the itemstack.
 */
public enum GlassCollisionPredicate implements IIcon {

    NONE(ctx -> false, null),

    PLAYERS_PASS(ctx -> ctx.getEntity() instanceof Player, EIOLang.GLASS_COLLISION_PLAYERS_PASS),

    PLAYERS_BLOCK(ctx -> !(ctx.getEntity() instanceof Player), EIOLang.GLASS_COLLISION_PLAYERS_BLOCK),

    MOBS_PASS(ctx -> ctx.getEntity() instanceof Mob, EIOLang.GLASS_COLLISION_MOBS_PASS),

    MOBS_BLOCK(ctx -> !(ctx.getEntity() instanceof Mob), EIOLang.GLASS_COLLISION_MOBS_BLOCK),

    ANIMALS_PASS(ctx -> ctx.getEntity() instanceof Animal, EIOLang.GLASS_COLLISION_ANIMALS_PASS),

    ANIMALS_BLOCK(ctx -> !(ctx.getEntity() instanceof Animal), EIOLang.GLASS_COLLISION_ANIMALS_BLOCK);

    private final Predicate<EntityCollisionContext> predicate;
    private final @Nullable Component description;

    public static final ResourceLocation TEXTURE = EnderIO.loc("textures/item/overlay/fused_quartz_hitbox_overlay.png");

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

    @Override
    public ResourceLocation getTextureLocation() {
        return TEXTURE;
    }

    @Override
    public Vector2i getIconSize() {
        return new Vector2i(32,32);
    }

    @Override
    public Vector2i getRenderSize() {
        return new Vector2i(16,16);
    }

    @Override
    public Vector2i getTexturePosition() {
        return switch (this) {
            case NONE, PLAYERS_PASS -> new Vector2i(0,0);
            case PLAYERS_BLOCK -> new Vector2i(0,32);
            case MOBS_PASS -> new Vector2i(32,0);
            case MOBS_BLOCK -> new Vector2i(32,32);
            case ANIMALS_PASS -> new Vector2i(64,0);
            case ANIMALS_BLOCK -> new Vector2i(64,32);
        };
    }

    @Override
    public boolean shouldRender() {
        return this != GlassCollisionPredicate.NONE;
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
     * @param token to invert
     * @return the predicate for the token or null if none found. Used for datagen
     */
    @Nullable
    public static GlassCollisionPredicate fromToken(Item token) {
        if (token == EIOItems.PLAYER_TOKEN.get())
            return PLAYERS_PASS;
        if (token == EIOItems.ANIMAL_TOKEN.get())
            return ANIMALS_PASS;
        if (token == EIOItems.MONSTER_TOKEN.get())
            return MOBS_PASS;
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