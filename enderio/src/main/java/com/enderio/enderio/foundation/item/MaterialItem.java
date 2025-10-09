package com.enderio.enderio.foundation.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class MaterialItem extends Item {
    private final boolean hasGlint;

    public MaterialItem(Properties props, boolean hasGlint) {
        super(props);
        this.hasGlint = hasGlint;
    }

    public static MaterialItem plain(Properties props) {
        return new MaterialItem(props, false);
    }

    public static MaterialItem glinted(Properties props) {
        return new MaterialItem(props, true);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return hasGlint;
    }
}
