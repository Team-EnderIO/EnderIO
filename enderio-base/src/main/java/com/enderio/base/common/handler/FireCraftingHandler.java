package com.enderio.base.common.handler;

import com.enderio.base.EnderIO;
import com.enderio.base.common.init.EIORecipes;
import com.enderio.base.common.recipe.FireCraftingRecipe;
import com.enderio.base.config.base.BaseConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = EnderIO.MODID)
public class FireCraftingHandler {
    private static final Random RANDOM = new Random();
    private static final Map<FireIndex, Long> FIRE_TRACKER = new HashMap<>();

    private static List<FireCraftingRecipe> cachedRecipes;
    private static boolean recipesCached = false;


    private record FireIndex(BlockPos pos, ResourceKey<Level> dimension) {}

    @SubscribeEvent
    public static void onRecipeUpdate(RecipesUpdatedEvent event) {
        recipesCached = false;
    }

    @SubscribeEvent
    public static void on(BlockEvent.NeighborNotifyEvent event) {
        if (event.getWorld() instanceof ServerLevel level) {
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
                cachedRecipes = level.getRecipeManager().getAllRecipesFor(EIORecipes.Types.FIRE_CRAFTING);
                recipesCached = false;
            }

            // Search for this recipe.
            FireCraftingRecipe matchingRecipe = null;
            for (FireCraftingRecipe recipe : cachedRecipes) {
                if (recipe.isBaseValid(baseBlock) && recipe.isDimensionValid(level.dimension())) {
                    matchingRecipe = recipe;
                    break;
                }
            }

            if (matchingRecipe == null)
                return;

            if (isFire) {
                // If we're tracking lots of fire, look at culling the herd.
                if (FIRE_TRACKER.size() > 100) {
                    FIRE_TRACKER.values().removeIf(age -> age < gameTime || FIRE_TRACKER.size() > 500);
                }

                // Add to the tracker.
                FIRE_TRACKER.putIfAbsent(fireIndex, gameTime + BaseConfig.COMMON.INFINITY.FIRE_MIN_AGE.get());
            } else if (FIRE_TRACKER.containsKey(fireIndex)) {
                if (level.getBlockState(pos).isAir() && gameTime > FIRE_TRACKER.get(fireIndex)) {
                    spawnInfinityDrops(level, pos, matchingRecipe.getLootTable());
                }
                FIRE_TRACKER.remove(fireIndex);
            }
        }
    }

    public static void spawnInfinityDrops(@NotNull ServerLevel level, @NotNull BlockPos pos, ResourceLocation lootTable) {
        LootContext ctx = (new LootContext.Builder(level)).create(LootContextParamSet.builder().build());
        LootTable table = ctx.getLootTable(lootTable);

        if (table != LootTable.EMPTY) {
            for (ItemStack item : table.getRandomItems(ctx)) {
                // Get random offset
                double x = RANDOM.nextFloat() * 0.5f + 0.25f;
                double y = RANDOM.nextFloat() * 0.5f + 0.25f;
                double z = RANDOM.nextFloat() * 0.5f + 0.25f;
                ItemEntity itemEntity = new ItemEntity(level, pos.getX() + x, pos.getY() + y, pos.getZ() + z, item);
                itemEntity.setDefaultPickUpDelay();

                // Make it survive the fire for a bit
                itemEntity.hurt(DamageSource.IN_FIRE, -100); // TODO: Do we just make it fireproof like netherite is?

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
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (!FIRE_TRACKER.isEmpty() && !event.world.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            // Create a list of positions that need to be turned to air. Fixes issues with the fire tracker being modified while we iterate
            List<BlockPos> blocksToClear = new ArrayList<>();

            // Search for any fires that are due to spawn drops.
            long gameTime = event.world.getGameTime();
            for (Map.Entry<FireIndex, Long> fire : FIRE_TRACKER.entrySet()) {
                BlockPos pos = fire.getKey().pos();
                if (gameTime > fire.getValue()) {
                    if (event.world.getBlockState(pos).getBlock() instanceof FireBlock) {
                        blocksToClear.add(pos);
                    } else {
                        FIRE_TRACKER.remove(fire.getKey());
                    }
                }
            }

            // Turn them to air to trigger the usual event.
            for (BlockPos pos : blocksToClear) {
                event.world.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }
}
