package com.enderio.decoration.common.init;

import java.util.ArrayList;
import java.util.List;

import com.enderio.base.common.item.EIOCreativeTabs;
import com.enderio.decoration.EIODecor;
import com.enderio.decoration.common.block.light.Light;
import com.enderio.decoration.common.block.light.LightNode;
import com.enderio.decoration.common.block.light.PoweredLight;
import com.enderio.decoration.common.block.painted.PaintedCraftingTableBlock;
import com.enderio.decoration.common.block.painted.PaintedFenceBlock;
import com.enderio.decoration.common.block.painted.PaintedFenceGateBlock;
import com.enderio.decoration.common.block.painted.PaintedRedstoneBlock;
import com.enderio.decoration.common.block.painted.PaintedSandBlock;
import com.enderio.decoration.common.block.painted.PaintedSlabBlock;
import com.enderio.decoration.common.block.painted.PaintedStairBlock;
import com.enderio.decoration.common.block.painted.PaintedTrapDoorBlock;
import com.enderio.decoration.common.block.painted.PaintedWoodenPressurePlateBlock;
import com.enderio.decoration.common.block.painted.SinglePaintedBlock;
import com.enderio.decoration.common.item.PaintedSlabBlockItem;
import com.enderio.decoration.datagen.loot.DecorLootTable;
import com.enderio.decoration.datagen.model.block.DecorBlockState;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

@SuppressWarnings("unused")
public class DecorBlocks {

    private static final Registrate REGISTRATE = EIODecor.registrate();
    public static final EIOCreativeTabs DECOR = new EIOCreativeTabs("decor", () -> Items.PAINTING); //TODO make proper tab


    // region Painted

    private static final List<NonNullSupplier<? extends Block>> painted = new ArrayList<>();

    public static final BlockEntry<PaintedFenceBlock> PAINTED_FENCE = paintedBlock("painted_fence", PaintedFenceBlock::new, Blocks.OAK_FENCE,
        BlockTags.WOODEN_FENCES, BlockTags.MINEABLE_WITH_AXE);

    public static final BlockEntry<PaintedFenceGateBlock> PAINTED_FENCE_GATE = paintedBlock("painted_fence_gate", PaintedFenceGateBlock::new,
        Blocks.OAK_FENCE_GATE, BlockTags.FENCE_GATES, BlockTags.MINEABLE_WITH_AXE);

    public static final BlockEntry<PaintedSandBlock> PAINTED_SAND = paintedBlock("painted_sand", PaintedSandBlock::new, Blocks.SAND, BlockTags.SAND,
        BlockTags.MINEABLE_WITH_SHOVEL);

    public static final BlockEntry<PaintedStairBlock> PAINTED_STAIRS = paintedBlock("painted_stairs", PaintedStairBlock::new, Blocks.OAK_STAIRS,
        BlockTags.WOODEN_STAIRS, BlockTags.MINEABLE_WITH_AXE);

    public static final BlockEntry<PaintedCraftingTableBlock> PAINTED_CRAFTING_TABLE = paintedBlock("painted_crafting_table", PaintedCraftingTableBlock::new,
        Blocks.CRAFTING_TABLE, BlockTags.MINEABLE_WITH_AXE);

    public static final BlockEntry<PaintedRedstoneBlock> PAINTED_REDSTONE_BLOCK = paintedBlock("painted_redstone_block", PaintedRedstoneBlock::new,
        Blocks.REDSTONE_BLOCK, BlockTags.MINEABLE_WITH_PICKAXE);

    public static final BlockEntry<PaintedTrapDoorBlock> PAINTED_TRAPDOOR = paintedBlock("painted_trapdoor", PaintedTrapDoorBlock::new, Blocks.OAK_TRAPDOOR,
        BlockTags.WOODEN_TRAPDOORS, BlockTags.MINEABLE_WITH_AXE);

    public static final BlockEntry<PaintedWoodenPressurePlateBlock> PAINTED_WOODEN_PRESSURE_PLATE = paintedBlock("painted_wooden_pressure_plate",
        PaintedWoodenPressurePlateBlock::new, Blocks.OAK_PRESSURE_PLATE, BlockTags.WOODEN_PRESSURE_PLATES, BlockTags.MINEABLE_WITH_AXE);

    public static final BlockEntry<PaintedSlabBlock> PAINTED_SLAB = REGISTRATE
        .block("painted_slab", PaintedSlabBlock::new)
        .blockstate((ctx, cons) -> DecorBlockState.paintedBlock(ctx, cons, Blocks.OAK_SLAB))
        .addLayer(() -> RenderType::translucent)
        .initialProperties(() -> Blocks.OAK_SLAB)
        .loot(DecorLootTable::paintedSlab)
        .tag(BlockTags.WOODEN_SLABS, BlockTags.MINEABLE_WITH_AXE)
        .item(PaintedSlabBlockItem::new)
        .build()
        .register();

    public static final BlockEntry<SinglePaintedBlock> PAINTED_GLOWSTONE = paintedBlock("painted_glowstone", SinglePaintedBlock::new, Blocks.GLOWSTONE);

    public static List<? extends Block> getPainted() {
        return painted.stream().map(NonNullSupplier::get).toList();
    }

    public static List<NonNullSupplier<? extends Block>> getPaintedSupplier() {
        return painted;
    }

    // endregion
    
    // region Light
    
    public static final BlockEntry<Light> LIGHT = lightBlock("light", s -> new Light(s, false));
    public static final BlockEntry<Light> LIGHT_INVERTED = lightBlock("light_inverted", s -> new Light(s, true));
    public static final BlockEntry<PoweredLight> POWERED_LIGHT = lightBlock("powerd_light", s -> new PoweredLight(s, false, false));
    public static final BlockEntry<PoweredLight> POWERED_LIGHT_INVERTED = lightBlock("powerd_light_inverted", s -> new PoweredLight(s, true, false));
    public static final BlockEntry<PoweredLight> POWERED_LIGHT_WIRELESS = lightBlock("powerd_light_wireless", s -> new PoweredLight(s, false, true));
    public static final BlockEntry<PoweredLight> POWERED_LIGHT_INVERTED_WIRELESS = lightBlock("powerd_light_inverted_wireless", s -> new PoweredLight(s, true, true));
    
    public static final BlockEntry<LightNode> LIGHT_NODE = REGISTRATE
    		.block("light_node", LightNode::new)
    		.blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models().withExistingParent("light_node", "block/air")))
    		.initialProperties(Material.AIR)
    		.properties(p -> p.lightLevel(l -> 15).noDrops().noCollission().noOcclusion())
    		.register();
    
    // endregion

    @SafeVarargs
    private static <T extends Block> BlockEntry<T> paintedBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> blockFactory, Block copyFrom,
        TagKey<Block>... tags) {
        BlockEntry<T> paintedBlockEntry = REGISTRATE
            .block(name, blockFactory)
            .blockstate((ctx, cons) -> DecorBlockState.paintedBlock(ctx, cons, copyFrom))
            .addLayer(() -> RenderType::translucent)
            .loot(DecorLootTable::withPaint)
            .initialProperties(() -> copyFrom)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .simpleItem()
            .tag(tags)
            .register();
        painted.add(paintedBlockEntry);
        return paintedBlockEntry;
    }
    
    public static <T extends Block> BlockEntry<T> lightBlock(String name, NonNullFunction<BlockBehaviour.Properties, T> blockFactory) { 
    	BlockEntry<T> lightBlockEntry = REGISTRATE
    		.block(name, blockFactory)
    		.blockstate((ctx, prov) -> DecorBlockState.lightBlock(ctx, prov))
    		.initialProperties(Material.METAL)
    		.properties(p -> p.lightLevel(l -> {
    				if (l.getValue(Light.ENABLED)) {
    					return 15;
    				}
    				return 0;
    			}))
    		.item()
    		.model((ctx, prov) -> prov.withExistingParent(name, "block/button_inventory"))
    		.tab(() -> DECOR)
    		.build()
    		.register();
    	return lightBlockEntry;
    }

    public static void classload() {}
}
