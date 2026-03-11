package com.enderio.core.common.registries;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public final class FluidRegistryObjects {
    private RegistryObject<? extends FluidType> type;
    private RegistryObject<? extends Fluid> source;
    private RegistryObject<? extends FlowingFluid> flowing;
    private RegistryObject<LiquidBlock> block;
    private @Nullable RegistryObject<BucketItem> bucket;

    public RegistryObject<? extends FluidType> type() {
        return type;
    }

    public RegistryObject<? extends Fluid> source() {
        return source;
    }

    public RegistryObject<? extends FlowingFluid> flowing() {
        return flowing;
    }

    public RegistryObject<LiquidBlock> block() {
        return block;
    }

    public @Nullable RegistryObject<BucketItem> bucket() {
        return bucket;
    }

    void type(RegistryObject<? extends FluidType> type) {
        this.type = type;
    }

    void source(RegistryObject<? extends Fluid> source) {
        this.source = source;
    }

    void flowing(RegistryObject<? extends FlowingFluid> flowing) {
        this.flowing = flowing;
    }

    void block(RegistryObject<LiquidBlock> block) {
        this.block = block;
    }

    void bucket(@Nullable RegistryObject<BucketItem> bucket) {
        this.bucket = bucket;
    }

    ForgeFlowingFluid.Properties createProperties() {
        //noinspection DataFlowIssue - allow bucket supplier to be null.
        return new ForgeFlowingFluid.Properties(type, source, flowing).block(block).bucket(bucket);
    }
}
