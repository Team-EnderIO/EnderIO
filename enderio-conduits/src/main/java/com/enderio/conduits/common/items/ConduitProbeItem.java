package com.enderio.conduits.common.items;

import com.enderio.conduits.common.conduit.bundle.ConduitBundleBlockEntity;
import com.enderio.conduits.common.init.ConduitComponents;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.conduits.common.network.C2SSyncProbeStatePacket;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
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
        ProbeConfigData configData = new ProbeConfigData(new HashMap<>(), true, true);

        // Get conduits and copy conduit data
        var conduits = conduitBlock.getConduits();
        if (conduits.isEmpty()) {
            return;
        }

        conduits.forEach(conduitType -> {
            var connectionState = conduitBlock.getConnectionConfig(conduitType, face);
            CompoundTag typeTag = new CompoundTag();

            if (connectionState != null) {
                // Copy connection state data
                typeTag.putBoolean("is_insert", connectionState.isConnected());
                typeTag.putBoolean("is_extract", connectionState.isConnected());
                // Add more data copying as needed
            } else {
                typeTag.putBoolean("is_insert", false);
                typeTag.putBoolean("is_extract", false);
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

        // StringBuilder builder = new StringBuilder();
        // for (String s : getState(stack).toString().toLowerCase().split("_")) {
        //     builder.append(StringUtils.capitalize(s));
        //     builder.append(" ");
        // }
        // builder.deleteCharAt(builder.length() - 1);
        tooltipComponents.add(ConduitLang.CONDUIT_PROBE_MODE_TOOLTIP);

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

        System.out.println(String.format("switch state to %s", newState));
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

    public record ProbeConfigData(Map<ResourceLocation, CompoundTag> conduitData, boolean isCopyMode, boolean includeFilters) {
        public static final Codec<ProbeConfigData> CODEC = RecordCodecBuilder.create(
            componentInstance -> componentInstance
                .group(
                    Codec.unboundedMap(ResourceLocation.CODEC, CompoundTag.CODEC)
                        .fieldOf("conduit_data")
                        .forGetter(ProbeConfigData::conduitData),
                    Codec.BOOL.fieldOf("is_copy_mode").forGetter(ProbeConfigData::isCopyMode),
                    Codec.BOOL.fieldOf("include_filters").forGetter(ProbeConfigData::includeFilters))
                .apply(componentInstance, ProbeConfigData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ProbeConfigData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.COMPOUND_TAG),
            ProbeConfigData::conduitData,
            ByteBufCodecs.BOOL,
            ProbeConfigData::isCopyMode,
            ByteBufCodecs.BOOL,
            ProbeConfigData::includeFilters,
            ProbeConfigData::new);
    }
}
