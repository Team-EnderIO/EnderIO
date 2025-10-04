package com.enderio.enderio.common.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.conduits.facade.FacadeType;
import com.enderio.enderio.client.content.conduits.ConduitFacadeColor;
import com.enderio.enderio.common.content.conduits.facades.ComponentBackedConduitFacadeProvider;
import com.enderio.enderio.common.content.conduits.facades.ConduitFacadeItem;
import com.enderio.enderio.common.content.conduits.probe.ConduitProbeItem;
import com.enderio.enderio.common.content.filters.redstone.DoubleRedstoneChannel;
import com.enderio.enderio.common.content.filters.redstone.RedstoneCountFilter;
import com.enderio.enderio.common.content.filters.redstone.RedstoneFilterItem;
import com.enderio.enderio.common.content.filters.redstone.RedstoneTLatchFilter;
import com.enderio.enderio.common.content.filters.redstone.RedstoneTimerFilter;
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
                        props -> new ConduitFacadeItem(props.component(ConduitComponents.FACADE_TYPE, type)))
                // TODO: Model for when there is no "paint"
                .setModelProvider((prov,
                        ctx) -> prov.getBuilder(name).customLoader(FacadeItemModelBuilder::begin).model(name).end())
                .setTab(EIOCreativeTabs.CONDUITS)
                .setColorSupplier(() -> ConduitFacadeColor::new)
                .addCapability(EnderIOCapabilities.CONDUIT_FACADE_PROVIDER,
                        ComponentBackedConduitFacadeProvider.PROVIDER);
    }

    public static final RegiliteItem<RedstoneFilterItem> NOT_FILTER = createRedstoneFilter("redstone_not_filter",
            ConduitComponents.REDSTONE_NOT_FILTER, Unit.INSTANCE, null)
        .addCapability(EnderIOCapabilities.REDSTONE_INSERT_FILTER, RedstoneFilterItem.NOT_FILTER_PROVIDER_INSERT)
        .addCapability(EnderIOCapabilities.REDSTONE_EXTRACT_FILTER, RedstoneFilterItem.NOT_FILTER_PROVIDER_EXTRACT);

    public static final RegiliteItem<RedstoneFilterItem> OR_FILTER = createRedstoneFilter("redstone_or_filter",
            ConduitComponents.REDSTONE_OR_FILTER, DoubleRedstoneChannel.INSTANCE, ConduitMenus.REDSTONE_DOUBLE_CHANNEL_FILTER::get)
        .addCapability(EnderIOCapabilities.REDSTONE_INSERT_FILTER, RedstoneFilterItem.OR_FILTER_PROVIDER);

    public static final RegiliteItem<RedstoneFilterItem> AND_FILTER = createRedstoneFilter("redstone_and_filter",
            ConduitComponents.REDSTONE_AND_FILTER, DoubleRedstoneChannel.INSTANCE, ConduitMenus.REDSTONE_DOUBLE_CHANNEL_FILTER::get)
        .addCapability(EnderIOCapabilities.REDSTONE_INSERT_FILTER, RedstoneFilterItem.AND_FILTER_PROVIDER);

    public static final RegiliteItem<RedstoneFilterItem> NOR_FILTER = createRedstoneFilter("redstone_nor_filter",
            ConduitComponents.REDSTONE_NOR_FILTER, DoubleRedstoneChannel.INSTANCE, ConduitMenus.REDSTONE_DOUBLE_CHANNEL_FILTER::get)
        .addCapability(EnderIOCapabilities.REDSTONE_INSERT_FILTER, RedstoneFilterItem.NOR_FILTER_PROVIDER);

    public static final RegiliteItem<RedstoneFilterItem> NAND_FILTER = createRedstoneFilter("redstone_nand_filter",
            ConduitComponents.REDSTONE_NAND_FILTER, DoubleRedstoneChannel.INSTANCE, ConduitMenus.REDSTONE_DOUBLE_CHANNEL_FILTER::get)
        .addCapability(EnderIOCapabilities.REDSTONE_INSERT_FILTER, RedstoneFilterItem.NAND_FILTER_PROVIDER);

    public static final RegiliteItem<RedstoneFilterItem> XOR_FILTER = createRedstoneFilter("redstone_xor_filter",
            ConduitComponents.REDSTONE_XOR_FILTER, DoubleRedstoneChannel.INSTANCE, ConduitMenus.REDSTONE_DOUBLE_CHANNEL_FILTER::get)
        .addCapability(EnderIOCapabilities.REDSTONE_INSERT_FILTER, RedstoneFilterItem.XOR_FILTER_PROVIDER);

    public static final RegiliteItem<RedstoneFilterItem> XNOR_FILTER = createRedstoneFilter("redstone_xnor_filter",
            ConduitComponents.REDSTONE_XNOR_FILTER, DoubleRedstoneChannel.INSTANCE, ConduitMenus.REDSTONE_DOUBLE_CHANNEL_FILTER::get)
        .addCapability(EnderIOCapabilities.REDSTONE_INSERT_FILTER, RedstoneFilterItem.XNOR_FILTER_PROVIDER);

    public static final RegiliteItem<RedstoneFilterItem> TLATCH_FILTER = createRedstoneFilter("redstone_toggle_filter",
            ConduitComponents.REDSTONE_TLATCH_FILTER, RedstoneTLatchFilter.INSTANCE, null)
        .addCapability(EnderIOCapabilities.REDSTONE_INSERT_FILTER, RedstoneFilterItem.TLATCH_FILTER_PROVIDER);

    public static final RegiliteItem<RedstoneFilterItem> COUNT_FILTER = createRedstoneFilter("redstone_counting_filter",
            ConduitComponents.REDSTONE_COUNT_FILTER, RedstoneCountFilter.INSTANCE, ConduitMenus.REDSTONE_COUNT_FILTER::get)
        .addCapability(EnderIOCapabilities.REDSTONE_INSERT_FILTER, RedstoneFilterItem.COUNT_FILTER_PROVIDER);

    public static final RegiliteItem<RedstoneFilterItem> SENSOR_FILTER = createRedstoneFilter("redstone_sensor_filter",
            ConduitComponents.REDSTONE_SENSOR_FILTER, Unit.INSTANCE, null)
        .addCapability(EnderIOCapabilities.REDSTONE_EXTRACT_FILTER, RedstoneFilterItem.SENSOR_FILTER_PROVIDER);

    public static final RegiliteItem<RedstoneFilterItem> TIMER_FILTER = createRedstoneFilter("redstone_timer_filter",
            ConduitComponents.REDSTONE_TIMER_FILTER, RedstoneTimerFilter.INSTANCE, ConduitMenus.REDSTONE_TIMER_FILTER::get)
        .addCapability(EnderIOCapabilities.REDSTONE_EXTRACT_FILTER, RedstoneFilterItem.TIMER_FILTER_PROVIDER);

    public static <T> RegiliteItem<RedstoneFilterItem> createRedstoneFilter(String name,
            DeferredHolder<DataComponentType<?>, DataComponentType<T>> type, T defaultValue,
        @Nullable Supplier<MenuType<?>> menu) {
        return ITEM_REGISTRY
                .registerItem(name,
                        properties -> new RedstoneFilterItem(properties.component(type, defaultValue), menu))
                .setTab(EIOCreativeTabs.CONDUITS);
    }

    public static final RegiliteItem<ConduitProbeItem> CONDUIT_PROBE = ITEM_REGISTRY
        .registerItem("conduit_probe", props -> new ConduitProbeItem(props.stacksTo(1)))
        .setModelProvider((prov, ctx) -> {})
        .setTab(EIOCreativeTabs.CONDUITS);

    public static void register(IEventBus bus) {
        ITEM_REGISTRY.register(bus);
    }
}
