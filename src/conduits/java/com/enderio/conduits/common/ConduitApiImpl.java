package com.enderio.conduits.common;

import com.enderio.api.conduit.ConduitApi;
import com.enderio.api.conduit.ConduitType;
import com.enderio.conduits.common.conduit.ConduitBlockItem;
import com.enderio.conduits.common.init.ConduitBlocks;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.NotImplementedException;

import java.util.function.Supplier;

public class ConduitApiImpl implements ConduitApi {

    // TODO: Take away this API.
    @Override
    public Item createConduitItem(Supplier<? extends ConduitType<?, ?, ?>> type, Item.Properties properties) {
        throw new NotImplementedException();
    }
}
