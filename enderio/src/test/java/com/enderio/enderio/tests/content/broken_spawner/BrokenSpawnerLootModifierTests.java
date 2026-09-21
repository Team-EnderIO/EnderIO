package com.enderio.enderio.tests.content.broken_spawner;

import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.content.broken_spawner.BrokenSpawnerLootModifier;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOItems;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(EphemeralTestServerProvider.class)
@ExtendWith(MockitoExtension.class)
public class BrokenSpawnerLootModifierTests {

    private static final BrokenSpawnerLootModifier MODIFIER = new BrokenSpawnerLootModifier(new LootItemCondition[]{});

    private static final SingleThreadedRandomSource RANDOM = new SingleThreadedRandomSource(1337L);

    @Mock
    private static ServerLevel level;

    @BeforeEach
    public void setup(MinecraftServer server) {
        when(level.getServer()).thenReturn(server);
        when(level.getRandom()).thenReturn(RANDOM);
    }

    @Test
    public void validSpawnerDropsBrokenSpawner() {
        // Arrange.
        ObjectArrayList<ItemStack> results = new ObjectArrayList<>();

        var spawner = new SpawnerBlockEntity(new BlockPos(0, 0, 0), Blocks.SPAWNER.defaultBlockState());
        spawner.setEntityId(EntityType.ALLAY, RANDOM);

        LootParams lootparams = new LootParams.Builder(level)
            .withParameter(LootContextParams.BLOCK_STATE, Blocks.SPAWNER.defaultBlockState())
            .withParameter(LootContextParams.ORIGIN, new Vec3(0.5, 0.5, 0.5))
            .withParameter(LootContextParams.TOOL, Items.DIAMOND_PICKAXE.getDefaultInstance())
            .withParameter(LootContextParams.BLOCK_ENTITY, spawner)
            .create(LootContextParamSets.BLOCK);
        LootContext context = new LootContext.Builder(lootparams).create(Optional.empty());

        // Act.
        MODIFIER.apply(results, context);

        // Assert.
        Assertions.assertEquals(1, results.size());
        ItemStack result = results.getFirst();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(EIOItems.BROKEN_SPAWNER.get(), result.getItem());
        Assertions.assertEquals(EntityType.ALLAY, result.getOrDefault(EIODataComponents.SOUL, Soul.EMPTY).entityType());
    }

    @Test
    public void invalidBlockAddsNothing() {
        // Arrange.
        ObjectArrayList<ItemStack> results = new ObjectArrayList<>();

        // Using glass as it won't drop with a diamond pick with no enchants
        LootParams lootparams = new LootParams.Builder(level)
            .withParameter(LootContextParams.BLOCK_STATE, Blocks.GLASS.defaultBlockState())
            .withParameter(LootContextParams.ORIGIN, new Vec3(0.5, 0.5, 0.5))
            .withParameter(LootContextParams.TOOL, Items.DIAMOND_PICKAXE.getDefaultInstance())
            .create(LootContextParamSets.BLOCK);
        LootContext context = new LootContext.Builder(lootparams).create(Optional.empty());

        // Act.
        MODIFIER.apply(results, context);

        // Assert.
        Assertions.assertTrue(results.isEmpty());
    }
}
