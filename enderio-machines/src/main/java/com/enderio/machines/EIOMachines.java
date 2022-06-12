package com.enderio.machines;

import com.enderio.machines.common.init.*;
import com.enderio.machines.common.lang.MachineLang;
import com.enderio.machines.data.recipes.AlloyRecipeProvider;
import com.enderio.machines.data.recipes.EnchanterRecipeProvider;
import com.enderio.machines.data.recipes.SagMillRecipeProvider;
import com.enderio.machines.data.recipes.SlicingRecipeProvider;
import com.tterrag.registrate.Registrate;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(EIOMachines.MODID)
public class EIOMachines {
    public static final String MODID = "enderio_machines";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    private static final Lazy<Registrate> REGISTRATE = Lazy.of(() -> Registrate.create(MODID));

    public EIOMachines() {
        // Perform classloads for everything so things are registered.
        MachineBlocks.classload();
        MachineBlockEntities.classload();
        MachineMenus.classload();
        MachineLang.classload();
        MachineRecipes.Serializer.classload();
        MachineCapacitorKeys.classload();

        // Run datagen after registrate
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(EventPriority.LOWEST, this::gatherData);

        // Helpers for registering stuff that registrate doesn't handle
        modEventBus.addGenericListener(RecipeSerializer.class, this::onRecipeSerializerRegistry);
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MODID, path);
    }
    
    public void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            generator.addProvider(new AlloyRecipeProvider(generator));
            generator.addProvider(new EnchanterRecipeProvider(generator));
            generator.addProvider(new SagMillRecipeProvider(generator));
            generator.addProvider(new SlicingRecipeProvider(generator));
        }
    }

    public void onRecipeSerializerRegistry(RegistryEvent.Register<RecipeSerializer<?>> event) {
        MachineRecipes.Types.classload();
    }

    public static Registrate registrate() {
        return REGISTRATE.get();
    }
}
