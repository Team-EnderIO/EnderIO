package com.enderio.base.common.handler;

import com.enderio.EnderIO;
import com.enderio.base.common.config.BaseConfig;
import com.enderio.base.common.init.EIORecipes;
import com.enderio.base.common.recipe.FireCraftingRecipe;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = EnderIO.MODID)
public class FireCraftingHandler {
    private static final Random RANDOM = new Random();
    private static final ConcurrentMap<FireIndex, Long> FIRE_TRACKER = new ConcurrentHashMap<>();

    private static List<RecipeHolder<FireCraftingRecipe>> cachedRecipes;
    private static boolean recipesCached = false;

    private record FireIndex(BlockPos pos, ResourceKey<Level> dimension) {}

    @SubscribeEvent
    public static void onRecipeUpdate(RecipesUpdatedEvent event) {
        recipesCached = false;
    }

    @SubscribeEvent
    public static void on(BlockEvent.NeighborNotifyEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {
            // Finish early if we're not tracking any fires and this is a fire removal.
            boolean isFire = event.getState().getBlock() instanceof FireBlock;
            if (FIRE_TRACKER.isEmpty() && !isFire) {
                return;
            }

            // Grab useful fields.
            BlockPos pos = event.getPos();
            FireIndex fireIndex = new FireIndex(pos, level.dimension());
            long gameTime = level.getGameTime();

            Block baseBlock = level.getBlockState(pos.below()).getBlock();

            // Cache recipes
            if (!recipesCached) {
                cachedRecipes = level.getRecipeManager().getAllRecipesFor(EIORecipes.FIRE_CRAFTING.type().get());
                recipesCached = false;
            }

            // Search for this recipe.
            FireCraftingRecipe matchingRecipe = null;
            for (var recipeHolder : cachedRecipes) {
                var recipe = recipeHolder.value();
                if (recipe.isBaseValid(baseBlock) && recipe.isDimensionValid(level.dimension())) {
                    matchingRecipe = recipe;
                    break;
                }
            }

            if (matchingRecipe == null) {
                return;
            }

            if (isFire) {
                // If we're tracking lots of fire, look at culling the herd.
                if (FIRE_TRACKER.size() > 100) {
                    FIRE_TRACKER.values().removeIf(age -> age < gameTime || FIRE_TRACKER.size() > 500);
                }

                // Add to the tracker.
                FIRE_TRACKER.putIfAbsent(fireIndex, gameTime + BaseConfig.COMMON.INFINITY.FIRE_MIN_AGE.get());
            } else if (FIRE_TRACKER.containsKey(fireIndex)) {
                if (level.getBlockState(pos).isAir() && gameTime > FIRE_TRACKER.get(fireIndex)) {
                    spawnInfinityDrops(level, pos, matchingRecipe.lootTable(), matchingRecipe.maxItemDrops());
                }
                FIRE_TRACKER.remove(fireIndex);
            }
        }
    }

    public static void spawnInfinityDrops(ServerLevel level, BlockPos pos, ResourceKey<LootTable> lootTable, int maxItemDrops) {
        LootParams lootparams = (new LootParams.Builder(level)).withParameter(LootContextParams.ORIGIN, pos.getCenter()).create(LootContextParamSets.COMMAND);

        LootTable table = level.getServer().reloadableRegistries().getLootTable(lootTable);

        if (table != LootTable.EMPTY) {
            ObjectArrayList<ItemStack> randomItems = table.getRandomItems(lootparams);
            for (int i = 0; i < randomItems.size(); i++) {
                if (i >= maxItemDrops) {
                    break;
                }

                ItemStack item = randomItems.get(i);

                // Get random offset
                double x = RANDOM.nextFloat() * 0.5f + 0.25f;
                double y = RANDOM.nextFloat() * 0.5f + 0.25f;
                double z = RANDOM.nextFloat() * 0.5f + 0.25f;
                ItemEntity itemEntity = new ItemEntity(level, pos.getX() + x, pos.getY() + y, pos.getZ() + z, item);
                itemEntity.setDefaultPickUpDelay();

                // Make it survive the fire for a bit
                itemEntity.hurt(itemEntity.damageSources().inFire(), -100);

                // Actually set it on fire
                itemEntity.setRemainingFireTicks(10);
                level.addFreshEntity(itemEntity);

                // Play explosion sound
                level.playSound(null, pos, SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, SoundSource.BLOCKS, 1.0f, RANDOM.nextFloat() * 0.4f + 0.8f);
            }
        }
    }

    // Support worlds where firetick is disabled:
    @SubscribeEvent
    public static void onWorldTick(LevelTickEvent.Pre event) {
        var level = event.getLevel();

        if (!FIRE_TRACKER.isEmpty() && !level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            // Create a list of positions that need to be turned to air. Fixes issues with the fire tracker being modified while we iterate
            List<BlockPos> blocksToClear = new ArrayList<>();

            // Search for any fires that are due to spawn drops.
            long gameTime = level.getGameTime();
            for (Map.Entry<FireIndex, Long> fire : FIRE_TRACKER.entrySet()) {
                if (!fire.getKey().dimension().equals(level.dimension())) {
                    continue;
                }

                BlockPos pos = fire.getKey().pos();
                if (gameTime > fire.getValue()) {
                    if (level.getBlockState(pos).getBlock() instanceof FireBlock) {
                        blocksToClear.add(pos);
                    } else {
                        FIRE_TRACKER.remove(fire.getKey());
                    }
                }
            }

            // Turn them to air to trigger the usual event.
            for (BlockPos pos : blocksToClear) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }
}
