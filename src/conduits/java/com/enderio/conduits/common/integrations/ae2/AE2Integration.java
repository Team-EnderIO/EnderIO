package com.enderio.conduits.common.integrations.ae2;

import appeng.api.implementations.items.IFacadeItem;
import appeng.api.networking.IInWorldGridNodeHost;
import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitApi;
import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.IConduitType;
import com.enderio.api.integration.Integration;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.regilite.holder.RegiliteItem;
import com.enderio.regilite.registry.ItemRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.CapabilityManager;
import net.neoforged.neoforge.common.capabilities.CapabilityToken;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Optional;
import java.util.function.Supplier;

public class AE2Integration implements Integration {

    private static final ItemRegistry ITEM_REGISTRY = ItemRegistry.createRegistry(EnderIO.MODID);

    private final Capability<IInWorldGridNodeHost> IN_WORLD_GRID_NODE_HOST = CapabilityManager.get(new CapabilityToken<>() {});

    private static final DeferredHolder<IConduitType<?>, AE2ConduitType> DENSE = ConduitTypes.CONDUIT_TYPES.register("dense_me", () -> new AE2ConduitType(true));
    private static final DeferredHolder<IConduitType<?>, AE2ConduitType> NORMAL = ConduitTypes.CONDUIT_TYPES.register("me", () -> new AE2ConduitType(false));
    public static final RegiliteItem<Item> DENSE_ITEM = createConduitItem(DENSE, "dense_me", "Dense ME Conduit");
    public static final RegiliteItem<Item> NORMAL_ITEM = createConduitItem(NORMAL, "me", "ME Conduit");

    @Override
    public void onModConstruct() {
    }

    @Override
    public void addEventListener(IEventBus modEventBus, IEventBus forgeEventBus) {
        ITEM_REGISTRY.register(modEventBus);
    }

    public Optional<BlockState> getFacadeOf(ItemStack stack) {
        if (stack.getItem() instanceof IFacadeItem facadeItem) {
            return Optional.of(facadeItem.getTextureBlockState(stack));
        }
        return Optional.empty();
    }

    public Capability<IInWorldGridNodeHost> getInWorldGridNodeHost() {
        return IN_WORLD_GRID_NODE_HOST;
    }

    private static RegiliteItem<Item> createConduitItem(Supplier<? extends IConduitType<?>> type, String itemName, String english) {
        return ITEM_REGISTRY
            .registerItem(itemName + "_conduit",
                properties -> ConduitApi.INSTANCE.createConduitItem(type, properties))
            .setTab(EIOCreativeTabs.CONDUITS)
            .setTranslation(english)
            .setModelProvider((prov, ctx) -> prov.withExistingParent(itemName+"_conduit", EnderIO.loc("item/conduit")).texture("0", type.get().getItemTexture()));
    }
}
