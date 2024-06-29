package com.enderio.conduits.common.integrations.ae2;

import appeng.api.ids.AEConstants;
import appeng.api.implementations.items.IFacadeItem;
import appeng.api.networking.IInWorldGridNodeHost;
import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitApi;
import com.enderio.api.conduit.ConduitDataSerializer;
import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.ConduitNetworkType;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.integration.Integration;
import com.enderio.api.registry.EnderIORegistries;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.conduits.common.conduit.block.ConduitBlockEntity;
import com.enderio.conduits.common.init.ConduitBlockEntities;
import com.enderio.regilite.holder.RegiliteItem;
import com.enderio.regilite.registry.ItemRegistry;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class AE2Integration implements Integration {

    public static final DeferredRegister<ConduitNetworkType<?, ?, ?>> CONDUIT_NETWORK_TYPES = DeferredRegister.create(EnderIORegistries.CONDUIT_NETWORK_TYPES, EnderIO.MODID);
    public static final DeferredRegister<ConduitType<?, ?, ?>> CONDUIT_TYPES = DeferredRegister.create(EnderIORegistries.CONDUIT_TYPES, EnderIO.MODID);
    public static final DeferredRegister<ConduitDataSerializer<?>> CONDUIT_DATA_SERIALIZERS = DeferredRegister.create(EnderIORegistries.CONDUIT_DATA_SERIALIZERS, EnderIO.MODID);

    private static final ItemRegistry ITEM_REGISTRY = EnderIO.getRegilite().itemRegistry();

    public static final Supplier<AE2ConduitNetworkType> NETWORK_TYPE = CONDUIT_NETWORK_TYPES.register("ae2", AE2ConduitNetworkType::new);

    //TODO use capability when moved to api by ea2
    public static BlockCapability<IInWorldGridNodeHost, @Nullable Direction> IN_WORLD_GRID_NODE_HOST = BlockCapability
        .createSided(ResourceLocation.fromNamespaceAndPath(AEConstants.MOD_ID, "inworld_gridnode_host"), IInWorldGridNodeHost.class);

    public static final Supplier<ConduitType<AE2ConduitOptions, ConduitNetworkContext.Dummy, AE2InWorldConduitNodeHost>> DENSE = CONDUIT_TYPES
        .register("dense_me", () -> new ConduitType<>(NETWORK_TYPE.get(), new AE2ConduitOptions(true)));

    public static final Supplier<ConduitType<AE2ConduitOptions, ConduitNetworkContext.Dummy, AE2InWorldConduitNodeHost>> NORMAL = CONDUIT_TYPES
        .register("me", () -> new ConduitType<>(NETWORK_TYPE.get(), new AE2ConduitOptions(false)));

    public static final Supplier<ConduitDataSerializer<AE2InWorldConduitNodeHost>> DATA_SERIALIZER =
        CONDUIT_DATA_SERIALIZERS.register("me", () -> AE2InWorldConduitNodeHost.Serializer.INSTANCE);

    public static final RegiliteItem<Item> DENSE_ITEM = createConduitItem(DENSE, "dense_me", "Dense ME Conduit");
    public static final RegiliteItem<Item> NORMAL_ITEM = createConduitItem(NORMAL, "me", "ME Conduit");

    @Override
    public void onModConstruct() {
    }

    @Override
    public void addEventListener(IEventBus modEventBus, IEventBus forgeEventBus) {
        ITEM_REGISTRY.register(modEventBus);
        CONDUIT_NETWORK_TYPES.register(modEventBus);
        CONDUIT_TYPES.register(modEventBus);
        CONDUIT_DATA_SERIALIZERS.register(modEventBus);
    }

    public Optional<BlockState> getFacadeOf(ItemStack stack) {
        if (stack.getItem() instanceof IFacadeItem facadeItem) {
            return Optional.of(facadeItem.getTextureBlockState(stack));
        }
        return Optional.empty();
    }

    public BlockCapability<IInWorldGridNodeHost, Direction> getInWorldGridNodeHost() {
        return IN_WORLD_GRID_NODE_HOST;
    }

    private static RegiliteItem<Item> createConduitItem(Supplier<? extends ConduitType<?, ?, ?>> type, String itemName, String english) {
        return ITEM_REGISTRY
            .registerItem(itemName + "_conduit",
                properties -> ConduitApi.INSTANCE.createConduitItem(type, properties))
            .setTab(EIOCreativeTabs.CONDUITS)
            .setTranslation(english)
            .setModelProvider((prov, ctx) -> {
                var conduitTypeKey = ConduitType.getKey(type.get());
                prov
                    .withExistingParent(conduitTypeKey.getPath() + "_conduit", EnderIO.loc("item/conduit"))
                    .texture("0", EnderIO.loc("block/conduit/" + conduitTypeKey.getPath()));
            });
    }
}
