package com.enderio.armory.common.init;

import static com.enderio.armory.common.capability.DarkSteelCapability.DarkSteelItemUpgrades.ITEM_UPGRADES_CODEC;

import com.enderio.armory.common.capability.DarkSteelCapability;
import com.enderio.base.api.EnderIO;
import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ArmoryDataComponents {

    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister
            .create(Registries.DATA_COMPONENT_TYPE, EnderIO.NAMESPACE);

    public static Supplier<DataComponentType<CustomData>> DARK_STEEL_UPGRADE = saved("dark_steel_upgrade",
            CustomData.CODEC);

    public static Supplier<DataComponentType<DarkSteelCapability.DarkSteelItemUpgrades>> DARK_STEEL_ITEM_UPGRADES = saved(
            "dark_steel_upgrafes", ITEM_UPGRADES_CODEC);

    private static <T> Supplier<DataComponentType<T>> saved(String name, Codec<T> codec) {
        return DATA_COMPONENT_TYPES.register(name, () -> DataComponentType.<T>builder().persistent(codec).build());
    }

    public static void register(IEventBus bus) {
        DATA_COMPONENT_TYPES.register(bus);
    }

}
