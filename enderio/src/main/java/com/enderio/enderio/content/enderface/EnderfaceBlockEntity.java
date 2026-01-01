package com.enderio.enderio.content.enderface;

import com.enderio.enderio.api.travel.TravelTarget;
import com.enderio.enderio.api.travel.TravelTargetApi;
import com.enderio.enderio.config.base.BaseConfig;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.block.EIOBlockEntity;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOFeatureFlags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Optional;

public class EnderfaceBlockEntity extends EIOBlockEntity {
    static String PITCH = "pitch";
    static String YAW = "yaw";
    static String DISTANCE = "distance";
    private float lastUiPitch = -45;
    private float lastUiYaw = 45;
    private float lastUiDistance = 10;

    public EnderfaceBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.ENDERFACE.get(), worldPosition, blockState);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);

        TravelTargetApi.INSTANCE.removeAt(level, pos);
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);

        getOrCreateTravelTarget();
    }

    @Override
    protected void saveAdditionalSynced(ValueOutput output) {
        super.saveAdditionalSynced(output);
        output.putFloat(PITCH, lastUiPitch);
        output.putFloat(YAW, lastUiYaw);
        output.putFloat(DISTANCE, lastUiDistance);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        lastUiPitch = input.getFloatOr(PITCH, -45);
        lastUiYaw = input.getFloatOr(YAW, 45);
        lastUiDistance = input.getFloatOr(DISTANCE, 10);
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
        if (!level.enabledFeatures().contains(EIOFeatureFlags.ENDERFACE)) {
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
