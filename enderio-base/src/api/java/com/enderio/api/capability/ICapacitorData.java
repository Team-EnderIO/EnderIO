package com.enderio.api.capability;

import net.minecraft.nbt.Tag;

import java.util.Map;

/**
 * interface for storing capacitor data
 */
public interface ICapacitorData extends INamedNBTSerializable<Tag> {
    @Override
    default String getSerializedName() {
        return "CapacitorData";
    }

    /**
     * Gets Base value used for non specialization.
     */
    void setBase(float base);

    /**
     * Sets Base value used for non specialization.
     */
    float getBase();

    /**
     * Add a specialization. It contains a String for the type and a float for the value.
     */
    void addSpecialization(String type, float modifier);

    /**
     * Clears old and adds a new specialization;
     */
    void addNewSpecialization(String type, float modifier);

    /**
     * Adds all specializations to the capacitor.
     */
    void addAllSpecialization(Map<String, Float> specializations);

    /**
     * Gets all specializations.
     */
    Map<String, Float> getSpecializations();

    default float getSpecialization(String specialization) {
        return getSpecializations().getOrDefault(specialization, getBase());
    }

    default float scale(String specialization, float base) {
        return base * getSpecialization(specialization);
    }

    default float scale(String specialization, String allSpecialization, float base) {
        float specificMult = getSpecialization(specialization);
        float allMult = getSpecialization(allSpecialization);
        return base * specificMult > allMult ? specificMult : allMult;
    }

    /**
     * Flavor text used by loot capacitor.
     */
    int getFlavor();

}
