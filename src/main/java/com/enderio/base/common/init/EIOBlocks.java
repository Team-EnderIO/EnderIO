package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.common.block.ColdFireBlock;
import com.enderio.base.common.block.DarkSteelLadderBlock;
import com.enderio.base.common.block.EIOPressurePlateBlock;
import com.enderio.base.common.block.EnderSkullBlock;
import com.enderio.base.common.block.IndustrialInsulationBlock;
import com.enderio.base.common.block.ReinforcedObsidianBlock;
import com.enderio.base.common.block.ResettingLeverBlock;
import com.enderio.base.common.block.SilentPressurePlateBlock;
import com.enderio.base.common.block.SilentWeightedPressurePlateBlock;
import com.enderio.base.common.block.WallEnderSkullBlock;
import com.enderio.base.common.block.glass.GlassBlocks;
import com.enderio.base.common.block.glass.GlassCollisionPredicate;
import com.enderio.base.common.block.glass.GlassIdentifier;
import com.enderio.base.common.block.glass.GlassLighting;
import com.enderio.base.common.block.light.Light;
import com.enderio.base.common.block.light.LightNode;
import com.enderio.base.common.block.light.PoweredLight;
import com.enderio.base.common.block.painted.PaintedCraftingTableBlock;
import com.enderio.base.common.block.painted.PaintedFenceBlock;
import com.enderio.base.common.block.painted.PaintedFenceGateBlock;
import com.enderio.base.common.block.painted.PaintedRedstoneBlock;
import com.enderio.base.common.block.painted.PaintedSandBlock;
import com.enderio.base.common.block.painted.PaintedSlabBlock;
import com.enderio.base.common.block.painted.PaintedStairBlock;
import com.enderio.base.common.block.painted.PaintedTrapDoorBlock;
import com.enderio.base.common.block.painted.PaintedWoodenPressurePlateBlock;
import com.enderio.base.common.block.painted.SinglePaintedBlock;
import com.enderio.base.common.item.PaintedBlockItem;
import com.enderio.base.common.item.PaintedSlabBlockItem;
import com.enderio.base.common.item.misc.EnderSkullBlockItem;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.data.loot.DecorLootTable;
import com.enderio.base.data.model.block.EIOBlockState;
import com.enderio.core.common.registry.EnderBlockRegistry;
import com.enderio.core.common.registry.EnderDeferredBlock;
import com.enderio.core.common.registry.EnderItemRegistry;
import com.enderio.core.data.loot.EnderBlockLootProvider;
import com.enderio.core.data.model.EnderItemModelProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class EIOBlocks {

    public static final EnderBlockRegistry BLOCKS = EnderBlockRegistry.createRegistry(EnderIO.MODID);
    //Not used, as this is done automatically by EnderBlockRegistry. In case you register without the helpers, use this.
    public static final EnderItemRegistry ITEMS = BLOCKS.getItemRegistry();

    // region Alloy Blocks

    public static final EnderDeferredBlock<? extends Block> COPPER_ALLOY_BLOCK = metalBlock("copper_alloy_block", EIOTags.Blocks.BLOCKS_COPPER_ALLOY,
        EIOTags.Items.BLOCKS_COPPER_ALLOY);
    public static final EnderDeferredBlock<? extends Block> ENERGETIC_ALLOY_BLOCK = metalBlock("energetic_alloy_block", EIOTags.Blocks.BLOCKS_ENERGETIC_ALLOY,
        EIOTags.Items.BLOCKS_ENERGETIC_ALLOY);
    public static final EnderDeferredBlock<? extends Block> VIBRANT_ALLOY_BLOCK = metalBlock("vibrant_alloy_block", EIOTags.Blocks.BLOCKS_VIBRANT_ALLOY,
        EIOTags.Items.BLOCKS_VIBRANT_ALLOY);
    public static final EnderDeferredBlock<? extends Block> REDSTONE_ALLOY_BLOCK = metalBlock("redstone_alloy_block", EIOTags.Blocks.BLOCKS_REDSTONE_ALLOY,
        EIOTags.Items.BLOCKS_REDSTONE_ALLOY);
    public static final EnderDeferredBlock<? extends Block> CONDUCTIVE_ALLOY_BLOCK = metalBlock("conductive_alloy_block", EIOTags.Blocks.BLOCKS_CONDUCTIVE_ALLOY,
        EIOTags.Items.BLOCKS_CONDUCTIVE_ALLOY);
    public static final EnderDeferredBlock<? extends Block> PULSATING_ALLOY_BLOCK = metalBlock("pulsating_alloy_block", EIOTags.Blocks.BLOCKS_PULSATING_ALLOY,
        EIOTags.Items.BLOCKS_PULSATING_ALLOY);
    public static final EnderDeferredBlock<? extends Block> DARK_STEEL_BLOCK = metalBlock("dark_steel_block", EIOTags.Blocks.BLOCKS_DARK_STEEL,
        EIOTags.Items.BLOCKS_DARK_STEEL);
    public static final EnderDeferredBlock<? extends Block> SOULARIUM_BLOCK = metalBlock("soularium_block", EIOTags.Blocks.BLOCKS_SOULARIUM,
        EIOTags.Items.BLOCKS_SOULARIUM);
    public static final EnderDeferredBlock<? extends Block> END_STEEL_BLOCK = metalBlock("end_steel_block", EIOTags.Blocks.BLOCKS_END_STEEL,
        EIOTags.Items.BLOCKS_END_STEEL);

    // endregion

    // region Chassis

    // Iron tier
    public static final EnderDeferredBlock<? extends Block> VOID_CHASSIS = chassisBlock("void_chassis");

    // Void chassis + some kind of dragons breath derrived process
    //    public static final BlockEntry<Block> REKINDLED_VOID_CHASSIS = chassisBlock("rekindled_void_chassis").register();

    // Soularium + soul/nether
    public static final EnderDeferredBlock<? extends Block> ENSOULED_CHASSIS = chassisBlock("ensouled_chassis");

    // Ensnared + Some kind of other material
    // This is for machines that require a bound soul
    //    public static final BlockEntry<Block> TRAPPED_CHASSIS = chassisBlock("trapped_chassis").register();

    // Dark steel + sculk
    //    public static final BlockEntry<Block> SCULK_CHASSIS = chassisBlock("sculk_chassis").register();

    // endregion

    // endregion

    // region Dark Steel Building Blocks

    public static final EnderDeferredBlock<DarkSteelLadderBlock> DARK_STEEL_LADDER = BLOCKS
        .register("dark_steel_ladder", () -> new DarkSteelLadderBlock(BlockBehaviour.Properties.of().strength(0.4f).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.METAL).noOcclusion()))
        .setBlockStateProvider((blockStateProvider, block) -> blockStateProvider.horizontalBlock(block, blockStateProvider
            .models()
            .withExistingParent("dark_steel_ladder", blockStateProvider.mcLoc("block/ladder"))
            .renderType(blockStateProvider.mcLoc("cutout_mipped"))
            .texture("particle", blockStateProvider.blockTexture(block))
            .texture("texture", blockStateProvider.blockTexture(block))))
        .addBlockTags(BlockTags.CLIMBABLE, BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .setLootTable(EnderBlockLootProvider::dropSelf)
        .createBlockItem()
        .setModelProvider(EnderItemModelProvider::basicBlock)
        .setTab(EIOCreativeTabs.BLOCKS)
        .finishBlockItem();

    public static final EnderDeferredBlock<IronBarsBlock> DARK_STEEL_BARS = BLOCKS
        .register("dark_steel_bars", () -> new IronBarsBlock(BlockBehaviour.Properties.of().strength(5.0f, 1000.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion()))
        .setBlockStateProvider(EIOBlockState::paneBlock)
        .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .setLootTable(EnderBlockLootProvider::dropSelf)
        .createBlockItem()
        .setTab(EIOCreativeTabs.BLOCKS)
        .setModelProvider(EnderItemModelProvider::basicBlock)
        .finishBlockItem();

    public static final EnderDeferredBlock<DoorBlock> DARK_STEEL_DOOR = BLOCKS
        .register("dark_steel_door", () -> new DoorBlock(BlockBehaviour.Properties.of().strength(5.0f, 2000.0f).sound(SoundType.METAL).mapColor(MapColor.METAL).noOcclusion(), BlockSetType.IRON))
        .setLootTable(EnderBlockLootProvider::createDoor)
        .setBlockStateProvider((blockStateProvider, doorBlock) -> blockStateProvider.doorBlockWithRenderType(doorBlock, blockStateProvider.mcLoc("block/dark_steel_door_bottom"), blockStateProvider.mcLoc("block/dark_steel_door_top"), blockStateProvider.mcLoc("cutout")))
        .addBlockTags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, BlockTags.DOORS)
        .createBlockItem()
        .setModelProvider(ItemModelProvider::basicItem)
        .setTab(EIOCreativeTabs.BLOCKS)
        .finishBlockItem();

    public static final EnderDeferredBlock<TrapDoorBlock> DARK_STEEL_TRAPDOOR = BLOCKS
        .register("dark_steel_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.of().strength(5.0f, 2000.0f).sound(SoundType.METAL).mapColor(MapColor.METAL).noOcclusion(), BlockSetType.IRON))
        .addBlockTags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL, BlockTags.TRAPDOORS)
        .setBlockStateProvider((blockStateProvider, trapDoorBlock) -> blockStateProvider.trapdoorBlockWithRenderType(trapDoorBlock, blockStateProvider.mcLoc("block/dark_steel_trapdoor"), true, blockStateProvider.mcLoc("cutout")))
        .setLootTable(EnderBlockLootProvider::dropSelf)
        .createBlockItem()
        .setModelProvider((enderItemModelProvider, item) -> enderItemModelProvider.withExistingParent("dark_steel_trapdoor", enderItemModelProvider.modLoc("block/dark_steel_trapdoor_bottom")))
        .setTab(EIOCreativeTabs.BLOCKS)
        .finishBlockItem();

    public static final EnderDeferredBlock<IronBarsBlock> END_STEEL_BARS = BLOCKS
        .register("end_steel_bars", () -> new IronBarsBlock(BlockBehaviour.Properties.of().strength(5.0f, 1000.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion()))
        .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .setLootTable(EnderBlockLootProvider::dropSelf)
        .setBlockStateProvider(EIOBlockState::paneBlock)
        .createBlockItem()
        .setModelProvider(EnderItemModelProvider::basicBlock)
        .setTab(EIOCreativeTabs.BLOCKS)
        .finishBlockItem();

    public static final EnderDeferredBlock<ReinforcedObsidianBlock> REINFORCED_OBSIDIAN = BLOCKS
        .register("reinforced_obsidian_block", () -> new ReinforcedObsidianBlock(BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(50, 2000).requiresCorrectToolForDrops().mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASEDRUM)))
        .addBlockTags(BlockTags.WITHER_IMMUNE, BlockTags.NEEDS_DIAMOND_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .setLootTable(EnderBlockLootProvider::dropSelf)
        .setBlockStateProvider(BlockStateProvider::simpleBlock)
        .createBlockItem()
        .setTab(EIOCreativeTabs.BLOCKS)
        .finishBlockItem();

    // endregion

    // region Fused Quartz/Glass

    public static final Map<GlassIdentifier, GlassBlocks> GLASS_BLOCKS = fillGlassMap();

    private static Map<GlassIdentifier, GlassBlocks> fillGlassMap() {
        Map<GlassIdentifier, GlassBlocks> map = new HashMap<>();
        for (GlassLighting lighting : GlassLighting.values()) {
            for (GlassCollisionPredicate collisionPredicate : GlassCollisionPredicate.values()) {
                for (Boolean isFused : new boolean[] { false, true }) {
                    GlassIdentifier identifier = new GlassIdentifier(lighting, collisionPredicate, isFused);
                    map.put(identifier, new GlassBlocks(BLOCKS, identifier));
                }
            }
        }
        return map;
    }

    // endregion

    // region Miscellaneous

    public static final EnderDeferredBlock<ChainBlock> SOUL_CHAIN = BLOCKS
        .register("soul_chain", () -> new ChainBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.CHAIN).noOcclusion().sound(SoundType.METAL).mapColor(MapColor.NONE)))
        .addBlockTags(BlockTags.NEEDS_IRON_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
        .setLootTable(EnderBlockLootProvider::dropSelf)
        .setBlockStateProvider((prov, item) -> {
            var model = prov
                .models()
                .withExistingParent("soul_chain", prov.mcLoc("block/chain"))
                .renderType(prov.mcLoc("cutout_mipped"))
                .texture("particle", prov.blockTexture(item))
                .texture("all", prov.blockTexture(item));

            prov.axisBlock(item, model, model);
        })
        .createBlockItem()
        .setModelProvider(EnderItemModelProvider::basicItem)
        .setTab(EIOCreativeTabs.BLOCKS)
        .finishBlockItem();

    public static final EnderDeferredBlock<ColdFireBlock> COLD_FIRE = BLOCKS
        .register("cold_fire", () -> new ColdFireBlock(BlockBehaviour.Properties.copy(Blocks.FIRE).noLootTable()))
        .setBlockStateProvider((prov, block) -> {
            // This generates the models used for the blockstate in our resources.
            // One day we may bother to datagen that file.
            String[] toCopy = { "fire_floor0", "fire_floor1", "fire_side0", "fire_side1", "fire_side_alt0", "fire_side_alt1", "fire_up0", "fire_up1",
                "fire_up_alt0", "fire_up_alt1" };

            for (String name : toCopy) {
                prov.models().withExistingParent(name, prov.mcLoc(name)).renderType("cutout");
            }
        });


    // endregion

    // region Pressure Plates

    public static final DeferredBlock<EIOPressurePlateBlock> DARK_STEEL_PRESSURE_PLATE = pressurePlateBlock("dark_steel_pressure_plate",
        EnderIO.loc("block/dark_steel_pressure_plate"), EIOPressurePlateBlock.PLAYER, false);

    public static final DeferredBlock<EIOPressurePlateBlock> SILENT_DARK_STEEL_PRESSURE_PLATE = pressurePlateBlock("silent_dark_steel_pressure_plate",
        EnderIO.loc("block/dark_steel_pressure_plate"), EIOPressurePlateBlock.PLAYER, true);

    public static final DeferredBlock<EIOPressurePlateBlock> SOULARIUM_PRESSURE_PLATE = pressurePlateBlock("soularium_pressure_plate",
        EnderIO.loc("block/soularium_pressure_plate"), EIOPressurePlateBlock.HOSTILE_MOB, false);

    public static final DeferredBlock<EIOPressurePlateBlock> SILENT_SOULARIUM_PRESSURE_PLATE = pressurePlateBlock("silent_soularium_pressure_plate",
        EnderIO.loc("block/soularium_pressure_plate"), EIOPressurePlateBlock.HOSTILE_MOB, true);

    public static final DeferredBlock<SilentPressurePlateBlock> SILENT_OAK_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.OAK_PRESSURE_PLATE);

    public static final DeferredBlock<SilentPressurePlateBlock> SILENT_ACACIA_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.ACACIA_PRESSURE_PLATE);

    public static final DeferredBlock<SilentPressurePlateBlock> SILENT_DARK_OAK_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.DARK_OAK_PRESSURE_PLATE);

    public static final DeferredBlock<SilentPressurePlateBlock> SILENT_SPRUCE_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.SPRUCE_PRESSURE_PLATE);

    public static final DeferredBlock<SilentPressurePlateBlock> SILENT_BIRCH_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.BIRCH_PRESSURE_PLATE);

    public static final DeferredBlock<SilentPressurePlateBlock> SILENT_JUNGLE_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.JUNGLE_PRESSURE_PLATE);

    public static final DeferredBlock<SilentPressurePlateBlock> SILENT_CRIMSON_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.CRIMSON_PRESSURE_PLATE);

    public static final DeferredBlock<SilentPressurePlateBlock> SILENT_WARPED_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.WARPED_PRESSURE_PLATE);

    public static final DeferredBlock<SilentPressurePlateBlock> SILENT_STONE_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.STONE_PRESSURE_PLATE);

    public static final DeferredBlock<SilentPressurePlateBlock> SILENT_POLISHED_BLACKSTONE_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE);

    public static final DeferredBlock<SilentWeightedPressurePlateBlock> SILENT_HEAVY_WEIGHTED_PRESSURE_PLATE = silentWeightedPressurePlateBlock(
        (WeightedPressurePlateBlock) Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);

    public static final DeferredBlock<SilentWeightedPressurePlateBlock> SILENT_LIGHT_WEIGHTED_PRESSURE_PLATE = silentWeightedPressurePlateBlock(
        (WeightedPressurePlateBlock) Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);

    // endregion

    // region resetting levers

    public static final DeferredBlock<ResettingLeverBlock> RESETTING_LEVER_FIVE = resettingLeverBlock("resetting_lever_five", 5, false);

    public static final DeferredBlock<ResettingLeverBlock> RESETTING_LEVER_FIVE_INV = resettingLeverBlock("resetting_lever_five_inv", 5, true);

    public static final DeferredBlock<ResettingLeverBlock> RESETTING_LEVER_TEN = resettingLeverBlock("resetting_lever_ten", 10, false);

    public static final DeferredBlock<ResettingLeverBlock> RESETTING_LEVER_TEN_INV = resettingLeverBlock("resetting_lever_ten_inv", 10, true);

    public static final DeferredBlock<ResettingLeverBlock> RESETTING_LEVER_THIRTY = resettingLeverBlock("resetting_lever_thirty", 30, false);

    public static final DeferredBlock<ResettingLeverBlock> RESETTING_LEVER_THIRTY_INV = resettingLeverBlock("resetting_lever_thirty_inv", 30, true);

    public static final DeferredBlock<ResettingLeverBlock> RESETTING_LEVER_SIXTY = resettingLeverBlock("resetting_lever_sixty", 60, false);

    public static final DeferredBlock<ResettingLeverBlock> RESETTING_LEVER_SIXTY_INV = resettingLeverBlock("resetting_lever_sixty_inv", 60, true);

    public static final DeferredBlock<ResettingLeverBlock> RESETTING_LEVER_THREE_HUNDRED = resettingLeverBlock("resetting_lever_three_hundred", 300, false);

    public static final DeferredBlock<ResettingLeverBlock> RESETTING_LEVER_THREE_HUNDRED_INV = resettingLeverBlock("resetting_lever_three_hundred_inv", 300, true);

    // endregion

    private static final List<Supplier<? extends Block>> PAINTED = new ArrayList<>();

    public static final EnderDeferredBlock<PaintedFenceBlock> PAINTED_FENCE = paintedBlock("painted_fence", PaintedFenceBlock::new, Blocks.OAK_FENCE,
        BlockTags.WOODEN_FENCES, BlockTags.MINEABLE_WITH_AXE);

    public static final EnderDeferredBlock<PaintedFenceGateBlock> PAINTED_FENCE_GATE = paintedBlock("painted_fence_gate", PaintedFenceGateBlock::new,
        Blocks.OAK_FENCE_GATE, BlockTags.FENCE_GATES, BlockTags.MINEABLE_WITH_AXE);

    public static final EnderDeferredBlock<PaintedSandBlock> PAINTED_SAND = paintedBlock("painted_sand", PaintedSandBlock::new, Blocks.SAND, BlockTags.SAND,
        BlockTags.MINEABLE_WITH_SHOVEL);

    public static final EnderDeferredBlock<PaintedStairBlock> PAINTED_STAIRS = paintedBlock("painted_stairs", PaintedStairBlock::new, Blocks.OAK_STAIRS, Direction.WEST,
        BlockTags.WOODEN_STAIRS, BlockTags.MINEABLE_WITH_AXE);

    public static final EnderDeferredBlock<PaintedCraftingTableBlock> PAINTED_CRAFTING_TABLE = paintedBlock("painted_crafting_table", PaintedCraftingTableBlock::new,
        Blocks.CRAFTING_TABLE, BlockTags.MINEABLE_WITH_AXE);

    public static final EnderDeferredBlock<PaintedRedstoneBlock> PAINTED_REDSTONE_BLOCK = paintedBlock("painted_redstone_block", PaintedRedstoneBlock::new,
        Blocks.REDSTONE_BLOCK, BlockTags.MINEABLE_WITH_PICKAXE);

    public static final EnderDeferredBlock<PaintedTrapDoorBlock> PAINTED_TRAPDOOR = paintedBlock("painted_trapdoor", PaintedTrapDoorBlock::new, Blocks.OAK_TRAPDOOR,
        BlockTags.WOODEN_TRAPDOORS, BlockTags.MINEABLE_WITH_AXE);

    public static final EnderDeferredBlock<PaintedWoodenPressurePlateBlock> PAINTED_WOODEN_PRESSURE_PLATE = paintedBlock("painted_wooden_pressure_plate",
        PaintedWoodenPressurePlateBlock::new, Blocks.OAK_PRESSURE_PLATE, BlockTags.WOODEN_PRESSURE_PLATES, BlockTags.MINEABLE_WITH_AXE);

    public static final EnderDeferredBlock<PaintedSlabBlock> PAINTED_SLAB = paintedBlock("painted_slab", PaintedSlabBlock::new, PaintedSlabBlockItem::new,
        Blocks.OAK_SLAB, BlockTags.WOODEN_SLABS, BlockTags.MINEABLE_WITH_AXE).setLootTable(DecorLootTable::paintedSlab);

    public static final EnderDeferredBlock<SinglePaintedBlock> PAINTED_GLOWSTONE = paintedBlock("painted_glowstone", SinglePaintedBlock::new,
        Blocks.GLOWSTONE);

    // endregion

    // region Light

    public static final EnderDeferredBlock<Light> LIGHT = lightBlock("light", s -> new Light(s, false));
    public static final EnderDeferredBlock<Light> LIGHT_INVERTED = lightBlock("light_inverted", s -> new Light(s, true));
    public static final EnderDeferredBlock<PoweredLight> POWERED_LIGHT = lightBlock("powered_light", s -> new PoweredLight(s, false, false));
    public static final EnderDeferredBlock<PoweredLight> POWERED_LIGHT_INVERTED = lightBlock("powered_light_inverted", s -> new PoweredLight(s, true, false));
    public static final EnderDeferredBlock<PoweredLight> POWERED_LIGHT_WIRELESS = lightBlock("powered_light_wireless", s -> new PoweredLight(s, false, true));
    public static final EnderDeferredBlock<PoweredLight> POWERED_LIGHT_INVERTED_WIRELESS = lightBlock("powered_light_inverted_wireless",
        s -> new PoweredLight(s, true, true));

    public static final EnderDeferredBlock<LightNode> LIGHT_NODE = BLOCKS
        .register("light_node", () -> new LightNode(BlockBehaviour.Properties.copy(Blocks.AIR).lightLevel(l -> 15).noLootTable().noCollission().noOcclusion()))
        .setBlockStateProvider((prov, block) -> prov.simpleBlock(block, prov.models().withExistingParent("light_node", "block/air")));

    public static final EnderDeferredBlock<EnderSkullBlock> ENDERMAN_HEAD = BLOCKS
        .register("enderman_head", () -> new EnderSkullBlock(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY)))
        .setLootTable(EnderBlockLootProvider::dropSelf)
        .setBlockStateProvider((prov, block) -> prov.simpleBlock(block, prov.models().getExistingFile(prov.mcLoc("block/skull"))))
        .createBlockItem((enderSkullBlock) -> new EnderSkullBlockItem(enderSkullBlock, new Item.Properties(), Direction.DOWN))
        .setTab(EIOCreativeTabs.MAIN)
        .setModelProvider((prov, block) -> prov.withExistingParent("enderman_head", "item/template_skull"))
        .finishBlockItem();

    public static final EnderDeferredBlock<WallEnderSkullBlock> WALL_ENDERMAN_HEAD = BLOCKS
        .register("wall_enderman_head", () -> new WallEnderSkullBlock(BlockBehaviour.Properties.of().strength(1.0F).lootFrom(ENDERMAN_HEAD).pushReaction(PushReaction.DESTROY)))
        .setBlockStateProvider((prov, block) -> prov.simpleBlock(block, prov.models().getExistingFile(prov.mcLoc("block/skull"))));

    private static EnderDeferredBlock<? extends Block> metalBlock(String name, TagKey<Block> blockTag, TagKey<Item> itemTag) {
        return BLOCKS.registerBlock(name, BlockBehaviour.Properties.of().sound(SoundType.METAL).mapColor(MapColor.METAL).strength(5, 6).requiresCorrectToolForDrops())
            .addBlockTags(BlockTags.NEEDS_STONE_TOOL, BlockTags.MINEABLE_WITH_PICKAXE, blockTag)
            .setLootTable(EnderBlockLootProvider::dropSelf)
            .createBlockItem()
            .setTab(EIOCreativeTabs.BLOCKS)
            .addBlockItemTags(itemTag)
            .finishBlockItem();
    }

    private static EnderDeferredBlock<? extends Block> chassisBlock(String name) {
        return BLOCKS.registerBlock(name, BlockBehaviour.Properties.of().noOcclusion().sound(SoundType.METAL).mapColor(MapColor.METAL).strength(5, 6))
            .setBlockStateProvider((blockStateProvider, block) ->
                blockStateProvider.simpleBlock(block, blockStateProvider.models().cubeAll(name, blockStateProvider.blockTexture(block)).renderType("translucent")))
            .addBlockTags(BlockTags.NEEDS_STONE_TOOL, BlockTags.MINEABLE_WITH_PICKAXE)
            .setLootTable(EnderBlockLootProvider::dropSelf)
            .createBlockItem()
            .setTab(EIOCreativeTabs.BLOCKS)
            .finishBlockItem();
    }

    private static DeferredBlock<EIOPressurePlateBlock> pressurePlateBlock(String name, ResourceLocation texture, EIOPressurePlateBlock.Detector type,
        boolean silent) {
        return BLOCKS.register(name, () -> new EIOPressurePlateBlock(BlockBehaviour.Properties.of().strength(5, 6).mapColor(MapColor.METAL), type, silent))
            .setBlockStateProvider((blockStateProvider, block) -> {
                BlockModelProvider modProv = blockStateProvider.models();
                ModelFile dm = modProv.withExistingParent(name + "_down", blockStateProvider.mcLoc("block/pressure_plate_down")).texture("texture", texture);
                ModelFile um = modProv.withExistingParent(name, blockStateProvider.mcLoc("block/pressure_plate_up")).texture("texture", texture);

                VariantBlockStateBuilder vb = blockStateProvider.getVariantBuilder(block);
                vb.partialState().with(PressurePlateBlock.POWERED, true).addModels(new ConfiguredModel(dm));
                vb.partialState().with(PressurePlateBlock.POWERED, false).addModels(new ConfiguredModel(um));
            })
            .addBlockTags(BlockTags.NEEDS_STONE_TOOL, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.PRESSURE_PLATES)
            .setLootTable(EnderBlockLootProvider::dropSelf)
            .createBlockItem()
            .setTab(EIOCreativeTabs.BLOCKS)
            .finishBlockItem();
    }

    private static EnderDeferredBlock<SilentPressurePlateBlock> silentPressurePlateBlock(final PressurePlateBlock block) {
        ResourceLocation upModelLoc = Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block));
        ResourceLocation downModelLoc = new ResourceLocation(upModelLoc.getNamespace(), upModelLoc.getPath() + "_down");
        return BLOCKS.register("silent_" + upModelLoc.getPath(), () -> new SilentPressurePlateBlock(block))
            .addBlockTags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.PRESSURE_PLATES)
            .setBlockStateProvider((blockStateProvider, block1) -> {
                VariantBlockStateBuilder vb = blockStateProvider.getVariantBuilder(block1);
                vb.partialState().with(PressurePlateBlock.POWERED, true).addModels(new ConfiguredModel(blockStateProvider.models().getExistingFile(downModelLoc)));
                vb.partialState().with(PressurePlateBlock.POWERED, false).addModels(new ConfiguredModel(blockStateProvider.models().getExistingFile(upModelLoc)));
            })
            .setLootTable(EnderBlockLootProvider::dropSelf)
            .createBlockItem()
            .setModelProvider((modelProvider, item) -> modelProvider.withExistingParent("silent_" + upModelLoc.getPath(), upModelLoc))
            .setTab(EIOCreativeTabs.BLOCKS)
            .finishBlockItem();
    }

    private static EnderDeferredBlock<SilentWeightedPressurePlateBlock> silentWeightedPressurePlateBlock(WeightedPressurePlateBlock block) {
        ResourceLocation upModelLoc = Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block));
        ResourceLocation downModelLoc = new ResourceLocation(upModelLoc.getNamespace(), upModelLoc.getPath() + "_down");
        return BLOCKS.register("silent_" + upModelLoc.getPath(), () -> new SilentWeightedPressurePlateBlock(block))
            .setLootTable(EnderBlockLootProvider::dropSelf)
            .addBlockTags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.PRESSURE_PLATES)
            .setBlockStateProvider((prov, plateBlock) -> prov.getVariantBuilder(plateBlock).forAllStates(blockState -> {
                if (blockState.getValue(WeightedPressurePlateBlock.POWER) == 0) {
                    return new ConfiguredModel[] { new ConfiguredModel(prov.models().getExistingFile(upModelLoc)) };
                }
                return new ConfiguredModel[] { new ConfiguredModel(prov.models().getExistingFile(downModelLoc)) };
            }))
            .createBlockItem()
            .setModelProvider((prov, item) -> prov.withExistingParent("silent_" + upModelLoc.getPath(), upModelLoc))
            .setTab(EIOCreativeTabs.BLOCKS)
            .finishBlockItem();
    }

    private static EnderDeferredBlock<ResettingLeverBlock> resettingLeverBlock(String name, int duration, boolean inverted) {
        String durLab = "(" + (duration >= 60 ? duration / 60 : duration) + " " + (duration == 60 ? "minute" : duration > 60 ? "minutes" : "seconds") + ")";
        return BLOCKS.register(name, () -> new ResettingLeverBlock(duration, inverted))
            .setTranslation("Resetting Lever " + (inverted ? "Inverted " : "") + durLab)
            .setBlockStateProvider((prov, block) -> {

                BlockModelProvider modProv = prov.models();
                ModelFile.ExistingModelFile baseModel = modProv.getExistingFile(prov.mcLoc("block/lever"));
                ModelFile.ExistingModelFile onModel = modProv.getExistingFile(prov.mcLoc("block/lever_on"));

                VariantBlockStateBuilder vb = prov.getVariantBuilder(block);

                vb.forAllStates(blockState -> {
                    ModelFile.ExistingModelFile model = blockState.getValue(ResettingLeverBlock.POWERED) ? onModel : baseModel;
                    int rotationX =
                        blockState.getValue(LeverBlock.FACE) == AttachFace.CEILING ? 180 : blockState.getValue(LeverBlock.FACE) == AttachFace.WALL ? 90 : 0;
                    Direction f = blockState.getValue(LeverBlock.FACING);
                    int rotationY = f.get2DDataValue() * 90;
                    if (blockState.getValue(LeverBlock.FACE) != AttachFace.CEILING) {
                        rotationY = (rotationY + 180) % 360;
                    }
                    return new ConfiguredModel[] { new ConfiguredModel(model, rotationX, rotationY, false) };
                });
            })
            .createBlockItem()
            .setModelProvider((prov, item) -> prov.withExistingParent(name, prov.mcLoc("item/lever")))
            .setTab(EIOCreativeTabs.BLOCKS)
            .finishBlockItem();
    }

    public static final EnderDeferredBlock<IndustrialInsulationBlock> INDUSTRIAL_INSULATION = BLOCKS
        .register("industrial_insulation_block", () -> new IndustrialInsulationBlock(BlockBehaviour.Properties.copy(Blocks.SPONGE)))
        .setTranslation("Industrial Insulation")
        .setLootTable(EnderBlockLootProvider::dropSelf)
        .createBlockItem()
        .setTab(EIOCreativeTabs.BLOCKS)
        .finishBlockItem();

    @SafeVarargs
    private static <T extends Block> EnderDeferredBlock<T> paintedBlock(String name, Function<BlockBehaviour.Properties, T> blockFactory,
        Block copyFrom, TagKey<Block>... tags) {
        return paintedBlock(name, blockFactory, copyFrom, null, tags);
    }

    @SafeVarargs
    private static <T extends Block> EnderDeferredBlock<T> paintedBlock(String name, Function<BlockBehaviour.Properties, T> blockFactory,
        Block copyFrom, @Nullable Direction itemTextureRotation, TagKey<Block>... tags) {
        return paintedBlock(name, blockFactory, PaintedBlockItem::new, copyFrom, itemTextureRotation, tags);
    }

    @SafeVarargs
    private static <T extends Block> EnderDeferredBlock<T> paintedBlock(String name, Function<BlockBehaviour.Properties, T> blockFactory,
        BiFunction<? super T, Item.Properties, ? extends BlockItem> itemFactory, Block copyFrom, TagKey<Block>... tags) {
        return paintedBlock(name, blockFactory, itemFactory, copyFrom, null, tags);
    }

    @SafeVarargs
    private static <T extends Block> EnderDeferredBlock<T> paintedBlock(String name, Function<BlockBehaviour.Properties, T> blockFactory,
        BiFunction<? super T, Item.Properties, ? extends BlockItem> itemFactory, Block copyFrom, @Nullable Direction itemTextureRotation,
        TagKey<Block>... tags) {
        return BLOCKS
            .register(name, () -> blockFactory.apply(BlockBehaviour.Properties.copy(copyFrom).noOcclusion()))
            .addBlockTags(tags)
            .setBlockStateProvider((prov, block) -> EIOBlockState.paintedBlock(block, prov, copyFrom, itemTextureRotation))
            //.color(() -> PaintedBlockColor::new) //TODO
            .setLootTable(DecorLootTable::withPaint)
            .createBlockItem(t -> itemFactory.apply(t, new Item.Properties()))
            //.color(() -> PaintedBlockColor::new)
            .finishBlockItem();
    }

    public static <T extends Block> EnderDeferredBlock<T> lightBlock(String name, Function<BlockBehaviour.Properties, T> blockFactory) {
        return BLOCKS.register(name, () -> blockFactory.apply(BlockBehaviour.Properties.of().sound(SoundType.METAL).mapColor(MapColor.METAL).lightLevel(l -> {
                if (l.getValue(Light.ENABLED)) {
                    return 15;
                }
                return 0;
            })))
            .setBlockStateProvider(EIOBlockState::lightBlock)
            .createBlockItem()
            .setModelProvider((prov, item) -> prov.withExistingParent(name, "block/button_inventory"))
            .setTab(EIOCreativeTabs.BLOCKS)
            .finishBlockItem();
    }

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
