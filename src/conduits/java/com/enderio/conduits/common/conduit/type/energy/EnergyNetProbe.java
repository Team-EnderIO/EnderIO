package com.enderio.conduits.common.conduit.type.energy;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class EnergyNetProbe {
    private long energyIn;
    private long energyOut;
    private final int ticks;
    private int ticksRemaining;
    
    public EnergyNetProbe(int ticks) {
        this.ticks = ticks;
        ticksRemaining = ticks;
    }
    
    public void addEnergyIn(long energyIn) {
        this.energyIn += energyIn;
    }
    
    public void addEnergyOut(long energyOut) {
        this.energyOut += energyOut;
    }
    
    public boolean tick() {
        return ticksRemaining-- <= 0;
    }

    public void finishProbe(Player player, long totalConduitEnergy, long totalConduitEnergyStorage,
        long machineEnergy, long machineEnergyStorage,
        long externalEnergy, long externalEnergyStorage
    ) {
        StringBuilder builder = new StringBuilder();
        DecimalFormat df = new DecimalFormat();
        df.setRoundingMode(RoundingMode.HALF_EVEN);
        String netBuffers = builder.append(" - Conduit Storage: ").append(totalConduitEnergy).append(" / ").append(totalConduitEnergyStorage).append(" µI").toString();
        builder.setLength(0);
        String machineBuffers = builder.append(" - Machine Buffers: ").append(machineEnergy).append(" / ").append(machineEnergyStorage).append(" µI").toString();
        builder.setLength(0);
        String storageBanks = builder.append(" - Energy Buffers: ").append(externalEnergy).append(" / ").append(externalEnergyStorage).append(" µI").toString();
        builder.setLength(0);
        String netInput = builder.append(" - Average input over ").append(df.format(ticks / 20f)).append(" seconds: ").append(energyIn / ticks).append(" µI/t").toString();
        builder.setLength(0);
        String netOutput = builder.append(" - Average output over ").append(df.format(ticks / 20f)).append(" seconds: ").append(energyOut / ticks).append(" µI/t").toString();
        player.sendSystemMessage(Component.literal("Energy Network").withStyle(ChatFormatting.GREEN));
        player.sendSystemMessage(Component.literal(netBuffers).withStyle(ChatFormatting.DARK_AQUA));
        player.sendSystemMessage(Component.literal(storageBanks).withStyle(ChatFormatting.DARK_AQUA));
        player.sendSystemMessage(Component.literal(machineBuffers).withStyle(ChatFormatting.DARK_AQUA));
        player.sendSystemMessage(Component.literal(netInput).withStyle(ChatFormatting.BLUE));
        player.sendSystemMessage(Component.literal(netOutput).withStyle(ChatFormatting.BLUE));
    }
}
