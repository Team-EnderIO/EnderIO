package com.enderio.base.common.menu;

import com.enderio.base.api.attachment.StoredEntityData;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.tag.EIOTags;
import net.minecraft.client.Minecraft;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

import java.util.function.Consumer;

public class EntityFilterSlot extends Slot {

    private static Container emptyInventory = new SimpleContainer(0);
    private final Consumer<StoredEntityData> consumer;

    public EntityFilterSlot(Consumer<StoredEntityData> consumer, int pSlot, int pX, int pY) {
        super(emptyInventory, pSlot, pX, pY);
        this.consumer = consumer;
    }

    @Override
    public ItemStack getItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public void set(ItemStack pStack) {
        setChanged();
    }

    @Override
    public void setChanged() {

    }

    @Override
    public ItemStack remove(int pAmount) {
        set(ItemStack.EMPTY);
        return ItemStack.EMPTY;
    }

    @Override
    public int getMaxStackSize() {
        return getItem().getMaxStackSize();
    }

    @Override
    public ItemStack safeInsert(ItemStack stack, int amount) {
        // If this stack is valid, set the inventory slot value.
        if (stack.isEmpty() || !mayPlace(stack)) {
            return stack;
        }
        if (stack.is(EIOTags.Items.ENTITY_STORAGE)) {
            StoredEntityData ghost = stack.get(EIODataComponents.STORED_ENTITY);
            consumer.accept(ghost);
        } else if (stack.getItem() instanceof SpawnEggItem spawnEggItem) {
            Entity entity = spawnEggItem.getType(stack).create(Minecraft.getInstance().level);
            if (entity instanceof LivingEntity livingEntity) {
                StoredEntityData ghost = new StoredEntityData(livingEntity.serializeNBT(Minecraft.getInstance().level.registryAccess()), livingEntity.getMaxHealth());
                consumer.accept(ghost);
            }
        }

        return stack;
    }
}
