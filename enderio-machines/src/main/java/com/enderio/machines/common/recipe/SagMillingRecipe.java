package com.enderio.machines.common.recipe;

import com.enderio.api.grindingball.IGrindingBallData;
import com.enderio.api.machines.recipes.MachineRecipe;
import com.enderio.api.machines.recipes.OutputStack;
import com.enderio.base.common.util.TagUtil;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class SagMillingRecipe implements MachineRecipe<SagMillingRecipe.Container> {
    private static final Random RANDOM = new Random();

    private final ResourceLocation id;
    private final Ingredient input;
    private final List<OutputItem> outputs;
    private final int energy;
    private final BonusType bonusType;

    public SagMillingRecipe(ResourceLocation id, Ingredient input, List<OutputItem> outputs, int energy, BonusType bonusType) {
        this.id = id;
        this.input = input;
        this.outputs = outputs;
        this.energy = energy;
        this.bonusType = bonusType;
    }

    public Ingredient getInput() {
        return input;
    }

    @Override
    public int getEnergyCost(Container container) {
        return (int) (energy * container.getGrindingBall().getPowerUse());
    }

    public BonusType getBonusType() {
        return bonusType;
    }

    @Override
    public List<OutputStack> craft(Container container) {
        List<OutputStack> outputs = new ArrayList<>();

        // Iterate over the number of outputs
        float outputCount = getBonusType().canMultiply() ? container.getGrindingBall().getOutputMultiplier() : 1.0f;
        float chanceMult = getBonusType().doChance() ? container.getGrindingBall().getBonusMultiplier() : 1.0f;

        // Iterate over the number of outputs.
        // Without a grinding ball this only runs once.
        while (outputCount > 0) {
            if (RANDOM.nextFloat() < outputCount) {
                for (OutputItem output : this.outputs) {
                    if (output.isPresent() && RANDOM.nextFloat() < output.getChance() * chanceMult) {
                        // Collect the output
                        Item item = output.getItem();
                        int count = output.getCount();

                        // Attempt to add to an existing stack.
                        for (OutputStack stack : outputs) {
                            if (count <= 0)
                                break;

                            ItemStack itemStack = stack.getItem();
                            if (itemStack.is(item)) {
                                int growth = Math.min(count, itemStack.getMaxStackSize());
                                itemStack.grow(growth);
                                count -= growth;
                            }
                        }

                        // Add new stack.
                        if (count >= 0) {
                            outputs.add(OutputStack.of(new ItemStack(item, count)));
                        }
                    }
                }
            }
            outputCount--;
        }

        return outputs;
    }

    @Override
    public List<OutputStack> getResultStacks() {
        // TODO: This logic seems dumb.
        // Gather guaranteed outputs (that are loaded)
        List<OutputStack> guaranteedOutputs = new ArrayList<>();
        for (OutputItem item : outputs) {
            if (item.chance >= 1.0f && item.isPresent()) {
                guaranteedOutputs.add(OutputStack.of(new ItemStack(item.getItem(), item.getCount())));
            }
        }
        return guaranteedOutputs;
    }

    @Override
    public boolean matches(Container container, Level level) {
        return input.test(container.getItem(0));
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MachineRecipes.Serializer.SAGMILLING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.Types.SAGMILLING;
    }

    public enum BonusType {
        NONE(false, false),
        MULTIPLY_OUTPUT(true, true),
        CHANCE_ONLY(false, true);

        private final boolean multiply, chance;

        BonusType(boolean multiply, boolean chance) {
            this.multiply = multiply;
            this.chance = chance;
        }

        public boolean canMultiply() {
            return multiply;
        }

        public boolean doChance() {
            return chance;
        }

        public boolean useGrindingBall() {
            return multiply || chance;
        }
    }

    public static class OutputItem {
        private final @Nullable Item item;
        private final @Nullable TagKey<Item> tag;
        private final int count;
        private final float chance;
        private final boolean optional;

        public static OutputItem of(Item item, int count, float chance, boolean optional) {
            return new OutputItem(item, null, count, chance, optional);
        }

        public static OutputItem of(TagKey<Item> tag, int count, float chance, boolean optional) {
            return new OutputItem(null, tag, count, chance, optional);
        }

        public OutputItem(@Nullable Item item, @Nullable TagKey<Item> tag, int count, float chance, boolean optional) {
            this.item = item;
            this.tag = tag;
            this.count = count;
            this.chance = chance;
            this.optional = optional;
        }

        public boolean isPresent() {
            return getItem() != null;
        }

        public @Nullable Item getItem() {
            if (item != null)
                return item;
            if (tag != null)
                return TagUtil.getOptionalItem(tag).orElse(null);
            return null;
        }

        public @Nullable TagKey<Item> getTag() {
            return tag;
        }

        public boolean isTag() {
            return tag != null;
        }

        public boolean isItem() {
            return item != null;
        }

        public int getCount() {
            return count;
        }

        public float getChance() {
            return chance;
        }

        public boolean isOptional() {
            return optional;
        }
    }

    public static class Container extends RecipeWrapper {

        private final Supplier<IGrindingBallData> grindingBallData;

        public Container(IItemHandlerModifiable inv, Supplier<IGrindingBallData> data) {
            super(inv);
            this.grindingBallData = data;
        }

        public final IGrindingBallData getGrindingBall() {
            return grindingBallData.get();
        }
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<SagMillingRecipe> {

        @Override
        public SagMillingRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            // Load ingredient
            Ingredient input = Ingredient.fromJson(serializedRecipe.get("input"));

            // Load energy
            int energy = serializedRecipe.get("energy").getAsInt();

            // Get the bonus type.
            BonusType bonusType = BonusType.MULTIPLY_OUTPUT;
            if (serializedRecipe.has("bonus")) {
                BonusType.valueOf(serializedRecipe.get("bonus").getAsString().toUpperCase());
            }

            // Load outputs
            JsonArray jsonOutputs = serializedRecipe.getAsJsonArray("outputs");
            List<OutputItem> outputs = new ArrayList<>();
            for (int i = 0; i < jsonOutputs.size(); i++) {
                JsonObject obj = jsonOutputs.get(i).getAsJsonObject();

                // Load misc properties
                int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
                float chance = obj.has("chance") ? obj.get("chance").getAsFloat() : 1.0f;
                boolean optional = obj.has("optional") && obj.get("optional").getAsBoolean();

                // Load item/tag and create output element
                if (obj.has("tag")) {
                    // Get tag
                    ResourceLocation id = new ResourceLocation(obj.get("tag").getAsString());
                    TagKey<Item> tag = ItemTags.create(id);

                    // TODO: move these tests into OutputItem instead..
                    // Check tag has entries if its required (although the point of a tag is generally this will be optional, its just in case
                    if (!optional && ForgeRegistries.ITEMS.tags().getTag(tag).isEmpty()) {
                        EIOMachines.LOGGER.error("Sag milling recipe {} is missing a required output tag {}", recipeId, id);
                        throw new RuntimeException("Sag milling recipe is missing a required output tag.");
                    }

                    outputs.add(OutputItem.of(tag, count, chance, optional));
                } else {
                    ResourceLocation id = new ResourceLocation(obj.get("item").getAsString());
                    Item item = ForgeRegistries.ITEMS.getValue(id);

                    // TODO: move these tests into OutputItem instead..
                    if (item == null && !optional) {
                        EIOMachines.LOGGER.error("Sag milling recipe {} is missing a required output item {}", recipeId, id);
                        throw new RuntimeException("Sag milling recipe is missing a required output item.");
                    }

                    outputs.add(OutputItem.of(item, count, chance, optional));
                }
            }

            return new SagMillingRecipe(recipeId, input, outputs, energy, bonusType);
        }

        @Nullable
        @Override
        public SagMillingRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            try {
                Ingredient input = Ingredient.fromNetwork(buffer);
                int energy = buffer.readInt();
                BonusType bonusType = buffer.readEnum(BonusType.class);

                List<OutputItem> outputs = new ArrayList<>();
                int outputCount = buffer.readInt();
                for (int i = 0; i < outputCount; i++) {
                    boolean isTag = buffer.readBoolean();
                    ResourceLocation id = buffer.readResourceLocation();

                    int count = buffer.readInt();
                    float chance = buffer.readFloat();
                    boolean optional = buffer.readBoolean();

                    if (isTag) {
                        // Create tag
                        TagKey<Item> tag = ItemTags.create(id);

                        // TODO: move these tests into OutputItem instead..
                        // Check tag has entries if its required (although the point of a tag is generally this will be optional, its just in case
                        if (!optional && ForgeRegistries.ITEMS.tags().getTag(tag).isEmpty()) {
                            EIOMachines.LOGGER.error("Sag milling recipe {} is missing a required output tag {}", recipeId, id);
                            throw new RuntimeException("Sag milling recipe is missing a required output tag.");
                        }

                        outputs.add(OutputItem.of(tag, count, chance, optional));
                    } else {
                        Item item = ForgeRegistries.ITEMS.getValue(id);

                        // TODO: move these tests into OutputItem instead..
                        if (item == null && !optional) {
                            EIOMachines.LOGGER.error("Sag milling recipe {} is missing a required output item {}", recipeId, id);
                            throw new RuntimeException("Sag milling recipe is missing a required output item.");
                        }

                        outputs.add(OutputItem.of(item, count, chance, optional));
                    }
                }

                return new SagMillingRecipe(recipeId, input, outputs, energy, bonusType);
            } catch (Exception ex) {
                EIOMachines.LOGGER.error("Error reading allow smelting recipe to packet.", ex);
                throw ex;
            }
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, SagMillingRecipe recipe) {
            try {
                recipe.input.toNetwork(buffer);
                buffer.writeInt(recipe.energy);
                buffer.writeEnum(recipe.bonusType);

                buffer.writeInt(recipe.outputs.size());
                for (OutputItem item : recipe.outputs) {
                    // Set a flag to determine tag or item
                    buffer.writeBoolean(item.isTag());

                    if (item.isTag()) {
                        buffer.writeResourceLocation(item.tag.location());
                    } else {
                        buffer.writeResourceLocation(item.item.getRegistryName());
                    }

                    buffer.writeInt(item.count);
                    buffer.writeFloat(item.chance);
                    buffer.writeBoolean(item.optional);
                }
            } catch (Exception ex) {
                EIOMachines.LOGGER.error("Error writing allow smelting recipe to packet.", ex);
                throw ex;
            }
        }
    }
}
