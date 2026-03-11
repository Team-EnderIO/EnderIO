package com.enderio.enderio.content.paint;

import com.enderio.enderio.content.paint.block.entity.PaintedBlockEntity;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOLootModifiers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Optional;

public class CopyPaintFunction extends LootItemConditionalFunction {

    public static final MapCodec<CopyPaintFunction> CODEC = RecordCodecBuilder.mapCodec(
        inst -> commonFields(inst)
            .and(Codec.BOOL.fieldOf("should_copy_primary").forGetter(m -> m.shouldCopyPrimary))
            .apply(inst, CopyPaintFunction::new)
    );

    private final boolean shouldCopyPrimary;

    CopyPaintFunction(List<LootItemCondition> conditions, boolean shouldCopyPrimary) {
        super(conditions);
        this.shouldCopyPrimary = shouldCopyPrimary;
    }

    @Override
    public LootItemFunctionType getType() {
        return EIOLootModifiers.COPY_PAINT.get();
    }

    @Override
    protected ItemStack run(ItemStack stack, LootContext context) {
        BlockEntity blockEntity = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);

        if (blockEntity instanceof PaintedBlockEntity paintedBlockEntity) {
            Optional<Block> currentPaint = shouldCopyPrimary
                ? paintedBlockEntity.getPrimaryPaint()
                : paintedBlockEntity.getSecondaryPaint();

            currentPaint.ifPresent(paint -> stack.set(EIODataComponents.BLOCK_PAINT, BlockPaintData.of(paint)));
        }

        return stack;
    }

    public static Builder<?> copyPrimary() {
        return simpleBuilder((conditions) -> new CopyPaintFunction(conditions, true));
    }

    public static Builder<?> copySecondary() {
        return simpleBuilder((conditions) -> new CopyPaintFunction(conditions, false));
    }
}
