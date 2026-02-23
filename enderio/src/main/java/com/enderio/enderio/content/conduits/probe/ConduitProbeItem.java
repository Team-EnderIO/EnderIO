package com.enderio.enderio.content.conduits.probe;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import com.enderio.enderio.content.conduits.ConduitLang;
import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlockEntity;
import com.enderio.enderio.foundation.network.packets.ServerboundSyncProbeStatePacket;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@EventBusSubscriber(Dist.CLIENT)
public class ConduitProbeItem extends Item {

    public static final Identifier PROBE_STATE_PREDICATE = EnderIO.id("probe_state");

    public ConduitProbeItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        BlockEntity block = context.getLevel().getBlockEntity(context.getClickedPos());
        Player player = context.getPlayer();
        if (player == null) {
            return super.onItemUseFirst(stack, context);
        }
        if (context.getLevel().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(block instanceof ConduitBundleBlockEntity conduit)) {
            return super.onItemUseFirst(stack, context);
        }

        var conduitConnection = conduit.getShape().getConnectionFromHit(context.getClickedPos(), context.getHitResult());
        if (conduitConnection == null) {
            return InteractionResult.FAIL;
        }

        Direction face = conduitConnection.getFirst();
        switch (getState(stack)) {
            case COPY_PASTE -> {
                if (context.isSecondaryUseActive()) {
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
                Identifier conduitKey = Identifier.parse(conduit.getRegisteredName());
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
        itemStack.set(EIODataComponents.PROBE_CONFIG, configData);

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
        ProbeConfigData configData = itemStack.get(EIODataComponents.PROBE_CONFIG);
        if (configData == null) {
            return;
        }

        List<String> pastedConduits = new ArrayList<String>();
        var conduits = conduitBlock.getConduits();
        conduits.forEach(conduit -> {
            Identifier conduitKey = Identifier.parse(conduit.getRegisteredName());
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
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

        tooltipAdder.accept(TooltipUtil.styledWithArgs(ConduitLang.CONDUIT_PROBE_MODE_TOOLTIP, getState(stack).getStateText()));
        ProbeConfigData configData = stack.get(EIODataComponents.PROBE_CONFIG);
        if (configData != null && !configData.conduitData().isEmpty()) {

            tooltipAdder.accept(ConduitLang.CONDUIT_PROBE_CONTAINS_COPIED.withStyle(ChatFormatting.GRAY));
            configData.conduitData().keySet().forEach(conduitKey -> {
                tooltipAdder.accept(Component.literal("- " + conduitKeyToDisplayName(conduitKey)).withStyle(ChatFormatting.DARK_GRAY));
            });
        }
    }

    public static State getState(ItemStack stack) {
        return stack.getOrDefault(EIODataComponents.PROBE_STATE, State.PROBE);
    }
    
    public static void setState(Player player, ItemStack stack, State state) {
        if (!stack.is(EIOItems.CONDUIT_PROBE)) {
            throw new IllegalArgumentException("Invalid item passed to setState.");
        }

        stack.set(EIODataComponents.PROBE_STATE, state);

        if (player.level().isClientSide()) {
            ClientPacketDistributor.sendToServer(new ServerboundSyncProbeStatePacket(state));
        }
    }

    public static void switchState(Player player, ItemStack stack) {
        if (!stack.is(EIOItems.CONDUIT_PROBE)) {
            throw new IllegalArgumentException("Invalid item passed to switchState.");
        }

        State currentState = getState(stack);
        State newState = State.values()[(currentState.ordinal() + 1) % State.values().length];

        setState(player, stack, newState);
        player.sendOverlayMessage(TooltipUtil.withArgs(ConduitLang.CONDUIT_PROBE_MESSAGE_SWITCHED_MODE, newState.getStateText()));
    }

    private String conduitKeyToDisplayName(Identifier conduitKey) {
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

    public record ProbeConfigData(Map<Identifier, ConnectionConfig> conduitData) {
        public static final Codec<ProbeConfigData> CODEC = RecordCodecBuilder.create(
            componentInstance -> componentInstance
                .group(
                    Codec.unboundedMap(Identifier.CODEC, ConnectionConfig.GENERIC_CODEC)
                        .fieldOf("conduit_data")
                        .forGetter(ProbeConfigData::conduitData))
                .apply(componentInstance, ProbeConfigData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ProbeConfigData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, ConnectionConfig.STREAM_CODEC),
            ProbeConfigData::conduitData,
            ProbeConfigData::new);
    }

    //TODO Move?
    @SubscribeEvent
    public static void itemOverrides(RegisterConditionalItemModelPropertyEvent event) {
        event.register(PROBE_STATE_PREDICATE, Probe.MAP_CODEC);
    }

    public static class Probe implements ConditionalItemModelProperty {
        public static final MapCodec<Probe> MAP_CODEC = MapCodec.unit(new Probe());

        @Override
        public MapCodec<? extends ConditionalItemModelProperty> type() {
            return MAP_CODEC;
        }

        @Override
        public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
            return getState(stack) == State.PROBE;
        }
    }
}
