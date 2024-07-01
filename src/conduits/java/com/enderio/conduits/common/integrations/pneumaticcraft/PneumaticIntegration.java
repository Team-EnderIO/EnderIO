package com.enderio.conduits.common.integrations.pneumaticcraft;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitItemFactory;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.integration.Integration;
import com.enderio.api.registry.EnderIORegistries;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class PneumaticIntegration implements Integration {

    public static final DeferredRegister<ConduitType<?>> CONDUIT_TYPES = DeferredRegister.create(EnderIORegistries.Keys.CONDUIT_TYPES, EnderIO.MODID);

    private static final RegistryObject<PressureConduitType> PRESSURE = CONDUIT_TYPES.register("pressure", PressureConduitType::new);

    public static final ItemEntry<Item> PRESSURE_ITEM = createConduitItem(PRESSURE, "pressure", "Pressurised Conduit");

    @Override
    public void addEventListener(IEventBus modEventBus, IEventBus forgeEventBus) {
        CONDUIT_TYPES.register(modEventBus);
    }

    private static ItemEntry<Item> createConduitItem(Supplier<? extends ConduitType<?>> type, String itemName, String english) {
        return EnderIO.registrate().item(itemName + "_conduit",
                properties -> ConduitItemFactory.build(type, properties))
            .tab(EIOCreativeTabs.CONDUITS)
            .lang(english)
            .model((ctx, prov) -> {
                var conduitTypeKey = ConduitType.getKey(type.get());
                prov
                    .withExistingParent(ctx.getName(), EnderIO.loc("item/conduit"))
                    .texture("0", EnderIO.loc("block/conduit/" + conduitTypeKey.getPath()));
            })
            .register();
    }
}
