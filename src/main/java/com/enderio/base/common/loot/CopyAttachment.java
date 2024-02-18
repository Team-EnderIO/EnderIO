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
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;

//Based on https://github.com/mekanism/Mekanism/blob/1.20.4/src/main/java/mekanism/common/item/loot/CopyContainersLootFunction.java

public class CopyAttachment extends LootItemConditionalFunction {

    public static final Codec<CopyAttachment> CODEC = RecordCodecBuilder.create(instance -> commonFields(instance)
        .and(NeoForgeRegistries.ATTACHMENT_TYPES.byNameCodec().listOf().fieldOf("types")
        .forGetter(function -> function.attachments))
        .apply(instance, CopyAttachment::new)
    );
    private final List<AttachmentType<?>> attachments;

    protected CopyAttachment(List<LootItemCondition> pPredicates, List<AttachmentType<?>> attachments) {
        super(pPredicates);
        this.attachments = attachments;
    }

    @Override
    public LootItemFunctionType getType() {
        return EIOLootFunctions.COPY_ATTACHMENT.get();
    }

    @Override
    protected ItemStack run(ItemStack pStack, LootContext pContext) {
        BlockEntity blockEntity = pContext.getParamOrNull(LootContextParams.BLOCK_ENTITY);
        CompoundTag tag = new CompoundTag();
        for (AttachmentType<?> type : attachments) {
            if (blockEntity.hasData(type)) {
                if (blockEntity.getData(type) instanceof INBTSerializable serializable) {
                    tag.put(NeoForgeRegistries.ATTACHMENT_TYPES.getKey(type).toString(),serializable.serializeNBT());
                }
            }
            if (pStack.hasData(EIOAttachments.NBT_ATTACHMENT)) {
                pStack.setData(EIOAttachments.NBT_ATTACHMENT, new NBTAttachment(tag));
            }
        }
        return pStack;
    }

    public static class Builder extends LootItemConditionalFunction.Builder<CopyAttachment.Builder> {

        private final List<AttachmentType<?>> attachments = new ArrayList<>();

        protected Builder() {
        }

        public Builder copy(AttachmentType<?> type) {
            attachments.add(type);
            return this;
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new CopyAttachment(getConditions(), this.attachments);
        }
    }
}
