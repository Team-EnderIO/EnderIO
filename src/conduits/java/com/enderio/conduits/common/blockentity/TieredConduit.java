package com.enderio.conduits.common.blockentity;

import com.enderio.EnderIO;
import com.enderio.api.conduit.IConduitScreenData;
import com.enderio.api.conduit.ticker.IConduitTicker;
import com.enderio.api.conduit.IConduitType;
import com.enderio.api.misc.Vector2i;
import com.enderio.conduits.common.init.ConduitItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class TieredConduit implements IConduitType {
    private final ResourceLocation texture;
    private final ResourceLocation type;
    private final int tier;
    @Nullable
    private final Supplier<Item> conduitItem;

    /**
     * @param texture
     * @param type
     * @param tier The tier of the conduit. For Energy this should be it's transfer rate to easily add and compare conduit strength
     * @param conduitItem Only use null here if your conduit is in the {@link ConduitItems#CONDUITS} field
     */

    //TODO unhardcode for Power Conduits
    public TieredConduit(ResourceLocation texture, ResourceLocation type, int tier, @Nullable Supplier<Item> conduitItem) {
        this.texture = texture;
        this.type = type;
        this.tier = tier;
        this.conduitItem = conduitItem;
    }

    @Override
    public ResourceLocation getTexture() {
        return texture;
    }

    @Override
    public Item getConduitItem() {
        if (conduitItem != null)
            return conduitItem.get();
        return ConduitItems.CONDUITS.entrySet().stream().filter(entry -> entry.getKey().get() == this).findFirst().get().getValue().get();
    }


    @Override
    public boolean canBeReplacedBy(IConduitType other) {
        if (!(other instanceof TieredConduit tieredOther))
            return false;

        if (type.equals(tieredOther.getType())) {
            return tier < tieredOther.getTier();
        }
        return false;
    }

    @Override
    public boolean canBeInSameBlock(IConduitType other) {
        if (!(other instanceof TieredConduit tieredOther))
            return true;
        // if they have the same type they can't be in the same block, their tier doesn't matter as canBeReplacedBy is checked first
        return !type.equals(tieredOther.getType());
    }

    public ResourceLocation getType() {
        return type;
    }

    public int getTier() {
        return tier;
    }


    @Override
    public ResourceLocation getTextureLocation() {
        return EnderIO.loc("textures/gui/conduit_icon.png");
    }

    @Override
    public Vector2i getTexturePosition() {
        return new Vector2i(0, 24*getTier());
    }

    @Override
    public IConduitScreenData getData() {
        return new ConduitScreenDataImpl(false, true, true, false, false, true);
    }
}
