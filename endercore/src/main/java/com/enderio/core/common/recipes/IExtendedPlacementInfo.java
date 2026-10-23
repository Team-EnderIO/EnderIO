package com.enderio.core.common.recipes;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;

import java.util.List;

public interface IExtendedPlacementInfo {

    IntList getSlotList();

    List<Holder<Item>> getItemList(List<Holder<Item>> itemsUsedPerIngredient);

    int getItemAmount(int index, Holder<Item> item, int adjustedAmountToCraft);
}
