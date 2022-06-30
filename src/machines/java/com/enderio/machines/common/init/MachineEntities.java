package com.enderio.machines.common.init;

import com.enderio.EnderIO;
import com.enderio.machines.common.entity.FallingMachineEntity;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.EntityEntry;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class MachineEntities {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final EntityEntry<FallingMachineEntity> FALLING_MACHINE = REGISTRATE
        .entity("falling_machine", (EntityType.EntityFactory<FallingMachineEntity>) FallingMachineEntity::new, MobCategory.MISC)
        .renderer(() -> FallingBlockRenderer::new)
        .lang("Falling Machine")
        .register();
}
