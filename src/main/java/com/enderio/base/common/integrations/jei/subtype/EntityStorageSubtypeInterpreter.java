//package com.enderio.base.common.integrations.jei.subtype;
//
//import com.enderio.base.common.init.EIOAttachments;
//import com.enderio.base.common.tag.EIOTags;
//import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
//import mezz.jei.api.ingredients.subtypes.UidContext;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.ItemStack;
//
//public class EntityStorageSubtypeInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
//    @Override
//    public String apply(ItemStack itemStack, UidContext context) {
//        if (itemStack.is(EIOTags.Items.ENTITY_STORAGE)) {
//            return itemStack.getData(EIOAttachments.STORED_ENTITY).getEntityType().map(ResourceLocation::toString).orElse(IIngredientSubtypeInterpreter.NONE);
//        }
//        return IIngredientSubtypeInterpreter.NONE;
//    }
//}
