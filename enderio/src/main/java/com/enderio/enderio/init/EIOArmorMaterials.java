package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.foundation.tag.EIOTags;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class EIOArmorMaterials {
    private static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL,
        EnderIO.MOD_ID);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> DARK_STEEL = register("dark_steel", 2, 5, 6, 2, 5,
        15, SoundEvents.ARMOR_EQUIP_IRON, 1.0f, 0.0f, EIOTags.Items.INGOTS_DARK_STEEL);

    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> END_STEEL = register("end_steel", 4, 7, 10, 4, 7,
        25, SoundEvents.ARMOR_EQUIP_IRON, 3.0f, 0.0f, EIOTags.Items.INGOTS_END_STEEL);

    private static DeferredHolder<ArmorMaterial, ArmorMaterial> register(String name, int boots, int leggings, int chestplate, int helmet, int body,
        int enchantability, Holder<SoundEvent> equipSound, float toughness, float knockbackResistance, TagKey<Item> repairTag) {

        return ARMOR_MATERIALS.register(name, () -> new ArmorMaterial(
            Map.of(
                ArmorItem.Type.BOOTS, boots,
                ArmorItem.Type.LEGGINGS, leggings,
                ArmorItem.Type.CHESTPLATE, chestplate,
                ArmorItem.Type.HELMET, helmet,
                ArmorItem.Type.BODY, body
            ),
            enchantability,
            equipSound,
            () -> Ingredient.of(repairTag),
            List.of(new ArmorMaterial.Layer(EnderIO.rl(name))), toughness, knockbackResistance));
    }

    public static void register(IEventBus eventBus) {
        ARMOR_MATERIALS.register(eventBus);
    }
}
