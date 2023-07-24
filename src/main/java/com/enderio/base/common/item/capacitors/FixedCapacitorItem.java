package com.enderio.base.common.item.capacitors;

import com.enderio.api.capability.IMultiCapabilityItem;
import com.enderio.api.capability.MultiCapabilityProvider;
import com.enderio.api.capacitor.ICapacitorData;
import com.enderio.base.common.init.EIOCapabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

/**
 * A capacitor item that has fixed capacitor data attached.
 */
public class FixedCapacitorItem extends BaseCapacitorItem implements IMultiCapabilityItem {
    private final ICapacitorData data;

    public FixedCapacitorItem(ICapacitorData data, Properties properties) {
        super(properties);
        this.data = data;
    }

    @Nullable
    @Override
    public MultiCapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt, MultiCapabilityProvider provider) {
        provider.add(EIOCapabilities.CAPACITOR, LazyOptional.of(() -> data));
        return provider;
    }

}
