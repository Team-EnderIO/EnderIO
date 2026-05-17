package com.enderio.modded_conduits.common.modules.cc_tweaked;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.network.wired.WiredElement;
import dan200.computercraft.api.network.wired.WiredNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * A WiredElement implementation for CC: Tweaked network conduits.
 * Each ConduitBundleBlockEntity containing a CC conduit will have one of these,
 * exposed via NeoForge capabilities.
 */
public class CCConduitElement implements WiredElement {

    private final WiredNode node;
    private final Level level;
    private final BlockPos pos;

    public CCConduitElement(Level level, BlockPos pos) {
        this.level = level;
        this.pos = pos;
        this.node = ComputerCraftAPI.createWiredNodeForElement(this);
    }

    @Override
    public WiredNode getNode() {
        return node;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public Vec3 getPosition() {
        return Vec3.atCenterOf(pos);
    }

    @Override
    public String getSenderID() {
        return "enderio:cc_conduit";
    }
}
