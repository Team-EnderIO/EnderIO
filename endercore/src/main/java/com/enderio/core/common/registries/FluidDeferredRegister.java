package com.enderio.core.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class FluidDeferredRegister {
    private final String namespace;
    private final DeferredRegister<FluidType> fluidTypes;
    private final DeferredRegister<Fluid> fluids;
    private final DeferredRegister.Blocks blocks;
    private final DeferredRegister.Items items;

    public static FluidDeferredRegister create(String namespace) {
        return new FluidDeferredRegister(namespace);
    }

    protected FluidDeferredRegister(String namespace) {
        this.namespace = namespace;
        fluidTypes = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, namespace);
        fluids = DeferredRegister.create(Registries.FLUID, namespace);
        blocks = DeferredRegister.createBlocks(namespace);
        items = DeferredRegister.createItems(namespace);
    }

    public Builder builder(String name) {
        return new Builder(name);
    }

    public DeferredRegister<FluidType> fluidTypesRegister() {
        return fluidTypes;
    }

    public DeferredRegister<Fluid> fluidsRegister() {
        return fluids;
    }

    public DeferredRegister.Items itemsRegister() {
        return items;
    }

    public DeferredRegister.Blocks blocksRegister() {
        return blocks;
    }

    public void register(IEventBus bus) {
        fluidTypes.register(bus);
        fluids.register(bus);
        blocks.register(bus);
        items.register(bus);
    }

    public class Builder {
        private final String name;

        private Function<FluidType.Properties, FluidType> fluidTypeFactory;
        private FluidType.Properties fluidTypeProperties;
        private BiFunction<FlowingFluid, BlockBehaviour.Properties, ? extends LiquidBlock> blockFactory;
        private BlockBehaviour.Properties blockProperties;

        @Nullable
        private Function<Fluid, BucketItem> bucketFactory;

        private Builder(String name) {
            this.name = name;

            fluidTypeProperties = FluidType.Properties.create();
            fluidTypeFactory = FluidType::new;
            blockFactory = LiquidBlock::new;
            blockProperties = BlockBehaviour.Properties.ofFullCopy(Blocks.WATER);
        }

        public Builder fluidTypeFactory(Function<FluidType.Properties, FluidType> fluidTypeFactory) {
            this.fluidTypeFactory = fluidTypeFactory;
            return this;
        }

        public Builder fluidProperties(UnaryOperator<FluidType.Properties> fluidProperties) {
            this.fluidTypeProperties = fluidProperties.apply(this.fluidTypeProperties);
            return this;
        }

        public Builder defaultBucket() {
            this.bucketFactory = (fluid) -> new BucketItem(fluid, new Item.Properties().stacksTo(1));
            return this;
        }

        public Builder bucketFactory(Function<Fluid, BucketItem> bucketFactory) {
            this.bucketFactory = bucketFactory;
            return this;
        }

        public Builder noBucket() {
            this.bucketFactory = null;
            return this;
        }

        public Builder blockProperties(UnaryOperator<BlockBehaviour.Properties> blockProperties) {
            this.blockProperties = blockProperties.apply(this.blockProperties);
            return this;
        }

        public FluidDeferredHolders register() {
            // Create the holder instance now so that we can reference it in the factories below.
            var fluid = new FluidDeferredHolders();

            // Register everything.
            fluid.type(fluidTypes.register(name, () -> fluidTypeFactory.apply(fluidTypeProperties)));
            fluid.source(fluids.register("fluid_" + name + "_still", () -> new BaseFlowingFluid.Source(fluid.createProperties())));
            fluid.flowing(fluids.register("fluid_" + name + "_flowing", () -> new BaseFlowingFluid.Flowing(fluid.createProperties())));
            fluid.block(blocks.register(name, () -> blockFactory.apply(fluid.flowing().get(), blockProperties)));

            if (bucketFactory != null) {
                fluid.bucket(items.register(name + "_bucket", () -> bucketFactory.apply(fluid.source().get())));
            }

            return fluid;
        }
    }
}
