package com.enderio.core.data.athena;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.Optionull;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

public interface AthenaConnection {

    BiMap<String, MapCodec<? extends AthenaConnection>> CONDITION_TYPES = HashBiMap.create();

    Codec<MapCodec<? extends AthenaConnection>> TYPE_CODEC = Codec.STRING
        .flatComapMap(
            (type) -> CONDITION_TYPES.getOrDefault(type, False.CODEC),
            (type) -> Optionull.mapOrElse(
                CONDITION_TYPES.inverse().get(type),
                DataResult::success,
                () -> DataResult.error(() -> "Unregistered connection type " + type)
            )
        );

    Codec<AthenaConnection> CODEC = Codec.withAlternative(TYPE_CODEC
        .dispatch(AthenaConnection::codec, Function.identity()), False.CODEC.codec()) ;

    MapCodec<AthenaConnection> CONNECTS_TO_CODEC = CODEC
        .optionalFieldOf("connect_to", SameState.INSTANCE);

    JsonObject toJson();

    MapCodec<? extends  AthenaConnection> codec();

    record Tag(TagKey<Block> tag) implements AthenaConnection {
        private static final MapCodec<Tag> CODEC = TagKey
            .codec(Registries.BLOCK)
            .fieldOf("tag")
            .xmap(Tag::new, Tag::tag);

        static {
            CONDITION_TYPES.put("tag", Tag.CODEC);
        }

        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", "tag");
            jsonObject.addProperty("tag", tag.toString());
            return jsonObject;
        }

        @Override
        public MapCodec<Tag> codec() {
            return CODEC;
        }

    }

    record SameState() implements AthenaConnection {
        public static final SameState INSTANCE = new SameState();
        private static final MapCodec<SameState> CODEC = MapCodec.unit(INSTANCE);

        static {
            CONDITION_TYPES.put("samestate", SameState.CODEC);
        }

        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", "samestate");
            return jsonObject;
        }

        @Override
        public MapCodec<SameState> codec() {
            return CODEC;
        }

    }

    record SameBlock() implements AthenaConnection {
        public static final SameBlock INSTANCE = new SameBlock();
        private static final MapCodec<SameBlock> CODEC = MapCodec.unit(INSTANCE);

        static {
            CONDITION_TYPES.put("sameblock", SameBlock.CODEC);
        }

        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", "sameblock");
            return jsonObject;
        }

        @Override
        public MapCodec<SameBlock> codec() {
            return CODEC;
        }

    }

    record False() implements AthenaConnection {
        public static final False INSTANCE = new False();
        private static final MapCodec<False> CODEC = MapCodec.unit(INSTANCE);

        static {
            CONDITION_TYPES.put("false", False.CODEC);
        }

        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", "false");
            return jsonObject;
        }

        @Override
        public MapCodec<False> codec() {
            return CODEC;
        }

    }
}
