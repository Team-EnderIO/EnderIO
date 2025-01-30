package com.enderio.armory.common.init;

import com.enderio.base.api.EnderIO;
import com.enderio.base.common.init.EIOItems;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public class ArmoryArmorMaterials {

    public static final Holder<ArmorMaterial> DARK_STEEL_ARMOR_MATERIAL = register("dark_steel",
            Util.make(new EnumMap<>(ArmorItem.Type.class), (map) -> {
                map.put(ArmorItem.Type.BOOTS, 3);
                map.put(ArmorItem.Type.LEGGINGS, 6);
                map.put(ArmorItem.Type.CHESTPLATE, 8);
                map.put(ArmorItem.Type.HELMET, 3);
                map.put(ArmorItem.Type.BODY, 11);
            }), 10, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, 0.0F, () -> Ingredient.of(EIOItems.DARK_STEEL_INGOT));

    private static Holder<ArmorMaterial> register(String name, EnumMap<ArmorItem.Type, Integer> defense,
            int enchantmentValue, Holder<SoundEvent> equipSound, float toughness, float knockbackResistance,
            Supplier<Ingredient> repairIngredient) {
        List<ArmorMaterial.Layer> list = List
                .of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(EnderIO.NAMESPACE, name)));
        return register(name, defense, enchantmentValue, equipSound, toughness, knockbackResistance, repairIngredient,
                list);
    }

    private static Holder<ArmorMaterial> register(String name, EnumMap<ArmorItem.Type, Integer> defense,
            int enchantmentValue, Holder<SoundEvent> equipSound, float toughness, float knockbackResistance,
            Supplier<Ingredient> repairIngridient, List<ArmorMaterial.Layer> layers) {
        EnumMap<ArmorItem.Type, Integer> enummap = new EnumMap<>(ArmorItem.Type.class);

        for (ArmorItem.Type armoritem$type : ArmorItem.Type.values()) {
            enummap.put(armoritem$type, defense.get(armoritem$type));
        }

        return Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, ResourceLocation.withDefaultNamespace(name),
                new ArmorMaterial(enummap, enchantmentValue, equipSound, repairIngridient, layers, toughness,
                        knockbackResistance));
    }

}
