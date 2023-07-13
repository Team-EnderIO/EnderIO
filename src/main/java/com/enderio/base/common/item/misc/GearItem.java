package com.enderio.base.common.item.misc;

import com.enderio.base.client.renderer.item.IRotatingItem;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class GearItem extends MaterialItem implements IRotatingItem {

    private final float ticksPerRotation;

    public GearItem(Properties props, float ticksPerRotation) {
        super(props, false);
        this.ticksPerRotation = ticksPerRotation;
    }

    // enables the use of a BEWLR
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        setupBEWLR(consumer);
    }

    @Override
    public float getTicksPerRotation() {
        return ticksPerRotation;
    }
}
