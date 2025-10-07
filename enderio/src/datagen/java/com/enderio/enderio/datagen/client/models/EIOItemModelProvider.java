package com.enderio.enderio.datagen.client.models;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class EIOItemModelProvider extends ItemModelProvider {
    public EIOItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, EnderIO.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // Alloys
        basicItem(EIOItems.COPPER_ALLOY_INGOT.get());
        basicItem(EIOItems.ENERGETIC_ALLOY_INGOT.get());
        basicItem(EIOItems.VIBRANT_ALLOY_INGOT.get());
        basicItem(EIOItems.REDSTONE_ALLOY_INGOT.get());
        basicItem(EIOItems.CONDUCTIVE_ALLOY_INGOT.get());
        basicItem(EIOItems.PULSATING_ALLOY_INGOT.get());
        basicItem(EIOItems.DARK_STEEL_INGOT.get());
        basicItem(EIOItems.SOULARIUM_INGOT.get());
        basicItem(EIOItems.END_STEEL_INGOT.get());

        basicItem(EIOItems.COPPER_ALLOY_NUGGET.get());
        basicItem(EIOItems.ENERGETIC_ALLOY_NUGGET.get());
        basicItem(EIOItems.VIBRANT_ALLOY_NUGGET.get());
        basicItem(EIOItems.REDSTONE_ALLOY_NUGGET.get());
        basicItem(EIOItems.CONDUCTIVE_ALLOY_NUGGET.get());
        basicItem(EIOItems.PULSATING_ALLOY_NUGGET.get());
        basicItem(EIOItems.DARK_STEEL_NUGGET.get());
        basicItem(EIOItems.SOULARIUM_NUGGET.get());
        basicItem(EIOItems.END_STEEL_NUGGET.get());

        // Crafting Components
        basicItem(EIOItems.SILICON.get());
        basicItem(EIOItems.GRAINS_OF_INFINITY.get());
        basicItem(EIOItems.INFINITY_ROD.get());
        basicItem(EIOItems.CONDUIT_BINDER_COMPOSITE.get());
        basicItem(EIOItems.CONDUIT_BINDER.get());
        basicItem(EIOItems.ZOMBIE_ELECTRODE.get());
        basicItem(EIOItems.Z_LOGIC_CONTROLLER.get());
        withExistingParent(EIOItems.FRANK_N_ZOMBIE.getId().toString(), EIOItems.Z_LOGIC_CONTROLLER.getId());
        basicItem(EIOItems.ENDER_RESONATOR.get());
        withExistingParent(EIOItems.SENTIENT_ENDER.getId().toString(), EIOItems.ENDER_RESONATOR.getId());
        basicItem(EIOItems.SKELETAL_CONTRACTOR.get());
        basicItem(EIOItems.GUARDIAN_DIODE.get());
        basicItem(EIOItems.SUSPICIOUS_SEED.get());
    }
}
