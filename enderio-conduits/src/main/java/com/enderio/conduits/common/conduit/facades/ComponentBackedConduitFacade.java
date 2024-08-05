package com.enderio.conduits.common.conduit.facades;

import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.paint.BlockPaintData;
import com.enderio.conduits.common.init.ConduitComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

import java.util.Objects;

public class ComponentBackedConduitFacade implements ConduitFacade {

    public static final ICapabilityProvider<ItemStack, Void, ConduitFacade> PROVIDER
        = (stack, v) -> new ComponentBackedConduitFacade(stack);

    private final ItemStack itemStack;

    public ComponentBackedConduitFacade(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public boolean isValid() {
        return itemStack.has(EIODataComponents.BLOCK_PAINT);
    }

    @Override
    public Block block() {
        if (!isValid()) {
            throw new IllegalStateException("Facade cannot be used if isValid() is false.");
        }

        return Objects.requireNonNull(itemStack.get(EIODataComponents.BLOCK_PAINT)).paint();
    }

    @Override
    public void block(Block block) {
        itemStack.set(EIODataComponents.BLOCK_PAINT, BlockPaintData.of(block));
    }

    @Override
    public FacadeType type() {
        return itemStack.getOrDefault(ConduitComponents.FACADE_TYPE, FacadeType.BASIC);
    }

    @Override
    public void type(FacadeType type) {
        itemStack.set(ConduitComponents.FACADE_TYPE, type);
    }
}
