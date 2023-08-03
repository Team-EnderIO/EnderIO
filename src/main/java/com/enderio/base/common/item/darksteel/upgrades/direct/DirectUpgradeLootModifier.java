package com.enderio.base.common.item.darksteel.upgrades.direct;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import java.util.function.Supplier;

public class DirectUpgradeLootModifier extends LootModifier {

    public static final Supplier<Codec<DirectUpgradeLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, DirectUpgradeLootModifier::new)));

    public DirectUpgradeLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (context.getParam(LootContextParams.THIS_ENTITY) instanceof Player player) {
            ObjectArrayList<ItemStack> remaining = new ObjectArrayList<>();
            for (ItemStack is : generatedLoot) {
                if(!player.addItem(is)) {
                    remaining.add(is);
                }
            }
            generatedLoot = remaining;
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
