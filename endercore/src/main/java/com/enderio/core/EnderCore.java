package com.enderio.core;

import com.enderio.core.common.lang.EnderCoreLang;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.NonNullLazyValue;
import net.minecraft.resources.ResourceLocation;
import com.enderio.core.common.network.EnderNetwork;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@Mod(EnderCore.MODID)
public class EnderCore {
    public static final @Nonnull String MODID = "endercore";
    public static final @Nonnull String DOMAIN = "endercore";

    private static final NonNullLazyValue<Registrate> REGISTRATE = new NonNullLazyValue<>(() -> Registrate.create(DOMAIN));

    public static final Logger LOGGER = LogManager.getLogManager().getLogger(MODID);

    public EnderCore() {
        EnderNetwork.getNetwork();
        EnderCoreLang.register();
    }

    public static Registrate registrate() {
        return REGISTRATE.get();
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(DOMAIN, path);
    }
}
