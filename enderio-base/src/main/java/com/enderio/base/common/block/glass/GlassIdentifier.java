package com.enderio.base.common.block.glass;

public record GlassIdentifier(GlassLighting lighting, GlassCollisionPredicate collisionPredicate, boolean explosion_resistance) {
    public GlassIdentifier withoutLight() {
        return new GlassIdentifier(GlassLighting.NONE, collisionPredicate, explosion_resistance);
    }
}
