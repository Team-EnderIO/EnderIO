package com.enderio.base.common.integrations.jei.subtype;

import com.enderio.base.api.attachment.StoredEntityData;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.tag.EIOTags;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class EntityStorageSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
    @Override
    public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
        if (ingredient.is(EIOTags.Items.ENTITY_STORAGE)) {
            return ingredient.getOrDefault(EIODataComponents.STORED_ENTITY, StoredEntityData.EMPTY);
        }
        return null;
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        if (ingredient.is(EIOTags.Items.ENTITY_STORAGE)) {
            return ingredient.getOrDefault(EIODataComponents.STORED_ENTITY, StoredEntityData.EMPTY)
                    .entityType()
                    .map(ResourceLocation::toString)
                    .orElse("");
        }

        return "";
    }
}
