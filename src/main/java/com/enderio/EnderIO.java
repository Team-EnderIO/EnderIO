package com.enderio;

import com.enderio.base.common.init.*;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.config.base.BaseConfig;
import com.enderio.base.config.decor.DecorConfig;
import com.enderio.base.config.machines.MachinesConfig;
import com.enderio.base.data.loot.FireCraftingLootProvider;
import com.enderio.base.data.recipe.*;
import com.enderio.base.data.tags.EIOBlockTagsProvider;
import com.enderio.base.data.tags.EIOFluidTagsProvider;
import com.enderio.base.data.tags.EIOItemTagsProvider;
import com.enderio.core.common.network.CoreNetwork;
import com.tterrag.registrate.Registrate;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;

@Mod(EnderIO.MODID)
public class EnderIO {
    public static final String MODID = "enderio";

    private static final Lazy<Registrate> REGISTRATE = Lazy.of(() -> Registrate.create(MODID));

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MODID, path);
    }

    public static Registrate registrate() {
        return REGISTRATE.get();
    }

    public EnderIO() {
        // Create configs subdirectory
        try {
            Files.createDirectories(FMLPaths.CONFIGDIR.get().resolve(MODID));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Register config files
        var ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.COMMON, BaseConfig.COMMON_SPEC, "enderio/base-common.toml");
        ctx.registerConfig(ModConfig.Type.CLIENT, BaseConfig.CLIENT_SPEC, "enderio/base-client.toml");
        ctx.registerConfig(ModConfig.Type.COMMON, DecorConfig.COMMON_SPEC, "enderio/decor-common.toml");
        ctx.registerConfig(ModConfig.Type.CLIENT, DecorConfig.CLIENT_SPEC, "enderio/decor-client.toml");
        ctx.registerConfig(ModConfig.Type.COMMON, MachinesConfig.COMMON_SPEC, "enderio/machines-common.toml");
        ctx.registerConfig(ModConfig.Type.CLIENT, MachinesConfig.CLIENT_SPEC, "enderio/machines-client.toml");

        // Setup core networking now
        CoreNetwork.networkInit();

        // Perform initialization and registration for everything so things are registered.
        EIORegistries.init();
        EIOItems.register();
        EIOBlocks.register();
        EIOBlockEntities.register();
        EIOFluids.register();
        EIOEnchantments.register();
        EIOTags.register();
        EIOMenus.register();
        EIOPackets.register();
        EIOLang.register();
        EIORecipes.register();
        EIOLootModifiers.register();

        // Run datagen after registrate is finished.
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(EventPriority.LOWEST, this::onGatherData);
    }

    public void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        generator.addProvider(event.includeServer(), new MaterialRecipes(generator));
        generator.addProvider(event.includeServer(), new BlockRecipes(generator));
        generator.addProvider(event.includeServer(), new ItemRecipes(generator));
        generator.addProvider(event.includeServer(), new GrindingBallRecipeProvider(generator));
        generator.addProvider(event.includeServer(), new GlassRecipes(generator));
        generator.addProvider(event.includeServer(), new FireCraftingRecipes(generator));

        ForgeBlockTagsProvider b = new ForgeBlockTagsProvider(generator, event.getExistingFileHelper());
        generator.addProvider(event.includeServer(), new EIOItemTagsProvider(generator, b, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new EIOFluidTagsProvider(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new EIOBlockTagsProvider(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new FireCraftingLootProvider(generator));
    }
}
