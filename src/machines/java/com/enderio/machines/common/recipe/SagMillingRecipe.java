package com.enderio.machines.common.recipe;

import com.enderio.EnderIO;
import com.enderio.api.grindingball.IGrindingBallData;
import com.enderio.core.common.integration.Integrations;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.core.common.util.JsonUtil;
import com.enderio.core.common.util.TagUtil;
import com.enderio.machines.common.blockentity.SagMillBlockEntity;
import com.enderio.machines.common.init.MachineRecipes;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
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
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
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

    /**
     * JEI for sag mill will not use this, it'll use a capacitor data.
     */
    @Override
    public int getBaseEnergyCost() {
        return energy;
    }

    @Override
    public int getEnergyCost(Container container) {
        return getEnergyCost(container.getGrindingBall());
    }

    public int getEnergyCost(IGrindingBallData grindingBallData) {
        return (int) (energy * grindingBallData.getPowerUse());
    }

    public BonusType getBonusType() {
        return bonusType;
    }

    @Override
    public List<OutputStack> craft(Container container, RegistryAccess registryAccess) {
        List<OutputStack> outputs = new ArrayList<>();

        // Iterate over the number of outputs
        float outputCount = getBonusType().canMultiply() ? container.getGrindingBall().getOutputMultiplier() : 1.0f;
        float chanceMult = getBonusType().doChance() ? container.getGrindingBall().getBonusMultiplier() : 1.0f;

        // Iterate over the number of outputs.
        // Without a grinding ball this only runs once.
        while (outputCount > 0) {
            if (RANDOM.nextFloat() < outputCount) {
                for (OutputItem output : this.outputs) {
                    if (output.isPresent() && RANDOM.nextFloat() < output.chance() * chanceMult) {
                        // Collect the output
                        ItemStack outputStack = output.getItemStack();

                        // Attempt to add to an existing stack.
                        for (OutputStack stack : outputs) {
                            if (outputStack.getCount() <= 0) {
                                break;
                            }

                            ItemStack itemStack = stack.getItem();
                            if (itemStack.is(outputStack.getItem())) {
                                int growth = Math.min(outputStack.getCount(), itemStack.getMaxStackSize());
                                itemStack.grow(growth);
                                outputStack.shrink(growth);
                            }
                        }

                        // Add new stack.
                        if (outputStack.getCount() >= 0) {
                            outputs.add(OutputStack.of(outputStack));
                        }
                    }
                }
            }
            outputCount--;
        }

        return outputs;
    }

    @Override
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
        // TODO: This logic seems dumb.
        // Gather guaranteed outputs (that are loaded)
        List<OutputStack> guaranteedOutputs = new ArrayList<>();
        for (OutputItem item : outputs) {
            if (item.chance >= 1.0f && item.isPresent()) {
                guaranteedOutputs.add(OutputStack.of(item.getItemStack()));
            }
        }
        return guaranteedOutputs;
    }

    public List<OutputItem> getOutputs() {
        return outputs;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, input);
    }

    @Override
    public boolean matches(Container container, Level level) {
        return input.test(SagMillBlockEntity.INPUT.getItemStack(container));
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MachineRecipes.SAG_MILLING.serializer().get();
    }

    @Override
    public RecipeType<?> getType() {
        return MachineRecipes.SAG_MILLING.type().get();
    }

    public enum BonusType {
        NONE(false, false),
        MULTIPLY_OUTPUT(true, true),
        CHANCE_ONLY(false, true);

        private final boolean multiply;
        private final boolean chance;

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

    public record OutputItem(
        Either<ItemStack, SizedTagOutput> output,
        float chance,
        boolean isOptional
    ) {
        public static OutputItem of(@Nullable Item item, int count, float chance, boolean optional) {
            return of(item == null ? ItemStack.EMPTY : new ItemStack(item, count), chance, optional);
        }

        public static OutputItem of(ItemStack item, float chance, boolean optional) {
            return new OutputItem(Either.left(item), chance, optional);
        }

        public static OutputItem of(TagKey<Item> tag, int count, float chance, boolean optional) {
            return new OutputItem(Either.right(new SizedTagOutput(tag, count)), chance, optional);
        }

        public boolean isPresent() {
            return !getItemStack().isEmpty();
        }

        public ItemStack getItemStack() {
            return output.map(ItemStack::copy, SizedTagOutput::getItemStack);
        }

        public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.add("item", output.map(JsonUtil::serializeItemStackWithoutNBT, SizedTagOutput::toJson));
            json.addProperty("chance", chance);
            json.addProperty("optional", isOptional);
            return json;
        }

        public static OutputItem fromJson(JsonObject json, ResourceLocation recipeId) {
            // Load misc properties
            float chance = json.has("chance") ? json.get("chance").getAsFloat() : 1.0f;
            boolean optional = json.has("optional") && json.get("optional").getAsBoolean();

            // NOTE: Count is ignored if the new ItemStack or new Tag entries are present.
            int legacyCount = json.has("count") ? json.get("count").getAsInt() : 1;

            if (json.has("tag")) {
                // Get tag
                ResourceLocation id = new ResourceLocation(json.get("tag").getAsString());
                TagKey<Item> tag = ItemTags.create(id);

                // Check tag has entries if its required (although the point of a tag is generally this will be optional, its just in case
                if (!optional && TagUtil.getOptionalItem(tag).isEmpty()) {
                    EnderIO.LOGGER.error("Sag milling recipe {} is missing a required output tag {}", recipeId, id);
                    throw new RuntimeException("Sag milling recipe is missing a required output tag.");
                }

                return OutputItem.of(tag, legacyCount, chance, optional);
            } else {
                JsonElement itemJson = json.get("item");

                if (itemJson.isJsonObject()) {
                    // NEW!
                    JsonObject newItemJson = itemJson.getAsJsonObject();

                    if (newItemJson.has("tag")) {
                        ResourceLocation id = new ResourceLocation(newItemJson.get("tag").getAsString());
                        TagKey<Item> tag = ItemTags.create(id);
                        int count = newItemJson.has("count") ? newItemJson.get("count").getAsInt() : 1;

                        // Check tag has entries if its required (although the point of a tag is generally this will be optional, its just in case
                        if (!optional && TagUtil.getOptionalItem(tag).isEmpty()) {
                            EnderIO.LOGGER.error("Sag milling recipe {} is missing a required output tag {}", recipeId, id);
                            throw new RuntimeException("Sag milling recipe is missing a required output tag.");
                        }

                        return OutputItem.of(tag, count, chance, optional);
                    } else {
                        ItemStack item = CraftingHelper.getItemStack(newItemJson.getAsJsonObject(), true, true);
                        return OutputItem.of(item, chance, optional);
                    }
                } else {
                    ResourceLocation id = new ResourceLocation(json.get("item").getAsString());
                    Item item = ForgeRegistries.ITEMS.getValue(id);

                    // Check that the required item exists.
                    if (item == null && !optional) {
                        EnderIO.LOGGER.error("Sag milling recipe {} is missing a required output item {}", recipeId, id);
                        throw new RuntimeException("Sag milling recipe is missing a required output item.");
                    }

                    return OutputItem.of(item, legacyCount, chance, optional);
                }
            }
        }

        public record SizedTagOutput(TagKey<Item> itemTag, int count) {
            public ItemStack getItemStack() {
                return TagUtil.getOptionalItem(itemTag)
                    .map(ItemStack::new)
                    .orElse(ItemStack.EMPTY);
            }

            public JsonObject toJson() {
                JsonObject json = new JsonObject();
                json.addProperty("tag", itemTag.location().toString());
                json.addProperty("count", count);
                return json;
            }
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

    public static class Serializer implements RecipeSerializer<SagMillingRecipe> {

        @Override
        public SagMillingRecipe fromJson(ResourceLocation recipeId, JsonObject serializedRecipe) {
            // Load ingredient
            Ingredient input = Ingredient.fromJson(serializedRecipe.get("input"));

            // Load energy
            int energy = serializedRecipe.get("energy").getAsInt();

            // Get the bonus type.
            BonusType bonusType = BonusType.MULTIPLY_OUTPUT;
            if (serializedRecipe.has("bonus")) {
                bonusType = BonusType.valueOf(serializedRecipe.get("bonus").getAsString().toUpperCase());
            }

            // Load outputs
            JsonArray jsonOutputs = serializedRecipe.getAsJsonArray("outputs");
            List<OutputItem> outputs = new ArrayList<>();
            for (int i = 0; i < jsonOutputs.size(); i++) {
                JsonObject obj = jsonOutputs.get(i).getAsJsonObject();
                outputs.add(OutputItem.fromJson(obj, recipeId));
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
                    boolean isItem = buffer.readBoolean();

                    if (isItem) {
                        ItemStack output = buffer.readItem();
                        float chance = buffer.readFloat();
                        boolean optional = buffer.readBoolean();
                        outputs.add(OutputItem.of(output, chance, optional));
                    } else {
                        // Create tag
                        ResourceLocation id = buffer.readResourceLocation();
                        TagKey<Item> tag = ItemTags.create(id);

                        int count = buffer.readInt();
                        float chance = buffer.readFloat();
                        boolean optional = buffer.readBoolean();
                        outputs.add(OutputItem.of(tag, count, chance, optional));
                    }
                }

                return new SagMillingRecipe(recipeId, input, outputs, energy, bonusType);
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error reading sag milling recipe to packet.", ex);
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
                    buffer.writeBoolean(item.output.left().isPresent());

                    if (item.output.left().isPresent()) {
                        buffer.writeItem(item.output.left().get());
                    } else {
                        buffer.writeResourceLocation(item.output.right().get().itemTag().location());
                        buffer.writeInt(item.output.right().get().count());
                    }

                    buffer.writeFloat(item.chance);
                    buffer.writeBoolean(item.isOptional);
                }
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error writing allow smelting recipe to packet.", ex);
                throw ex;
            }
        }
    }
}
