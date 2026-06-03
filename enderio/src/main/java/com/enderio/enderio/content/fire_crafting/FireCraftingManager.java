package com.enderio.enderio.content.fire_crafting;

import com.enderio.core.annotations.UseOnly;
import com.enderio.enderio.config.base.BaseConfig;
import com.enderio.enderio.init.EIORecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@EventBusSubscriber
public class FireCraftingManager {
    public static final AttachmentType<FireCraftingManager> ATTACHMENT_TYPE = AttachmentType.builder(FireCraftingManager::new).build();

    private final ConcurrentMap<BlockPos, Long> fireTracker = new ConcurrentHashMap<>();

    private WeakReference<RecipeManager> cachedRecipeManager = new WeakReference<>(null);
    private static Collection<RecipeHolder<FireCraftingRecipe>> cachedRecipes = List.of();

    public FireCraftingManager() {
    }

    // For use from *other* blocks, i.e. non fire.
    public boolean tryPerformFireCrafting(ServerLevel level, BlockPos pos) {
        FireCraftingRecipe matchingRecipe = getMatchingRecipe(level, pos);

        if (matchingRecipe == null) {
            return false;
        }

        spawnInfinityDrops(level, pos, matchingRecipe);
        return true;
    }

    @Nullable
    public FireCraftingRecipe getMatchingRecipe(ServerLevel level, BlockPos pos) {
        BlockState blockBelow = level.getBlockState(pos.below());

        for (var recipeHolder : getRecipes(level)) {
            var recipe = recipeHolder.value();
            if (recipe.isBaseValid(blockBelow.getBlock()) && recipe.isDimensionValid(level.dimension())) {
                return recipe;
            }
        }

        return null;
    }

    public Collection<RecipeHolder<FireCraftingRecipe>> getRecipes(ServerLevel level) {
        if (level.recipeAccess() != cachedRecipeManager.get()) {
            cachedRecipeManager = new WeakReference<>(level.recipeAccess());
            cachedRecipes = level.recipeAccess().recipeMap().byType(EIORecipeTypes.FIRE_CRAFTING.get());
        }

        return cachedRecipes;
    }

    private static void spawnInfinityDrops(Level level, BlockPos pos, FireCraftingRecipe recipe) {
        var randomSource = level.getRandom();
        boolean didDrop = false;

        for (var result : recipe.results()) {
            float dropChance = level.getRandom().nextFloat();
            if (dropChance >= result.chance()) {
                continue;
            }

            int itemCount = level.getRandom().nextIntBetweenInclusive(result.minCount(), result.maxCount());
            if (itemCount <= 0) {
                continue;
            }

            ItemStack resultStack = result.result().create();
            resultStack.setCount(itemCount);

            // Get random offset
            double x = randomSource.nextFloat() * 0.5f + 0.25f;
            double y = randomSource.nextFloat() * 0.5f + 0.25f;
            double z = randomSource.nextFloat() * 0.5f + 0.25f;
            ItemEntity itemEntity = new ItemEntity(level, pos.getX() + x, pos.getY() + y, pos.getZ() + z, resultStack);
            itemEntity.setDefaultPickUpDelay();

            // Make it survive the fire for a bit
            itemEntity.hurt(itemEntity.damageSources().inFire(), -100);

            // Actually set it on fire
            itemEntity.setRemainingFireTicks(10);
            level.addFreshEntity(itemEntity);
            didDrop = true;
        }

        if (didDrop) {
            // Play explosion sound
            if (BaseConfig.COMMON.INFINITY.MAKES_SOUND.get()) {
                level.playSound(null, pos, SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, SoundSource.BLOCKS, 1.0f,
                    randomSource.nextFloat() * 0.4f + 0.8f);
            }

            // Replace the base (if applicable)
            if (recipe.blockAfterBurning().isPresent()) {
                level.setBlock(pos.below(), recipe.blockAfterBurning().get().defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    @UseOnly(LogicalSide.SERVER)
    private void onRandomTick(ServerLevel level, BlockPos pos) {
        // Get the ticked block state
        BlockState tickedBlockState = level.getBlockState(pos);

        // Ensure that we should process this block
        boolean isFire = tickedBlockState.is(Blocks.FIRE);
        if (!isFire && !fireTracker.containsKey(pos)) {
            return;
        }

        // Find a potential recipe for this fire block.
        var matchingRecipe = getMatchingRecipe(level, pos);
        if (matchingRecipe == null) {
            // No recipe found, remove the fire block from tracking.
            fireTracker.remove(pos);
            return;
        }

        long gameTime = level.getGameTime();

        // If it's still fire, it should be tracked.
        // If it has been extinguished, it should 'pop'.
        if (isFire) {
            // Prune the oldest fires if we have too many in the tracker.
            int maxTracked = BaseConfig.COMMON.INFINITY.MAX_TRACKED_FIRES.get();
            int excess = fireTracker.size() - maxTracked;
            if (excess > 0) {
                // Remove the oldest fires (smallest expiration time first) until within the max
                var toRemove = fireTracker.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .limit(excess)
                    .map(Map.Entry::getKey)
                    .toList();

                toRemove.forEach(fireTracker::remove);
            }

            fireTracker.putIfAbsent(pos, gameTime + BaseConfig.COMMON.INFINITY.FIRE_MIN_AGE.get());
        } else {
            if (level.getBlockState(pos).isAir() && gameTime > fireTracker.get(pos)) {
                spawnInfinityDrops(level, pos, matchingRecipe);
            }

            fireTracker.remove(pos);
        }
    }

    private void onWorldTick(ServerLevel level) {
        // Only run if fire tick is disabled.
        if (level.getGameRules().get(GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER) > 0) {
            return;
        }

        // Create a list of positions that need to be turned to air. Fixes issues with
        // the fire tracker being modified while we iterate
        List<BlockPos> blocksToClear = new ArrayList<>();

        // Search for any fires that are due to spawn drops.
        long gameTime = level.getGameTime();
        for (Map.Entry<BlockPos, Long> fire : fireTracker.entrySet()) {
            BlockPos pos = fire.getKey();
            if (gameTime > fire.getValue()) {
                if (level.getBlockState(pos).getBlock() instanceof FireBlock) {
                    blocksToClear.add(pos);
                } else {
                    fireTracker.remove(fire.getKey());
                }
            }
        }

        // Turn them to air to trigger the usual event.
        for (BlockPos pos : blocksToClear) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    @SubscribeEvent
    public static void onNeighborNotifyEvent(BlockEvent.NeighborNotifyEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {
            var fireCraftingManager = level.getData(FireCraftingManager.ATTACHMENT_TYPE);
            fireCraftingManager.onRandomTick(level, event.getPos());
        }
    }

    // Support worlds where firetick is disabled
    @SubscribeEvent
    public static void onWorldTick(LevelTickEvent.Pre event) {
        if (event.getLevel() instanceof ServerLevel level) {
            var fireCraftingManager = level.getData(FireCraftingManager.ATTACHMENT_TYPE);
            fireCraftingManager.onWorldTick(level);
        }
    }
}
