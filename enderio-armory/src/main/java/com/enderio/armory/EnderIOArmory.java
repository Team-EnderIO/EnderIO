package com.enderio.armory;

import com.enderio.armory.common.config.ArmoryConfig;
import com.enderio.armory.common.config.ArmoryConfigLang;
import com.enderio.armory.common.init.ArmoryDataComponents;
import com.enderio.armory.common.init.ArmoryItems;
import com.enderio.armory.common.init.ArmoryLootModifiers;
import com.enderio.armory.common.init.ArmoryRecipes;
import com.enderio.armory.common.item.darksteel.AnvilRecipeHandler;
import com.enderio.armory.common.item.darksteel.DarkSteelSwordItem;
import com.enderio.armory.common.item.darksteel.upgrades.StepAssistUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.flight.GliderIntegration;
import com.enderio.armory.common.item.darksteel.upgrades.jump.JumpUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.nightvision.NightVisionHandler;
import com.enderio.armory.common.item.darksteel.upgrades.solar.SolarUpgradeHandler;
import com.enderio.armory.common.item.darksteel.upgrades.speed.SpeedUpgrade;
import com.enderio.armory.common.lang.ArmoryLang;
import com.enderio.armory.common.tag.ArmoryTags;
import com.enderio.armory.data.loot.ArmoryLootModifiersProvider;
import com.enderio.armory.data.recipe.ItemRecipeProvider;
import com.enderio.armory.data.tags.ArmoryBlockTagsProvider;
import com.enderio.base.api.EnderIO;
import com.enderio.base.api.integration.IntegrationManager;
import com.enderio.base.data.EIODataProvider;
import com.enderio.regilite.Regilite;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@EventBusSubscriber(modid = EnderIOArmory.MODULE_MOD_ID)
@Mod(EnderIOArmory.MODULE_MOD_ID)
public class EnderIOArmory {

    public static final String MODULE_MOD_ID = "enderio_armory";

    public static final Regilite REGILITE = new Regilite(EnderIO.NAMESPACE);

    public EnderIOArmory(IEventBus modEventBus, ModContainer modContainer) {
        // Register config files
        modContainer.registerConfig(ModConfig.Type.COMMON, ArmoryConfig.COMMON_SPEC, "enderio/armory-common.toml");
        modContainer.registerConfig(ModConfig.Type.CLIENT, ArmoryConfig.CLIENT_SPEC, "enderio/armory-client.toml");
        ArmoryConfigLang.register();

        // Perform initialization and registration for everything so things are
        // registered.
        ArmoryItems.register(modEventBus);
        ArmoryRecipes.register(modEventBus);
        ArmoryLootModifiers.register(modEventBus);
        ArmoryDataComponents.register(modEventBus);
        ArmoryTags.register();
        ArmoryLang.register();

        REGILITE.register(modEventBus);

        // Specific event listeners
        NeoForge.EVENT_BUS.addListener(DarkSteelSwordItem::onEntityTeleport);
        NeoForge.EVENT_BUS.addListener(DarkSteelSwordItem::applyAttackModifiers);
        NeoForge.EVENT_BUS.addListener(StepAssistUpgrade::applyStepHeightModifiers);
        NeoForge.EVENT_BUS.addListener(SpeedUpgrade::applySpeedModifiers);
        NeoForge.EVENT_BUS.addListener(SpeedUpgrade::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(JumpUpgrade::doExtraJumps);
        NeoForge.EVENT_BUS.addListener(AnvilRecipeHandler::handleAnvilRecipe);
        NeoForge.EVENT_BUS.addListener(NightVisionHandler.INST::updateEffect);
        NeoForge.EVENT_BUS.addListener(SolarUpgradeHandler::onPlayerTick);

        IntegrationManager.addIntegration(GliderIntegration.INSTANCE);
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        EIODataProvider provider = new EIODataProvider("armory");

        provider.addSubProvider(event.includeServer(), new ItemRecipeProvider(packOutput, lookupProvider));
        provider.addSubProvider(event.includeServer(), new ArmoryLootModifiersProvider(packOutput, lookupProvider));

        var b = new ArmoryBlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
        provider.addSubProvider(event.includeServer(), b);

        event.getGenerator().addProvider(true, provider);
    }

    @SubscribeEvent
    public static void addBuiltInPacks(final AddPackFindersEvent event) {
        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath(MODULE_MOD_ID, "data/enderio_armory/datapacks/armory_rewrite"),
                PackType.SERVER_DATA, ArmoryLang.ARMORY_REWRITE_EXPERIMENT, PackSource.FEATURE, false,
                Pack.Position.TOP);
    }
}
