package com.enderio.conduits.common.conduit.upgrade;

import com.enderio.conduits.common.capability.ExtractionSpeedUpgrade;
import com.enderio.conduits.common.init.ConduitCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpeedUpgradeItem extends Item {

    private final LazyOptional<ExtractionSpeedUpgrade> upgradeCapability;

    public SpeedUpgradeItem(Properties properties, int tier) {
        super(properties);
        upgradeCapability = LazyOptional.of(() -> new ExtractionSpeedUpgrade(tier));
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            @Override
            public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
                if (capability == ConduitCapabilities.CONDUIT_UPGRADE) {
                    return upgradeCapability.cast();
                }

                return LazyOptional.empty();
            }
        };
    }
}
