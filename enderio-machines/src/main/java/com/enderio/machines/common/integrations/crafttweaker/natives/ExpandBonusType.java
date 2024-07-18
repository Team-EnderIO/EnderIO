package com.enderio.machines.common.integrations.crafttweaker.natives;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.BracketEnum;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import com.enderio.machines.common.recipe.SagMillingRecipe;
import org.openzen.zencode.java.ZenCodeType;

import java.util.Locale;

@ZenRegister
@NativeTypeRegistration(value = SagMillingRecipe.BonusType.class, zenCodeName = "mods.enderio.BonusType")
@Document("mods/enderio/constants/BonusType")
@BracketEnum("enderio:bonus_type")
public class ExpandBonusType {
    @ZenCodeType.Method
    @ZenCodeType.Getter("canMultiply")
    public boolean canMultiply(SagMillingRecipe.BonusType internal) {
        return internal.canMultiply();
    }

    @ZenCodeType.Method
    @ZenCodeType.Getter("chance")
    public boolean doChance(SagMillingRecipe.BonusType internal) {
        return internal.doChance();
    }

    @ZenCodeType.Method
    public boolean useGrindingBall(SagMillingRecipe.BonusType internal) {
        return canMultiply(internal) || doChance(internal);
    }

    @ZenCodeType.Method
    @ZenCodeType.Getter("commandString")
    public String getCommandString(SagMillingRecipe.BonusType internal) {
        return "<constant:enderio:bonus_type:" + internal.name().toLowerCase(Locale.ROOT) + ">";
    }
}
