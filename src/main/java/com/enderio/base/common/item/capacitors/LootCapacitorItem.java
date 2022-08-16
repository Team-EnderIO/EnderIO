package com.enderio.base.common.item.capacitors;

import com.enderio.api.capability.IMultiCapabilityItem;
import com.enderio.api.capability.MultiCapabilityProvider;
import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.base.common.capacitor.LootCapacitorData;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;

import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

public class LootCapacitorItem extends Item implements IMultiCapabilityItem {

    public LootCapacitorItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pStack.getCapability(EIOCapabilities.CAPACITOR).ifPresent(data -> {
            NumberFormat fmt = NumberFormat.getInstance();
            pTooltipComponents.add(TooltipUtil.styledWithArgs(EIOLang.CAPACITOR_TOOLTIP_BASE, fmt.format(data.getBase())));
            if (data instanceof LootCapacitorData lootCapacitorData) {
                for (Map.Entry<CapacitorModifier, Float> modifier : lootCapacitorData.getAllModifiers().entrySet()) {
                    pTooltipComponents.add(TooltipUtil.styledWithArgs(new ResourceLocation("tooltip", modifier.getKey().id.toLanguageKey()), fmt.format(modifier.getValue())));
                }
            }
        });
    }

    @Nullable
    @Override
    public MultiCapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt, MultiCapabilityProvider provider) {
        provider.addSerialized(EIOCapabilities.CAPACITOR, LazyOptional.of(LootCapacitorData::new));
        return provider;
    }
}