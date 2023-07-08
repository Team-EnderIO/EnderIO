package com.enderio.conduits.common.integrations.ae2;

import appeng.api.implementations.items.IFacadeItem;
import appeng.api.networking.IInWorldGridNodeHost;
import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitItemFactory;
import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.IConduitType;
import com.enderio.api.integration.Integration;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;
import java.util.function.Supplier;

public class AE2Integration implements Integration {

    private final Capability<IInWorldGridNodeHost> IN_WORLD_GRID_NODE_HOST = CapabilityManager.get(new CapabilityToken<>() {});

    private static final RegistryObject<AE2ConduitType> DENSE = ConduitTypes.CONDUIT_TYPES.register("dense_me", () -> new AE2ConduitType(true));
    private static final RegistryObject<AE2ConduitType> NORMAL = ConduitTypes.CONDUIT_TYPES.register("me", () -> new AE2ConduitType(false));
    public static final ItemEntry<Item> DENSE_ITEM = createConduitItem(DENSE, "dense_me", "Dense ME Conduit");
    public static final ItemEntry<Item> NORMAL_ITEM = createConduitItem(NORMAL, "me", "ME Conduit");

    @Override
    public void onModConstruct() {
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

    private static ItemEntry<Item> createConduitItem(Supplier<? extends IConduitType<?>> type, String itemName, String english) {
        return EnderIO.registrate().item(itemName + "_conduit",
                properties -> ConduitItemFactory.build(type, properties))
            .tab(EIOCreativeTabs.CONDUITS)
            .lang(english)
            .model((ctx, prov) -> prov.withExistingParent(itemName+"_conduit", EnderIO.loc("item/conduit")).texture("0", type.get().getItemTexture()))
            .register();
    }
}
