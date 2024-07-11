package com.enderio.base.common.enchantment;//package com.enderio.base.common.enchantment;
//
//import com.mojang.serialization.MapCodec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import it.unimi.dsi.fastutil.objects.ObjectArrayList;
//import net.minecraft.world.SimpleContainer;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.crafting.RecipeType;
//import net.minecraft.world.level.storage.loot.LootContext;
//import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
//import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
//import net.neoforged.neoforge.common.loot.LootModifier;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.stream.Collectors;
//
//// Based on forge example: https://github.com/MinecraftForge/MinecraftForge/blob/1.20.x/src/test/java/net/minecraftforge/debug/gameplay/loot/GlobalLootModifiersTest.java#L137
//public class AutoSmeltModifier extends LootModifier {
//    public static final MapCodec<AutoSmeltModifier> CODEC = RecordCodecBuilder.mapCodec(inst -> codecStart(inst).apply(inst, AutoSmeltModifier::new));
//
//    public AutoSmeltModifier(LootItemCondition[] conditionsIn) {
//        super(conditionsIn);
//    }
//
//    @Override
//    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
//        var level = context.getLevel();
//        var recipeManager = level.getRecipeManager();
//        return generatedLoot.stream().map(stack ->
//            recipeManager.getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), level)
//                .map(r -> r.value().assemble(new SimpleContainer(stack), level.registryAccess()))
//                .filter(itemStack -> !itemStack.isEmpty())
//                .map(itemStack -> itemStack.copyWithCount(stack.getCount() * itemStack.getCount()))
//                .orElse(stack))
//            .collect(Collectors.toCollection(ObjectArrayList<ItemStack>::new));
//    }
//
//    @Override
//    public MapCodec<? extends IGlobalLootModifier> codec() {
//        return CODEC;
//    }
//}
