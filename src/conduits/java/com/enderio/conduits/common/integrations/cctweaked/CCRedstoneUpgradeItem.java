package com.enderio.conduits.common.integrations.cctweaked;

import com.enderio.api.conduit.upgrade.ConduitUpgrade;
import com.enderio.conduits.common.init.ConduitComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

public class CCRedstoneUpgradeItem extends Item {

    public static final ICapabilityProvider<ItemStack, Void, ConduitUpgrade> CC_REDSTONE_UPGRADE_PROVIDER
        = (stack, v) -> stack.get(ConduitComponents.FLUID_SPEED_UPGRADE);

    public CCRedstoneUpgradeItem(Properties pProperties) {
        super(pProperties);
    }
}
