package com.enderio.conduits.client.gui.conduit;

import com.enderio.EnderIO;
import com.enderio.api.ConduitDataAccessor;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.screen.ConduitScreenExtension;
import com.enderio.api.misc.Vector2i;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.conduits.common.init.ConduitTypes;
import com.enderio.core.client.gui.widgets.CheckBox;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ItemConduitScreenExtension implements ConduitScreenExtension {

    private static final ResourceLocation ROUND_ROBIN_ICON = EnderIO.loc("textures/gui/round_robin.png");
    private static final ResourceLocation SELF_FEED_ICON = EnderIO.loc("textures/gui/self_feed.png");

    @Override
    public List<AbstractWidget> createWidgets(Screen screen, ConduitDataAccessor conduitDataAccessor, UpdateDispatcher updateConduitData,
        Supplier<Direction> direction, Vector2i widgetsStart) {
        List<AbstractWidget> widgets = new ArrayList<>();

        widgets.add(new CheckBox(
            ROUND_ROBIN_ICON,
            widgetsStart.add(110, 20),
            () -> conduitDataAccessor.getOrCreateData(ConduitTypes.Data.ITEM.get()).get(direction.get()).isRoundRobin,
            bool -> {
                var data = conduitDataAccessor.getOrCreateData(ConduitTypes.Data.ITEM.get());
                var sideData = data.compute(direction.get());
                sideData.isRoundRobin = bool;
                updateConduitData.sendUpdate();
            }, () -> EIOLang.ROUND_ROBIN_ENABLED, () -> EIOLang.ROUND_ROBIN_DISABLED));

        widgets.add(new CheckBox(
            SELF_FEED_ICON,
            widgetsStart.add(130, 20),
            () -> conduitDataAccessor.getOrCreateData(ConduitTypes.Data.ITEM.get()).get(direction.get()).isSelfFeed,
            bool -> {
                var data = conduitDataAccessor.getOrCreateData(ConduitTypes.Data.ITEM.get());
                var sideData = data.compute(direction.get());
                sideData.isSelfFeed = bool;
                updateConduitData.sendUpdate();
            }, () -> EIOLang.SELF_FEED_ENABLED, () -> EIOLang.SELF_FEED_DISABLED));

        return widgets;
    }
}
