package com.enderio.base.common.loot;

import com.enderio.api.attachment.NBTAttachment;
import com.enderio.base.common.init.EIOAttachments;
import com.enderio.base.common.init.EIOLootFunctions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.ArrayList;
import java.util.List;

public class CopyNBT extends LootItemConditionalFunction {

    public static final Codec<CopyNBT> CODEC = RecordCodecBuilder.create(instance -> commonFields(instance)
        .and(Codec.STRING.listOf().fieldOf("keys")
            .forGetter(function -> function.keys))
        .apply(instance, CopyNBT::new)
    );

    private final List<String> keys;

    protected CopyNBT(List<LootItemCondition> pPredicates, List<String> keys) {
        super(pPredicates);
        this.keys = keys;
    }

    @Override
    protected ItemStack run(ItemStack pStack, LootContext pContext) {
        BlockEntity blockEntity = pContext.getParamOrNull(LootContextParams.BLOCK_ENTITY);
        CompoundTag tag = new CompoundTag();
        if (pStack.hasData(EIOAttachments.NBT_ATTACHMENT)) {
            tag = pStack.getData(EIOAttachments.NBT_ATTACHMENT).getTag();
        }
        for (String key : keys) {
            CompoundTag serializeNBT = blockEntity.serializeNBT();
            if (serializeNBT.contains(key)) {
                tag.put(key, serializeNBT.get(key));
            }
        }
        pStack.setData(EIOAttachments.NBT_ATTACHMENT, new NBTAttachment(tag));

        return pStack;
    }

    @Override
    public LootItemFunctionType getType() {
        return EIOLootFunctions.COPY_NBT.get();
    }

    public static class Builder extends LootItemConditionalFunction.Builder<CopyNBT.Builder> {

        private final List<String> keys = new ArrayList<>();

        protected Builder() {
        }

        public CopyNBT.Builder copy(String key) {
            keys.add(key);
            return this;
        }

        @Override
        protected CopyNBT.Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new CopyNBT(getConditions(), this.keys);
        }
    }
}
