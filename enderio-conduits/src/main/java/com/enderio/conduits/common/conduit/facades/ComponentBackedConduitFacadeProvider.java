package com.enderio.conduits.common.conduit.facades;

import com.enderio.base.common.init.EIODataComponents;
import com.enderio.conduits.api.facade.ConduitFacadeProvider;
import com.enderio.conduits.api.facade.FacadeType;
import com.enderio.conduits.common.init.ConduitComponents;
import java.util.Objects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

public class ComponentBackedConduitFacadeProvider implements ConduitFacadeProvider {

    public static final ICapabilityProvider<ItemStack, Void, ConduitFacadeProvider> PROVIDER = (stack,
            v) -> new ComponentBackedConduitFacadeProvider(stack);

    private final ItemStack itemStack;

    public ComponentBackedConduitFacadeProvider(ItemStack itemStack) {
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
    public FacadeType type() {
        return itemStack.getOrDefault(ConduitComponents.FACADE_TYPE, FacadeType.BASIC);
    }
}
