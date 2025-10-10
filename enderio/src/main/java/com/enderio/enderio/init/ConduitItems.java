package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.conduits.facade.FacadeType;
import com.enderio.enderio.content.conduits.facades.ComponentBackedConduitFacadeProvider;
import com.enderio.enderio.content.conduits.facades.ConduitFacadeItem;
import com.enderio.enderio.content.conduits.probe.ConduitProbeItem;
import com.enderio.enderio.content.filters.redstone.DoubleRedstoneChannel;
import com.enderio.enderio.content.filters.redstone.RedstoneCountFilter;
import com.enderio.enderio.content.filters.redstone.RedstoneFilterItem;
import com.enderio.enderio.content.filters.redstone.RedstoneTLatchFilter;
import com.enderio.enderio.content.filters.redstone.RedstoneTimerFilter;
import com.enderio.enderio.data.model.item.FacadeItemModelBuilder;
import com.enderio.regilite.holder.RegiliteItem;
import com.enderio.regilite.registry.ItemRegistry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.Unit;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ConduitItems {
    private static final ItemRegistry ITEM_REGISTRY = EnderIO.REGILITE.itemRegistry();

    public static final RegiliteItem<ConduitFacadeItem> CONDUIT_FACADE = conduitFacade("conduit_facade",
            FacadeType.BASIC);
    public static final RegiliteItem<ConduitFacadeItem> TRANSPARENT_CONDUIT_FACADE = conduitFacade(
            "transparent_conduit_facade", FacadeType.TRANSPARENT);
    public static final RegiliteItem<ConduitFacadeItem> HARDENED_CONDUIT_FACADE = conduitFacade(
            "hardened_conduit_facade", FacadeType.HARDENED);
    public static final RegiliteItem<ConduitFacadeItem> TRANSPARENT_HARDENED_CONDUIT_FACADE = conduitFacade(
            "transparent_hardened_conduit_facade", FacadeType.TRANSPARENT_HARDENED);

    private static RegiliteItem<ConduitFacadeItem> conduitFacade(String name, FacadeType type) {
        return ITEM_REGISTRY
                .registerItem(name,
                        props -> new ConduitFacadeItem(props.component(EIODataComponents.FACADE_TYPE, type)))
                // TODO: Model for when there is no "paint"
                .setModelProvider((prov,
                        ctx) -> prov.getBuilder(name).customLoader(FacadeItemModelBuilder::begin).model(name).end())
                .setTab(EIOCreativeTabs.MAIN)
                .addCapability(EnderIOCapabilities.CONDUIT_FACADE_PROVIDER,
                        ComponentBackedConduitFacadeProvider.PROVIDER);
    }

    public static void register(IEventBus bus) {
        ITEM_REGISTRY.register(bus);
    }
}
