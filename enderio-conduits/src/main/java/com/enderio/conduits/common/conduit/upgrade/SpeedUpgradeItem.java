package com.enderio.conduits.common.conduit.upgrade;

import com.enderio.conduits.api.upgrade.ConduitUpgrade;
import com.enderio.conduits.common.components.ExtractionSpeedUpgrade;
import com.enderio.conduits.common.init.ConduitComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

public class SpeedUpgradeItem extends Item {

    public static final ICapabilityProvider<ItemStack, Void, ConduitUpgrade> CAPABILITY_PROVIDER
        = (stack, v) -> new ExtractionSpeedUpgrade(ConduitComponents.EXTRACTION_SPEED_UPGRADE_TIER, stack);

    public SpeedUpgradeItem(Properties pProperties) {
        super(pProperties);
    }
}
