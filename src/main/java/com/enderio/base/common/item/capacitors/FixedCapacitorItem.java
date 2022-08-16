package com.enderio.base.common.item.capacitors;

import com.enderio.api.capability.IMultiCapabilityItem;
import com.enderio.api.capability.MultiCapabilityProvider;
import com.enderio.api.capacitor.ICapacitorData;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.List;

/**
 * A capacitor item that has fixed capacitor data attached.
 */
public class FixedCapacitorItem extends Item implements IMultiCapabilityItem {
    private final ICapacitorData data;

    public FixedCapacitorItem(ICapacitorData data, Properties properties) {
        super(properties);
        this.data = data;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(TooltipUtil.styledWithArgs(EIOLang.CAPACITOR_TOOLTIP_BASE, NumberFormat.getInstance().format(data.getBase())));
    }

    @Nullable
    @Override
    public MultiCapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt, MultiCapabilityProvider provider) {
        provider.addSimple(EIOCapabilities.CAPACITOR, LazyOptional.of(() -> data));
        return provider;
    }
}
