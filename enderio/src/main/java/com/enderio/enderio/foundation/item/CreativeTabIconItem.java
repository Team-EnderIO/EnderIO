package com.enderio.enderio.foundation.item;

import com.enderio.core.common.item.ICustomCreativeTabEntries;
import net.minecraft.world.item.Item;

public class CreativeTabIconItem extends Item implements ICustomCreativeTabEntries {
    public CreativeTabIconItem(Properties props) {
        super(props);
    }

    // Disable creative tab - this is a stupid pattern.
    @Override
    public boolean shouldAddDefaultItem() {
        return false;
    }
}
