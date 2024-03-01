package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitApi;
import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.IConduitType;
import com.enderio.api.integration.Integration;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.conduits.common.blockentity.ConduitBlockEntity;
import com.enderio.conduits.common.init.ConduitBlockEntities;
import com.enderio.regilite.holder.RegiliteItem;
import com.enderio.regilite.registry.ItemRegistry;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public class MekanismIntegration implements Integration {

    private static final ItemRegistry ITEM_REGISTRY = EnderIO.getRegilite().itemRegistry();
    private static final DeferredHolder<IConduitType<?>, ChemicalConduitType> CHEMICAL = ConduitTypes.CONDUIT_TYPES.register("chemical", ChemicalConduitType::new);
    public static final RegiliteItem<Item> CHEMICAL_ITEM = createConduitItem(CHEMICAL, "chemical", "Chemical Conduit");

    @Override
    public void addEventListener(IEventBus modEventBus, IEventBus forgeEventBus) {
        ITEM_REGISTRY.register(modEventBus);
        modEventBus.addListener(this::addCapability);
    }

    private static RegiliteItem<Item> createConduitItem(Supplier<? extends IConduitType<?>> type, String itemName, String english) {
        return ITEM_REGISTRY
            .registerItem(itemName + "_conduit",
                properties -> ConduitApi.INSTANCE.createConduitItem(type, properties))
            .setTab(EIOCreativeTabs.CONDUITS)
            .setTranslation(english)
            .setModelProvider((prov, ctx) -> prov.withExistingParent(itemName+"_conduit", EnderIO.loc("item/conduit")).texture("0", type.get().getItemTexture()));
    }

    public void addCapability(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.GAS.block(), ConduitBlockEntities.CONDUIT.get(), ConduitBlockEntity.createConduitCap(Capabilities.GAS.block()));
        event.registerBlockEntity(Capabilities.SLURRY.block(), ConduitBlockEntities.CONDUIT.get(), ConduitBlockEntity.createConduitCap(Capabilities.SLURRY.block()));
        event.registerBlockEntity(Capabilities.INFUSION.block(), ConduitBlockEntities.CONDUIT.get(), ConduitBlockEntity.createConduitCap(Capabilities.INFUSION.block()));
        event.registerBlockEntity(Capabilities.PIGMENT.block(), ConduitBlockEntities.CONDUIT.get(), ConduitBlockEntity.createConduitCap(Capabilities.PIGMENT.block()));
    }
}
