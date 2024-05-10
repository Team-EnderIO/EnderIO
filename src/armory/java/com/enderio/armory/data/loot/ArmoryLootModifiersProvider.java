package com.enderio.armory.data.loot;

import com.enderio.EnderIO;
import com.enderio.armory.common.item.darksteel.upgrades.direct.DirectUpgradeLootCondition;
import com.enderio.armory.common.item.darksteel.upgrades.direct.DirectUpgradeLootModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;

import java.util.concurrent.CompletableFuture;

public class ArmoryLootModifiersProvider extends GlobalLootModifierProvider {
    public ArmoryLootModifiersProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, EnderIO.MODID);
    }

    @Override
    protected void start() {
        add("direct_upgrade", new DirectUpgradeLootModifier(
            new LootItemCondition[]{
                new DirectUpgradeLootCondition()
            }
        ));
    }
}
