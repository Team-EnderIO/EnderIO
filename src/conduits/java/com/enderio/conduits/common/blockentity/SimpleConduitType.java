package com.enderio.conduits.common.blockentity;

import com.enderio.EnderIO;
import com.enderio.api.conduit.IConduitScreenData;
import com.enderio.api.conduit.ticker.IConduitTicker;
import com.enderio.api.conduit.IConduitType;
import com.enderio.api.misc.Vector2i;
import com.enderio.conduits.common.init.ConduitItems;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

/**
 * Only to be used for conduits in EnderIOs Namespace
 */
public class SimpleConduitType implements IConduitType {


    private final ResourceLocation texture;
    private final int activeLightLevel;

    public SimpleConduitType(ResourceLocation texture) {
        this(texture, 0);
    }

    public SimpleConduitType(ResourceLocation texture, int activeLightLevel) {
        this.texture = texture;
        this.activeLightLevel = activeLightLevel;
    }
    @Override
    public ResourceLocation getTexture() {
        return texture;
    }

    @Override
    public Item getConduitItem() {
        for (Map.Entry<RegistryObject<IConduitType>, ItemEntry<Item>> registryObjectItemEntryEntry : ConduitItems.CONDUITS.entrySet()) {
            if (registryObjectItemEntryEntry.getKey().get() == this)
                return registryObjectItemEntryEntry.getValue().get();
        }
        throw new NullPointerException();
    }

    @Override
    public int getLightLevel(boolean isActive) {
        return isActive ? activeLightLevel : 0;
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return EnderIO.loc("textures/gui/conduit_icon.png");
    }

    @Override
    public Vector2i getTexturePosition() {
        //TODO: allow other things
        return Vector2i.ZERO;
    }

    @Override
    public IConduitTicker getTicker() {
        return ((graph, level) -> {});
    }

    @Override
    public IConduitScreenData getData() {
        return new ConduitScreenDataImpl(false, false, false, true, true, false);
    }
}
