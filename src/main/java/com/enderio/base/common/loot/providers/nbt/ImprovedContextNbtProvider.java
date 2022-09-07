package com.enderio.base.common.loot.providers.nbt;

import com.enderio.base.common.init.EIOLootNbtProvider;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProvider;

/**
 * A NbtProvider that provides either the
 * {@linkplain LootContextParams#BLOCK_ENTITY block entity}'s NBT data or an
 * entity's NBT data based on an {@link LootContext.EntityTarget} but without
 * the id and positional data.
 */
public class ImprovedContextNbtProvider implements NbtProvider {
    private static final String BLOCK_ENTITY_ID = "block_entity";

    private static final ImprovedContextNbtProvider.Getter BLOCK_ENTITY_PROVIDER = new ImprovedContextNbtProvider.Getter() {
        public Tag get(LootContext pLootContext) {
            BlockEntity blockentity = pLootContext.getParamOrNull(LootContextParams.BLOCK_ENTITY);
            if (blockentity == null) {
                return null;
            }
            CompoundTag tag = blockentity.saveWithoutMetadata();
            return tag.isEmpty() ? null : tag;
        }

        public String getId() {
            return BLOCK_ENTITY_ID;
        }

        public Set<LootContextParam<?>> getReferencedContextParams() {
            return ImmutableSet.of(LootContextParams.BLOCK_ENTITY);
        }
    };
    public static final ImprovedContextNbtProvider BLOCK_ENTITY = new ImprovedContextNbtProvider(BLOCK_ENTITY_PROVIDER);
    final ImprovedContextNbtProvider.Getter getter;

    private static ImprovedContextNbtProvider.Getter forEntity(final LootContext.EntityTarget pEntityTarget) {
        return new ImprovedContextNbtProvider.Getter() {
            @Nullable
            public Tag get(LootContext pLootContext) {
                Entity entity = pLootContext.getParamOrNull(pEntityTarget.getParam());
                return entity != null ? NbtPredicate.getEntityTagToCompare(entity) : null;
            }

            public String getId() {
                return pEntityTarget.getName();
            }

            public Set<LootContextParam<?>> getReferencedContextParams() {
                return ImmutableSet.of(pEntityTarget.getParam());
            }
        };
    }

    private ImprovedContextNbtProvider(ImprovedContextNbtProvider.Getter pGetter) {
        this.getter = pGetter;
    }

    public LootNbtProviderType getType() {
        return EIOLootNbtProvider.IMPROVED_CONTEXT.get();
    }

    @Nullable
    public Tag get(LootContext pLootContext) {
        return this.getter.get(pLootContext);
    }

    public Set<LootContextParam<?>> getReferencedContextParams() {
        return this.getter.getReferencedContextParams();
    }

    public static NbtProvider forContextEntity(LootContext.EntityTarget pEntityTarget) {
        return new ImprovedContextNbtProvider(forEntity(pEntityTarget));
    }

    static ImprovedContextNbtProvider createFromContext(String pTargetName) {
        if (pTargetName.equals(BLOCK_ENTITY_ID)) {
            return new ImprovedContextNbtProvider(BLOCK_ENTITY_PROVIDER);
        }

        LootContext.EntityTarget lootcontext$entitytarget = LootContext.EntityTarget.getByName(pTargetName);
        return new ImprovedContextNbtProvider(forEntity(lootcontext$entitytarget));
    }

    interface Getter {
        @Nullable
        Tag get(LootContext pLootContext);

        String getId();

        Set<LootContextParam<?>> getReferencedContextParams();
    }

    public static class InlineSerializer implements GsonAdapterFactory.InlineSerializer<ImprovedContextNbtProvider> {
        public JsonElement serialize(ImprovedContextNbtProvider pProvider, JsonSerializationContext pJsonContext) {
            return new JsonPrimitive(pProvider.getter.getId());
        }

        public ImprovedContextNbtProvider deserialize(JsonElement pJsonElement, JsonDeserializationContext pJsonContext) {
            return ImprovedContextNbtProvider.createFromContext(pJsonElement.getAsString());
        }
    }

    public static class JsonSerializer implements net.minecraft.world.level.storage.loot.Serializer<ImprovedContextNbtProvider> {
        /**
         * Serialize the value by putting its data into the JsonObject.
         */
        public void serialize(JsonObject pJsonObject, ImprovedContextNbtProvider pProvider, JsonSerializationContext pContext) {
            pJsonObject.addProperty("target", pProvider.getter.getId());
        }

        /**
         * Deserialize a value by reading it from the JsonObject.
         */
        public ImprovedContextNbtProvider deserialize(JsonObject pJsonObject, JsonDeserializationContext pContext) {
            return ImprovedContextNbtProvider.createFromContext(GsonHelper.getAsString(pJsonObject, "target"));
        }
    }
}
