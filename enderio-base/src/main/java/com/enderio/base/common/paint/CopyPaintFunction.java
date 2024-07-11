package com.enderio.base.common.paint;

import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.init.EIOLootModifiers;
import com.enderio.base.common.paint.blockentity.PaintedBlockEntity;
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
    public LootItemFunctionType<? extends LootItemConditionalFunction> getType() {
        return EIOLootModifiers.COPY_PAINT.get();
    }

    @Override
    protected ItemStack run(ItemStack pStack, LootContext pContext) {
        BlockEntity blockEntity = pContext.getParamOrNull(LootContextParams.BLOCK_ENTITY);

        if (blockEntity instanceof PaintedBlockEntity paintedBlockEntity) {
            Optional<Block> currentPaint = shouldCopyPrimary
                ? paintedBlockEntity.getPrimaryPaint()
                : paintedBlockEntity.getSecondaryPaint();

            currentPaint.ifPresent(paint -> pStack.set(EIODataComponents.BLOCK_PAINT, BlockPaintData.of(paint)));
        }

        return pStack;
    }

    public static Builder<?> copyPrimary() {
        return simpleBuilder((conditions) -> new CopyPaintFunction(conditions, true));
    }

    public static Builder<?> copySecondary() {
        return simpleBuilder((conditions) -> new CopyPaintFunction(conditions, false));
    }
}
