package com.enderio.conduits.common.items;

import com.enderio.conduits.common.conduit.bundle.ConduitBundleBlockEntity;
import com.enderio.conduits.common.init.ConduitComponents;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.conduits.common.network.C2SSyncProbeStatePacket;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.conduits.api.connection.config.ConnectionConfig;
import com.enderio.conduits.api.connection.config.IOConnectionConfig;
import com.enderio.conduits.api.connection.config.RedstoneSensitiveConnectionConfig;
import com.enderio.conduits.common.conduit.type.item.ItemConduitConnectionConfig;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduitConnectionConfig;
import com.enderio.conduits.common.conduit.type.energy.EnergyConduitConnectionConfig;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitConnectionConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipContext;
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
        Direction face = pContext.getClickedFace();
        BlockEntity block = pContext.getLevel().getBlockEntity(pContext.getClickedPos());

        if (block instanceof ConduitBundleBlockEntity conduit) {
            if (pContext.getLevel().isClientSide()) return InteractionResult.SUCCESS;

            switch (getState(stack)) {
                case COPY_PASTE -> {
                    if (pContext.isSecondaryUseActive()) {
                        handleCopy(conduit, face, stack);
                        System.out.println("handle copy");
                    } else {
                        handlePaste(conduit, face, stack);
                        System.out.println("handle paste");
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
        return super.onItemUseFirst(stack, pContext);
    }

    private void handleCopy(ConduitBundleBlockEntity conduitBlock, Direction face, ItemStack itemStack) {
        // Create probe config data
        ProbeConfigData configData = new ProbeConfigData(new HashMap<>());

        // Get conduits and copy conduit data
        var conduits = conduitBlock.getConduits();
        if (conduits.isEmpty()) {
            return;
        }

        conduits.forEach(conduitType -> {
            ConnectionConfig connectionConfig = conduitBlock.getConnectionConfig(conduitType, face);
            CompoundTag typeTag = new CompoundTag();

            if (connectionConfig instanceof IOConnectionConfig ioConfig) {
                typeTag.putBoolean("is_insert", ioConfig.isInsert());
                typeTag.putBoolean("is_extract", ioConfig.isExtract());
                typeTag.putInt("insert_channel", ioConfig.insertChannel().ordinal());
                typeTag.putInt("extract_channel", ioConfig.extractChannel().ordinal());

                if (ioConfig instanceof ItemConduitConnectionConfig itemConfig) {
                    typeTag.putBoolean("is_round_robin", itemConfig.isRoundRobin());
                    typeTag.putBoolean("is_self_feed", itemConfig.isSelfFeed());
                    typeTag.putInt("priority", itemConfig.priority());
                }
                else if (ioConfig instanceof FluidConduitConnectionConfig fluidConfig) {
                    typeTag.putInt("insert_priority", fluidConfig.insertPriority());
                }
                else if (ioConfig instanceof EnergyConduitConnectionConfig energyConfig) {
                    typeTag.putInt("priority", energyConfig.priority());
                }
                else if (ioConfig instanceof RedstoneConduitConnectionConfig redstoneConfig) {
                    typeTag.putBoolean("is_strong_output_signal", redstoneConfig.isStrongOutputSignal());
                }
            }
            // TODO: Check if AE2/RS conduits are capable of being disconnected manually
            // else {
            //     typeTag.putBoolean("is_connected", connectionConfig.isConnected());
            // }

            if (connectionConfig instanceof RedstoneSensitiveConnectionConfig redstoneSensitiveConfig) {
                typeTag.putInt("extract_redstone_control", redstoneSensitiveConfig.extractRedstoneControl().ordinal());
                typeTag.putInt("extract_redstone_channel", redstoneSensitiveConfig.extractRedstoneChannel().ordinal());
            }

            ResourceLocation conduitKey = ResourceLocation.parse(conduitType.getRegisteredName());
            configData.conduitData().put(conduitKey, typeTag);            
        });

        // Store the config data in the item stack
        itemStack.set(ConduitComponents.PROBE_CONFIG, configData);
    }
    
    public void handlePaste(ConduitBundleBlockEntity conduitBlock, Direction face, ItemStack itemStack) {
        ProbeConfigData configData = itemStack.get(ConduitComponents.PROBE_CONFIG);
        if (configData == null || configData.conduitData().isEmpty()) {
            return;
        }
        
        var conduits = conduitBlock.getConduits();
        for (var conduit : conduits) {
            ResourceLocation conduitKey = ResourceLocation.parse(conduit.getRegisteredName());
            CompoundTag typeTag = configData.conduitData().get(conduitKey);

            if (typeTag != null) {
                // Restore connection state from saved data (commented out until implementation is ready)
                // boolean isInsert = typeTag.getBoolean("is_insert");
                // boolean isExtract = typeTag.getBoolean("is_extract");
                
                // Create new connection state with restored data
                // var newConnectionState = createConnectionStateFromTag(typeTag, isInsert, isExtract);
                
                // Apply the connection config if needed
                // conduitBlock.setConnectionConfig(conduit, face, newConnectionConfig);
            }
        }

        conduitBlock.setChanged();
        conduitBlock.updateShape();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        if (!(stack.getItem() instanceof ConduitProbeItem)) {
            System.out.println("wrong item, no hover");
            return;
        }
        tooltipComponents.add(TooltipUtil.withArgs(ConduitLang.CONDUIT_PROBE_MODE_TOOLTIP, Component.literal(getStateText(getState(stack)))));

        // Add info about stored data if any
        ProbeConfigData configData = stack.get(ConduitComponents.PROBE_CONFIG);
        if (configData != null && !configData.conduitData().isEmpty()) {
            tooltipComponents.add(Component.literal("Contains copied conduit data").withStyle(ChatFormatting.GRAY));
        }

        super.appendHoverText(stack, context, tooltipComponents, isAdvanced);
    }

    public static State getState(ItemStack stack) {
        return stack.getOrDefault(ConduitComponents.PROBE_STATE, State.PROBE);
    }
    
    public static void setState(ItemStack stack, State state, boolean syncToServer) {
        stack.set(ConduitComponents.PROBE_STATE, state);
        if (syncToServer) {
            // Send network packet to sync state
            PacketDistributor.sendToServer(new C2SSyncProbeStatePacket(state));
        }
    }

    public static void switchState(ItemStack stack, boolean syncToServer) {
        State currentState = getState(stack);
        State newState = State.values()[(currentState.ordinal() + 1) % State.values().length];
        setState(stack, newState, syncToServer);

        System.out.println(String.format("switch state to %s", getStateText(newState)));
    }

    public static String getStateText(State state) {
        return switch (state) {
            case PROBE -> ConduitLang.CONDUIT_PROBE_STATE_PROBE.getString();
            case COPY_PASTE -> ConduitLang.CONDUIT_PROBE_STATE_COPY_PASTE.getString();
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

    public record ProbeConfigData(Map<ResourceLocation, CompoundTag> conduitData) {
        public static final Codec<ProbeConfigData> CODEC = RecordCodecBuilder.create(
            componentInstance -> componentInstance
                .group(
                    Codec.unboundedMap(ResourceLocation.CODEC, CompoundTag.CODEC)
                        .fieldOf("conduit_data")
                        .forGetter(ProbeConfigData::conduitData))
                .apply(componentInstance, ProbeConfigData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ProbeConfigData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.COMPOUND_TAG),
            ProbeConfigData::conduitData,
            ProbeConfigData::new);
    }
}
