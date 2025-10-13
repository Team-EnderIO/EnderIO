package com.enderio.enderio.content.glass;

public record GlassIdentifier(GlassLighting lighting, GlassCollisionPredicate collisionPredicate, boolean explosionResistance) {
    public GlassIdentifier withoutLight() {
        return new GlassIdentifier(GlassLighting.NONE, collisionPredicate, explosionResistance);
    }

    public GlassIdentifier withCollision(GlassCollisionPredicate collisionPredicate) {
        return new GlassIdentifier(lighting, collisionPredicate, explosionResistance);
    }

    public String glassName() {
        StringBuilder main = new StringBuilder();
        if (explosionResistance()) {
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
