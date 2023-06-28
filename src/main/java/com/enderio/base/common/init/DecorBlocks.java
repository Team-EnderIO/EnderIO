package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.common.block.painted.*;
import com.enderio.base.client.renderer.PaintedBlockColor;
import com.enderio.base.common.block.light.Light;
import com.enderio.base.common.block.light.LightNode;
import com.enderio.base.common.block.light.PoweredLight;
import com.enderio.base.common.item.PaintedBlockItem;
import com.enderio.base.common.item.PaintedSlabBlockItem;
import com.enderio.base.data.loot.DecorLootTable;
import com.enderio.base.data.model.block.EIOBlockState;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class DecorBlocks {

    private static final Registrate REGISTRATE = EnderIO.registrate();

    // region Painted

    private static final List<NonNullSupplier<? extends Block>> painted = new ArrayList<>();

    public static final BlockEntry<PaintedFenceBlock> PAINTED_FENCE = paintedBlock("painted_fence", PaintedFenceBlock::new, Blocks.OAK_FENCE,
        BlockTags.WOODEN_FENCES, BlockTags.MINEABLE_WITH_AXE).register();

    public static final BlockEntry<PaintedFenceGateBlock> PAINTED_FENCE_GATE = paintedBlock("painted_fence_gate", PaintedFenceGateBlock::new,
        Blocks.OAK_FENCE_GATE, BlockTags.FENCE_GATES, BlockTags.MINEABLE_WITH_AXE).register();

    public static final BlockEntry<PaintedSandBlock> PAINTED_SAND = paintedBlock("painted_sand", PaintedSandBlock::new, Blocks.SAND, BlockTags.SAND,
        BlockTags.MINEABLE_WITH_SHOVEL).register();

    public static final BlockEntry<PaintedStairBlock> PAINTED_STAIRS = paintedBlock("painted_stairs", PaintedStairBlock::new, Blocks.OAK_STAIRS,
        Direction.WEST,BlockTags.WOODEN_STAIRS, BlockTags.MINEABLE_WITH_AXE).register();

    public static final BlockEntry<PaintedCraftingTableBlock> PAINTED_CRAFTING_TABLE = paintedBlock("painted_crafting_table", PaintedCraftingTableBlock::new,
        Blocks.CRAFTING_TABLE, BlockTags.MINEABLE_WITH_AXE).register();

    public static final BlockEntry<PaintedRedstoneBlock> PAINTED_REDSTONE_BLOCK = paintedBlock("painted_redstone_block", PaintedRedstoneBlock::new,
        Blocks.REDSTONE_BLOCK, BlockTags.MINEABLE_WITH_PICKAXE).register();

    public static final BlockEntry<PaintedTrapDoorBlock> PAINTED_TRAPDOOR = paintedBlock("painted_trapdoor", PaintedTrapDoorBlock::new, Blocks.OAK_TRAPDOOR,
        BlockTags.WOODEN_TRAPDOORS, BlockTags.MINEABLE_WITH_AXE).register();

    public static final BlockEntry<PaintedWoodenPressurePlateBlock> PAINTED_WOODEN_PRESSURE_PLATE = paintedBlock("painted_wooden_pressure_plate",
        PaintedWoodenPressurePlateBlock::new, Blocks.OAK_PRESSURE_PLATE, BlockTags.WOODEN_PRESSURE_PLATES, BlockTags.MINEABLE_WITH_AXE).register();

    public static final BlockEntry<PaintedSlabBlock> PAINTED_SLAB = paintedBlock("painted_slab",
        PaintedSlabBlock::new, PaintedSlabBlockItem::new, Blocks.OAK_SLAB, BlockTags.WOODEN_SLABS, BlockTags.MINEABLE_WITH_AXE)
        .loot(DecorLootTable::paintedSlab)
        .register();

    public static final BlockEntry<SinglePaintedBlock> PAINTED_GLOWSTONE = paintedBlock("painted_glowstone", SinglePaintedBlock::new, Blocks.GLOWSTONE).register();

    // endregion
    
    // region Light
    
    public static final BlockEntry<Light> LIGHT = lightBlock("light", s -> new Light(s, false));
    public static final BlockEntry<Light> LIGHT_INVERTED = lightBlock("light_inverted", s -> new Light(s, true));
    public static final BlockEntry<PoweredLight> POWERED_LIGHT = lightBlock("powered_light", s -> new PoweredLight(s, false, false));
    public static final BlockEntry<PoweredLight> POWERED_LIGHT_INVERTED = lightBlock("powered_light_inverted", s -> new PoweredLight(s, true, false));
    public static final BlockEntry<PoweredLight> POWERED_LIGHT_WIRELESS = lightBlock("powered_light_wireless", s -> new PoweredLight(s, false, true));
    public static final BlockEntry<PoweredLight> POWERED_LIGHT_INVERTED_WIRELESS = lightBlock("powered_light_inverted_wireless", s -> new PoweredLight(s, true, true));

    public static final BlockEntry<LightNode> LIGHT_NODE = REGISTRATE
    		.block("light_node", LightNode::new)
    		.blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models().withExistingParent("light_node", "block/air")))
    		.initialProperties(() -> Blocks.AIR)
    		.properties(p -> p.lightLevel(l -> 15).noLootTable().noCollission().noOcclusion())
    		.register();
    
    // endregion

    @SafeVarargs
    private static <T extends Block> BlockBuilder<T, Registrate> paintedBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> blockFactory, Block copyFrom,
        TagKey<Block>... tags) {
        return paintedBlock(name, blockFactory, copyFrom, null, tags);
    }

    @SafeVarargs
    private static <T extends Block> BlockBuilder<T, Registrate> paintedBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> blockFactory, Block copyFrom,
        @Nullable Direction itemTextureRotation, TagKey<Block>... tags) {
        return paintedBlock(name, blockFactory, PaintedBlockItem::new, copyFrom, itemTextureRotation, tags);
    }

    @SafeVarargs
    private static <T extends Block> BlockBuilder<T, Registrate> paintedBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> blockFactory,
        NonNullBiFunction<? super T, Item.Properties, ? extends BlockItem> itemFactory, Block copyFrom, TagKey<Block>... tags) {
        return paintedBlock(name, blockFactory, itemFactory, copyFrom, null, tags);
    }

    @SafeVarargs
    private static <T extends Block> BlockBuilder<T, Registrate> paintedBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> blockFactory,
        NonNullBiFunction<? super T, Item.Properties, ? extends BlockItem> itemFactory, Block copyFrom,
        @Nullable Direction itemTextureRotation, TagKey<Block>... tags) {
        return REGISTRATE
            .block(name, blockFactory)
            .blockstate((ctx, cons) -> EIOBlockState.paintedBlock(ctx, cons, copyFrom, itemTextureRotation))
            .color(() -> PaintedBlockColor::new)
            .loot(DecorLootTable::withPaint)
            .initialProperties(() -> copyFrom)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .item(itemFactory)
            .color(() -> PaintedBlockColor::new)
            .build()
            .tag(tags);
    }

    public static <T extends Block> BlockEntry<T> lightBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> blockFactory) {
    	return REGISTRATE
    		.block(name, blockFactory)
    		.blockstate(EIOBlockState::lightBlock)
    		.properties(p -> p
                .sound(SoundType.METAL)
                .mapColor(MapColor.METAL)
                .lightLevel(l -> {
    				if (l.getValue(Light.ENABLED)) {
    					return 15;
    				}
    				return 0;
    			}))
    		.item()
    		.model((ctx, prov) -> prov.withExistingParent(name, "block/button_inventory"))
    		.tab(EIOCreativeTabs.BLOCKS)
    		.build()
    		.register();
    }

    public static void register() {}
}
