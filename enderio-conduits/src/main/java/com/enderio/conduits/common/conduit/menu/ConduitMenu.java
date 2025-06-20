package com.enderio.conduits.common.conduit.menu;

import com.enderio.base.api.UseOnly;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.connection.config.ConnectionConfig;
import com.enderio.conduits.api.connection.config.ConnectionConfigType;
import com.enderio.conduits.common.conduit.bundle.ConduitBundleBlockEntity;
import com.enderio.conduits.common.init.ConduitMenus;
import com.enderio.conduits.common.network.C2SOpenConduitFilterMenu;
import com.enderio.conduits.common.network.S2CConduitExtraGuiDataPacket;
import com.enderio.conduits.common.network.S2CConduitListPacket;
import com.enderio.conduits.common.network.SetConduitConnectionConfigPacket;
import com.enderio.core.common.menu.BaseEnderMenu;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class ConduitMenu extends BaseEnderMenu {

    public static void openConduitMenu(ServerPlayer serverPlayer, ConduitBundleBlockEntity conduitBundle,
            Direction side, Holder<Conduit<?, ?>> conduit) {
        serverPlayer.openMenu(new MenuProvider(conduitBundle, side, conduit), buf -> {
            buf.writeBlockPos(conduitBundle.getBlockPos());
            buf.writeEnum(side);
            Conduit.STREAM_CODEC.encode(buf, conduit);
            ClientConnectionAccessor.writeStartingSyncData(conduitBundle, conduit, side, buf);
        });
    }

    public static final int BUTTON_CHANGE_CONDUIT_START_ID = 0;
    public static final int BUTTON_CHANGE_CONDUIT_ID_COUNT = ConduitBundleBlockEntity.MAX_CONDUITS;

    private final BlockPos pos;
    private final Holder<Conduit<?, ?>> conduit;
    private final Direction side;
    private final ConnectionAccessor connectionAccessor;

    @Nullable
    private final IItemHandlerModifiable conduitInventory;

    @UseOnly(LogicalSide.SERVER)
    private ConnectionConfig remoteConnectionConfig;

    @UseOnly(LogicalSide.SERVER)
    private CompoundTag remoteExtraGuiData;

    @UseOnly(LogicalSide.SERVER)
    private int conduitListHashCode;

    public ConduitMenu(int containerId, Inventory playerInventory, ConduitBundleBlockEntity conduitBundle,
            Holder<Conduit<?, ?>> conduit, Direction side) {
        super(ConduitMenus.CONDUIT_MENU.get(), containerId, playerInventory);

        this.pos = conduitBundle.getBlockPos();
        this.side = side;
        this.conduit = conduit;
        this.connectionAccessor = conduitBundle;

        // Set to sensible defaults to allow a sync after the menu opens
        this.remoteConnectionConfig = conduit.value().connectionConfigType().getDefault();
        this.conduitInventory = conduitBundle.getConnectionInventory(conduit, side);

        addSlots();
    }

    public ConduitMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(ConduitMenus.CONDUIT_MENU.get(), containerId, playerInventory);

        pos = buf.readBlockPos();
        side = buf.readEnum(Direction.class);
        conduit = Conduit.STREAM_CODEC.decode(buf);

        // Uses initially transmitted state to immediately show an up-to-date UI.
        this.connectionAccessor = new ClientConnectionAccessor(buf);

        // If this conduit has an inventory, create its client representation.
        if (conduit.value().getInventorySize() > 0) {
            this.conduitInventory = new ClientConduitInventory();
        } else {
            this.conduitInventory = null;
        }

        addSlots();
    }

    private void addSlots() {
        if (conduitInventory != null) {
            for (int i = 0; i < conduit.value().getInventorySize(); i++) {
                var pos = conduit.value().getInventorySlotPosition(i);
                addSlot(new SlotItemHandler(conduitInventory, i, pos.x, pos.y));
            }
        }

        addPlayerInventorySlots(23, 113);
    }

    public BlockPos getBlockPos() {
        return pos;
    }

    public Direction getSide() {
        return side;
    }

    public Holder<Conduit<?, ?>> getConduit() {
        return conduit;
    }

    public List<Holder<Conduit<?, ?>>> getConnectedConduits() {
        return connectionAccessor.getAllOpenableConduits(side);
    }

    @Nullable
    public IItemHandler getConduitInventory() {
        return conduitInventory;
    }

    @EnsureSide(EnsureSide.Side.CLIENT)
    public void setConnectedConduits(List<Holder<Conduit<?, ?>>> connectedConduits) {
        if (connectionAccessor instanceof ClientConnectionAccessor clientConnectionAccessor) {
            clientConnectionAccessor.connectedConduits = connectedConduits;
        }
    }

    public ConnectionConfigType<?> connectionConfigType() {
        return conduit.value().connectionConfigType();
    }

    public ConnectionConfig connectionConfig() {
        return connectionAccessor.getConnectionConfig(conduit, side);
    }

    public <T extends ConnectionConfig> T connectionConfig(ConnectionConfigType<T> type) {
        var config = connectionConfig();
        if (config.type() == type) {
            // noinspection unchecked
            return (T) config;
        }

        throw new IllegalStateException("Connection config type mismatch");
    }

    public void setConnectionConfig(ConnectionConfig config) {
        if (getPlayerInventory().player instanceof LocalPlayer localPlayer) {
            // Prevent editing while player is in spectator mode.
            if (!localPlayer.isSpectator()) {
                connectionAccessor.setConnectionConfig(conduit, side, config);
                PacketDistributor.sendToServer(new SetConduitConnectionConfigPacket(containerId, config));
            }
        } else {
            connectionAccessor.setConnectionConfig(conduit, side, config);
        }
    }

    public void handleConnectionConfigUpdate(SetConduitConnectionConfigPacket packet) {
        connectionAccessor.setConnectionConfig(conduit, side, packet.connectionConfig());
    }

    @Nullable
    public CompoundTag extraGuiData() {
        return connectionAccessor.getConduitExtraGuiData(conduit, side);
    }

    @EnsureSide(EnsureSide.Side.CLIENT)
    public void setExtraGuiData(CompoundTag extraGuiData) {
        if (connectionAccessor instanceof ClientConnectionAccessor clientConnectionAccessor) {
            clientConnectionAccessor.extraGuiData = extraGuiData;
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return connectionAccessor.stillValid(player) && connectionAccessor.canOpenScreen(conduit, side);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (player instanceof ServerPlayer serverPlayer) {
            if (id >= BUTTON_CHANGE_CONDUIT_START_ID
                    && id <= BUTTON_CHANGE_CONDUIT_ID_COUNT + BUTTON_CHANGE_CONDUIT_ID_COUNT) {
                int conduitIndex = id - BUTTON_CHANGE_CONDUIT_START_ID;
                var connectedConduits = getConnectedConduits();
                if (conduitIndex < connectedConduits.size()) {
                    openConduitMenu(serverPlayer, (ConduitBundleBlockEntity) connectionAccessor, side,
                            connectedConduits.get(conduitIndex));
                }
            }
        }

        return super.clickMenuButton(player, id);
    }

    // TODO
    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    public void tryOpenFilterMenu(int slot) {
        if (getPlayerInventory().player instanceof ServerPlayer serverPlayer) {
            var stack = conduitInventory.getStackInSlot(slot);
            var menuProvider = stack.getCapability(EIOCapabilities.FILTER_MENU_PROVIDER);
            if (menuProvider != null) {
                menuProvider.openMenu(serverPlayer, conduitInventory, slot, () -> openConduitMenu(serverPlayer,
                        (ConduitBundleBlockEntity) connectionAccessor, side, conduit));
            }
        } else {
            PacketDistributor.sendToServer(new C2SOpenConduitFilterMenu(containerId, slot));
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        // Belt & Braces check to ensure the menu is in a valid state before accessing connections
        if (getPlayerInventory().player instanceof ServerPlayer serverPlayer && stillValid(serverPlayer)) {
            if (!Objects.equals(connectionConfig(), remoteConnectionConfig)) {
                PacketDistributor.sendToPlayer(serverPlayer,
                        new SetConduitConnectionConfigPacket(containerId, connectionConfig()));
                this.remoteConnectionConfig = connectionConfig();
            }

            var extraGuiData = extraGuiData();
            if (!Objects.equals(extraGuiData, remoteExtraGuiData)) {
                PacketDistributor.sendToPlayer(serverPlayer,
                        new S2CConduitExtraGuiDataPacket(containerId, extraGuiData));
                this.remoteExtraGuiData = extraGuiData;
            }

            var conduitList = connectionAccessor.getAllOpenableConduits(side);
            if (conduitListHashCode != conduitList.hashCode()) {
                PacketDistributor.sendToPlayer(serverPlayer, new S2CConduitListPacket(containerId, conduitList));
                conduitListHashCode = conduitList.hashCode();
            }
        }
    }

    public interface ConnectionAccessor {
        List<Holder<Conduit<?, ?>>> getAllOpenableConduits(Direction side);

        ConnectionConfig getConnectionConfig(Holder<Conduit<?, ?>> conduit, Direction side);

        void setConnectionConfig(Holder<Conduit<?, ?>> conduit, Direction side, ConnectionConfig config);

        boolean canOpenScreen(Holder<Conduit<?, ?>> conduit, Direction side);

        @Nullable
        CompoundTag getConduitExtraGuiData(Holder<Conduit<?, ?>> conduit, Direction side);

        boolean stillValid(Player player);
    }

    private static class ClientConnectionAccessor implements ConnectionAccessor {

        private List<Holder<Conduit<?, ?>>> connectedConduits = List.of();

        private ConnectionConfig connectionConfig;

        @Nullable
        private CompoundTag extraGuiData;

        public ClientConnectionAccessor(RegistryFriendlyByteBuf buf) {
            this.connectedConduits = Conduit.STREAM_CODEC
                    .apply(ByteBufCodecs.list(ConduitBundleBlockEntity.MAX_CONDUITS))
                    .decode(buf);

            this.connectionConfig = ConnectionConfig.STREAM_CODEC.decode(buf);

            extraGuiData = ByteBufCodecs.optional(ByteBufCodecs.COMPOUND_TAG)
                    .map(opt -> opt.orElse(null), Optional::ofNullable)
                    .decode(buf);
        }

        private static void writeStartingSyncData(ConduitBundleBlockEntity conduitBundle, Holder<Conduit<?, ?>> conduit,
                Direction side, RegistryFriendlyByteBuf buf) {
            Conduit.STREAM_CODEC.apply(ByteBufCodecs.list(ConduitBundleBlockEntity.MAX_CONDUITS))
                    .encode(buf, conduitBundle.getAllOpenableConduits(side));

            ConnectionConfig.STREAM_CODEC.encode(buf, conduitBundle.getConnectionConfig(conduit, side));

            // noinspection DataFlowIssue
            ByteBufCodecs.optional(ByteBufCodecs.COMPOUND_TAG)
                    .map(opt -> opt.orElse(null), Optional::ofNullable)
                    .encode(buf, conduitBundle.getConduitExtraGuiData(conduit, side));
        }

        @Override
        public List<Holder<Conduit<?, ?>>> getAllOpenableConduits(Direction side) {
            return connectedConduits;
        }

        @Override
        public ConnectionConfig getConnectionConfig(Holder<Conduit<?, ?>> conduit, Direction side) {
            return connectionConfig;
        }

        @Override
        public void setConnectionConfig(Holder<Conduit<?, ?>> conduit, Direction side, ConnectionConfig config) {
            connectionConfig = config;
        }

        @Override
        public boolean canOpenScreen(Holder<Conduit<?, ?>> conduit, Direction side) {
            return true;
        }

        @Override
        public CompoundTag getConduitExtraGuiData(Holder<Conduit<?, ?>> conduit, Direction side) {
            return extraGuiData;
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }
    }

    private class ClientConduitInventory extends ItemStackHandler {
        private ClientConduitInventory() {
            super(conduit.value().getInventorySize());
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return conduit.value().isItemValid(slot, stack);
        }
    }

    private record MenuProvider(ConduitBundleBlockEntity conduitBundle, Direction side, Holder<Conduit<?, ?>> conduit)
            implements net.minecraft.world.MenuProvider {

        @Override
        public Component getDisplayName() {
            return conduit.value().description();
        }

        @Override
        public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
            return new ConduitMenu(containerId, inventory, conduitBundle, conduit, side);
        }

        @Override
        public boolean shouldTriggerClientSideContainerClosingOnOpen() {
            // Prevents the mouse from jumping when changing between conduits.
            return false;
        }
    }
}
