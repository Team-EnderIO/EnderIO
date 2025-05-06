package com.enderio.armory.common.item.darksteel.upgrades.direct;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class DirectUpgradeLootModifier extends LootModifier {

    public static final MapCodec<DirectUpgradeLootModifier> CODEC = RecordCodecBuilder
            .mapCodec(inst -> codecStart(inst).apply(inst, DirectUpgradeLootModifier::new));

    public DirectUpgradeLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        Player player = null;
        if (context.getParam(LootContextParams.THIS_ENTITY) instanceof Player pl) {
            player = pl;
        } else if (context.getParam(LootContextParams.ATTACKING_ENTITY) instanceof Player pl) {
            player = pl;
        }
        if (player != null) {
            ObjectArrayList<ItemStack> remaining = new ObjectArrayList<>();
            for (ItemStack is : generatedLoot) {
                if (!player.addItem(is)) {
                    remaining.add(is);
                }
            }
            generatedLoot = remaining;
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
