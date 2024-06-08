package com.enderio.conduits.common.redstone;

import com.enderio.api.filter.ResourceFilter;
import com.enderio.conduits.common.init.ConduitComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

public class RedstoneFilterItem extends Item {

    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> AND_FILTER_PROVIDER =
        (stack, v) -> stack.get(ConduitComponents.REDSTONE_AND_FILTER);
    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> COUNT_FILTER_PROVIDER =
        (stack, v) -> stack.get(ConduitComponents.REDSTONE_COUNT_FILTER);
    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> NAND_FILTER_PROVIDER =
        (stack, v) -> stack.get(ConduitComponents.REDSTONE_NAND_FILTER);
    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> NOR_FILTER_PROVIDER =
        (stack, v) -> stack.get(ConduitComponents.REDSTONE_NOR_FILTER);
    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> NOT_FILTER_PROVIDER =
        (stack, v) -> stack.get(ConduitComponents.REDSTONE_NOT_FILTER);
    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> OR_FILTER_PROVIDER =
        (stack, v) -> stack.get(ConduitComponents.REDSTONE_OR_FILTER);
    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> SENSOR_FILTER_PROVIDER =
        (stack, v) -> stack.get(ConduitComponents.REDSTONE_SENSOR_FILTER);
    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> TLATCH_FILTER_PROVIDER =
        (stack, v) -> stack.get(ConduitComponents.REDSTONE_TLATCH_FILTER);
    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> XNOR_FILTER_PROVIDER =
        (stack, v) -> stack.get(ConduitComponents.REDSTONE_XNOR_FILTER);
    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> XOR_FILTER_PROVIDER =
        (stack, v) -> stack.get(ConduitComponents.REDSTONE_XOR_FILTER);

    public RedstoneFilterItem(Properties pProperties) {
        super(pProperties);
    }
}
