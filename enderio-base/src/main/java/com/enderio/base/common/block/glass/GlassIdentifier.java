package com.enderio.base.common.block.glass;

public record GlassIdentifier(GlassLighting lighting, GlassCollisionPredicate collisionPredicate, boolean explosion_resistance) {
    public GlassIdentifier withoutLight() {
        return new GlassIdentifier(GlassLighting.NONE, collisionPredicate, explosion_resistance);
    }

    public GlassIdentifier withCollision(GlassCollisionPredicate collisionPredicate) {
        return new GlassIdentifier(lighting, collisionPredicate, explosion_resistance);
    }

    public String glassName() {
        StringBuilder main = new StringBuilder();
        if (explosion_resistance()) {
            main.append("fused_quartz");
        } else {
            main.append("clear_glass");
        }
        StringBuilder modifier = new StringBuilder();
        modifier.append(lighting().shortName());
        modifier.append(collisionPredicate().shortName());
        if (!modifier.isEmpty()) {
            main.append("_");
            main.append(modifier);
        }
        return main.toString();
    }
}
