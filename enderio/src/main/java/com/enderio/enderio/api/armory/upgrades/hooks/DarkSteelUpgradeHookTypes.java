package com.enderio.enderio.api.armory.upgrades.hooks;

import com.enderio.enderio.api.armory.upgrades.hooks.armor.CanFreezeHook;
import com.enderio.enderio.api.armory.upgrades.hooks.armor.ElytraFlyHook;
import com.enderio.enderio.api.armory.upgrades.hooks.armor.WalkOnPowderedSnowHook;
import com.enderio.enderio.api.armory.upgrades.hooks.general.DarkSteelInventoryTickHook;
import com.enderio.enderio.api.armory.upgrades.hooks.general.ModifyAttributesHook;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.CanPerformActionHook;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.GetDestroySpeedHook;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.IsCorrectToolForDropsHook;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.MineBlockHook;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.UseHook;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.UseOnHook;

public class DarkSteelUpgradeHookTypes {
    public static final DarkSteelUpgradeHookType<DarkSteelInventoryTickHook> INVENTORY_TICK = new DarkSteelUpgradeHookType<>();
    public static final DarkSteelUpgradeHookType<ModifyAttributesHook> MODIFY_ATTRIBUTES = new DarkSteelUpgradeHookType<>();
    public static final DarkSteelUpgradeHookType<CanPerformActionHook> CAN_PERFORM_ACTION = new DarkSteelUpgradeHookType<>();
    public static final DarkSteelUpgradeHookType<UseOnHook> USE_ON = new DarkSteelUpgradeHookType<>();
    public static final DarkSteelUpgradeHookType<UseHook> USE = new DarkSteelUpgradeHookType<>();
    public static final DarkSteelUpgradeHookType<GetDestroySpeedHook> GET_DESTROY_SPEED = new DarkSteelUpgradeHookType<>();
    public static final DarkSteelUpgradeHookType<IsCorrectToolForDropsHook> IS_CORRECT_TOOL_FOR_DROPS = new DarkSteelUpgradeHookType<>();
    public static final DarkSteelUpgradeHookType<MineBlockHook> MINE_BLOCK = new DarkSteelUpgradeHookType<>();
    public static final DarkSteelUpgradeHookType<ElytraFlyHook> ELYTRA_FLY = new DarkSteelUpgradeHookType<>();
    public static final DarkSteelUpgradeHookType<CanFreezeHook> CAN_FREEZE = new DarkSteelUpgradeHookType<>();
    public static final DarkSteelUpgradeHookType<WalkOnPowderedSnowHook> WALK_ON_POWDERED_SNOW = new DarkSteelUpgradeHookType<>();
}
