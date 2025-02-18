package com.enderio.armory.client.renderer;

import com.enderio.armory.common.capability.DarkSteelHelper;
import com.enderio.armory.common.init.ArmoryDataComponents;
import com.enderio.armory.common.item.darksteel.upgrades.flight.ElytraUpgrade;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ElytraUpgradeRenderLayer<ENTITY extends LivingEntity, MODEL extends EntityModel<ENTITY>>
        extends ElytraLayer<ENTITY, MODEL> {

    public ElytraUpgradeRenderLayer(RenderLayerParent<ENTITY, MODEL> entityRenderer, EntityModelSet modelSet) {
        super(entityRenderer, modelSet);
    }

    @Override
    public boolean shouldRender(ItemStack stack, LivingEntity entity) {
        return DarkSteelHelper.hasUpgrade(stack, ElytraUpgrade.NAME)
                && stack.getOrDefault(ArmoryDataComponents.DARK_STEEL_FLIGHT_ACTIVE, false);
    }

}
