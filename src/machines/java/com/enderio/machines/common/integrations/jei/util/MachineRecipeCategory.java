package com.enderio.machines.common.integrations.jei.util;

import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.machines.common.recipe.MachineRecipe;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Common machine recipe category utilities.
 * Currently has very little, but I'd like this to house some
 *  rendering stuff when that gets more complex.
 */
public abstract class MachineRecipeCategory<T> implements IRecipeCategory<T> {
    protected static boolean playerHasEnoughLevels(@Nullable LocalPlayer player, int cost) {
        if (player == null) {
            return true;
        }
        if (player.isCreative()) {
            return true;
        }
        return cost < 40 && cost <= player.experienceLevel;
    }

    protected static <T extends MachineRecipe<?>> Component getBasicEnergyString(T recipe) {
        return TooltipUtil.withArgs(EIOLang.ENERGY_AMOUNT, NumberFormat.getIntegerInstance(Locale.ENGLISH).format(recipe.getBaseEnergyCost()));
    }
}
