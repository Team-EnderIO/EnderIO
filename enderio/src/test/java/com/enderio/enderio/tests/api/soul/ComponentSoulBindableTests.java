package com.enderio.enderio.tests.api.soul;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.soul.binding.ComponentSoulBindable;
import com.enderio.enderio.api.soul.binding.SoulBindable;
import com.enderio.enderio.content.broken_spawner.BrokenSpawnerItem;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityTypeIds;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(EphemeralTestServerProvider.class)
public class ComponentSoulBindableTests {
    @Test
    public void testReadSoul(MinecraftServer server) {
        // Arrange.
        ItemStack spawnerItemStack = BrokenSpawnerItem.forSoul(Soul.of(EntityTypeIds.ALLAY));
        ItemAccess itemAccess = ItemAccess.forStack(spawnerItemStack);
        SoulBindable soulBindable = itemAccess.getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM);

        // Assume capability setup is working -and- the right capability is in use.
        Assumptions.assumeTrue(soulBindable != null);
        Assumptions.assumeTrue(soulBindable instanceof ComponentSoulBindable);

        // Act & Assert.
        Assertions.assertNotNull(soulBindable.boundSoul());
        Assertions.assertEquals(EntityTypeIds.ALLAY, soulBindable.boundSoul().entityTypeKey());
    }

    @Test
    public void testBindSoulAndCommit(MinecraftServer server) {
        // Arrange.
        ItemStack spawnerItemStack = new ItemStack(EIOItems.BROKEN_SPAWNER.get());
        ItemAccess itemAccess = ItemAccess.forStack(spawnerItemStack);
        SoulBindable soulBindable = itemAccess.getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM);

        // Assume capability setup is working -and- the right capability is in use.
        Assumptions.assumeTrue(soulBindable != null);
        Assumptions.assumeTrue(soulBindable instanceof ComponentSoulBindable);
        Assumptions.assumeFalse(soulBindable.isBound());

        // Act.
        boolean didBind;
        try (Transaction transaction = Transaction.openRoot()) {
            didBind = soulBindable.bindSoul(Soul.of(EntityTypeIds.ALLAY), transaction);
            transaction.commit();
        }

        Assertions.assertTrue(didBind);
        Assertions.assertEquals(EntityTypeIds.ALLAY, soulBindable.boundSoul().entityTypeKey());
    }

    @Test
    public void testBindSoulAndNotCommit(MinecraftServer server) {
        // Arrange.
        ItemStack spawnerItemStack = new ItemStack(EIOItems.BROKEN_SPAWNER.get());
        ItemAccess itemAccess = ItemAccess.forStack(spawnerItemStack);
        SoulBindable soulBindable = itemAccess.getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM);

        // Assume capability setup is working -and- the right capability is in use.
        Assumptions.assumeTrue(soulBindable != null);
        Assumptions.assumeTrue(soulBindable instanceof ComponentSoulBindable);
        Assumptions.assumeFalse(soulBindable.isBound());

        // Act.
        boolean didBind;
        try (Transaction transaction = Transaction.openRoot()) {
            didBind = soulBindable.bindSoul(Soul.of(EntityTypeIds.ALLAY), transaction);
        }

        Assertions.assertTrue(didBind);
        Assertions.assertFalse(soulBindable.isBound());
    }
}
