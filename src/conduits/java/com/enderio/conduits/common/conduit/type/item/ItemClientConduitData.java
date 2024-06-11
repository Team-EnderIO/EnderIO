package com.enderio.conduits.common.conduit.type.item;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ClientConduitData;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.gui.widgets.CheckBox;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ItemClientConduitData implements ClientConduitData<ItemConduitData> {

    @Override
    public List<AbstractWidget> createWidgets(Screen screen, Supplier<ItemConduitData> conduitDataSupplier,
        UpdateExtendedData<ItemConduitData> updateConduitData, Supplier<Direction> direction, Vector2i widgetsStart) {
        List<AbstractWidget> widgets = new ArrayList<>();

        widgets.add(new CheckBox(
            EnderIO.loc("textures/gui/round_robin.png"),
            widgetsStart.add(110, 20),
            () -> conduitDataSupplier.get().get(direction.get()).isRoundRobin,
            bool -> {
                updateConduitData.update(data -> {
                    var sideData = data.compute(direction.get());
                    sideData.isRoundRobin = bool;
                    return data;
                });
            }, () -> EIOLang.ROUND_ROBIN_ENABLED, () -> EIOLang.ROUND_ROBIN_DISABLED));

        widgets.add(new CheckBox(
            EnderIO.loc("textures/gui/self_feed.png"),
            widgetsStart.add(130, 20),
            () -> conduitDataSupplier.get().get(direction.get()).isSelfFeed,
            bool -> {
                updateConduitData.update(data -> {
                    var sideData = data.compute(direction.get());
                    sideData.isSelfFeed = bool;
                    return data;
                });
            }, () -> EIOLang.SELF_FEED_ENABLED, () -> EIOLang.SELF_FEED_DISABLED));
        return widgets;
    }
}
