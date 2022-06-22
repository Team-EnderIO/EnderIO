package com.enderio.base;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.base.common.init.*;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.config.base.BaseConfig;
import com.enderio.base.config.decor.DecorConfig;
import com.enderio.base.config.machines.MachinesConfig;
import com.enderio.base.data.loot.FireCraftingLootProvider;
import com.enderio.base.data.recipe.standard.StandardRecipes;
import com.enderio.base.data.tags.EIOBlockTagsProvider;
import com.enderio.base.data.tags.EIOFluidTagsProvider;
import com.enderio.base.data.tags.EIOItemTagsProvider;
import com.tterrag.registrate.Registrate;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;

@Mod(EnderIO.MODID)
public class EnderIO {
    public static final @Nonnull String MODID = "enderio";

    private static final Lazy<Registrate> REGISTRATE = Lazy.of(() -> Registrate.create(MODID));

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static ResourceLocation CAPACITOR_KEY_REGISTRY_KEY = new ResourceLocation(MODID, "capacitor_keys");

    @Nullable public static IForgeRegistry<CapacitorKey> CAPACITOR_KEY_REGISTRY;

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

        // Perform classloads for everything so things are registered.
        EIOItems.classload();
        EIOBlocks.classload();
        EIOBlockEntities.classload();
        EIOFluids.classload();
        EIOEnchantments.classload();
        EIOTags.classload();
        EIOMenus.classload();
        EIOPackets.classload();
        EIOLang.classload();
        EIORecipes.Serializer.classload();

        // Run datagen after registrate is finished.
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(EventPriority.LOWEST, this::gatherData);
        modEventBus.addListener(this::createRegistries);

        // Helpers for registering stuff that registrate doesn't handle
        modEventBus.addGenericListener(RecipeSerializer.class, this::onRecipeSerializerRegistry);
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MODID, path);
    }

    public void createRegistries(NewRegistryEvent event) {
        event.create(new RegistryBuilder<CapacitorKey>().setName(CAPACITOR_KEY_REGISTRY_KEY).setType(CapacitorKey.class),
            registry -> CAPACITOR_KEY_REGISTRY = registry);
    }

    public void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        if (event.includeServer()) {
            StandardRecipes.generate(generator);
            ForgeBlockTagsProvider b = new ForgeBlockTagsProvider(generator, event.getExistingFileHelper());
            generator.addProvider(new EIOItemTagsProvider(generator, b, event.getExistingFileHelper()));
            generator.addProvider(new EIOFluidTagsProvider(generator, event.getExistingFileHelper()));
            generator.addProvider(new EIOBlockTagsProvider(generator, event.getExistingFileHelper()));
            generator.addProvider(new FireCraftingLootProvider(generator));
        }
    }

    public void onRecipeSerializerRegistry(RegistryEvent.Register<RecipeSerializer<?>> event) {
        EIORecipes.Types.classload();
    }

    public static Registrate registrate() {
        return REGISTRATE.get();
    }
}
