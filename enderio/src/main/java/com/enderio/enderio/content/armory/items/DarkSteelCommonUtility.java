package com.enderio.enderio.content.armory.items;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.armory.DarkSteelEnergyStorage;
import com.enderio.enderio.api.armory.capacitor.CapacitorSocket;
import com.enderio.enderio.api.capacitor.CapacitorData;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.InverseScalable;
import com.enderio.enderio.config.base.BaseConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class DarkSteelCommonUtility {
    private static final InverseScalable DURABILITY_TO_ENERGY_USE = new InverseScalable(CapacitorModifier.ENERGY_USE, BaseConfig.COMMON.ENERGY.ARMORY_DURABILITY_TO_ENERGY_USE);

    public static <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (!(energyStorage instanceof DarkSteelEnergyStorage darkSteelEnergyStorage)) {
            return amount;
        }

        int durabilityToEnergyUse = BaseConfig.COMMON.ENERGY.ARMORY_DURABILITY_TO_ENERGY_USE.get();
        CapacitorSocket capacitorSocket = stack.getCapability(EnderIOCapabilities.CAPACITOR_SOCKET);
        if (capacitorSocket != null) {
            durabilityToEnergyUse = DURABILITY_TO_ENERGY_USE.scaleI(capacitorSocket::getCapacitorData).get();
        }

        // Cannot convert durability damage to energy, so abort.
        if (durabilityToEnergyUse <= 0) {
            return amount;
        }

        int durabilityAbsorbed = Math.min(amount, darkSteelEnergyStorage.getEnergyStored() / durabilityToEnergyUse);
        if (durabilityAbsorbed <= 0) {
            return amount;
        }

        if (darkSteelEnergyStorage.consumeEnergy(durabilityAbsorbed * durabilityToEnergyUse, false)) {
            return amount - durabilityAbsorbed;
        }

        return amount;
    }
}
