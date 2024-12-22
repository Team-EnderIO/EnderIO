package com.enderio.core.common.network.menu;

import com.enderio.core.common.network.menu.payload.NullSlotPayload;
import com.enderio.core.common.network.menu.payload.ResourceLocationSlotPayload;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class RecipeSyncSlot<T extends Recipe<?>> implements SyncSlot {

    public static <T extends Recipe<?>> RecipeSyncSlot<T> standalone(RecipeType<T> recipeType) {
        return new RecipeSyncSlot<>(recipeType) {
            @Nullable
            private RecipeHolder<T> value;

            @Override
            @Nullable
            public RecipeHolder<T> get() {
                return value;
            }

            @Override
            public void set(@Nullable RecipeHolder<T> value) {
                this.value = value;
            }
        };
    }

    public static <T extends Recipe<?>> RecipeSyncSlot<T> simple(RecipeType<T> recipeType,
            Supplier<RecipeHolder<T>> getter, Consumer<RecipeHolder<T>> setter) {
        return new RecipeSyncSlot<>(recipeType) {

            @Override
            @Nullable
            public RecipeHolder<T> get() {
                return getter.get();
            }

            @Override
            public void set(@Nullable RecipeHolder<T> value) {
                setter.accept(value);
            }
        };
    }

    public static <T extends Recipe<?>> RecipeSyncSlot<T> readOnly(RecipeType<T> recipeType,
            Supplier<RecipeHolder<T>> getter) {
        return new RecipeSyncSlot<>(recipeType) {

            @Override
            @Nullable
            public RecipeHolder<T> get() {
                return getter.get();
            }

            @Override
            public void set(@Nullable RecipeHolder<T> value) {
                throw new UnsupportedOperationException("Attempt to set a read-only sync slot.");
            }
        };
    }

    private final RecipeType<T> recipeType;
    private RecipeHolder<T> lastValue;

    public RecipeSyncSlot(RecipeType<T> recipeType) {
        this.recipeType = recipeType;
    }

    @Nullable
    public abstract RecipeHolder<T> get();

    public abstract void set(@Nullable RecipeHolder<T> value);

    @Override
    public ChangeType detectChanges() {
        var currentValue = get();
        var changeType = Objects.equals(currentValue, lastValue) ? ChangeType.NONE : ChangeType.FULL;
        lastValue = currentValue;
        return changeType;
    }

    @Override
    public SlotPayload createPayload(Level level, ChangeType changeType) {
        var value = get();
        if (value == null) {
            return new NullSlotPayload();
        }

        return new ResourceLocationSlotPayload(value.id());
    }

    @Override
    public void unpackPayload(Level level, SlotPayload payload) {
        if (payload instanceof ResourceLocationSlotPayload resourceLocationSlotPayload) {
            Optional<RecipeHolder<?>> recipe = level.getRecipeManager().byKey(resourceLocationSlotPayload.value());

            set(recipe.map(holder -> {
                if (holder.value().getType() == recipeType) {
                    // noinspection unchecked
                    return (RecipeHolder<T>) holder;
                } else {
                    return null;
                }
            }).orElse(null));
        } else if (payload instanceof NullSlotPayload) {
            set(null);
        }
    }
}
