package com.enderio.modconduits.mods.pneumaticcraft;

import com.enderio.conduits.api.ConduitNode;
import it.unimi.dsi.fastutil.floats.FloatPredicate;
import me.desht.pneumaticcraft.api.pressure.PressureTier;
import me.desht.pneumaticcraft.api.tileentity.IAirHandlerMachine;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public record PressureConduitStorage(PressureTier tier, ConduitNode node) implements IAirHandlerMachine {


    @Override
    public float getDangerPressure() {
        return tier.getDangerPressure();
    }

    @Override
    public float getCriticalPressure() {
        return tier.getCriticalPressure();
    }

    @Override
    public void setPressure(float v) {
        addAir(((int) (getPressure() * getVolume())) - getAir());
    }

    @Override
    public void setVolumeUpgrades(int i) {

    }

    @Override
    public void enableSafetyVenting(FloatPredicate floatPredicate, Direction direction) {

    }

    @Override
    public void disableSafetyVenting() {

    }

    @Override
    public void tick(BlockEntity blockEntity) {

    }

    @Override
    public void setSideLeaking(@Nullable Direction direction) {

    }

    @Override
    public @Nullable Direction getSideLeaking() {
        return null;
    }

    @Override
    public List<Connection> getConnectedAirHandlers(BlockEntity blockEntity) {
        return List.of();
    }

    @Override
    public void setConnectableFaces(Collection<Direction> collection) {

    }

    @Override
    public Tag serializeNBT() {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {

    }

    @Override
    public void addPendingAir(int i) {

    }

    @Override
    public float getPressure() {
        return this.getAir() / (float) this.getVolume();
    }

    @Override
    public int getAir() {
        if (node.getParentGraph() == null) { //TODO why null?
            return 0;
        }
        PressureConduitContext context = node.getParentGraph().getContext(PneumaticModule.NetworkContexts.PRESSURE_NETWORK.get());
        if (context != null) {
            return context.getAir();
        }
        return 0;
    }

    @Override
    public void addAir(int i) {
        PressureConduitContext context = node.getParentGraph().getOrCreateContext(PneumaticModule.NetworkContexts.PRESSURE_NETWORK.get());
        context.setAir(context.getAir() + i);
    }

    @Override
    public int getBaseVolume() {
        if (node.getParentGraph() == null) { //TODO why null?
            return 0;
        }
        return node.getParentGraph().getNodes().size() * 1000;
    }

    @Override
    public void setBaseVolume(int i) {

    }

    @Override
    public int getVolume() {
        return getBaseVolume();
    }

    @Override
    public float maxPressure() {
        return 10f;
    }

    @Override
    public void printManometerMessage(Player player, List<Component> list) {

    }
}
