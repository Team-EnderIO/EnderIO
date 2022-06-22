package com.enderio.decoration;

import com.enderio.decoration.common.init.DecorBlockEntities;
import com.enderio.decoration.common.init.DecorBlocks;
import com.enderio.decoration.common.init.DecorEntities;
import com.enderio.decoration.common.network.EnderDecorNetwork;
import com.tterrag.registrate.Registrate;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.common.Mod;

@Mod(EIODecor.MODID)
public class EIODecor {
    public static final String MODID = "enderio_decoration";

    private static final Lazy<Registrate> REGISTRATE = Lazy.of(() -> Registrate.create(MODID));

    public EIODecor() {
        // Perform classloads for everything so things are registered.
        DecorBlocks.classload();
        DecorBlockEntities.classload();
        DecorEntities.classload();
        EnderDecorNetwork.register();
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MODID, path);
    }

    public static Registrate registrate() {
        return REGISTRATE.get();
    }
}
