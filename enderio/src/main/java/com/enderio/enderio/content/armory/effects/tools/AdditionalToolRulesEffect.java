package com.enderio.enderio.content.armory.effects.tools;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeEffect;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookType;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.GetDestroySpeedHook;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.IsCorrectToolForDropsHook;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public sealed abstract class AdditionalToolRulesEffect extends DarkSteelUpgradeEffect implements GetDestroySpeedHook, IsCorrectToolForDropsHook {

    public static final class MinesAndDrops extends AdditionalToolRulesEffect {

        public static final MapCodec<MinesAndDrops> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            TagKey.codec(Registries.BLOCK).fieldOf("mineable_blocks").forGetter(i -> i.mineableBlocksTag)
        ).apply(inst, MinesAndDrops::new));

        private static final Map<Pair<Item, TagKey<Block>>, List<Tool.Rule>> ruleCache = new HashMap<>();

        private final TagKey<Block> mineableBlocksTag;

        public MinesAndDrops(TagKey<Block> mineableBlocksTag) {
            this.mineableBlocksTag = mineableBlocksTag;
        }

        @Override
        protected List<Tool.Rule> getRules(DarkSteelHookContext context) {
            if (!(context.itemStack().getItem() instanceof TieredItem tieredItem)) {
                return List.of();
            }

            // As the current tier will already have the rules for the incorrect tag, just add the mine and drops tag
            return ruleCache.computeIfAbsent(Pair.of(tieredItem, mineableBlocksTag), (pair) ->
                List.of(Tool.Rule.minesAndDrops(mineableBlocksTag, tieredItem.getTier().getSpeed())));
        }

        @Override
        protected MapCodec<? extends DarkSteelUpgradeEffect> codec() {
            return CODEC;
        }
    }

    public static final class Raw extends AdditionalToolRulesEffect {

        public static final MapCodec<Raw> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.list(Tool.Rule.CODEC, 1, 255).fieldOf("rules").forGetter(i -> i.rules)
        ).apply(inst, Raw::new));

        private final List<Tool.Rule> rules;

        public Raw(List<Tool.Rule> rules) {
            this.rules = rules;
        }

        @Override
        protected List<Tool.Rule> getRules(DarkSteelHookContext context) {
            return rules;
        }

        @Override
        protected MapCodec<? extends DarkSteelUpgradeEffect> codec() {
            return CODEC;
        }
    }

    protected abstract List<Tool.Rule> getRules(DarkSteelHookContext context);

    @Override
    public List<DarkSteelUpgradeHookType<?>> getActiveHooks() {
        return List.of(DarkSteelUpgradeHookTypes.GET_DESTROY_SPEED, DarkSteelUpgradeHookTypes.IS_CORRECT_TOOL_FOR_DROPS);
    }

    @Override
    public @Nullable Float getDestroySpeed(DarkSteelHookContext context, BlockState state) {
        for (Tool.Rule rule : getRules(context)) {
            if (rule.speed().isPresent() && state.is(rule.blocks())) {
                return rule.speed().get();
            }
        }

        return null;
    }

    @Override
    public @Nullable Boolean isCorrectToolForDrops(DarkSteelHookContext context, BlockState state) {
        for (Tool.Rule rule : getRules(context)) {
            if (rule.correctForDrops().isPresent() && state.is(rule.blocks())) {
                return rule.correctForDrops().get();
            }
        }

        return null;
    }
}
