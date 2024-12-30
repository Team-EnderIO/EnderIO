package com.enderio.modconduits.mods.appeng;

import appeng.api.ids.AEComponents;
import com.enderio.conduits.api.facade.ConduitFacadeProvider;
import com.enderio.conduits.api.facade.FacadeType;
import net.minecraft.core.Holder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

/**
 * Exposes AE2 facades to the Ender IO facade system.
 */
public class AE2ConduitFacadeProvider implements ConduitFacadeProvider {

    public static final ICapabilityProvider<ItemStack, Void, ConduitFacadeProvider> PROVIDER = (stack,
            v) -> new AE2ConduitFacadeProvider(stack);

    private final ItemStack itemStack;

    public AE2ConduitFacadeProvider(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public boolean isValid() {
        Holder<Item> item = itemStack.get(AEComponents.FACADE_ITEM);
        return item != null && item.value() instanceof BlockItem;
    }

    @Override
    public Block block() {
        Holder<Item> item = itemStack.get(AEComponents.FACADE_ITEM);
        if (item != null && item.value() instanceof BlockItem blockItem) {
            return blockItem.getBlock();
        }

        // TODO: Ideally an exception?
        return Blocks.AIR;
    }

    @Override
    public FacadeType type() {
        // TODO: Could make this configurable?
        return FacadeType.BASIC;
    }
}
