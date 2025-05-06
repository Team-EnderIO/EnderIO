package com.enderio.machines.common.blocks.obelisks.attractor;

import com.enderio.base.api.UseOnly;
import com.enderio.base.api.capacitor.CapacitorModifier;
import com.enderio.base.api.capacitor.LinearScalable;
import com.enderio.base.api.capacitor.QuadraticScalable;
import com.enderio.base.api.filter.SoulFilter;
import com.enderio.base.api.io.energy.EnergyIOMode;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.machines.common.blocks.base.blockentity.flags.CapacitorSupport;
import com.enderio.machines.common.blocks.base.inventory.MachineInventoryLayout;
import com.enderio.machines.common.blocks.obelisks.ObeliskBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.obelisk.ObeliskAreaManager;
import com.mojang.authlib.GameProfile;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

public class AttractorObeliskBlockEntity extends ObeliskBlockEntity<AttractorObeliskBlockEntity> {

    private static final QuadraticScalable ENERGY_CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.ATTRACTOR_CAPACITY);
    private static final LinearScalable ENERGY_USAGE = new LinearScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.ATTRACTOR_USAGE);
    private static final LinearScalable RANGE = new LinearScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ATTRACTOR_RANGE);

    private final Vec3 targetPos;

    @UseOnly(LogicalSide.SERVER)
    private FakePlayer fakePlayer;

    public AttractorObeliskBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MachineBlockEntities.ATTRACTOR_OBELISK.get(), worldPosition, blockState, false, CapacitorSupport.REQUIRED,
                EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_USAGE);
        targetPos = new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
    }

    @Override
    protected @Nullable ObeliskAreaManager<AttractorObeliskBlockEntity> getAreaManager(ServerLevel level) {
        return null;
    }

    @Override
    public @Nullable MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder()
                .inputSlot((integer, itemStack) -> itemStack.getCapability(EIOCapabilities.SOUL_FILTER) != null)
                .slotAccess(FILTER)
                .capacitor()
                .build();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory pPlayerInventory, Player pPlayer) {
        return new AttractorObeliskMenu(containerId, pPlayerInventory, this);
    }

    @Override
    public int getMaxRange() {
        return RANGE.scaleI(this::getCapacitorData).get();
    }

    @Override
    public String getColor() {
        return MachinesConfig.CLIENT.BLOCKS.ATTRACTOR_RANGE_COLOR.get();
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (level instanceof ServerLevel sl) {
            if (this.getMachineOwner() == null) {
                this.setMachineOwner(UUID.randomUUID()); // Fallback
            }
            fakePlayer = new FakePlayer(sl, new GameProfile(getMachineOwner(), "enderio:attractor:" + worldPosition));
            fakePlayer.setPos(targetPos.x, targetPos.y, targetPos.z);
        }
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (isActive()) {
            doAttract();
        }
    }

    private void doAttract() {
        if (level == null) {
            return;
        }
        AABB aabb = getAABB();
        if (aabb == null) {
            return;
        }
        SoulFilter filter = getSoulFilter();
        if (filter == null) {
            return;
        }
        List<Mob> filteredEntities = level.getEntities(EntityTypeTest.forClass(Mob.class), aabb,
                mob -> filter.test(mob));
        for (Mob mob : filteredEntities) {
            if (!MachinesConfig.COMMON.ATTRACTOR_PULL_BOSSES.get() && mob.getType().is(Tags.EntityTypes.BOSSES)) {
                // ignore
            } else if (mob instanceof WitherBoss) {
                mob.goalSelector.disableControlFlag(Goal.Flag.TARGET);
                mob.goalSelector.disableControlFlag(Goal.Flag.LOOK);
                mob.setTarget(null);
                directPull(mob, 2.25f);
            } else if (mob instanceof PathfinderMob) {
                attractMob(mob);
            } else if (useTarget(mob)) {
                setTarget(mob);
            } else if (mob instanceof Ghast) {
                directPull(mob, 1);
            }
        }
    }

    private boolean useTarget(Mob mob) {
        return mob instanceof Slime || mob instanceof Phantom;
    }

    private void setTarget(Mob mob) {
        assert level != null;
        mob.setTarget(fakePlayer);
    }

    private void attractMob(Mob mob) {
        mob.goalSelector.enableControlFlag(Goal.Flag.MOVE);
        Vec3 moveOffset = targetPos.subtract(mob.getX(), mob.getY(), mob.getZ());
        // keep them 1 block away
        moveOffset = moveOffset.subtract(moveOffset.normalize());
        mob.getNavigation().moveTo(mob.getX() + moveOffset.x, mob.getY() + moveOffset.y, mob.getZ() + moveOffset.z, 1);
    }

    private void directPull(LivingEntity mob, float speed) {
        Vec3 dir = targetPos.subtract(new Vec3(mob.xo, mob.yo, mob.zo)).normalize();
        dir = dir.scale(speed);
        AABB aabb = mob.getBoundingBox();
        aabb = aabb.move(dir);
        if (level != null && level.noCollision(mob, aabb)) {
            mob.setDeltaMovement(dir);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (!level.isClientSide) {
            fakePlayer.discard();
        }
    }
}
