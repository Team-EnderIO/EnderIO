package com.enderio.enderio.common.conduits.probe;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import com.enderio.enderio.common.conduits.bundle.ConduitBundleBlockEntity;
import com.enderio.enderio.common.init.ConduitComponents;
import com.enderio.enderio.common.init.ConduitItems;
import com.enderio.enderio.common.init.ConduitLang;
import com.enderio.enderio.common.network.packets.ServerboundSyncProbeStatePacket;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ConduitProbeItem extends Item {

    public ConduitProbeItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext pContext) {
        BlockEntity block = pContext.getLevel().getBlockEntity(pContext.getClickedPos());
        Player player = pContext.getPlayer();
        if (player == null) {
            return super.onItemUseFirst(stack, pContext);
        }
        if (pContext.getLevel().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(block instanceof ConduitBundleBlockEntity conduit)) {
            return super.onItemUseFirst(stack, pContext);
        }

        var conduitConnection = conduit.getShape().getConnectionFromHit(pContext.getClickedPos(), pContext.getHitResult());
        if (conduitConnection == null) {
            return InteractionResult.FAIL;
        }

        Direction face = conduitConnection.getFirst();
        switch (getState(stack)) {
            case COPY_PASTE -> {
                if (pContext.isSecondaryUseActive()) {
                    handleCopy(conduit, face, stack, player);
                } else {
                    handlePaste(conduit, face, stack, player);
                }
            }
            case PROBE -> {
                player.sendSystemMessage(Component.literal("This feature isn't implemented yet.").withStyle(ChatFormatting.RED));
            }
        }
        return InteractionResult.SUCCESS;
    }

    private static final Set<String> SKIP_FIELDS = Set.of("DEFAULT", "CODEC", "STREAM_CODEC", "TYPE");

    private void handleCopy(ConduitBundleBlockEntity conduitBlock, Direction face, ItemStack itemStack, Player player) {
        ProbeConfigData configData = new ProbeConfigData(new HashMap<>());

        var conduits = conduitBlock.getConduits();
        conduits.forEach(conduit -> {
            ConnectionConfig connectionConfig = conduitBlock.getConnectionConfig(conduit, face);
            if (connectionConfig != null) {
                ResourceLocation conduitKey = ResourceLocation.parse(conduit.getRegisteredName());
                RegistryOps<Tag> ops = net.minecraft.resources.RegistryOps.create(
                    net.minecraft.nbt.NbtOps.INSTANCE,
                    conduitBlock.getLevel().registryAccess()
                );
                Optional<Tag> nbt = ConnectionConfig.GENERIC_CODEC
                    .encodeStart(ops, connectionConfig)
                    .result();
                nbt.flatMap(tag -> ConnectionConfig.GENERIC_CODEC
                        .parse(ops, tag)
                        .result()
                )
                .ifPresent(cloned -> configData.conduitData().put(conduitKey, cloned));
            }
        });
        itemStack.set(ConduitComponents.PROBE_CONFIG, configData);

        if (!configData.conduitData().isEmpty()) {
            MutableComponent message = Component.empty();
            configData.conduitData().forEach((conduitKey, connectionConfig) -> {
                String conduitName = conduitKeyToDisplayName(conduitKey);
                message.append(Component.literal("\n" + conduitName + ":\n").withStyle(ChatFormatting.UNDERLINE));

                for (Field field : connectionConfig.getClass().getDeclaredFields()) {
                    if (SKIP_FIELDS.contains(field.getName())) continue;
                    message.append(createFieldTextComponent(field, connectionConfig));
                }
            });
            player.sendSystemMessage(TooltipUtil.withArgs(ConduitLang.CONDUIT_PROBE_MESSAGE_COPIED, message));
        }
    }

    public void handlePaste(ConduitBundleBlockEntity conduitBlock, Direction face, ItemStack itemStack, Player player) {
        ProbeConfigData configData = itemStack.get(ConduitComponents.PROBE_CONFIG);
        if (configData == null) {
            return;
        }

        List<String> pastedConduits = new ArrayList<String>();
        var conduits = conduitBlock.getConduits();
        conduits.forEach(conduit -> {
            ResourceLocation conduitKey = ResourceLocation.parse(conduit.getRegisteredName());
            ConnectionConfig storedConfig = configData.conduitData().get(conduitKey);
            if (storedConfig != null) {
                conduitBlock.setConnectionConfig(conduit, face, storedConfig);
                pastedConduits.add(conduitKeyToDisplayName(conduitKey));
            }
        });

        if (!pastedConduits.isEmpty()) {
            String pastedConduitsString = String.join(", ", pastedConduits);
            player.sendSystemMessage(TooltipUtil.withArgs(ConduitLang.CONDUIT_PROBE_MESSAGE_PASTED, pastedConduitsString));
        }
        conduitBlock.setChanged();
        conduitBlock.updateShape();
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        tooltipComponents.add(TooltipUtil.styledWithArgs(ConduitLang.CONDUIT_PROBE_MODE_TOOLTIP, getState(stack).getStateText()));
        ProbeConfigData configData = stack.get(ConduitComponents.PROBE_CONFIG);
        if (configData != null && !configData.conduitData().isEmpty()) {
            
            tooltipComponents.add(ConduitLang.CONDUIT_PROBE_CONTAINS_COPIED.withStyle(ChatFormatting.GRAY));
            configData.conduitData().keySet().forEach(conduitKey -> {
                tooltipComponents.add(Component.literal("- " + conduitKeyToDisplayName(conduitKey)).withStyle(ChatFormatting.DARK_GRAY));
            });
        }
    }

    public static State getState(ItemStack stack) {
        return stack.getOrDefault(ConduitComponents.PROBE_STATE, State.PROBE);
    }
    
    public static void setState(Player player, ItemStack stack, State state) {
        if (!stack.is(ConduitItems.CONDUIT_PROBE)) {
            throw new IllegalArgumentException("Invalid item passed to setState.");
        }

        stack.set(ConduitComponents.PROBE_STATE, state);

        if (player.level().isClientSide()) {
            PacketDistributor.sendToServer(new ServerboundSyncProbeStatePacket(state));
        }
    }

    public static void switchState(Player player, ItemStack stack) {
        if (!stack.is(ConduitItems.CONDUIT_PROBE)) {
            throw new IllegalArgumentException("Invalid item passed to switchState.");
        }

        State currentState = getState(stack);
        State newState = State.values()[(currentState.ordinal() + 1) % State.values().length];

        setState(player, stack, newState);
        player.displayClientMessage(TooltipUtil.withArgs(ConduitLang.CONDUIT_PROBE_MESSAGE_SWITCHED_MODE, newState.getStateText()), true);
    }

    private String conduitKeyToDisplayName(ResourceLocation conduitKey) {
        String translationKey = "item." + conduitKey.getNamespace() + ".conduit." + conduitKey.getPath();
        return Component.translatable(translationKey).getString();
    }

    private Component createFieldTextComponent(Field field, ConnectionConfig connectionConfig) {
        StringBuilder sb = new StringBuilder();
        sb.append(" - " + field.getName() + ": ");
        try {
            field.setAccessible(true);
            Object value = field.get(connectionConfig);
            sb.append(value);
        } catch (IllegalAccessException e) {
            sb.append(": <unable to access>");
        }
        sb.append("\n");
        return Component.literal(sb.toString()).withStyle(ChatFormatting.GRAY);
    }

    public enum State {
        PROBE,
        COPY_PASTE;

        public Component getStateText() {
            return switch (this) {
                case PROBE -> ConduitLang.CONDUIT_PROBE_STATE_PROBE;
                case COPY_PASTE -> ConduitLang.CONDUIT_PROBE_STATE_COPY_PASTE;
            };
        }

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
