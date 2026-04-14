package com.enderio.enderio.datagen.client.models;

import com.enderio.enderio.EnderIO;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureSlot;

public class EIOModelTemplates {
    public static final ModelTemplate PHOTOVOLTAIC_MODULE_BASE = ModelTemplates.create(EnderIO.id("template_photovoltaic_module_base").toString(), "_base", TextureSlot.SIDE, EIOTextureSlots.PANEL);
    public static final ModelTemplate PHOTOVOLTAIC_MODULE_CORNER = ModelTemplates.create(EnderIO.id("template_photovoltaic_module_corner").toString(), "_corner", TextureSlot.SIDE);
    public static final ModelTemplate PHOTOVOLTAIC_MODULE_SIDE = ModelTemplates.create(EnderIO.id("template_photovoltaic_module_side").toString(), "_side", TextureSlot.SIDE);

    public static final ModelTemplate PHOTOVOLTAIC_MODULE_ITEM = ModelTemplates.createItem(EnderIO.id("template_photovoltaic_module").toString(), "_side", TextureSlot.SIDE, EIOTextureSlots.PANEL);
}
