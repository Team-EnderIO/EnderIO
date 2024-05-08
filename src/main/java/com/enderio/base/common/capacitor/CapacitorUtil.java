package com.enderio.base.common.capacitor;

import com.enderio.api.capacitor.CapacitorData;
import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.base.common.init.EIODataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

/**
 * Helper class for Capacitors
 */
public class CapacitorUtil {
    /**
     * Adds a tooltip for loot capacitors based on it's stats.
     *
     * @param stack
     * @param tooltipComponents
     */
    public static void getTooltip(ItemStack stack, List<Component> tooltipComponents) {
        // TODO: Revisit in future
//        stack.getCapability(EIOCapabilities.CAPACITOR).ifPresent(cap -> {
//            if (cap.getSpecializations().size() > 0) {
//                TranslatableComponent t = new TranslatableComponent(getFlavor(cap.getFlavor()),
//                    getGradeText(cap.getSpecializations().values().iterator().next()),
//                    getTypeText(cap.getSpecializations().keySet().iterator().next()),
//                    getBaseText(cap.getBase()));
//                tooltipComponents.add(t);
//            }
//        });
    }

    //TODO depending on direction
    private static String getFlavor(int flavor) {
        return "description.enderio.capacitor.flavor." + flavor;
    }

    //TODO depending on direction
    private static MutableComponent getBaseText(float base) {
        MutableComponent t = Component.translatable("description.enderio.capacitor.base." + (int) Math.ceil(base));
        t.withStyle(ChatFormatting.ITALIC);
        return t;
    }

    //TODO depending on direction
    private static MutableComponent getTypeText(String type) {
        MutableComponent t = Component.translatable("description.enderio.capacitor.type." + type);
        t.withStyle(ChatFormatting.ITALIC);
        return t;
    }

    //TODO depending on direction
    private static MutableComponent getGradeText(float grade) {
        MutableComponent t = Component.translatable("description.enderio.capacitor.grade." + (int) Math.ceil(grade));
        t.withStyle(ChatFormatting.ITALIC);
        return t;
    }

    /**
     * @deprecated Use {@link ItemStack#get(DataComponentType)} using {@link EIODataComponents#CAPACITOR_DATA} instead.
     */
    @Deprecated(forRemoval = true, since = "6.1")
    public static Optional<CapacitorData> getCapacitorData(ItemStack itemStack) {
        return Optional.ofNullable(itemStack.get(EIODataComponents.CAPACITOR_DATA));
    }

    /**
     * @deprecated Use {@link ItemStack#has(DataComponentType)} to check for {@link EIODataComponents#CAPACITOR_DATA} instead.
     */
    @Deprecated(forRemoval = true, since = "6.1")
    public static boolean isCapacitor(ItemStack itemStack) {
        return itemStack.has(EIODataComponents.CAPACITOR_DATA);
    }

    public static CapacitorModifier getRandomModifier(RandomSource randomSource) {
        return CapacitorModifier.SELECTABLE_MODIFIERS.get(randomSource.nextInt(CapacitorModifier.SELECTABLE_MODIFIERS.size()));
    }
}
