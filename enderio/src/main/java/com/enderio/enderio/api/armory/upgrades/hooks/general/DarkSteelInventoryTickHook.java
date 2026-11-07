package com.enderio.enderio.api.armory.upgrades.hooks.general;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeContainer;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

// Just an example at this stage
@ApiStatus.Experimental
public interface DarkSteelInventoryTickHook {
    void onInventoryTick(Level level, Entity entity);

    static void onInventoryTick(DarkSteelUpgradeContainer upgradeContainer, Level level, Entity entity) {
        upgradeContainer.getHooks(DarkSteelUpgradeHookTypes.INVENTORY_TICK)
            .forEach(hook -> hook.onInventoryTick(level, entity));
    }
}
