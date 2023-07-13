package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.client.renderer.entity.PaintedSandRenderer;
import com.enderio.base.common.entity.PaintedSandEntity;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.EntityEntry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class EIOEntities {

    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final EntityEntry<PaintedSandEntity> PAINTED_SAND = REGISTRATE
        .entity("painted_sand", (EntityType.EntityFactory<PaintedSandEntity>) PaintedSandEntity::new, MobCategory.MISC)
        .renderer(() -> PaintedSandRenderer::new)
        .lang("Painted Sand")
        .register();

    public static void register() {}
}
