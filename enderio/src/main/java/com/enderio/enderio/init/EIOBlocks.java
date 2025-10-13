package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.cold_fire.ColdFireBlock;
import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlock;
import com.enderio.enderio.content.decor.DarkSteelLadderBlock;
import com.enderio.enderio.content.glass.GlassBlocks;
import com.enderio.enderio.content.glass.GlassCollisionPredicate;
import com.enderio.enderio.content.glass.GlassIdentifier;
import com.enderio.enderio.content.glass.GlassLighting;
import com.enderio.enderio.content.misc_blocks.EIOPressurePlateBlock;
import com.enderio.enderio.content.misc_blocks.IndustrialInsulationBlock;
import com.enderio.enderio.content.misc_blocks.ReinforcedObsidianBlock;
import com.enderio.enderio.content.misc_blocks.ResettingLeverBlock;
import com.enderio.enderio.content.misc_blocks.SilentPressurePlateBlock;
import com.enderio.enderio.content.misc_blocks.SilentWeightedPressurePlateBlock;
import com.enderio.enderio.content.misc_blocks.skull.EnderSkullBlock;
import com.enderio.enderio.content.misc_blocks.skull.EnderSkullBlockItem;
import com.enderio.enderio.content.misc_blocks.skull.WallEnderSkullBlock;
import com.enderio.enderio.content.paint.block.PaintedCraftingTableBlock;
import com.enderio.enderio.content.paint.block.PaintedFenceBlock;
import com.enderio.enderio.content.paint.block.PaintedFenceGateBlock;
import com.enderio.enderio.content.paint.block.PaintedRedstoneBlock;
import com.enderio.enderio.content.paint.block.PaintedSandBlock;
import com.enderio.enderio.content.paint.block.PaintedSlabBlock;
import com.enderio.enderio.content.paint.block.PaintedStairBlock;
import com.enderio.enderio.content.paint.block.PaintedTrapDoorBlock;
import com.enderio.enderio.content.paint.block.PaintedWallBlock;
import com.enderio.enderio.content.paint.block.PaintedWoodenPressurePlateBlock;
import com.enderio.enderio.content.paint.block.SinglePaintedBlock;
import com.enderio.enderio.content.paint.item.PaintedBlockItem;
import com.enderio.enderio.content.paint.item.PaintedSlabBlockItem;
import com.enderio.enderio.data.loot.DecorLootTable;
import com.enderio.enderio.data.model.block.EIOBlockState;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.regilite.data.RegiliteBlockLootProvider;
import com.enderio.regilite.holder.RegiliteBlock;
import com.enderio.regilite.registry.BlockRegistry;
import com.enderio.regilite.registry.ItemRegistry;
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class EIOBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(EnderIO.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EnderIO.MOD_ID);

    // ======== NEW

    // region Alloy Blocks

    public static final DeferredBlock<Block> COPPER_ALLOY_BLOCK = registerMetalBlock("copper_alloy_block");
    public static final DeferredBlock<Block> ENERGETIC_ALLOY_BLOCK = registerMetalBlock("energetic_alloy_block");
    public static final DeferredBlock<Block> VIBRANT_ALLOY_BLOCK = registerMetalBlock("vibrant_alloy_block");
    public static final DeferredBlock<Block> REDSTONE_ALLOY_BLOCK = registerMetalBlock("redstone_alloy_block");
    public static final DeferredBlock<Block> CONDUCTIVE_ALLOY_BLOCK = registerMetalBlock("conductive_alloy_block");
    public static final DeferredBlock<Block> PULSATING_ALLOY_BLOCK = registerMetalBlock("pulsating_alloy_block");
    public static final DeferredBlock<Block> DARK_STEEL_BLOCK = registerMetalBlock("dark_steel_block");
    public static final DeferredBlock<Block> SOULARIUM_BLOCK = registerMetalBlock("soularium_block");
    public static final DeferredBlock<Block> END_STEEL_BLOCK = registerMetalBlock("end_steel_block");

    private static DeferredBlock<Block> registerMetalBlock(String name) {
        return registerWithItem(name, Block::new,
            BlockBehaviour.Properties.of().sound(SoundType.METAL).mapColor(MapColor.METAL).strength(5, 6).requiresCorrectToolForDrops());
    }

    // endregion

    // region Chassis

    // Iron tier
    public static final DeferredBlock<Block> VOID_CHASSIS = registerChassisBlock("void_chassis");

    // Void chassis + some kind of dragons breath derrived process
    //    public static final RegiliteBlock<Block> REKINDLED_VOID_CHASSIS = registerChassisBlock("rekindled_void_chassis");

    // Soularium + soul/nether
    public static final DeferredBlock<Block> ENSOULED_CHASSIS = registerChassisBlock("ensouled_chassis");

    // Ensnared + Some kind of other material
    // This is for machines that require a bound soul
    //    public static final RegiliteBlock<Block> TRAPPED_CHASSIS = registerChassisBlock("trapped_chassis");

    // Dark steel + sculk
    //    public static final RegiliteBlock<Block> SCULK_CHASSIS = registerChassisBlock("sculk_chassis");

    private static DeferredBlock<Block> registerChassisBlock(String name) {
        return registerWithItem(name, Block::new, BlockBehaviour.Properties.of().noOcclusion().sound(SoundType.METAL).mapColor(MapColor.METAL).strength(5, 6));
    }

    // endregion

    // region Dark Steel Building Blocks

    public static final DeferredBlock<DarkSteelLadderBlock> DARK_STEEL_LADDER = registerWithItem("dark_steel_ladder", DarkSteelLadderBlock::new, BlockBehaviour.Properties.of().strength(0.4f).requiresCorrectToolForDrops().sound(SoundType.METAL).mapColor(MapColor.METAL).noOcclusion());

    public static final DeferredBlock<IronBarsBlock> DARK_STEEL_BARS = registerWithItem("dark_steel_bars", IronBarsBlock::new,
            BlockBehaviour.Properties.of().strength(5.0f, 1000.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<DoorBlock> DARK_STEEL_DOOR = registerWithItem("dark_steel_door", props -> new DoorBlock(BlockSetType.IRON, props),
            BlockBehaviour.Properties.of().strength(5.0f, 2000.0f).sound(SoundType.METAL).mapColor(MapColor.METAL).noOcclusion());

    public static final DeferredBlock<TrapDoorBlock> DARK_STEEL_TRAPDOOR = registerWithItem("dark_steel_trapdoor", props -> new TrapDoorBlock(BlockSetType.IRON, props),
            BlockBehaviour.Properties.of().strength(5.0f, 2000.0f).sound(SoundType.METAL).mapColor(MapColor.METAL).noOcclusion());

    public static final DeferredBlock<IronBarsBlock> END_STEEL_BARS = registerWithItem("end_steel_bars", IronBarsBlock::new,
            BlockBehaviour.Properties.of().strength(5.0f, 1000.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).noOcclusion());

    public static final DeferredBlock<ReinforcedObsidianBlock> REINFORCED_OBSIDIAN = registerWithItem("reinforced_obsidian_block", ReinforcedObsidianBlock::new,
            BlockBehaviour.Properties.of()
                .sound(SoundType.STONE)
                .strength(50, 2000)
                .requiresCorrectToolForDrops()
                .mapColor(MapColor.COLOR_BLACK)
                .instrument(NoteBlockInstrument.BASEDRUM));

    // endregion

    // region Painted Blocks

    // endregion

    // region Resetting Levers

    public static final Set<DeferredBlock<ResettingLeverBlock>> RESETTING_LEVERS = new HashSet<>();

    public static final DeferredBlock<ResettingLeverBlock> RESETTING_LEVER_FIVE = registerResettingLever("resetting_lever_five", 5, false);
    public static final DeferredBlock<ResettingLeverBlock> RESETTING_LEVER_FIVE_INV = registerResettingLever("resetting_lever_five_inv", 5, true);
    public static final DeferredBlock<ResettingLeverBlock> RESETTING_LEVER_TEN = registerResettingLever("resetting_lever_ten", 10, false);
    public static final DeferredBlock<ResettingLeverBlock> RESETTING_LEVER_TEN_INV = registerResettingLever("resetting_lever_ten_inv", 10, true);
    public static final DeferredBlock<ResettingLeverBlock> RESETTING_LEVER_THIRTY = registerResettingLever("resetting_lever_thirty", 30, false);
    public static final DeferredBlock<ResettingLeverBlock> RESETTING_LEVER_THIRTY_INV = registerResettingLever("resetting_lever_thirty_inv", 30, true);
    public static final DeferredBlock<ResettingLeverBlock> RESETTING_LEVER_SIXTY = registerResettingLever("resetting_lever_sixty", 60, false);
    public static final DeferredBlock<ResettingLeverBlock> RESETTING_LEVER_SIXTY_INV = registerResettingLever("resetting_lever_sixty_inv", 60, true);
    public static final DeferredBlock<ResettingLeverBlock> RESETTING_LEVER_THREE_HUNDRED = registerResettingLever("resetting_lever_three_hundred", 300, false);
    public static final DeferredBlock<ResettingLeverBlock> RESETTING_LEVER_THREE_HUNDRED_INV = registerResettingLever("resetting_lever_three_hundred_inv", 300, true);

    private static DeferredBlock<ResettingLeverBlock> registerResettingLever(String name, int delay, boolean inverted) {
        var blockHolder = registerWithItem(name, p -> new ResettingLeverBlock(delay, inverted), BlockBehaviour.Properties.of());
        RESETTING_LEVERS.add(blockHolder);
        return blockHolder;
    }

    // endregion

    // region Miscellaneous

    // Note: Due to the unique nature of the conduit bundle, all block items are registered in EIOItems instead.
    public static final DeferredBlock<ConduitBundleBlock> CONDUIT_BUNDLE = BLOCKS.registerBlock("conduit",
        ConduitBundleBlock::new, BlockBehaviour.Properties.of()
            .strength(1.5f, 10)
            .noLootTable()
            .noOcclusion()
            .dynamicShape()
            .mapColor(MapColor.STONE));

    public static final DeferredBlock<ChainBlock> SOUL_CHAIN = registerWithItem("soul_chain", ChainBlock::new,
        BlockBehaviour.Properties.of()
            .requiresCorrectToolForDrops()
            .strength(5.0F, 6.0F)
            .sound(SoundType.CHAIN)
            .noOcclusion()
            .mapColor(MapColor.NONE));

    public static final DeferredBlock<ColdFireBlock> COLD_FIRE = BLOCKS
        .registerBlock("cold_fire", ColdFireBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.FIRE).noLootTable());

    public static final DeferredBlock<EnderSkullBlock> ENDERMAN_HEAD = registerWithItem("enderman_head", EnderSkullBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.SKELETON_SKULL).instrument(NoteBlockInstrument.SKELETON).strength(1.0F).pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<WallEnderSkullBlock> WALL_ENDERMAN_HEAD = BLOCKS.registerBlock("wall_enderman_head", WallEnderSkullBlock::new,
        BlockBehaviour.Properties.ofFullCopy(Blocks.SKELETON_SKULL).strength(1.0F).lootFrom(ENDERMAN_HEAD).pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<IndustrialInsulationBlock> INDUSTRIAL_INSULATION = registerWithItem("industrial_insulation",
        IndustrialInsulationBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.SPONGE));

    // endregion

    private static <B extends Block> DeferredBlock<B> registerWithItem(String name, Function<BlockBehaviour.Properties, ? extends B> func, BlockBehaviour.Properties props) {
        var blockHolder = BLOCKS.<B>registerBlock(name, func, props);
        ITEMS.registerSimpleBlockItem(blockHolder);
        return blockHolder;
    }

    // ======== OLD

    private static final BlockRegistry BLOCK_REGISTRY = EnderIO.REGILITE.blockRegistry();
    private static final ItemRegistry ITEM_REGISTRY = EnderIO.REGILITE.itemRegistry();

    // endregion

    // region Fused Quartz/Glass

    public static final Map<GlassIdentifier, GlassBlocks> GLASS_BLOCKS = fillGlassMap();

    private static Map<GlassIdentifier, GlassBlocks> fillGlassMap() {
        Map<GlassIdentifier, GlassBlocks> map = new HashMap<>();
        for (GlassLighting lighting : GlassLighting.values()) {
            for (GlassCollisionPredicate collisionPredicate : GlassCollisionPredicate.values()) {
                for (Boolean isFused : new boolean[] { false, true }) {
                    GlassIdentifier identifier = new GlassIdentifier(lighting, collisionPredicate, isFused);
                    map.put(identifier, new GlassBlocks(BLOCK_REGISTRY, ITEM_REGISTRY, identifier));
                }
            }
        }
        return map;
    }

    // endregion

    // region Pressure Plates

    public static final RegiliteBlock<EIOPressurePlateBlock> DARK_STEEL_PRESSURE_PLATE = pressurePlateBlock("dark_steel_pressure_plate",
        EnderIO.rl("block/dark_steel_pressure_plate"), EIOPressurePlateBlock.PLAYER, false);

    public static final RegiliteBlock<EIOPressurePlateBlock> SILENT_DARK_STEEL_PRESSURE_PLATE = pressurePlateBlock("silent_dark_steel_pressure_plate",
        EnderIO.rl("block/dark_steel_pressure_plate"), EIOPressurePlateBlock.PLAYER, true);

    public static final RegiliteBlock<EIOPressurePlateBlock> SOULARIUM_PRESSURE_PLATE = pressurePlateBlock("soularium_pressure_plate",
        EnderIO.rl("block/soularium_pressure_plate"), EIOPressurePlateBlock.HOSTILE_MOB, false);

    public static final RegiliteBlock<EIOPressurePlateBlock> SILENT_SOULARIUM_PRESSURE_PLATE = pressurePlateBlock("silent_soularium_pressure_plate",
        EnderIO.rl("block/soularium_pressure_plate"), EIOPressurePlateBlock.HOSTILE_MOB, true);

    public static final RegiliteBlock<SilentPressurePlateBlock> SILENT_OAK_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.OAK_PRESSURE_PLATE);

    public static final RegiliteBlock<SilentPressurePlateBlock> SILENT_ACACIA_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.ACACIA_PRESSURE_PLATE);

    public static final RegiliteBlock<SilentPressurePlateBlock> SILENT_DARK_OAK_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.DARK_OAK_PRESSURE_PLATE);

    public static final RegiliteBlock<SilentPressurePlateBlock> SILENT_SPRUCE_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.SPRUCE_PRESSURE_PLATE);

    public static final RegiliteBlock<SilentPressurePlateBlock> SILENT_BIRCH_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.BIRCH_PRESSURE_PLATE);

    public static final RegiliteBlock<SilentPressurePlateBlock> SILENT_JUNGLE_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.JUNGLE_PRESSURE_PLATE);

    public static final RegiliteBlock<SilentPressurePlateBlock> SILENT_CRIMSON_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.CRIMSON_PRESSURE_PLATE);

    public static final RegiliteBlock<SilentPressurePlateBlock> SILENT_WARPED_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.WARPED_PRESSURE_PLATE);

    public static final RegiliteBlock<SilentPressurePlateBlock> SILENT_STONE_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.STONE_PRESSURE_PLATE);

    public static final RegiliteBlock<SilentPressurePlateBlock> SILENT_POLISHED_BLACKSTONE_PRESSURE_PLATE = silentPressurePlateBlock(
        (PressurePlateBlock) Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE);

    public static final RegiliteBlock<SilentWeightedPressurePlateBlock> SILENT_HEAVY_WEIGHTED_PRESSURE_PLATE = silentWeightedPressurePlateBlock(
        (WeightedPressurePlateBlock) Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);

    public static final RegiliteBlock<SilentWeightedPressurePlateBlock> SILENT_LIGHT_WEIGHTED_PRESSURE_PLATE = silentWeightedPressurePlateBlock(
        (WeightedPressurePlateBlock) Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);

    // endregion

    private static final List<Supplier<? extends Block>> PAINTED = new ArrayList<>();

    public static final RegiliteBlock<PaintedFenceBlock> PAINTED_FENCE = paintedBlock("painted_fence", PaintedFenceBlock::new, Blocks.OAK_FENCE,
        BlockTags.WOODEN_FENCES, BlockTags.MINEABLE_WITH_AXE);

    public static final RegiliteBlock<PaintedFenceGateBlock> PAINTED_FENCE_GATE = paintedBlock("painted_fence_gate", PaintedFenceGateBlock::new,
        Blocks.OAK_FENCE_GATE, BlockTags.FENCE_GATES, BlockTags.MINEABLE_WITH_AXE);

    public static final RegiliteBlock<PaintedSandBlock> PAINTED_SAND = paintedBlock("painted_sand", PaintedSandBlock::new, Blocks.SAND, BlockTags.SAND,
        BlockTags.MINEABLE_WITH_SHOVEL);

    public static final RegiliteBlock<PaintedStairBlock> PAINTED_STAIRS = paintedBlock("painted_stairs", PaintedStairBlock::new, Blocks.OAK_STAIRS, Direction.WEST,
        BlockTags.WOODEN_STAIRS, BlockTags.MINEABLE_WITH_AXE);

    public static final RegiliteBlock<PaintedCraftingTableBlock> PAINTED_CRAFTING_TABLE = paintedBlock("painted_crafting_table", PaintedCraftingTableBlock::new,
        Blocks.CRAFTING_TABLE, BlockTags.MINEABLE_WITH_AXE);

    public static final RegiliteBlock<PaintedRedstoneBlock> PAINTED_REDSTONE_BLOCK = paintedBlock("painted_redstone_block", PaintedRedstoneBlock::new,
        Blocks.REDSTONE_BLOCK, BlockTags.MINEABLE_WITH_PICKAXE);

    public static final RegiliteBlock<PaintedTrapDoorBlock> PAINTED_TRAPDOOR = paintedBlock("painted_trapdoor", PaintedTrapDoorBlock::new, Blocks.OAK_TRAPDOOR,
        BlockTags.WOODEN_TRAPDOORS, BlockTags.MINEABLE_WITH_AXE);

    public static final RegiliteBlock<PaintedWoodenPressurePlateBlock> PAINTED_WOODEN_PRESSURE_PLATE = paintedBlock("painted_wooden_pressure_plate",
        PaintedWoodenPressurePlateBlock::new, Blocks.OAK_PRESSURE_PLATE, BlockTags.WOODEN_PRESSURE_PLATES, BlockTags.MINEABLE_WITH_AXE);

    public static final RegiliteBlock<PaintedSlabBlock> PAINTED_SLAB = paintedBlock("painted_slab", PaintedSlabBlock::new, PaintedSlabBlockItem::new,
        Blocks.OAK_SLAB, BlockTags.WOODEN_SLABS, BlockTags.MINEABLE_WITH_AXE).setLootTable(DecorLootTable::paintedSlab);

    public static final RegiliteBlock<SinglePaintedBlock> PAINTED_GLOWSTONE = paintedBlock("painted_glowstone", SinglePaintedBlock::new,
        Blocks.GLOWSTONE);

    public static final RegiliteBlock<PaintedWallBlock> PAINTED_WALL = paintedBlock("painted_wall", PaintedWallBlock::new, Blocks.COBBLESTONE_WALL,
        BlockTags.WALLS, BlockTags.MINEABLE_WITH_PICKAXE);

    // endregion

    private static RegiliteBlock<EIOPressurePlateBlock> pressurePlateBlock(String name, ResourceLocation texture, EIOPressurePlateBlock.Detector type,
        boolean silent) {

        return BLOCK_REGISTRY
            .registerBlock(name, props -> new EIOPressurePlateBlock(props, type, silent),
                BlockBehaviour.Properties.of().strength(5, 6).mapColor(MapColor.METAL))
            .setBlockStateProvider((prov, ctx) -> {
                BlockModelProvider modProv = prov.models();
                ModelFile dm = modProv.withExistingParent(name + "_down", prov.mcLoc("block/pressure_plate_down")).texture("texture", texture);
                ModelFile um = modProv.withExistingParent(name, prov.mcLoc("block/pressure_plate_up")).texture("texture", texture);

                VariantBlockStateBuilder vb = prov.getVariantBuilder(ctx.get());
                vb.partialState().with(PressurePlateBlock.POWERED, true).addModels(new ConfiguredModel(dm));
                vb.partialState().with(PressurePlateBlock.POWERED, false).addModels(new ConfiguredModel(um));
            })
            .addBlockTags(BlockTags.NEEDS_STONE_TOOL, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.PRESSURE_PLATES)
            .createBlockItem(ITEM_REGISTRY, item -> item
                .setTab(EIOCreativeTabs.MAIN)
            );
    }

    private static RegiliteBlock<SilentPressurePlateBlock> silentPressurePlateBlock(final PressurePlateBlock block) {
        ResourceLocation upModelLoc = Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block));
        ResourceLocation downModelLoc = ResourceLocation.fromNamespaceAndPath(upModelLoc.getNamespace(), upModelLoc.getPath() + "_down");

        return BLOCK_REGISTRY
            .registerBlock("silent_" + upModelLoc.getPath(), props -> new SilentPressurePlateBlock(block),
                BlockBehaviour.Properties.of())
            .addBlockTags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.PRESSURE_PLATES)
            .setBlockStateProvider((prov, ctx) -> {
                VariantBlockStateBuilder vb = prov.getVariantBuilder(ctx.get());
                vb.partialState().with(PressurePlateBlock.POWERED, true).addModels(new ConfiguredModel(prov.models().getExistingFile(downModelLoc)));
                vb.partialState().with(PressurePlateBlock.POWERED, false).addModels(new ConfiguredModel(prov.models().getExistingFile(upModelLoc)));
            })
            .createBlockItem(ITEM_REGISTRY, item -> item
                .setModelProvider((prov, ctx) -> prov.withExistingParent(ctx.getName(), upModelLoc))
                .setTab(EIOCreativeTabs.MAIN)
            );
    }

    private static RegiliteBlock<SilentWeightedPressurePlateBlock> silentWeightedPressurePlateBlock(WeightedPressurePlateBlock block) {
        ResourceLocation upModelLoc = Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block));
        ResourceLocation downModelLoc = ResourceLocation.fromNamespaceAndPath(upModelLoc.getNamespace(), upModelLoc.getPath() + "_down");

        return BLOCK_REGISTRY
            .registerBlock("silent_" + upModelLoc.getPath(), props -> new SilentWeightedPressurePlateBlock(block),
                BlockBehaviour.Properties.of())
            .setBlockStateProvider((prov, ctx) -> prov.getVariantBuilder(ctx.get()).forAllStates(blockState -> {
                if (blockState.getValue(WeightedPressurePlateBlock.POWER) == 0) {
                    return new ConfiguredModel[] { new ConfiguredModel(prov.models().getExistingFile(upModelLoc)) };
                }
                return new ConfiguredModel[] { new ConfiguredModel(prov.models().getExistingFile(downModelLoc)) };
            }))
            .addBlockTags(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.PRESSURE_PLATES)
            .createBlockItem(ITEM_REGISTRY, item -> item
                .setModelProvider((prov, ctx) -> prov.withExistingParent(ctx.getName(), upModelLoc))
                .setTab(EIOCreativeTabs.MAIN)
            );
    }

    @SafeVarargs
    private static <T extends Block> RegiliteBlock<T> paintedBlock(String name, Function<BlockBehaviour.Properties, T> blockFactory,
        Block copyFrom, TagKey<Block>... tags) {
        return paintedBlock(name, blockFactory, copyFrom, null, tags);
    }

    @SafeVarargs
    private static <T extends Block> RegiliteBlock<T> paintedBlock(String name, Function<BlockBehaviour.Properties, T> blockFactory,
        Block copyFrom, @Nullable Direction itemTextureRotation, TagKey<Block>... tags) {
        return paintedBlock(name, blockFactory, PaintedBlockItem::new, copyFrom, itemTextureRotation, tags);
    }

    @SafeVarargs
    private static <T extends Block> RegiliteBlock<T> paintedBlock(String name, Function<BlockBehaviour.Properties, T> blockFactory,
        BiFunction<? super T, Item.Properties, ? extends BlockItem> itemFactory, Block copyFrom, TagKey<Block>... tags) {
        return paintedBlock(name, blockFactory, itemFactory, copyFrom, null, tags);
    }

    @SafeVarargs
    private static <T extends Block> RegiliteBlock<T> paintedBlock(String name, Function<BlockBehaviour.Properties, T> blockFactory,
        BiFunction<? super T, Item.Properties, ? extends BlockItem> itemFactory, Block copyFrom, @Nullable Direction itemTextureRotation,
        TagKey<Block>... tags) {

        return BLOCK_REGISTRY
            .registerBlock(name, blockFactory, BlockBehaviour.Properties.ofFullCopy(copyFrom).noOcclusion())
            .setBlockStateProvider((prov, ctx) -> EIOBlockState.paintedBlock(name, prov, ctx.get(), copyFrom, itemTextureRotation))
            .setLootTable(DecorLootTable::withPaint)
            .addBlockTags(tags)
            .createBlockItem(
                ITEM_REGISTRY,
                b -> itemFactory.apply(b, new Item.Properties()),
                item -> {});
    }

    public static void register(IEventBus bus) {
        BLOCKS.addAlias(EnderIO.rl("industrial_insulation_block"), EnderIO.rl("industrial_insulation"));
        ITEMS.addAlias(EnderIO.rl("industrial_insulation_block"), EnderIO.rl("industrial_insulation"));

        BLOCKS.register(bus);
        ITEMS.register(bus);

        BLOCK_REGISTRY.register(bus);
        ITEM_REGISTRY.register(bus);
    }

}
