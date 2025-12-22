//package com.enderio.enderio.compat.jei.subtype;
//
//import com.enderio.enderio.api.EnderIODataComponents;
//import com.enderio.enderio.api.conduits.Conduit;
//import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
//import mezz.jei.api.ingredients.subtypes.UidContext;
//import net.minecraft.core.Holder;
//import net.minecraft.world.item.ItemStack;
//import org.jetbrains.annotations.Nullable;
//
//public class ConduitSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {
//
//    @Override
//    public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context) {
//        Holder<Conduit<?, ?>> conduit = ingredient.get(EnderIODataComponents.CONDUIT);
//        if (conduit != null) {
//            return conduit.getRegisteredName();
//        }
//
//        return null;
//    }
//
//    @Override
//    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
//        Holder<Conduit<?, ?>> conduit = ingredient.get(EnderIODataComponents.CONDUIT);
//        if (conduit != null) {
//            return conduit.getRegisteredName();
//        }
//
//        return "";
//    }
//}
