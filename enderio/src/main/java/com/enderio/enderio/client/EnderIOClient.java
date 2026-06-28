package com.enderio.enderio.client;

import com.enderio.core.client.item.FluidBarDecorator;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.api.conduits.model.RegisterConduitModelModifiersEvent;
import com.enderio.enderio.api.conduits.screen.RegisterConduitScreenTypesEvent;
import com.enderio.enderio.api.travel.RegisterTravelRenderersEvent;
import com.enderio.enderio.client.content.conduits.ConduitBundleExtension;
import com.enderio.enderio.client.content.conduits.gui.ConduitScreen;
import com.enderio.enderio.client.content.conduits.gui.screen_type.ConduitScreenTypes;
import com.enderio.enderio.client.content.conduits.gui.screen_type.EnergyConduitScreenType;
import com.enderio.enderio.client.content.conduits.gui.screen_type.FluidConduitScreenType;
import com.enderio.enderio.client.content.conduits.gui.screen_type.ItemConduitScreenType;
import com.enderio.enderio.client.content.conduits.gui.screen_type.RedstoneConduitScreenType;
import com.enderio.enderio.client.content.conduits.model.bundle.port.ConduitBaker;
import com.enderio.enderio.client.content.conduits.model.bundle.port.ConduitBlockStateModel;
import com.enderio.enderio.client.content.conduits.model.bundle.port.ConduitItemModel;
import com.enderio.enderio.client.content.conduits.model.facades.port.FacadeItemModel;
import com.enderio.enderio.client.content.conduits.model.modifier.RedstoneConduitModelModifier;
import com.enderio.enderio.client.content.enderface.EnderfaceRenderer;
import com.enderio.enderio.client.content.filters.redstone.RedstoneCountFilterScreen;
import com.enderio.enderio.client.content.filters.redstone.RedstoneDoubleChannelFilterScreen;
import com.enderio.enderio.client.content.filters.redstone.RedstoneTimerFilterScreen;
import com.enderio.enderio.client.content.fluid_tank.FluidTankItemRenderer;
import com.enderio.enderio.client.content.glass.GlassIconDecorator;
import com.enderio.enderio.client.content.glass.GlassItemColor;
import com.enderio.enderio.client.content.machines.IOOverlayBlockStateModel;
import com.enderio.enderio.client.content.machines.gui.screen.AlloySmelterScreen;
import com.enderio.enderio.client.content.machines.gui.screen.AttractorObeliskScreen;
import com.enderio.enderio.client.content.machines.gui.screen.AversionObeliskScreen;
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
import com.enderio.enderio.client.content.machines.renderer.blockentity.FluidTankBER;
import com.enderio.enderio.client.content.machines.renderer.blockentity.NiardBER;
import com.enderio.enderio.client.content.machines.renderer.blockentity.ObeliskBER;
import com.enderio.enderio.client.content.machines.renderer.blockentity.PoweredSpawnerBER;
import com.enderio.enderio.client.content.misc_blocks.EnderSkullRenderer;
import com.enderio.enderio.client.content.paint.PaintedSandRenderer;
import com.enderio.enderio.client.content.paint.model.port.PaintedBlockStateModel;
import com.enderio.enderio.client.content.paint.model.port.PaintedItemModel;
import com.enderio.enderio.client.content.tools.CoordinateMenuScreen;
import com.enderio.enderio.client.content.travel.TravelAnchorHud;
import com.enderio.enderio.client.content.travel.TravelAnchorRenderer;
import com.enderio.enderio.client.content.travel.TravelTargetRendering;
import com.enderio.enderio.client.foundation.model.IOConfigRealLevelWorkaroundBlockModel;
import com.enderio.enderio.client.foundation.model.item.HasFluidItemProperty;
import com.enderio.enderio.client.foundation.model.item.IsSoulBoundItemProperty;
import com.enderio.enderio.client.foundation.particle.RangeParticle;
import com.enderio.enderio.client.foundation.widgets.ioconfig.IOConfigSceneRenderState;
import com.enderio.enderio.client.foundation.widgets.ioconfig.IOConfigSceneRenderer;
import com.enderio.enderio.content.conduits.probe.ConduitProbeItem;
import com.enderio.enderio.content.filters.fluid.EnderFluidFilter;
import com.enderio.enderio.content.filters.item.existing.ExistingItemFilter;
import com.enderio.enderio.content.filters.item.general.EnderItemFilter;
import com.enderio.enderio.content.filters.item.limited.LimitedItemFilter;
import com.enderio.enderio.content.filters.soul.EnderSoulFilter;
import com.enderio.enderio.content.fun.EnderiosItem;
import com.enderio.enderio.content.misc_blocks.skull.EnderSkullBlock;
import com.enderio.enderio.content.tools.vials.SoulVialItem;
import com.enderio.enderio.content.tools.vials.VoidVialItem;
import com.enderio.enderio.content.paint.block.PaintExtension;
import com.enderio.enderio.content.paint.block.PaintedBlock;
import com.enderio.enderio.foundation.block.MachineBlock;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOConduitTypes;
import com.enderio.enderio.init.EIOEntities;
import com.enderio.enderio.init.EIOFluids;
import com.enderio.enderio.init.EIOItems;
import com.enderio.enderio.init.EIOMenus;
import com.enderio.enderio.init.EIOParticles;
import com.enderio.enderio.init.EIOTravelTargets;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.renderer.block.BuiltInBlockModels;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BlockStateModelWrapper;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterBlockModelsEvent;
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;
import com.enderio.enderio.client.content.filters.fluid.ClientEnderFluidFilterTooltip;
import com.enderio.enderio.client.content.filters.fluid.EnderFluidFilterScreen;
import com.enderio.enderio.client.content.filters.item.EnderItemFilterScreen;
import com.enderio.enderio.client.content.filters.item.LimitedItemFilterScreen;
import com.enderio.enderio.client.content.filters.soul.EnderSoulFilterScreen;
import com.enderio.enderio.client.content.filters.item.ClientEnderItemFilterTooltip;
import com.enderio.enderio.client.content.filters.item.ClientExistingItemFilterTooltip;
import com.enderio.enderio.client.content.filters.item.ClientLimitedItemFilterTooltip;
import com.enderio.enderio.client.content.filters.soul.ClientEnderSoulFilterTooltip;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterFluidModelsEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import net.neoforged.neoforge.client.event.RegisterItemModelsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;
import net.neoforged.neoforge.fluids.FluidType;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@EventBusSubscriber(value = Dist.CLIENT)
@Mod(value = EnderIO.MOD_ID, dist = Dist.CLIENT)
public class EnderIOClient {

    private static final Map<Item, Identifier> HANG_GLIDER_MODEL_LOCATION = new HashMap<>();
    //public static final Map<Item, BakedModel> GLIDER_MODELS = new HashMap<>();

    private static final Identifier TRAVEL_ANCHOR_BACKDROP_MODEL_LOCATION = Identifier.fromNamespaceAndPath(EnderIOAPI.MOD_ID, "block/travel_anchor_backdrop");
    public static final StandaloneModelKey<BlockModel> TRAVEL_ANCHOR_BACKDROP = new StandaloneModelKey<>(() -> "travel_anchor_backdrop");

    public EnderIOClient(ModContainer modContainer) {
        // TODO: Re-enable after config rework.
//        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void registerClientReloadListeners(AddClientReloadListenersEvent event) {
        // Provides proper access to the block atlas for ConduitBaker.ConduitMaterialBaker.
        event.addListener(EnderIO.id("conduit_baker"), ConduitBaker::reload);
    }

    @SubscribeEvent
    public static void registerPip(RegisterPictureInPictureRenderersEvent event) {
        // 26.2-port: IOConfigSceneRenderer is stubbed (MultiBufferSource removed in 26.2)
        // event.register(IOConfigSceneRenderState.class, IOConfigSceneRenderer::new);
    }

    @SubscribeEvent
    public static void registerConditionalProperties(RegisterConditionalItemModelPropertyEvent event) {
        event.register(EnderIO.id("is_soul_bound"), IsSoulBoundItemProperty.MAP_CODEC);
        event.register(EnderIO.id("has_fluid"), HasFluidItemProperty.MAP_CODEC);
        event.register(EnderIO.id("conduit_probe_mode"), ConduitProbeItem.ProbeModeItemProperty.MAP_CODEC);
        event.register(EnderIO.id("enderios_inverted"), EnderiosItem.SoiredneItemProperty.MAP_CODEC);
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        TravelTargetRendering.init();
        ConduitScreenTypes.init();
    }

    @SubscribeEvent
    public static void registerClientTooltipComponents(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(EnderItemFilter.class, ClientEnderItemFilterTooltip::new);
        event.register(ExistingItemFilter.class, ClientExistingItemFilterTooltip::new);
        event.register(LimitedItemFilter.class, ClientLimitedItemFilterTooltip::new);
        event.register(EnderSoulFilter.class, ClientEnderSoulFilterTooltip::new);
        event.register(EnderFluidFilter.class, ClientEnderFluidFilterTooltip::new);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        // Filter screens
        event.register(EIOMenus.BASIC_ITEM_FILTER.get(), EnderItemFilterScreen::new);
        event.register(EIOMenus.ADVANCED_ITEM_FILTER.get(), EnderItemFilterScreen::new);
        event.register(EIOMenus.BIG_ITEM_FILTER.get(), EnderItemFilterScreen::new);
        event.register(EIOMenus.BIG_ADVANCED_ITEM_FILTER.get(), EnderItemFilterScreen::new);
        event.register(EIOMenus.LIMITED_ITEM_FILTER.get(), LimitedItemFilterScreen::new);
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
//        event.register(EIOMenus.CAPACITOR_BANK.get(), CapacitorBankScreen::new);
        event.register(EIOMenus.FLUID_TANK.get(), FluidTankScreen::new);

        // Misc screens
        event.register(EIOMenus.COORDINATE.get(), CoordinateMenuScreen::new);
        event.register(EIOMenus.CONDUIT_MENU.get(), ConduitScreen::new);
        event.register(EIOMenus.ENCHANTER.get(), EnchanterScreen::new);
    }

    @SubscribeEvent
    public static void modelRenderer(EntityRenderersEvent.RegisterRenderers event) {
//        for (var capBank : EIOBlockEntities.CAPACITOR_BANKS.values()) {
//            event.registerBlockEntityRenderer(capBank.get(), CapacitorBankBER::new);
//        }

        event.registerBlockEntityRenderer(EIOBlockEntities.NIARD.get(), NiardBER::new);
        event.registerBlockEntityRenderer(EIOBlockEntities.XP_OBELISK.get(), c -> new ObeliskBER(EIOItems.EXPERIENCE_ROD::get));
        event.registerBlockEntityRenderer(EIOBlockEntities.INHIBITOR_OBELISK.get(), c -> new ObeliskBER(() -> Items.ENDER_PEARL));
        event.registerBlockEntityRenderer(EIOBlockEntities.AVERSION_OBELISK.get(), c -> new ObeliskBER(EIOBlocks.ENDERMAN_HEAD::asItem));
        event.registerBlockEntityRenderer(EIOBlockEntities.RELOCATOR_OBELISK.get(), c -> new ObeliskBER(() -> Items.PRISMARINE));
        event.registerBlockEntityRenderer(EIOBlockEntities.ATTRACTOR_OBELISK.get(), c -> new ObeliskBER(EIOItems.ELECTROMAGNET::get));
        event.registerBlockEntityRenderer(EIOBlockEntities.WEATHER_OBELISK.get(), c -> new ObeliskBER(() -> Items.FIREWORK_ROCKET));

        // TODO:  Custom model and BER for mind killer maybe?
        event.registerBlockEntityRenderer(EIOBlockEntities.MIND_KILLER.get(), c -> new ObeliskBER(() -> Items.ZOMBIE_HEAD));

        event.registerBlockEntityRenderer(EIOBlockEntities.FLUID_TANK.get(), FluidTankBER::new);
        event.registerBlockEntityRenderer(EIOBlockEntities.PRESSURIZED_FLUID_TANK.get(), FluidTankBER::new);
        event.registerBlockEntityRenderer(EIOBlockEntities.POWERED_SPAWNER.get(), PoweredSpawnerBER::new);

        event.registerBlockEntityRenderer(EIOBlockEntities.ENDER_SKULL.get(), EnderSkullRenderer::new);

        event.registerEntityRenderer(EIOEntities.PAINTED_SAND.get(), PaintedSandRenderer::new);
    }

    @SubscribeEvent
    public static void registerSpecialModels(RegisterSpecialModelRendererEvent event) {
        event.register(EnderIO.id("fluid_tank"), FluidTankItemRenderer.Unbaked.CODEC);
    }

    @SubscribeEvent
    public static void registerBlockColorHandlers(RegisterColorHandlersEvent.BlockTintSources event) {
//        event.register(ConduitFacadeColor.INSTANCE, EIOBlocks.CONDUIT_BUNDLE.get());
//
//        event.register(PaintedBlockColor.INSTANCE,
//            EIOBlocks.PAINTED_FENCE.get(),
//            EIOBlocks.PAINTED_FENCE_GATE.get(),
//            EIOBlocks.PAINTED_SAND.get(),
//            EIOBlocks.PAINTED_STAIRS.get(),
//            EIOBlocks.PAINTED_CRAFTING_TABLE.get(),
//            EIOBlocks.PAINTED_REDSTONE_BLOCK.get(),
//            EIOBlocks.PAINTED_TRAPDOOR.get(),
//            EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE.get(),
//            EIOBlocks.PAINTED_SLAB.get(),
//            EIOBlocks.PAINTED_GLOWSTONE.get(),
//            EIOBlocks.PAINTED_WALL.get());

        for (var glassBlocks : EIOBlocks.GLASS_BLOCKS.values()) {
            for (var entry : glassBlocks.COLORS.entrySet()) {
                event.register(List.of(BlockTintSources.constant(entry.getKey().getMapColor().col | 0xff000000)), entry.getValue().get());
            }
        }
    }

    @SubscribeEvent
    public static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(EnderIO.id("glass"), GlassItemColor.CODEC);
    }

    // TODO: 1.21.4: Deal with the new item tint system
//    @SubscribeEvent
//    public static void registerItemColorHandlers(RegisterColorHandlersEvent.Item event) {
//        event.register(PaintedBlockColor.INSTANCE,
//            EIOBlocks.PAINTED_FENCE.get(),
//            EIOBlocks.PAINTED_FENCE_GATE.get(),
//            EIOBlocks.PAINTED_SAND.get(),
//            EIOBlocks.PAINTED_STAIRS.get(),
//            EIOBlocks.PAINTED_CRAFTING_TABLE.get(),
//            EIOBlocks.PAINTED_REDSTONE_BLOCK.get(),
//            EIOBlocks.PAINTED_TRAPDOOR.get(),
//            EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE.get(),
//            EIOBlocks.PAINTED_SLAB.get(),
//            EIOBlocks.PAINTED_GLOWSTONE.get(),
//            EIOBlocks.PAINTED_WALL.get());
//
//        for (var glassBlocks : EIOBlocks.GLASS_BLOCKS.values()) {
//            for (var entry : glassBlocks.COLORS.entrySet()) {
//                event.register((stack, tintIndex) -> entry.getKey().getMapColor().col, entry.getValue().asItem());
//            }
//        }
//    }

    @SubscribeEvent
    public static void additionalModels(ModelEvent.RegisterStandalone event) {
        // TODO: standalone models
//        Set<ResourceLocation> gliderModels = Minecraft.getInstance()
//                .getResourceManager()
//                .listResources("models/enderio_glider", rl -> rl.getPath().endsWith(".json"))
//                .keySet();
//
//        for (ResourceLocation gliderModelPath : gliderModels) {
//            Optional<Item> gliderItem = findGliderForModelRL(gliderModelPath);
//            if (gliderItem.isPresent()) {
//                ResourceLocation modelLookupLocation = ResourceLocation
//                        .fromNamespaceAndPath(gliderModelPath.getNamespace(), gliderModelPath.getPath()
//                                .substring("models/".length(), gliderModelPath.getPath().length() - 5));
//
//                ModelResourceLocation modelLocation = ModelResourceLocation.standalone(modelLookupLocation);
//                event.register(modelLocation);
//                HANG_GLIDER_MODEL_LOCATION.put(gliderItem.get(), modelLocation);
//            }
//        }
//
        event.register(TRAVEL_ANCHOR_BACKDROP, new SimpleUnbakedStandaloneModel<>(TRAVEL_ANCHOR_BACKDROP_MODEL_LOCATION, (model, baker, _) -> {
            var stateModel = new SingleVariant(SimpleModelWrapper.bake(baker, model, BlockModelRotation.IDENTITY));
            return new BlockStateModelWrapper(stateModel, List.of(), new Matrix4f());
        }));
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
//            if (event.getSkin(skin) instanceof PlayerRenderer playerRenderer) {
//                playerRenderer.addLayer(new ActiveGliderRenderLayer(playerRenderer));
//            }
        }
    }

    @SubscribeEvent
    public static void bakingCompleted(ModelEvent.BakingCompleted event) {
//        GLIDER_MODELS.clear();
//        HANG_GLIDER_MODEL_LOCATION.forEach((item, modelRL) -> {
//            var bakedModel = event.getModelManager().getStandaloneModel(modelRL);
//            GLIDER_MODELS.put(item, bakedModel);
//        });
//        HANG_GLIDER_MODEL_LOCATION.clear();
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(EIOParticles.RANGE_PARTICLE.get(), RangeParticle.Provider::new);
    }

    private static Optional<Item> findGliderForModelRL(Identifier rl) {
        String namespace = rl.getNamespace();
        String path = rl.getPath().substring("models/enderio_glider/".length(), rl.getPath().length() - 5);
        return Optional.of(BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(namespace, path)));
    }

    @SubscribeEvent
    public static void registerGeometryLoaders(ModelEvent.RegisterLoaders event) {
//        event.register(EnderIO.rl("painted_block"), new PaintedBlockGeometry.Loader());
//        event.register(EnderIO.rl("io_overlay"), new IOOverlayBakedModel.Loader());
//        event.register(EnderIO.rl("conduit_item"), new ConduitItemModelLoader());
//        event.register(EnderIO.rl("facades_item"), new FacadeItemGeometry.Loader());
    }

    @SubscribeEvent
    public static void registerGeometryLoaders(RegisterBlockStateModels event) {
        event.registerModel(EnderIO.id("conduit"), ConduitBlockStateModel.Unbaked.CODEC);
        event.registerModel(EnderIO.id("io_overlay"), IOOverlayBlockStateModel.Unbaked.MAP_CODEC);
        event.registerModel(EnderIO.id("paint"), PaintedBlockStateModel.Unbaked.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerBlockModels(RegisterBlockModelsEvent event) {
        BuiltInBlockModels.ModelFactory factory = (_, state) -> new IOConfigRealLevelWorkaroundBlockModel.Unbaked(state, Optional.empty());

        for (var block : EIOBlocks.BLOCKS.getEntries()) {
            if (block.get() instanceof MachineBlock<?>) {
                event.register(factory, block.get());
            }

            if (block.get() instanceof PaintedBlock) {
                event.register(factory, block.get());
            }
        }

        event.register(factory, EIOBlocks.CONDUIT_BUNDLE.get());
    }

    @SubscribeEvent
    public static void registerItemModels(RegisterItemModelsEvent event) {
        event.register(EnderIO.id("conduit"), ConduitItemModel.Unbaked.CODEC);
        event.register(EnderIO.id("facade"), FacadeItemModel.Unbaked.CODEC);
        event.register(EnderIO.id("painted"), PaintedItemModel.Unbaked.CODEC);
    }

    @SubscribeEvent
    public static void registerModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(EnderSkullRenderer.ENDER_SKULL,
                EnderSkullRenderer.EnderSkullModel::createMobHeadLayer);
    }

    @SubscribeEvent
    public static void registerEnderSkulls(EntityRenderersEvent.CreateSkullModels event) {
        event.registerSkullModel(EnderSkullBlock.EIOSkulls.ENDERMAN, entityModelSet -> new EnderSkullRenderer.EnderSkullModel(
                entityModelSet.bakeLayer(EnderSkullRenderer.ENDER_SKULL)), Identifier.withDefaultNamespace("textures/entity/enderman/enderman.png"));
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, EnderIO.id("anchor_hud"), TravelAnchorHud.INSTANCE);
    }

    @SubscribeEvent
    public static void registerFluidModels(RegisterFluidModelsEvent event) {
        Map<FluidType, FluidModel.Unbaked> fluidTypeToModel = new HashMap<>();

        for (Holder<FluidType> fluidType : EIOFluids.FLUIDS.fluidTypesRegister().getEntries()) {
            String name = Objects.requireNonNull(fluidType.getKey()).identifier().getPath();

            fluidTypeToModel.put(fluidType.value(), new FluidModel.Unbaked(
                new Material(EnderIO.id("block/" + name + "_still")),
                new Material(EnderIO.id("block/" + name + "_flowing")),
                null,
                null,
                null
            ));
        }

        for (Holder<Fluid> fluid : EIOFluids.FLUIDS.fluidsRegister().getEntries()) {
            FluidModel.Unbaked model = fluidTypeToModel.get(fluid.value().getFluidType());
            event.register(model, fluid.value());
        }
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerBlock(ConduitBundleExtension.INSTANCE, EIOBlocks.CONDUIT_BUNDLE);
        for (var entry : EIOBlocks.PAINTED_BLOCKS) {
            event.registerBlock(PaintExtension.INSTANCE, entry.first());
        }
        event.registerBlock(PaintExtension.INSTANCE, EIOBlocks.PAINTED_TRAVEL_ANCHOR);
    }

    @SubscribeEvent
    public static void registerTravelRenderers(RegisterTravelRenderersEvent event) {
        // TODO: 26.1
        event.register(EIOTravelTargets.TRAVEL_ANCHOR_TYPE.get(), TravelAnchorRenderer::new);
        event.register(EIOTravelTargets.ENDERFACE_TYPE.get(), EnderfaceRenderer::new);
    }

    // region Conduits

    @SubscribeEvent
    public static void registerConduitCoreModelModifiers(RegisterConduitModelModifiersEvent event) {
        event.register(EIOConduitTypes.REDSTONE.get(), RedstoneConduitModelModifier::new);
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
