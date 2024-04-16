package com.enderio.conduits.common;

import com.enderio.api.capability.IConduitFilter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ConduitFilter implements IConduitFilter {
    public static String TAG_FILTER_INVERT_MODE = "invert_mode";
    public static String TAG_FILTER_STRICT_MODE = "strict_mode";

    private final ItemStack stack;
    private final int size;
    private boolean invertMode;
    private boolean strictMode;

    @Nullable private List<ItemStack> filterItems;

    public ConduitFilter(ItemStack stack, int size) {
        this.stack = stack;
        this.size = size;
        this.invertMode = stack.getOrCreateTag().getBoolean(TAG_FILTER_INVERT_MODE);
        this.strictMode = stack.getOrCreateTag().getBoolean(TAG_FILTER_STRICT_MODE);
    }

    @Override
    public List<ItemStack> getItems() {
        if (filterItems == null) {
            if (stack.hasTag()) {
                ListTag list = stack.getTag().getList("items", 10);
                filterItems = new ArrayList<>(list.size());

                for (int i = 0; i < list.size(); i++) {
                    ItemStack stack = ItemStack.of(list.getCompound(i));

                    if (!stack.isEmpty()) {
                        filterItems.add(stack);
                    }
                }
            } else {
                filterItems = new ArrayList<>(2);
            }
        }
        return filterItems;
    }

    @Override
    public boolean testItem(ItemStack item) {
        for (var ourItem : getItems()) {
            if (strictMode ? ItemStack.isSameItemSameTags(item, ourItem) : ItemStack.isSameItem(item, ourItem)) return !invertMode;
        }
        return invertMode;
    }

    @Override
    public void save() {
        if (stack.isEmpty()) return;

        if (getItems().isEmpty()) {
            stack.removeTagKey("items");
            return;
        }

        ListTag list = new ListTag();

        for (ItemStack stack : getItems()) {
            list.add(stack.save(new CompoundTag()));
        }

        stack.addTagElement("items", list);
    }

    // region getters/setters
    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean getIgnoreMode() {
        return invertMode;
    }

    @Override
    public void setIgnoreMode(boolean newState) {
        if (stack.isEmpty()) return;
        if (invertMode == newState) return;
        invertMode = newState;
        stack.getOrCreateTag().putBoolean(TAG_FILTER_INVERT_MODE, invertMode);
    }

    @Override
    public boolean getStrictMode() {
        return strictMode;
    }

    @Override
    public void setStrictMode(boolean newState) {
        if (stack.isEmpty()) return;
        if (strictMode == newState) return;
        strictMode = newState;
        stack.getOrCreateTag().putBoolean(TAG_FILTER_STRICT_MODE, strictMode);
    }
    // endregion
}
