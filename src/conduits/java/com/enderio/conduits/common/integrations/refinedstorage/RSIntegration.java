package com.enderio.conduits.common.integrations.refinedstorage;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitItemFactory;
import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.IConduitType;
import com.enderio.api.integration.Integration;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class RSIntegration implements Integration {

    private static final RegistryObject<RSConduitType> NORMAL = ConduitTypes.CONDUIT_TYPES.register("refined_storage", RSConduitType::new);
    public static final ItemEntry<Item> NORMAL_ITEM = createConduitItem(NORMAL, "refined_storage", "Refined Storage Conduit");

    private static ItemEntry<Item> createConduitItem(Supplier<? extends IConduitType<?>> type, String itemName, String english) {
        return EnderIO.registrate().item(itemName + "_conduit",
                properties -> ConduitItemFactory.build(type, properties))
            .tab(EIOCreativeTabs.CONDUITS)
            .lang(english)
            .model((ctx, prov) -> prov.withExistingParent(itemName+"_conduit", EnderIO.loc("item/conduit")).texture("0", type.get().getItemTexture()))
            .register();
    }

}
