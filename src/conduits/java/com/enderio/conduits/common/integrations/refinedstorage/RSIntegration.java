package com.enderio.conduits.common.integrations.refinedstorage;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitItemFactory;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.integration.Integration;
import com.enderio.base.common.init.EIOCreativeTabs;
import com.enderio.conduits.common.init.EIOConduitTypes;
import com.refinedmods.refinedstorage.item.CoverItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;
import java.util.function.Supplier;

public class RSIntegration implements Integration {

    private static final RegistryObject<RSConduitType> NORMAL = EIOConduitTypes.CONDUIT_TYPES.register("refined_storage", RSConduitType::new);
    public static final ItemEntry<Item> NORMAL_ITEM = createConduitItem(NORMAL, "refined_storage", "Refined Storage Conduit");

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

    @Override
    public Optional<BlockState> getFacadeOf(ItemStack stack) {
        if (stack.getItem() instanceof CoverItem) {
            ItemStack coverItem = CoverItem.getItem(stack);
            return Optional.of(Block.byItem(coverItem.getItem()).defaultBlockState());
        }
        return Optional.empty();
    }
}
