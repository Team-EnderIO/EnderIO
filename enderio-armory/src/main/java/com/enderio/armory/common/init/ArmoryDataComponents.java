package com.enderio.armory.common.init;

import static com.enderio.armory.common.capability.DarkSteelCapability.DarkSteelItemUpgrades.ITEM_UPGRADES_CODEC;

import com.enderio.armory.common.capability.DarkSteelCapability;
import com.enderio.base.api.EnderIO;
import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ArmoryDataComponents {

    private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister
            .create(Registries.DATA_COMPONENT_TYPE, EnderIO.NAMESPACE);

    public static final Supplier<DataComponentType<DarkSteelCapability.DarkSteelItemUpgrades>> DARK_STEEL_ITEM_UPGRADES = saved(
            "dark_steel_upgrades", ITEM_UPGRADES_CODEC);

    /**
     * Used to ensure any equipped dark steel items have their ItemAttributeModifierEvent event fired so they can update
     * any attribute modifiers when energy is lost or regained
     */
    public static final Supplier<DataComponentType<Boolean>> DARK_STEEL_ITEM_HAS_ENERGY = saved("dark_steel_item_has_energy",
            Codec.BOOL);

    public static final Supplier<DataComponentType<Boolean>> DARK_STEEL_FLIGHT_ACTIVE = saved("dark_steel_flight_active",
            Codec.BOOL);

    public static final Supplier<DataComponentType<Boolean>> DARK_STEEL_NIGHT_VISION_ACTIVE = saved(
            "dark_steel_nightvision_active", Codec.BOOL);

    public static final Supplier<DataComponentType<Integer>> DARK_STEEL_SOLAR_CHARGE_INDEX = saved(
            "dark_steel_solar_charge_index", Codec.INT);

    private static <T> Supplier<DataComponentType<T>> saved(String name, Codec<T> codec) {
        return DATA_COMPONENT_TYPES.register(name, () -> DataComponentType.<T>builder().persistent(codec).build());
    }

    public static void register(IEventBus bus) {
        DATA_COMPONENT_TYPES.register(bus);
    }

}
