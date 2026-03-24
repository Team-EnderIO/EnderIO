package com.enderio.enderio.datagen.client.models;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.conduits.model.bundle.port.ConduitItemModel;
import com.enderio.enderio.client.content.conduits.model.facades.port.FacadeItemModel;
import com.enderio.enderio.client.content.fluid_tank.FluidTankItemRenderer;
import com.enderio.enderio.client.foundation.model.item.IsSoulBoundItemProperty;
import com.enderio.enderio.content.conduits.facades.ConduitFacadeItem;
import com.enderio.enderio.content.conduits.probe.ConduitProbeItem;
import com.enderio.enderio.content.fun.EnderiosItem;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOFluids;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.model.item.DynamicFluidContainerModel;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.Optional;
import java.util.stream.Stream;

public class EIOItemModelProvider extends ModelProvider {
    public EIOItemModelProvider(PackOutput output) {
        super(output, EnderIO.MOD_ID);
    }

    @Override
    public String getName() {
        return "Ender IO Item Model Definitions";
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return Stream.empty();
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        // Alloys
        itemModels.generateFlatItem(EIOItems.ENERGETIC_ALLOY_INGOT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.VIBRANT_ALLOY_INGOT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.REDSTONE_ALLOY_INGOT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.CONDUCTIVE_ALLOY_INGOT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.PULSATING_ALLOY_INGOT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.DARK_STEEL_INGOT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.SOULARIUM_INGOT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.END_STEEL_INGOT.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(EIOItems.ENERGETIC_ALLOY_NUGGET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.VIBRANT_ALLOY_NUGGET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.REDSTONE_ALLOY_NUGGET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.CONDUCTIVE_ALLOY_NUGGET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.PULSATING_ALLOY_NUGGET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.DARK_STEEL_NUGGET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.SOULARIUM_NUGGET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.END_STEEL_NUGGET.get(), ModelTemplates.FLAT_ITEM);

        // Grinding Balls
        itemModels.generateFlatItem(EIOItems.ENERGETIC_ALLOY_BALL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.VIBRANT_ALLOY_BALL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.REDSTONE_ALLOY_BALL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.CONDUCTIVE_ALLOY_BALL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.PULSATING_ALLOY_BALL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.DARK_STEEL_BALL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.SOULARIUM_BALL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.END_STEEL_BALL.get(), ModelTemplates.FLAT_ITEM);

        // Crafting Components
        itemModels.generateFlatItem(EIOItems.SILICON.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.GRAINS_OF_INFINITY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.INFINITY_ROD.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.CONDUIT_BINDER_COMPOSITE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.CONDUIT_BINDER.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(EIOItems.GEAR_IRON.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.GEAR_ENERGIZED.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.GEAR_VIBRANT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.GEAR_DARK_STEEL.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(EIOItems.ZOMBIE_ELECTRODE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.Z_LOGIC_CONTROLLER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.ENDER_RESONATOR.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.FRANK_N_ZOMBIE.get(), EIOItems.Z_LOGIC_CONTROLLER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.SENTIENT_ENDER.get(), EIOItems.ENDER_RESONATOR.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(EIOItems.SKELETAL_CONTRACTOR.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.GUARDIAN_DIODE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.SUSPICIOUS_SEED.get(), ModelTemplates.FLAT_ITEM);

        // Capacitors
        itemModels.generateFlatItem(EIOItems.BASIC_CAPACITOR.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.DOUBLE_LAYER_CAPACITOR.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.OCTADIC_CAPACITOR.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.LOOT_CAPACITOR.get(), ModelTemplates.FLAT_ITEM); // TODO: Multiple variants of loot capacitor.

        // Crystals
        itemModels.generateFlatItem(EIOItems.PULSATING_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.VIBRANT_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.ENDER_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.ENTICING_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.WEATHER_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.PRESCIENT_CRYSTAL.get(), ModelTemplates.FLAT_ITEM);

        // Powders and Fragments
        itemModels.generateFlatItem(EIOItems.POWDERED_COAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.POWDERED_IRON.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.POWDERED_GOLD.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.POWDERED_COPPER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.POWDERED_TIN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.POWDERED_ENDER_PEARL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.POWDERED_OBSIDIAN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.POWDERED_LAPIS_LAZULI.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.POWDERED_QUARTZ.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.PRESCIENT_POWDER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.VIBRANT_POWDER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.PULSATING_POWDER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.ENDER_CRYSTAL_POWDER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.PHOTOVOLTAIC_COMPOSITE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.SOUL_POWDER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.CONFUSION_POWDER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.WITHERING_POWDER.get(), ModelTemplates.FLAT_ITEM);

        // Misc Materials
        itemModels.generateFlatItem(EIOItems.PHOTOVOLTAIC_PLATE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.NUTRITIOUS_STICK.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.PLANT_MATTER_GREEN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.PLANT_MATTER_BROWN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.GLIDER_WING.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.ANIMAL_TOKEN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.MONSTER_TOKEN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.PLAYER_TOKEN.get(), ModelTemplates.FLAT_ITEM);
        createFakeBlock(blockModels, EIOItems.BROKEN_SPAWNER.get());

        // Gliders
        itemModels.generateFlatItem(EIOItems.GLIDER.get(), ModelTemplates.FLAT_ITEM);

        generateEnderios(itemModels, EIOItems.ENDERIOS.get());

        generateSoulVial(itemModels, EIOItems.SOUL_VIAL.get());

        itemModels.generateFlatItem(EIOItems.VOID_VIAL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.YETA_WRENCH.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.COORDINATE_SELECTOR.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.LOCATION_PRINTOUT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.LEVITATION_STAFF.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.TRAVEL_STAFF.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.ELECTROMAGNET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.COLD_FIRE_IGNITER.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(EIOItems.DARK_STEEL_SWORD.get(), ModelTemplates.FLAT_HANDHELD_ITEM);

        // Filters
        itemModels.generateFlatItem(EIOItems.BASIC_ITEM_FILTER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.BIG_ITEM_FILTER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.ADVANCED_ITEM_FILTER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.BIG_ADVANCED_ITEM_FILTER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.LIMITED_ITEM_FILTER.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(EIOItems.BASIC_FLUID_FILTER.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(EIOItems.BASIC_SOUL_FILTER.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(EIOItems.REDSTONE_FILTER_BASE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.NOT_FILTER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.OR_FILTER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.AND_FILTER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.NOR_FILTER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.NAND_FILTER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.XOR_FILTER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.XNOR_FILTER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.TLATCH_FILTER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.COUNT_FILTER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.SENSOR_FILTER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(EIOItems.TIMER_FILTER.get(), ModelTemplates.FLAT_ITEM);

        // Creative Tab Icon
        itemModels.generateFlatItem(EIOItems.CREATIVE_ICON.get(),  ModelTemplates.FLAT_ITEM);

        // Buckets
        for (var item : EIOFluids.FLUIDS.itemsRegister().getEntries()) {
            // They should all be bucket items.
            if (item.get() instanceof BucketItem bucketItem) {
                var type = bucketItem.content.getFluidType();
                bucketItem(itemModels, bucketItem, bucketItem.content, type.isLighterThanAir(), type.getLightLevel() > 0);
            }
        }

        itemModels.itemModelOutput.accept(EIOItems.CONDUIT.get(), new ConduitItemModel.Unbaked());
        createFacade(itemModels, EIOItems.CONDUIT_FACADE.get());
        createFacade(itemModels, EIOItems.TRANSPARENT_CONDUIT_FACADE.get());
        createFacade(itemModels, EIOItems.HARDENED_CONDUIT_FACADE.get());
        createFacade(itemModels, EIOItems.TRANSPARENT_HARDENED_CONDUIT_FACADE.get());

        generateProbe(itemModels, EIOItems.CONDUIT_PROBE.get());

        fluidTank(itemModels, EIOBlocks.FLUID_TANK.get());
        fluidTank(itemModels, EIOBlocks.PRESSURIZED_FLUID_TANK.get());
    }

    private static void createFacade(ItemModelGenerators itemModels, Item item) {
        itemModels.itemModelOutput.accept(item,
            ItemModelUtils.composite(new FacadeItemModel.Unbaked(),
                ItemModelUtils.conditional(new ConduitFacadeItem.Painted(),
                    ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(EIOItems.CONDUIT_FACADE.get(),"_overlay")),
                    ItemModelUtils.plainModel(ModelLocationUtils.getModelLocation(item)))));
    }

    public void fluidTank(ItemModelGenerators itemModelGenerators, Block block) {
        String name = name(block);
        Identifier tank =  EnderIO.id(String.format("block/%s_body", name));

        itemModelGenerators.itemModelOutput.accept(block.asItem(),
            ItemModelUtils.composite(ItemModelUtils.plainModel(tank),
                ItemModelUtils.specialModel(tank, new FluidTankItemRenderer.Unbaked())));
    }

    protected void registerModels() {
//TODO
        // Conduit facades
//        getBuilder(EIOItems.CONDUIT_FACADE.getId().toString())
//            .customLoader(FacadeItemModelBuilder::begin)
//            .model(EIOItems.CONDUIT_FACADE.getId().getPath());
//
//        getBuilder(EIOItems.TRANSPARENT_CONDUIT_FACADE.getId().toString())
//            .customLoader(FacadeItemModelBuilder::begin)
//            .model(EIOItems.TRANSPARENT_CONDUIT_FACADE.getId().getPath());
//
//        getBuilder(EIOItems.HARDENED_CONDUIT_FACADE.getId().toString())
//            .customLoader(FacadeItemModelBuilder::begin)
//            .model(EIOItems.HARDENED_CONDUIT_FACADE.getId().getPath());
//
//        getBuilder(EIOItems.TRANSPARENT_HARDENED_CONDUIT_FACADE.getId().toString())
//            .customLoader(FacadeItemModelBuilder::begin)
//            .model(EIOItems.TRANSPARENT_HARDENED_CONDUIT_FACADE.getId().getPath());

        // endregion
    }

    private void createFakeBlock(BlockModelGenerators blockModelGenerators, Item item) {
        Identifier Identifier = ModelTemplates.CUBE_ALL.create(item, TextureMapping.cube(TextureMapping.getItemTexture(item)), blockModelGenerators.modelOutput);
        blockModelGenerators.registerSimpleItemModel(item, Identifier);
    }

    private void createFakeBlock(BlockModelGenerators blockModelGenerators, Item item, Identifier identifier) {
        Identifier Identifier = ModelTemplates.CUBE_ALL.create(item, TextureMapping.cube(new Material(identifier)), blockModelGenerators.modelOutput);
        blockModelGenerators.registerSimpleItemModel(item, Identifier);
    }

    public void bucketItem(ItemModelGenerators itemModelGenerators, BucketItem item, Fluid fluid, boolean flipGas, boolean applyFluidLuminosity) {
        Material drip = new Material(Identifier.fromNamespaceAndPath(NeoForgeMod.MOD_ID, "item/mask/bucket_fluid_drip"));
        Material bucket = new Material(Identifier.withDefaultNamespace("item/bucket"));
        DynamicFluidContainerModel.Textures textures = new DynamicFluidContainerModel.Textures(Optional.empty(), Optional.of(bucket), Optional.of(drip), Optional.empty());
        itemModelGenerators.itemModelOutput.accept(item, new DynamicFluidContainerModel.Unbaked(textures, fluid, flipGas, false, applyFluidLuminosity));
    }


    public void generateEnderios(ItemModelGenerators itemModelGenerators, Item item) {
        ItemModel.Unbaked plain = ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(item, ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked soiredne = ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(item, "_inverted",ModelTemplates.FLAT_ITEM));
        itemModelGenerators.itemModelOutput.accept(item, ItemModelUtils.conditional(new EnderiosItem.SoiredneItemProperty(), soiredne, plain));
    }

    public void generateSoulVial(ItemModelGenerators itemModelGenerators, Item item) {
        ItemModel.Unbaked plain = ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(item, ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked filled = ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(item, "_filled", ModelTemplates.FLAT_ITEM));
        itemModelGenerators.itemModelOutput.accept(item, ItemModelUtils.conditional(new IsSoulBoundItemProperty(), filled, plain));
    }

    public void generateProbe(ItemModelGenerators itemModelGenerators, Item item) {
        ItemModel.Unbaked probe = ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(item, "_probe", ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked copy = ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(item, "_copy", ModelTemplates.FLAT_ITEM));
        itemModelGenerators.itemModelOutput.accept(item, ItemModelUtils.conditional(new ConduitProbeItem.ProbeModeItemProperty(), probe, copy));
    }

    private Identifier key(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    private String name(Block block) {
        return this.key(block).getPath();
    }
}
