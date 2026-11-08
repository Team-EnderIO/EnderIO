package com.enderio.enderio.content.armory.effects.tools;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradableItem;
import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeEffect;
import com.enderio.enderio.api.armory.upgrades.conditions.AlwaysActiveCondition;
import com.enderio.enderio.api.armory.upgrades.conditions.EffectCondition;
import com.enderio.enderio.api.armory.upgrades.costs.EffectCost;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookType;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.MineBlockHook;
import com.enderio.enderio.content.armory.ItemDarkSteelUpgrades;
import com.enderio.enderio.init.EIOAttributes;
import com.enderio.enderio.init.EIODataComponents;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

@EventBusSubscriber
public class AreaMiningEffect extends DarkSteelUpgradeEffect implements MineBlockHook {

    public static final MapCodec<AreaMiningEffect> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(
            RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("targets").forGetter(AreaMiningEffect::targets),
            EffectCost.CODEC.fieldOf("cost_per_block").forGetter(AreaMiningEffect::costPerBlock),
            EffectCondition.CODEC.optionalFieldOf("condition", AlwaysActiveCondition.INSTANCE).forGetter(AreaMiningEffect::condition),
            Codec.BOOL.optionalFieldOf("align_to_feet", true).forGetter(AreaMiningEffect::alignToFeet),
            Codec.BOOL.optionalFieldOf("should_display_smoke_particles", true).forGetter(AreaMiningEffect::shouldDisplaySmokeParticles),
            Codec.BOOL.optionalFieldOf("should_display_explosion_particles", true).forGetter(AreaMiningEffect::shouldDisplayExplosionEffect)
        ).apply(inst, AreaMiningEffect::new));

    private static final List<DarkSteelUpgradeHookType<?>> HOOKS = List.of(DarkSteelUpgradeHookTypes.MINE_BLOCK);

    private static final Set<UUID> PLAYERS_MINING_AREA = new HashSet<>();

    // The face each player is mining, as reported by the client. This is what the client highlighted, so the mined area matches it.
    private static final Map<Player, MiningTarget> MINING_TARGETS = new WeakHashMap<>();

    private final Optional<HolderSet<Block>> targets;
    private final EffectCost costPerBlock;
    private final EffectCondition condition;
    private final boolean alignToFeet;
    private final boolean shouldDisplaySmokeParticles;
    private final boolean shouldDisplayExplosionParticles;

    public AreaMiningEffect(Optional<HolderSet<Block>> targets, EffectCost costPerBlock, EffectCondition condition, boolean alignToFeet,
        boolean shouldDisplaySmokeParticles, boolean shouldDisplayExplosionParticles) {
        this.targets = targets;
        this.costPerBlock = costPerBlock;
        this.condition = condition;
        this.alignToFeet = alignToFeet;
        this.shouldDisplaySmokeParticles = shouldDisplaySmokeParticles;
        this.shouldDisplayExplosionParticles = shouldDisplayExplosionParticles;
    }

    public Optional<HolderSet<Block>> targets() {
        return targets;
    }

    public EffectCost costPerBlock() {
        return costPerBlock;
    }

    public EffectCondition condition() {
        return condition;
    }

    public boolean alignToFeet() {
        return alignToFeet;
    }

    public boolean shouldDisplaySmokeParticles() {
        return shouldDisplaySmokeParticles;
    }

    public boolean shouldDisplayExplosionEffect() {
        return shouldDisplayExplosionParticles;
    }

    @Override
    public List<DarkSteelUpgradeHookType<?>> getActiveHooks() {
        return HOOKS;
    }

    @Override
    protected MapCodec<? extends DarkSteelUpgradeEffect> codec() {
        return CODEC;
    }

    /**
     * @return the area mining effect that applies to this stack, if any.
     */
    @Nullable
    public static AreaMiningEffect getActiveEffect(ItemStack stack) {
        if (!(stack.getItem() instanceof DarkSteelUpgradableItem upgradableItem)
            || !upgradableItem.availableHookTypes().contains(DarkSteelUpgradeHookTypes.MINE_BLOCK)) {
            return null;
        }

        ItemDarkSteelUpgrades upgrades = stack.get(EIODataComponents.DARK_STEEL_UPGRADES);
        if (upgrades == null) {
            return null;
        }

        List<AreaMiningEffect> effects = upgrades.getAllEffectsOfType(AreaMiningEffect.class);
        return effects.isEmpty() ? null : effects.getFirst();
    }

    /**
     * Gets the area that would be mined, including the origin. Shared with the client for the block highlight.
     * @param face the face of the origin block that was hit.
     * @return the area, or null if the player's area attributes are both zero.
     */
    @Nullable
    public BoundingBox getArea(Player player, BlockPos origin, Direction face) {
        int radius = (int) player.getAttributeValue(EIOAttributes.MINING_AREA_RADIUS);
        int depth = (int) player.getAttributeValue(EIOAttributes.MINING_AREA_DEPTH);
        if (radius <= 0 && depth <= 0) {
            return null;
        }

        // Spread across the hit face, but not along its axis.
        Direction.Axis axis = face.getAxis();
        int rx = axis == Direction.Axis.X ? 0 : radius;
        int ry = axis == Direction.Axis.Y ? 0 : radius;
        int rz = axis == Direction.Axis.Z ? 0 : radius;

        // Depth goes into the block, away from the hit face.
        Direction inwards = face.getOpposite();
        int dx = inwards.getStepX() * depth;
        int dy = inwards.getStepY() * depth;
        int dz = inwards.getStepZ() * depth;

        // When tunnelling sideways at body height, keep the floor of the tunnel level with the player's feet.
        int shiftY = 0;
        int feetY = player.getBlockY();
        if (alignToFeet && axis.isHorizontal() && radius > 0 && (origin.getY() == feetY || origin.getY() == feetY + 1)) {
            shiftY = radius - (origin.getY() - feetY);
        }

        return new BoundingBox(
            origin.getX() - rx + Math.min(0, dx), origin.getY() - ry + Math.min(0, dy) + shiftY, origin.getZ() - rz + Math.min(0, dz),
            origin.getX() + rx + Math.max(0, dx), origin.getY() + ry + Math.max(0, dy) + shiftY, origin.getZ() + rz + Math.max(0, dz));
    }

    @Override
    public void mineBlock(DarkSteelHookContext context, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (!(level instanceof ServerLevel serverLevel) || !(miningEntity instanceof ServerPlayer player)) {
            return;
        }

        ItemStack tool = context.itemStack();
        if (tool.isEmpty() || tool != player.getMainHandItem() || !condition.isActive(tool, level, player) || getActiveEffect(tool) != this) {
            return;
        }

        // Prevent re-entrance
        if (!PLAYERS_MINING_AREA.add(player.getUUID())) {
            return;
        }

        try {
            mineArea(serverLevel, player, tool, state, pos);
        } finally {
            PLAYERS_MINING_AREA.remove(player.getUUID());
        }
    }

    private void mineArea(ServerLevel level, ServerPlayer player, ItemStack tool, BlockState originState, BlockPos origin) {
        List<BlockPos> positions = getMinablePositions(level, player, tool, origin, originState, getHitFace(player, origin));
        float originHardness = originState.getDestroySpeed(level, origin);

        boolean brokeAny = false;
        for (BlockPos pos : positions) {
            // Earlier breaks (e.g. falling blocks or block updates) may have changed this block since it was checked.
            if (!canMine(level, player, tool, pos, level.getBlockState(pos), originHardness)) {
                continue;
            }

            if (!costPerBlock.canAfford(level, player, tool)) {
                break;
            }

            // Fires the break event and handles drops, experience, stats and durability like a normal break.
            if (!player.gameMode.destroyBlock(pos)) {
                continue;
            }

            brokeAny = true;

            // Breaking the block may have used up the tool's durability.
            if (tool.isEmpty()) {
                break;
            }

            costPerBlock.take(level, player, tool);
            if (tool.isEmpty()) {
                break;
            }

            if (shouldDisplaySmokeParticles && level.random.nextFloat() < 0.3F) {
                level.sendParticles(ParticleTypes.POOF, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, 0.2, 0.2, 0.2, 0.0);
            }
        }

        if (brokeAny && shouldDisplayExplosionParticles) {
            level.sendParticles(ParticleTypes.EXPLOSION, origin.getX() + 0.5, origin.getY() + 0.5, origin.getZ() + 0.5, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    /**
     * Gets the blocks around the origin that would be mined, excluding the origin itself. Shared with the client for the block highlight,
     * so what is highlighted is exactly what is mined. Costs are not checked, so mining may stop early when they cannot be afforded.
     * @return the positions, nearest to the origin first.
     */
    public List<BlockPos> getMinablePositions(Level level, Player player, ItemStack tool, BlockPos origin, BlockState originState, Direction face) {
        BoundingBox area = getArea(player, origin, face);
        if (area == null) {
            return List.of();
        }

        float originHardness = originState.getDestroySpeed(level, origin);

        // Nearest first, so if the cost or durability runs out, the blocks closest to the origin are the ones broken.
        return BlockPos.betweenClosedStream(area)
            .filter(pos -> !pos.equals(origin))
            .map(BlockPos::immutable)
            .filter(pos -> canMine(level, player, tool, pos, level.getBlockState(pos), originHardness))
            .sorted(Comparator.comparingDouble(pos -> pos.distSqr(origin)))
            .toList();
    }

    private boolean canMine(Level level, Player player, ItemStack tool, BlockPos pos, BlockState state, float originHardness) {
        if (state.isAir()) {
            return false;
        }

        if (targets.isPresent() && !state.is(targets.get())) {
            return false;
        }

        // Stops a soft block from being used to break harder (or unbreakable) blocks around it.
        float hardness = state.getDestroySpeed(level, pos);
        if (hardness < 0 || hardness > originHardness) {
            return false;
        }

        return tool.isCorrectToolForDrops(state) && level.mayInteract(player, pos);
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getLevel().isClientSide() || event.getFace() == null) {
            return;
        }

        // The client sends the face it is looking at both when it starts and when it finishes breaking a block.
        if (event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.START
            || event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.STOP) {
            MINING_TARGETS.put(event.getEntity(), new MiningTarget(event.getPos().immutable(), event.getFace()));
        }
    }

    private static Direction getHitFace(Player player, BlockPos origin) {
        MiningTarget target = MINING_TARGETS.get(player);
        if (target != null && target.pos().equals(origin)) {
            return target.face();
        }

        // Fall back to working it out from where the player is looking.
        HitResult hit = player.pick(player.blockInteractionRange(), 1.0F, false);
        if (hit instanceof BlockHitResult blockHit && hit.getType() == HitResult.Type.BLOCK && blockHit.getBlockPos().equals(origin)) {
            return blockHit.getDirection();
        }

        return Direction.orderedByNearest(player)[0].getOpposite();
    }

    private record MiningTarget(BlockPos pos, Direction face) {
    }
}
