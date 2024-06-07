package com.enderio.conduits.common.integrations.cctweaked;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ExtendedConduitData;
import com.enderio.api.integration.Integration;
import com.enderio.api.misc.ColorControl;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.conduits.common.conduit.block.ConduitBlockEntity;
import com.enderio.conduits.common.conduit.connection.ConnectionState;
import com.enderio.conduits.common.conduit.connection.DynamicConnectionState;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneExtendedData;
import com.enderio.conduits.common.init.ConduitCapabilities;
import com.enderio.conduits.common.init.EIOConduitTypes;
import com.enderio.regilite.holder.RegiliteItem;
import com.enderio.regilite.registry.ItemRegistry;
import com.mojang.serialization.Codec;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.redstone.BundledRedstoneProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CCIntegration implements Integration {

    private static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(EnderIO.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CCRedstoneUpgrade>> CC_REDSTONE = DATA_COMPONENT_TYPES
        .registerComponentType("cc_redstone_upgrade", builder -> builder.persistent(Codec.unit(CCRedstoneUpgrade.INSTANCE)).networkSynchronized(StreamCodec.unit(CCRedstoneUpgrade.INSTANCE)));

    private static final ItemRegistry ITEM_REGISTRY = EnderIO.getRegilite().itemRegistry();

    public static final RegiliteItem<CCRedstoneUpgradeItem> CC_REDSTONE_UPGRADE = ITEM_REGISTRY.registerItem("cc_redstone_upgrade", properties ->
        new CCRedstoneUpgradeItem(properties.component(CC_REDSTONE, CCRedstoneUpgrade.INSTANCE)))
        .setTranslation("CC Redstone Upgrade")
        .setTab(EIOCreativeTabs.CONDUITS)
        .addCapability(ConduitCapabilities.ConduitUpgrade.ITEM, CCRedstoneUpgradeItem.CC_REDSTONE_UPGRADE_PROVIDER);

    @Override
    public void onModConstruct() {
    }

    @Override
    public void addEventListener(IEventBus modEventBus, IEventBus forgeEventBus) {
        DATA_COMPONENT_TYPES.register(modEventBus);
        ITEM_REGISTRY.register(modEventBus);

        ComputerCraftAPI.registerBundledRedstoneProvider(bundle);
    }

    private static final BundledRedstoneProvider bundle = new BundledRedstoneProvider (){

        @Override
        public int getBundledRedstoneOutput(Level world, BlockPos pos, Direction side) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof ConduitBlockEntity conduit) {
                ConnectionState connectionState = conduit.getBundle().getConnectionState(side, EIOConduitTypes.Types.REDSTONE.get());
                if (connectionState instanceof DynamicConnectionState dyn && dyn.isInsert()) {
                    ExtendedConduitData<?> extendedConduitData = conduit.getBundle().getNodeFor(EIOConduitTypes.Types.REDSTONE.get()).getExtendedConduitData();
                    if (extendedConduitData instanceof RedstoneExtendedData redstone) {
                        int out = 0;
                        for (ColorControl control : ColorControl.values()) {
                            out |= (redstone.isActive(control) ? 1 : 0) << (getColor(control).getId());
                        }
                        return out;
                    }
                }
            }
            return -1;
        }

        private DyeColor getColor(ColorControl control) {
            return switch (control) {
                case GREEN -> DyeColor.GREEN;
                case BROWN -> DyeColor.BROWN;
                case BLUE -> DyeColor.BLUE;
                case PURPLE -> DyeColor.PURPLE;
                case CYAN -> DyeColor.CYAN;
                case LIGHT_GRAY -> DyeColor.LIGHT_GRAY;
                case GRAY -> DyeColor.GRAY;
                case PINK -> DyeColor.PINK;
                case LIME -> DyeColor.LIME;
                case YELLOW -> DyeColor.YELLOW;
                case LIGHT_BLUE -> DyeColor.LIGHT_BLUE;
                case MAGENTA -> DyeColor.MAGENTA;
                case ORANGE -> DyeColor.ORANGE;
                case WHITE -> DyeColor.WHITE;
                case BLACK -> DyeColor.BLACK;
                case RED -> DyeColor.RED;
            };
        }
    };
}
