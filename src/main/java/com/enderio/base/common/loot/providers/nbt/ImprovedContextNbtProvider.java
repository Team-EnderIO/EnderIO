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
 * entity's NBT data based on an {@link LootContext.EntityTarget} but without the id and positional data.
 */
public class ImprovedContextNbtProvider implements NbtProvider {
    private static final String BLOCK_ENTITY_ID = "block_entity";

    private static final ImprovedContextNbtProvider.Getter BLOCK_ENTITY_PROVIDER = new ImprovedContextNbtProvider.Getter() {
        public Tag get(LootContext p_165582_) {
            BlockEntity blockentity = p_165582_.getParamOrNull(LootContextParams.BLOCK_ENTITY);
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
            public Tag get(LootContext p_165589_) {
                Entity entity = p_165589_.getParamOrNull(pEntityTarget.getParam());
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
        } else {
            LootContext.EntityTarget lootcontext$entitytarget = LootContext.EntityTarget.getByName(pTargetName);
            return new ImprovedContextNbtProvider(forEntity(lootcontext$entitytarget));
        }
    }

    interface Getter {
        @Nullable
        Tag get(LootContext pLootContext);

        String getId();

        Set<LootContextParam<?>> getReferencedContextParams();
    }

    public static class InlineSerializer implements GsonAdapterFactory.InlineSerializer<ImprovedContextNbtProvider> {
        public JsonElement serialize(ImprovedContextNbtProvider p_165597_, JsonSerializationContext p_165598_) {
            return new JsonPrimitive(p_165597_.getter.getId());
        }

        public ImprovedContextNbtProvider deserialize(JsonElement p_165603_, JsonDeserializationContext p_165604_) {
            String s = p_165603_.getAsString();
            return ImprovedContextNbtProvider.createFromContext(s);
        }
    }

    public static class Serializer
            implements net.minecraft.world.level.storage.loot.Serializer<ImprovedContextNbtProvider> {
        /**
         * Serialize the value by putting its data into the JsonObject.
         */
        public void serialize(JsonObject p_165610_, ImprovedContextNbtProvider p_165611_,
                JsonSerializationContext p_165612_) {
            p_165610_.addProperty("target", p_165611_.getter.getId());
        }

        /**
         * Deserialize a value by reading it from the JsonObject.
         */
        public ImprovedContextNbtProvider deserialize(JsonObject p_165618_, JsonDeserializationContext p_165619_) {
            String s = GsonHelper.getAsString(p_165618_, "target");
            return ImprovedContextNbtProvider.createFromContext(s);
        }
    }
}
