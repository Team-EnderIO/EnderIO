package com.enderio.base.common.item.darksteel.upgrades.direct;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DirectUpgradeLootModifier extends LootModifier {

    protected DirectUpgradeLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
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

    public static class Serializer extends GlobalLootModifierSerializer<DirectUpgradeLootModifier> {

        @Override
        public DirectUpgradeLootModifier read(ResourceLocation name, JsonObject object, LootItemCondition[] conditions) {
            return new DirectUpgradeLootModifier(conditions);
        }

        @Override
        public JsonObject write(DirectUpgradeLootModifier instance) {
            return makeConditions(instance.conditions);
        }
    }

}
