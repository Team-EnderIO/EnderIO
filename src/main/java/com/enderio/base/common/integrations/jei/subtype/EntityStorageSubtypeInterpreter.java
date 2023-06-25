package com.enderio.base.common.integrations.jei.subtype;

import com.enderio.base.common.init.EIOCapabilities;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class EntityStorageSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
    @Override
    public String apply(ItemStack itemStack, UidContext context) {
        return itemStack
            .getCapability(EIOCapabilities.ENTITY_STORAGE)
            .map(cap -> cap.getStoredEntityData().getEntityType().map(ResourceLocation::toString).orElse(IIngredientSubtypeInterpreter.NONE))
            .orElse(IIngredientSubtypeInterpreter.NONE);
    }
}
