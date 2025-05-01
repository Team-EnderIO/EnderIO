package com.enderio.machines.common.blocks.enderface;

import com.enderio.base.api.travel.TravelTarget;
import com.enderio.base.api.travel.TravelTargetApi;
import com.enderio.base.common.block.EIOBlockEntity;
import com.enderio.base.common.config.BaseConfig;
import com.enderio.core.common.network.NetworkDataSlot;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineFeatureFlags;
import com.enderio.machines.common.travel.EnderfaceTravelTarget;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public class EnderfaceBlockEntity extends EIOBlockEntity {
    private float lastUiPitch = -45;
    private float lastUiYaw = 45;
    private float lastUiDistance = 10;

    private final NetworkDataSlot<EnderfaceTravelTarget> travelTargetDataSlot;

    public EnderfaceBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MachineBlockEntities.ENDERFACE.get(), worldPosition, blockState);

        travelTargetDataSlot = addDataSlot(
                EnderfaceTravelTarget.DATA_SLOT_TYPE.create(this::getOrCreateTravelTarget, this::setTravelTarget));
    }

    @Override
    protected void saveAdditionalSynced(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditionalSynced(tag, registries);
        // TODO save ui pitch, etc
    }

    public float getLastUiPitch() {
        return lastUiPitch;
    }

    public float getLastUiYaw() {
        return lastUiYaw;
    }

    public float getLastUiDistance() {
        return lastUiDistance;
    }

    public void setLastUiPitch(float lastUiPitch) {
        this.lastUiPitch = lastUiPitch;
    }

    public void setLastUiYaw(float lastUiYaw) {
        this.lastUiYaw = lastUiYaw;
    }

    public void setLastUiDistance(float lastUiDistance) {
        this.lastUiDistance = lastUiDistance;
    }

    private EnderfaceTravelTarget getOrCreateTravelTarget() {
        Optional<TravelTarget> travelTarget = TravelTargetApi.INSTANCE.get(level, worldPosition);
        if (travelTarget.isPresent() && travelTarget.get() instanceof EnderfaceTravelTarget anchorTravelTarget) {
            return anchorTravelTarget;
        }

        EnderfaceTravelTarget anchorTravelTarget = new EnderfaceTravelTarget(worldPosition);
        setTravelTarget(anchorTravelTarget);
        return anchorTravelTarget;
    }

    private void setTravelTarget(EnderfaceTravelTarget target) {
        TravelTargetApi.INSTANCE.set(level, target);
    }

    public boolean canBeUsedByPlayer(Player player) {
        // TODO public/private enderfaces
        int usageRange = BaseConfig.COMMON.ITEMS.TRAVELLING_TO_BLOCK_RANGE.get();
        return worldPosition.distToCenterSqr(player.position()) <= usageRange * usageRange;
    }

    public static boolean canPlayerInteractWithBlock(Player player, Level level, BlockPos pos) {
        if (!level.enabledFeatures().contains(MachineFeatureFlags.ENDERFACE)) {
            return false;
        }

        // Iterate over all nearby chunks and look for a close enough enderface
        int range = MachinesConfig.COMMON.ENDERFACE_RANGE.getAsInt();
        int rangeSqr = range * range;
        int x1 = SectionPos.blockToSectionCoord(pos.getX() - range),
                x2 = SectionPos.blockToSectionCoord(pos.getX() + range),
                z1 = SectionPos.blockToSectionCoord(pos.getZ() - range),
                z2 = SectionPos.blockToSectionCoord(pos.getZ() + range);
        for (int z = z1; z <= z2; z++) {
            for (int x = x1; x <= x2; x++) {
                var chunk = level.getChunk(x, z, ChunkStatus.FULL, false);
                if (chunk instanceof LevelChunk levelChunk) {
                    var blockEntities = levelChunk.getBlockEntities();
                    if (!blockEntities.isEmpty()) {
                        for (BlockPos bePos : blockEntities.keySet()) {
                            if (bePos.distSqr(pos) <= rangeSqr
                                    && chunk.getBlockEntity(bePos) instanceof EnderfaceBlockEntity enderface
                                    && enderface.canBeUsedByPlayer(player)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
