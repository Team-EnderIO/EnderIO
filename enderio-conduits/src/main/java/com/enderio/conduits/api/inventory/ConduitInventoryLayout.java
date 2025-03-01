package com.enderio.conduits.api.inventory;

import com.enderio.conduits.api.bundle.SlotType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@ApiStatus.Experimental
public class ConduitInventoryLayout {

    private int slotCount;
    private List<Vector2i> slotPositions;
    private Map<Integer, Function<ItemStack, Boolean>> slotPredicates;

    private ConduitInventoryLayout(List<Vector2i> slotPositions, Map<Integer, Function<ItemStack, Boolean>> slotPredicates) {
        this.slotCount = slotPositions.size();
        this.slotPositions = slotPositions;
        this.slotPredicates = slotPredicates;
    }

    public int getSize() {
        return slotCount;
    }

    public int getSlotLimit() {
        // TODO: Configurable.
        return 1;
    }

    public Vector2i getPosition(int slot) {
        return slotPositions.get(slot);
    }

    public boolean isValid(int slot, ItemStack stack) {
        if (slotPredicates.containsKey(slot)) {
            return slotPredicates.get(slot).apply(stack);
        }

        return true;
    }

    public static Builder legacyBuilder() {
        return new Builder();
    }

    // TODO! Temporary while I build out the ConduitInventoryLayout logic :)
    public static class Builder {

        private final List<SlotType> allowedSlotTypes = new ArrayList<>();

        private Builder() {
        }

        public Builder addSlot(SlotType slotType) {
            if (allowedSlotTypes.contains(slotType)) {
                throw new IllegalArgumentException("Slot type " + slotType + " is already allowed.");
            }

            allowedSlotTypes.add(slotType);
            // TODO: Slot predicates...
            return this;
        }

        public ConduitInventoryLayout build() {
            var sortedSlots = allowedSlotTypes.stream().sorted(Enum::compareTo).toList();

            // Build list of positions
            var positions = new ArrayList<Vector2i>();
            for (var slot : sortedSlots) {
                positions.add(new Vector2i(slot.getX(), slot.getY()));
            }

            return new ConduitInventoryLayout(positions, Map.of());
        }
    }
}
