package com.enderio.conduits.common.items;

import com.enderio.conduits.common.conduit.bundle.ConduitBundleBlockEntity;
import com.enderio.conduits.common.init.ConduitComponents;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.conduits.common.network.C2SSyncProbeStatePacket;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.conduits.api.connection.config.ConnectionConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConduitProbeItem extends Item {

    public ConduitProbeItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext pContext) {
        BlockEntity block = pContext.getLevel().getBlockEntity(pContext.getClickedPos());

        if (block instanceof ConduitBundleBlockEntity conduit) {
            if (pContext.getLevel().isClientSide()) return InteractionResult.SUCCESS;

            var conduitConnection = conduit.getShape().getConnectionFromHit(pContext.getClickedPos(), pContext.getHitResult());
            if (conduitConnection != null) {
                Direction face = conduitConnection.getFirst();
                switch (getState(stack)) {
                    case COPY_PASTE -> {
                        if (pContext.isSecondaryUseActive()) {
                            handleCopy(conduit, face, stack);
                        } else {
                            handlePaste(conduit, face, stack);
                        }
                    }
                    case PROBE -> {
                        var player = pContext.getPlayer();
                        if (player != null) {
                            player.sendSystemMessage(Component.literal("This feature isn't implemented yet.").withStyle(ChatFormatting.RED));
                        }
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.onItemUseFirst(stack, pContext);
    }

    private void handleCopy(ConduitBundleBlockEntity conduitBlock, Direction face, ItemStack itemStack) {
        ProbeConfigData configData = new ProbeConfigData(new HashMap<>());

        var conduits = conduitBlock.getConduits();
        conduits.forEach(conduitType -> {
            ConnectionConfig connectionConfig = conduitBlock.getConnectionConfig(conduitType, face);
            if (connectionConfig != null) {
                ResourceLocation conduitKey = ResourceLocation.parse(conduitType.getRegisteredName());
                configData.conduitData().put(conduitKey, connectionConfig);
            }
        });
        itemStack.set(ConduitComponents.PROBE_CONFIG, configData);
    }
    
    public void handlePaste(ConduitBundleBlockEntity conduitBlock, Direction face, ItemStack itemStack) {
        ProbeConfigData configData = itemStack.get(ConduitComponents.PROBE_CONFIG);
        if (configData == null) {
            return;
        }

        var conduits = conduitBlock.getConduits();
        for (var conduit : conduits) {
            ResourceLocation conduitKey = ResourceLocation.parse(conduit.getRegisteredName());
            ConnectionConfig storedConfig = configData.conduitData().get(conduitKey);
            if (storedConfig != null) {
                conduitBlock.setConnectionConfig(conduit, face, storedConfig);
            }
        }

        conduitBlock.setChanged();
        conduitBlock.updateShape();
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        tooltipComponents.add(TooltipUtil.withArgs(ConduitLang.CONDUIT_PROBE_MODE_TOOLTIP, getStateText(getState(stack))));
        ProbeConfigData configData = stack.get(ConduitComponents.PROBE_CONFIG);
        if (configData != null && !configData.conduitData().isEmpty()) {
            tooltipComponents.add(ConduitLang.CONDUIT_PROBE_CONTAINS_COPIED.withStyle(ChatFormatting.GRAY));
        }
    }

    public static State getState(ItemStack stack) {
        return stack.getOrDefault(ConduitComponents.PROBE_STATE, State.PROBE);
    }
    
    public static void setState(ItemStack stack, State state, boolean syncToServer) {
        stack.set(ConduitComponents.PROBE_STATE, state);
        if (syncToServer) {
            PacketDistributor.sendToServer(new C2SSyncProbeStatePacket(state));
        }
    }

    public static void switchState(ItemStack stack, boolean syncToServer) {
        State currentState = getState(stack);
        State newState = State.values()[(currentState.ordinal() + 1) % State.values().length];
        setState(stack, newState, syncToServer);

        System.out.println(String.format("switch state to %s", getStateText(newState)));
    }

    public static Component getStateText(State state) {
        return switch (state) {
            case PROBE -> ConduitLang.CONDUIT_PROBE_STATE_PROBE;
            case COPY_PASTE -> ConduitLang.CONDUIT_PROBE_STATE_COPY_PASTE;
        };
    }

    public enum State {
        PROBE,
        COPY_PASTE;

        public static final Codec<State> CODEC = Codec.STRING.xmap(
            State::valueOf,
            State::name
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, State> STREAM_CODEC = ByteBufCodecs.STRING_UTF8
            .map(State::valueOf, State::name)
            .cast();
    }

    public record ProbeConfigData(Map<ResourceLocation, ConnectionConfig> conduitData) {
        public static final Codec<ProbeConfigData> CODEC = RecordCodecBuilder.create(
            componentInstance -> componentInstance
                .group(
                    Codec.unboundedMap(ResourceLocation.CODEC, ConnectionConfig.GENERIC_CODEC)
                        .fieldOf("conduit_data")
                        .forGetter(ProbeConfigData::conduitData))
                .apply(componentInstance, ProbeConfigData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ProbeConfigData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ConnectionConfig.STREAM_CODEC),
            ProbeConfigData::conduitData,
            ProbeConfigData::new);
    }
}
