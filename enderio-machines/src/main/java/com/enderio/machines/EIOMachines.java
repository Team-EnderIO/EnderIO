package com.enderio.machines;

import com.enderio.machines.common.init.MachineBlocks;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.datagen.recipe.MachineRecipeGenerator;
import com.tterrag.registrate.Registrate;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod(EIOMachines.MODID)
public class EIOMachines {
    public static final String MODID = "enderio_machines";

    private static final Lazy<Registrate> REGISTRATE = Lazy.of(() -> Registrate.create(MODID));

    public EIOMachines() {
        MachineBlocks.register();
        MachineBlockEntities.register();
        MachineMenus.register();
        MachineLang.register();
        
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        MachineRecipes.register(modEventBus);
        
        modEventBus.addListener(EventPriority.LOWEST, this::gatherData);
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MODID, path);
    }
    
    public void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            MachineRecipeGenerator.generate(generator);
        }
    }

    public static Registrate registrate() {
        return REGISTRATE.get();
    }
}
