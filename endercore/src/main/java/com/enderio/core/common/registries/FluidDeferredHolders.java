package com.enderio.core.common.registries;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jspecify.annotations.Nullable;

public final class FluidDeferredHolders {
    private DeferredHolder<FluidType, ? extends FluidType> type;
    private DeferredHolder<Fluid, ? extends Fluid> source;
    private DeferredHolder<Fluid, ? extends FlowingFluid> flowing;
    private DeferredBlock<LiquidBlock> block;
    private @Nullable DeferredItem<BucketItem> bucket;

    public DeferredHolder<FluidType, ? extends FluidType> type() {
        return type;
    }

    public DeferredHolder<Fluid, ? extends Fluid> source() {
        return source;
    }

    public DeferredHolder<Fluid, ? extends FlowingFluid> flowing() {
        return flowing;
    }

    public DeferredBlock<LiquidBlock> block() {
        return block;
    }

    public @Nullable DeferredItem<BucketItem> bucket() {
        return bucket;
    }

    void type(DeferredHolder<FluidType, ? extends FluidType> type) {
        this.type = type;
    }

    void source(DeferredHolder<Fluid, ? extends Fluid> source) {
        this.source = source;
    }

    void flowing(DeferredHolder<Fluid, ? extends FlowingFluid> flowing) {
        this.flowing = flowing;
    }

    void block(DeferredBlock<LiquidBlock> block) {
        this.block = block;
    }

    void bucket(@Nullable DeferredItem<BucketItem> bucket) {
        this.bucket = bucket;
    }

    BaseFlowingFluid.Properties createProperties() {
        //noinspection DataFlowIssue - allow bucket supplier to be null.
        return new BaseFlowingFluid.Properties(type, source, flowing).block(block).bucket(bucket);
    }
}
