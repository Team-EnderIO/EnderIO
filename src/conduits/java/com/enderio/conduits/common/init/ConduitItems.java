package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitItemFactory;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.filter.ResourceFilter;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.conduits.common.conduit.upgrade.SpeedUpgradeItem;
import com.enderio.conduits.common.redstone.DoubleRedstoneChannel;
import com.enderio.conduits.common.redstone.RedstoneANDFilter;
import com.enderio.conduits.common.redstone.RedstoneCountFilter;
import com.enderio.conduits.common.redstone.RedstoneFilterItem;
import com.enderio.conduits.common.redstone.RedstoneNANDFilter;
import com.enderio.conduits.common.redstone.RedstoneNORFilter;
import com.enderio.conduits.common.redstone.RedstoneNOTFilter;
import com.enderio.conduits.common.redstone.RedstoneORFilter;
import com.enderio.conduits.common.redstone.RedstoneSensorFilter;
import com.enderio.conduits.common.redstone.RedstoneTLatchFilter;
import com.enderio.conduits.common.redstone.RedstoneTimerFilter;
import com.enderio.conduits.common.redstone.RedstoneXNORFilter;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.util.Unit;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Function;
import java.util.function.Supplier;

@Mod.EventBusSubscriber
public class ConduitItems {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final ItemEntry<Item> ENERGY = createConduitItem(EIOConduitTypes.ENERGY, "energy");
    public static final ItemEntry<Item> FLUID = createConduitItem(EIOConduitTypes.FLUID, "fluid");
    public static final ItemEntry<Item> PRESSURIZED_FLUID = createConduitItem(EIOConduitTypes.FLUID2, "pressurized_fluid");
    public static final ItemEntry<Item> ENDER_FLUID = createConduitItem(EIOConduitTypes.FLUID3, "ender_fluid");
    public static final ItemEntry<Item> REDSTONE = createConduitItem(EIOConduitTypes.REDSTONE, "redstone");
    public static final ItemEntry<Item> ITEM = createConduitItem(EIOConduitTypes.ITEM, "item");

    public static final ItemEntry<SpeedUpgradeItem> EXTRACTION_SPEED_UPGRADE_1 = REGISTRATE
        .item("extraction_speed_upgrade_1", props -> new SpeedUpgradeItem(props, 1))
        .lang("Tier 1 Extraction Speed Upgrade")
        .tab(EIOCreativeTabs.CONDUITS)
        .register();

    public static final ItemEntry<SpeedUpgradeItem> EXTRACTION_SPEED_UPGRADE_2 = REGISTRATE
        .item("extraction_speed_upgrade_2", props -> new SpeedUpgradeItem(props, 2))
        .lang("Tier 2 Extraction Speed Upgrade")
        .tab(EIOCreativeTabs.CONDUITS)
        .register();

    public static final ItemEntry<SpeedUpgradeItem> EXTRACTION_SPEED_UPGRADE_3 = REGISTRATE
        .item("extraction_speed_upgrade_3", props -> new SpeedUpgradeItem(props, 3))
        .lang("Tier 3 Extraction Speed Upgrade")
        .tab(EIOCreativeTabs.CONDUITS)
        .register();

    public static final ItemEntry<SpeedUpgradeItem> EXTRACTION_SPEED_UPGRADE_4 = REGISTRATE
        .item("extraction_speed_upgrade_4", props -> new SpeedUpgradeItem(props, 4))
        .lang("Tier 4 Extraction Speed Upgrade")
        .tab(EIOCreativeTabs.CONDUITS)
        .register();

    public static final ItemEntry<RedstoneFilterItem> AND_FILTER = createRedstoneFilter("redstone_and_filter", RedstoneANDFilter::new, ConduitMenus.REDSTONE_DOUBLE_CHANNEL_FILTER::get);
    public static final ItemEntry<RedstoneFilterItem> COUNT_FILTER = createRedstoneFilter("redstone_counting_filter", RedstoneCountFilter::new, ConduitMenus.REDSTONE_COUNT_FILTER::get);
    public static final ItemEntry<RedstoneFilterItem> NAND_FILTER = createRedstoneFilter("redstone_nand_filter", RedstoneNANDFilter::new, ConduitMenus.REDSTONE_DOUBLE_CHANNEL_FILTER::get);
    public static final ItemEntry<RedstoneFilterItem> NOR_FILTER = createRedstoneFilter("redstone_nor_filter", RedstoneNORFilter::new, ConduitMenus.REDSTONE_DOUBLE_CHANNEL_FILTER::get);
    public static final ItemEntry<RedstoneFilterItem> NOT_FILTER = createRedstoneFilter("redstone_not_filter", stack -> RedstoneNOTFilter.INSTANCE, null);
    public static final ItemEntry<RedstoneFilterItem> OR_FILTER = createRedstoneFilter("redstone_or_filter", RedstoneORFilter::new, ConduitMenus.REDSTONE_DOUBLE_CHANNEL_FILTER::get);
    public static final ItemEntry<RedstoneFilterItem> SENSOR_FILTER = createRedstoneFilter("redstone_sensor_filter", stack -> RedstoneSensorFilter.INSTANCE, null);
    public static final ItemEntry<RedstoneFilterItem> TIMER_FILTER = createRedstoneFilter("redstone_timer_filter", RedstoneTimerFilter::new, ConduitMenus.REDSTONE_TIMER_FILTER::get);
    public static final ItemEntry<RedstoneFilterItem> TLATCH_FILTER = createRedstoneFilter("redstone_toggle_filter", RedstoneTLatchFilter::new, null);
    public static final ItemEntry<RedstoneFilterItem> XNOR_FILTER = createRedstoneFilter("redstone_xnor_filter", RedstoneXNORFilter::new, ConduitMenus.REDSTONE_DOUBLE_CHANNEL_FILTER::get);
    public static final ItemEntry<RedstoneFilterItem> XOR_FILTER = createRedstoneFilter("redstone_xor_filter", RedstoneXNORFilter::new, ConduitMenus.REDSTONE_DOUBLE_CHANNEL_FILTER::get);

    private static ItemEntry<Item> createConduitItem(Supplier<? extends ConduitType<?>> type, String itemName) {
        return REGISTRATE.item(itemName + "_conduit",
            properties -> ConduitItemFactory.build(type, properties))
            .tab(EIOCreativeTabs.CONDUITS)
            .model((ctx, prov) -> {
                var conduitTypeKey = ConduitType.getKey(type.get());
                prov
                    .withExistingParent(ctx.getName(), EnderIO.loc("item/conduit"))
                    .texture("0", EnderIO.loc("block/conduit/" + conduitTypeKey.getPath()));
            })
            .register();
    }

    public static <T> ItemEntry<RedstoneFilterItem> createRedstoneFilter(String name, Function<ItemStack, ResourceFilter> capabilityFactory, Supplier<MenuType<?>> menu) {
        return REGISTRATE
            .item(name, properties -> new RedstoneFilterItem(properties, capabilityFactory, menu))
            .tab(EIOCreativeTabs.CONDUITS)
            .register();
    }

    public static void register() {}
}
