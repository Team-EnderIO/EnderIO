package com.enderio.enderio.client;

import com.enderio.core.client.item.FluidBarDecorator;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.conduits.model.RegisterConduitModelModifiersEvent;
import com.enderio.enderio.api.conduits.screen.RegisterConduitScreenTypesEvent;
import com.enderio.enderio.api.soul.SoulBoundUtils;
import com.enderio.enderio.api.travel.RegisterTravelRenderersEvent;
import com.enderio.enderio.client.content.conduits.ConduitBundleExtension;
import com.enderio.enderio.client.content.conduits.ConduitFacadeColor;
import com.enderio.enderio.client.content.conduits.gui.ConduitScreen;
import com.enderio.enderio.client.content.conduits.gui.screen_type.ConduitScreenTypes;
import com.enderio.enderio.client.content.conduits.gui.screen_type.EnergyConduitScreenType;
import com.enderio.enderio.client.content.conduits.gui.screen_type.FluidConduitScreenType;
import com.enderio.enderio.client.content.conduits.gui.screen_type.ItemConduitScreenType;
import com.enderio.enderio.client.content.conduits.gui.screen_type.RedstoneConduitScreenType;
import com.enderio.enderio.client.content.conduits.model.ConduitItemModelLoader;
import com.enderio.enderio.client.content.conduits.model.bundle.ConduitBundleGeometry;
import com.enderio.enderio.client.content.conduits.model.facades.FacadeItemGeometry;
import com.enderio.enderio.client.content.conduits.model.modifier.FluidConduitModelModifier;
import com.enderio.enderio.client.content.conduits.model.modifier.RedstoneConduitModelModifier;
import com.enderio.enderio.client.content.enderface.EnderfaceRenderer;
import com.enderio.enderio.client.content.filters.EnderFluidFilterScreen;
import com.enderio.enderio.client.content.filters.EnderItemFilterScreen;
import com.enderio.enderio.client.content.filters.EnderSoulFilterScreen;
import com.enderio.enderio.client.content.filters.redstone.RedstoneCountFilterScreen;
import com.enderio.enderio.client.content.filters.redstone.RedstoneDoubleChannelFilterScreen;
import com.enderio.enderio.client.content.filters.redstone.RedstoneTimerFilterScreen;
import com.enderio.enderio.client.content.fluid_tank.FluidTankBEWLR;
import com.enderio.enderio.client.content.glass.GlassIconDecorator;
import com.enderio.enderio.client.content.machines.IOOverlayBakedModel;
import com.enderio.enderio.client.content.machines.gui.screen.AlloySmelterScreen;
import com.enderio.enderio.client.content.machines.gui.screen.AttractorObeliskScreen;
import com.enderio.enderio.client.content.machines.gui.screen.AversionObeliskScreen;
import com.enderio.enderio.client.content.machines.gui.screen.CapacitorBankScreen;
import com.enderio.enderio.client.content.machines.gui.screen.CrafterScreen;
import com.enderio.enderio.client.content.machines.gui.screen.DrainScreen;
import com.enderio.enderio.client.content.machines.gui.screen.EnchanterScreen;
import com.enderio.enderio.client.content.machines.gui.screen.FarmingStationScreen;
import com.enderio.enderio.client.content.machines.gui.screen.FluidTankScreen;
import com.enderio.enderio.client.content.machines.gui.screen.ImpulseHopperScreen;
import com.enderio.enderio.client.content.machines.gui.screen.InhibitorObeliskScreen;
import com.enderio.enderio.client.content.machines.gui.screen.NiardScreen;
import com.enderio.enderio.client.content.machines.gui.screen.PaintingMachineScreen;
import com.enderio.enderio.client.content.machines.gui.screen.PoweredSpawnerScreen;
import com.enderio.enderio.client.content.machines.gui.screen.RelocatorObeliskScreen;
import com.enderio.enderio.client.content.machines.gui.screen.SagMillScreen;
import com.enderio.enderio.client.content.machines.gui.screen.SlicerScreen;
import com.enderio.enderio.client.content.machines.gui.screen.SoulBinderScreen;
import com.enderio.enderio.client.content.machines.gui.screen.SoulEngineScreen;
import com.enderio.enderio.client.content.machines.gui.screen.StirlingGeneratorScreen;
import com.enderio.enderio.client.content.machines.gui.screen.TravelAnchorScreen;
import com.enderio.enderio.client.content.machines.gui.screen.VacuumChestScreen;
import com.enderio.enderio.client.content.machines.gui.screen.VatScreen;
import com.enderio.enderio.client.content.machines.gui.screen.WeatherObeliskScreen;
import com.enderio.enderio.client.content.machines.gui.screen.WiredChargerScreen;
import com.enderio.enderio.client.content.machines.gui.screen.WirelessChargerScreen;
import com.enderio.enderio.client.content.machines.gui.screen.XPObeliskScreen;
import com.enderio.enderio.client.content.machines.gui.screen.XPVacuumScreen;
import com.enderio.enderio.client.content.machines.renderer.blockentity.CapacitorBankBER;
import com.enderio.enderio.client.content.machines.renderer.blockentity.FluidTankBER;
import com.enderio.enderio.client.content.machines.renderer.blockentity.NiardBER;
import com.enderio.enderio.client.content.machines.renderer.blockentity.ObeliskBER;
import com.enderio.enderio.client.content.misc_blocks.EnderSkullRenderer;
import com.enderio.enderio.client.content.paint.PaintedBlockColor;
import com.enderio.enderio.client.content.paint.PaintedSandRenderer;
import com.enderio.enderio.client.content.paint.model.PaintedBlockGeometry;
import com.enderio.enderio.client.content.tools.ActiveGliderRenderLayer;
import com.enderio.enderio.client.content.tools.CoordinateMenuScreen;
import com.enderio.enderio.client.content.travel.TravelAnchorHud;
import com.enderio.enderio.client.content.travel.TravelAnchorRenderer;
import com.enderio.enderio.client.content.travel.TravelTargetRendering;
import com.enderio.enderio.client.foundation.particle.RangeParticle;
import com.enderio.enderio.content.conduits.probe.ConduitProbeItem;
import com.enderio.enderio.content.fun.EnderiosItem;
import com.enderio.enderio.content.misc_blocks.skull.EnderSkullBlock;
import com.enderio.enderio.content.tools.vials.SoulVialItem;
import com.enderio.enderio.init.ConduitBlocks;
import com.enderio.enderio.init.ConduitItems;
import com.enderio.enderio.init.EIOConduitTypes;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOEntities;
import com.enderio.enderio.init.EIOItems;
import com.enderio.enderio.init.EIOMenus;
import com.enderio.enderio.init.EIOParticles;
import com.enderio.enderio.init.MachineBlocks;
import com.enderio.enderio.init.EIOTravelTargets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.util.Lazy;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@EventBusSubscriber(value = Dist.CLIENT)
@Mod(value = EnderIO.MOD_ID, dist = Dist.CLIENT)
public class EnderIOClient {

    private static final Map<Item, ModelResourceLocation> HANG_GLIDER_MODEL_LOCATION = new HashMap<>();
    public static final Map<Item, BakedModel> GLIDER_MODELS = new HashMap<>();

    public EnderIOClient(ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        TravelTargetRendering.init();
        ConduitScreenTypes.init();

        event.enqueueWork(() -> {
            //switch to item model component in 1.21.2
            ItemProperties.register(EIOItems.SOUL_VIAL.get(), SoulVialItem.FILLED_MODEL_PROPERTY,
                (stack, level, player, seed) -> SoulBoundUtils.isBound(stack) ? 1 : 0);

            ItemProperties.register(EIOItems.ENDERIOS.asItem(), EnderiosItem.INVERTED_PROPERTY,
                (ClampedItemPropertyFunction) (itemStack, clientLevel, livingEntity, seed) -> {
                    Component name = itemStack.get(DataComponents.CUSTOM_NAME);
                    if (name != null && name.getContents() instanceof PlainTextContents literal && literal.text().equalsIgnoreCase("soiredne")) {
                        return 1;
                    }
                    return 0;
                });

            // Register item property for conduit probe state switching
            ItemProperties.register(ConduitItems.CONDUIT_PROBE.get(), EnderIO.rl("probe_state"),
                (stack, level, player, seed) -> {
                    ConduitProbeItem.State state = ConduitProbeItem.getState(stack);
                    return state == ConduitProbeItem.State.COPY_PASTE ? 1.0f : 0.0f;
                });
        });
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        // Filter screens
        event.register(EIOMenus.BASIC_ITEM_FILTER.get(), EnderItemFilterScreen::new);
        event.register(EIOMenus.ADVANCED_ITEM_FILTER.get(), EnderItemFilterScreen::new);
        event.register(EIOMenus.BIG_ITEM_FILTER.get(), EnderItemFilterScreen::new);
        event.register(EIOMenus.BIG_ADVANCED_ITEM_FILTER.get(), EnderItemFilterScreen::new);
        event.register(EIOMenus.BASIC_FLUID_FILTER.get(), EnderFluidFilterScreen::new);
        event.register(EIOMenus.BASIC_SOUL_FILTER.get(), EnderSoulFilterScreen::new);
        event.register(EIOMenus.REDSTONE_DOUBLE_CHANNEL_FILTER.get(), RedstoneDoubleChannelFilterScreen::new);
        event.register(EIOMenus.REDSTONE_TIMER_FILTER.get(), RedstoneTimerFilterScreen::new);
        event.register(EIOMenus.REDSTONE_COUNT_FILTER.get(), RedstoneCountFilterScreen::new);

        // Machines
        event.register(EIOMenus.ALLOY_SMELTER.get(), AlloySmelterScreen::new);
        event.register(EIOMenus.SAG_MILL.get(), SagMillScreen::new);
        event.register(EIOMenus.STIRLING_GENERATOR.get(), StirlingGeneratorScreen::new);
        event.register(EIOMenus.SLICE_N_SPLICE.get(), SlicerScreen::new);
        event.register(EIOMenus.IMPULSE_HOPPER.get(), ImpulseHopperScreen::new);
        event.register(EIOMenus.SOUL_BINDER.get(), SoulBinderScreen::new);
        event.register(EIOMenus.POWERED_SPAWNER.get(), PoweredSpawnerScreen::new);
        event.register(EIOMenus.VACUUM_CHEST.get(), VacuumChestScreen::new);
        event.register(EIOMenus.XP_VACUUM.get(), XPVacuumScreen::new);
        event.register(EIOMenus.CRAFTER.get(), CrafterScreen::new);
        event.register(EIOMenus.DRAIN.get(), DrainScreen::new);
        event.register(EIOMenus.NIARD.get(), NiardScreen::new);
        event.register(EIOMenus.WIRED_CHARGER.get(), WiredChargerScreen::new);
        event.register(EIOMenus.WIRELESS_CHARGER.get(), WirelessChargerScreen::new);
        event.register(EIOMenus.PAINTING_MACHINE.get(), PaintingMachineScreen::new);
        event.register(EIOMenus.SOUL_ENGINE.get(), SoulEngineScreen::new);
        event.register(EIOMenus.TRAVEL_ANCHOR.get(), TravelAnchorScreen::new);
        event.register(EIOMenus.XP_OBELISK.get(), XPObeliskScreen::new);
        event.register(EIOMenus.FARMING_STATION.get(), FarmingStationScreen::new);
        event.register(EIOMenus.INHIBITOR_OBELISK.get(), InhibitorObeliskScreen::new);
        event.register(EIOMenus.AVERSION_OBELISK.get(), AversionObeliskScreen::new);
        event.register(EIOMenus.RELOCATOR_OBELISK.get(), RelocatorObeliskScreen::new);
        event.register(EIOMenus.ATTRACTOR_OBELISK.get(), AttractorObeliskScreen::new);
        event.register(EIOMenus.WEATHER_OBELISK.get(), WeatherObeliskScreen::new);
        event.register(EIOMenus.VAT.get(), VatScreen::new);

        // Storage
        event.register(EIOMenus.CAPACITOR_BANK.get(), CapacitorBankScreen::new);
        event.register(EIOMenus.FLUID_TANK.get(), FluidTankScreen::new);

        // Misc screens
        event.register(EIOMenus.COORDINATE.get(), CoordinateMenuScreen::new);
        event.register(EIOMenus.CONDUIT_MENU.get(), ConduitScreen::new);
        event.register(EIOMenus.ENCHANTER.get(), EnchanterScreen::new);
    }

    @SubscribeEvent
    public static void modelRenderer(EntityRenderersEvent.RegisterRenderers event) {
        for (var capBank : EIOBlockEntities.CAPACITOR_BANKS.values()) {
            event.registerBlockEntityRenderer(capBank.get(), CapacitorBankBER::new);
        }

        event.registerBlockEntityRenderer(EIOBlockEntities.NIARD.get(), NiardBER::new);
        event.registerBlockEntityRenderer(EIOBlockEntities.XP_OBELISK.get(), c -> new ObeliskBER(EIOItems.VOID_VIAL::get));
        event.registerBlockEntityRenderer(EIOBlockEntities.INHIBITOR_OBELISK.get(), c -> new ObeliskBER(() -> Items.ENDER_PEARL));
        event.registerBlockEntityRenderer(EIOBlockEntities.AVERSION_OBELISK.get(), c -> new ObeliskBER(EIOBlocks.ENDERMAN_HEAD::asItem));
        event.registerBlockEntityRenderer(EIOBlockEntities.RELOCATOR_OBELISK.get(), c -> new ObeliskBER(() -> Items.PRISMARINE));
        event.registerBlockEntityRenderer(EIOBlockEntities.ATTRACTOR_OBELISK.get(), c -> new ObeliskBER(EIOItems.ELECTROMAGNET::get));
        event.registerBlockEntityRenderer(EIOBlockEntities.WEATHER_OBELISK.get(), c -> new ObeliskBER(() -> Items.FIREWORK_ROCKET));

        // TODO:  Custom model and BER for mind killer maybe?
        event.registerBlockEntityRenderer(EIOBlockEntities.MIND_KILLER.get(), c -> new ObeliskBER(() -> Items.ZOMBIE_HEAD));

        event.registerBlockEntityRenderer(EIOBlockEntities.FLUID_TANK.get(), FluidTankBER::new);
        event.registerBlockEntityRenderer(EIOBlockEntities.PRESSURIZED_FLUID_TANK.get(), FluidTankBER::new);

        event.registerBlockEntityRenderer(EIOBlockEntities.ENDER_SKULL.get(), EnderSkullRenderer::new);

        event.registerEntityRenderer(EIOEntities.PAINTED_SAND.get(), PaintedSandRenderer::new);
    }

    @SubscribeEvent
    public static void registerBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
        event.register(ConduitFacadeColor.INSTANCE, ConduitBlocks.CONDUIT.get());

        event.register(PaintedBlockColor.INSTANCE,
            EIOBlocks.PAINTED_FENCE.get(),
            EIOBlocks.PAINTED_FENCE_GATE.get(),
            EIOBlocks.PAINTED_SAND.get(),
            EIOBlocks.PAINTED_STAIRS.get(),
            EIOBlocks.PAINTED_CRAFTING_TABLE.get(),
            EIOBlocks.PAINTED_REDSTONE_BLOCK.get(),
            EIOBlocks.PAINTED_TRAPDOOR.get(),
            EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE.get(),
            EIOBlocks.PAINTED_SLAB.get(),
            EIOBlocks.PAINTED_GLOWSTONE.get(),
            EIOBlocks.PAINTED_WALL.get());
    }

    @SubscribeEvent
    public static void registerItemColorHandlers(RegisterColorHandlersEvent.Item event) {
        event.register(ConduitFacadeColor.INSTANCE,
            ConduitItems.CONDUIT_FACADE.get(),
            ConduitItems.TRANSPARENT_CONDUIT_FACADE.get(),
            ConduitItems.HARDENED_CONDUIT_FACADE.get(),
            ConduitItems.TRANSPARENT_HARDENED_CONDUIT_FACADE.get());

        event.register(PaintedBlockColor.INSTANCE,
            EIOBlocks.PAINTED_FENCE.get(),
            EIOBlocks.PAINTED_FENCE_GATE.get(),
            EIOBlocks.PAINTED_SAND.get(),
            EIOBlocks.PAINTED_STAIRS.get(),
            EIOBlocks.PAINTED_CRAFTING_TABLE.get(),
            EIOBlocks.PAINTED_REDSTONE_BLOCK.get(),
            EIOBlocks.PAINTED_TRAPDOOR.get(),
            EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE.get(),
            EIOBlocks.PAINTED_SLAB.get(),
            EIOBlocks.PAINTED_GLOWSTONE.get(),
            EIOBlocks.PAINTED_WALL.get());
    }

    @SubscribeEvent
    public static void additionalModels(ModelEvent.RegisterAdditional event) {
        Set<ResourceLocation> gliderModels = Minecraft.getInstance()
                .getResourceManager()
                .listResources("models/enderio_glider", rl -> rl.getPath().endsWith(".json"))
                .keySet();

        for (ResourceLocation gliderModelPath : gliderModels) {
            Optional<Item> gliderItem = findGliderForModelRL(gliderModelPath);
            if (gliderItem.isPresent()) {
                ResourceLocation modelLookupLocation = ResourceLocation
                        .fromNamespaceAndPath(gliderModelPath.getNamespace(), gliderModelPath.getPath()
                                .substring("models/".length(), gliderModelPath.getPath().length() - 5));

                ModelResourceLocation modelLocation = ModelResourceLocation.standalone(modelLookupLocation);
                event.register(modelLocation);
                HANG_GLIDER_MODEL_LOCATION.put(gliderItem.get(), modelLocation);
            }
        }
    }

    @SubscribeEvent
    public static void itemDecorators(RegisterItemDecorationsEvent event) {
        // Register tools
        event.register(EIOItems.LEVITATION_STAFF.get(), FluidBarDecorator.INSTANCE);

        // Register all glass blocks
        EIOBlocks.GLASS_BLOCKS.values()
                .forEach(blocks -> blocks.getAllBlocks()
                        .forEach(block -> event.register(block.get(), GlassIconDecorator.INSTANCE)));
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        for (var skin : event.getSkins()) {
            if (event.getSkin(skin) instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new ActiveGliderRenderLayer(playerRenderer));
            }
        }
    }

    @SubscribeEvent
    public static void bakingCompleted(ModelEvent.BakingCompleted event) {
        GLIDER_MODELS.clear();
        HANG_GLIDER_MODEL_LOCATION.forEach((item, modelRL) -> {
            BakedModel bakedModel = event.getModels().get(modelRL);
            if (bakedModel != null) {
                GLIDER_MODELS.put(item, bakedModel);
            }
        });
        HANG_GLIDER_MODEL_LOCATION.clear();
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(EIOParticles.RANGE_PARTICLE.get(), RangeParticle.Provider::new);
    }

    private static Optional<Item> findGliderForModelRL(ResourceLocation rl) {
        String namespace = rl.getNamespace();
        String path = rl.getPath().substring("models/enderio_glider/".length(), rl.getPath().length() - 5);
        return Optional.of(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(namespace, path)));
    }

    @SubscribeEvent
    public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(EnderIO.rl("painted_block"), new PaintedBlockGeometry.Loader());
        event.register(EnderIO.rl("io_overlay"), new IOOverlayBakedModel.Loader());
        event.register(EnderIO.rl("conduit"), new ConduitBundleGeometry.Loader());
        event.register(EnderIO.rl("conduit_item"), new ConduitItemModelLoader());
        event.register(EnderIO.rl("facades_item"), new FacadeItemGeometry.Loader());
    }

    @SubscribeEvent
    public static void registerModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(EnderSkullRenderer.ENDER_SKULL,
                EnderSkullRenderer.EnderSkullModel::createMobHeadLayer);
    }

    @SubscribeEvent
    public static void registerEnderSkulls(EntityRenderersEvent.CreateSkullModels event) {
        event.registerSkullModel(EnderSkullBlock.EIOSkulls.ENDERMAN, new EnderSkullRenderer.EnderSkullModel(
                event.getEntityModelSet().bakeLayer(EnderSkullRenderer.ENDER_SKULL)));
        SkullBlockRenderer.SKIN_BY_TYPE.put(EnderSkullBlock.EIOSkulls.ENDERMAN,
                ResourceLocation.withDefaultNamespace("textures/entity/enderman/enderman.png"));
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, EnderIO.rl("anchor_hud"), TravelAnchorHud.INSTANCE);
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerBlock(ConduitBundleExtension.INSTANCE, ConduitBlocks.CONDUIT);

        event.registerItem(new IClientItemExtensions() {
            // Minecraft can be null during datagen
            final Lazy<BlockEntityWithoutLevelRenderer> renderer = Lazy.of(() -> FluidTankBEWLR.INSTANCE);

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer.get();
            }
        }, MachineBlocks.FLUID_TANK.asItem(), MachineBlocks.PRESSURIZED_FLUID_TANK.asItem());
    }

    @SubscribeEvent
    public static void registerTravelRenderers(RegisterTravelRenderersEvent event) {
        event.register(EIOTravelTargets.TRAVEL_ANCHOR_TYPE.get(), TravelAnchorRenderer::new);
        event.register(EIOTravelTargets.ENDERFACE_TYPE.get(), EnderfaceRenderer::new);
    }

    // region Conduits

    @SubscribeEvent
    public static void registerConduitCoreModelModifiers(RegisterConduitModelModifiersEvent event) {
        event.register(EIOConduitTypes.REDSTONE.get(), RedstoneConduitModelModifier::new);
        event.register(EIOConduitTypes.FLUID.get(), FluidConduitModelModifier::new);
    }

    @SubscribeEvent
    public static void registerConduitScreenTypes(RegisterConduitScreenTypesEvent event) {
        event.register(EIOConduitTypes.ENERGY.get(), new EnergyConduitScreenType());
        event.register(EIOConduitTypes.FLUID.get(), new FluidConduitScreenType());
        event.register(EIOConduitTypes.REDSTONE.get(), new RedstoneConduitScreenType());
        event.register(EIOConduitTypes.ITEM.get(), new ItemConduitScreenType());
    }

    // endregion
}
