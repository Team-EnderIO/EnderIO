package com.enderio.machines.common.recipe;

import com.enderio.EnderIO;
import com.enderio.api.grindingball.IGrindingBallData;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.core.common.util.TagUtil;
import com.enderio.machines.common.blockentity.SagMillBlockEntity;
import com.enderio.machines.common.init.MachineRecipes;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

public class SagMillingRecipe implements MachineRecipe<SagMillingRecipe.Container> {
    private static final Random RANDOM = new Random();

    final Ingredient input;
    final List<OutputItem> outputs;
    final int energy;
    final BonusType bonusType;

    public SagMillingRecipe(Ingredient input, List<OutputItem> outputs, int energy, BonusType bonusType) {
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
                    if (output.isPresent() && RANDOM.nextFloat() < output.getChance() * chanceMult) {
                        // Collect the output
                        Item item = output.getItem();
                        int count = output.getCount();

                        // Attempt to add to an existing stack.
                        for (OutputStack stack : outputs) {
                            if (count <= 0) {
                                break;
                            }

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
    public List<OutputStack> getResultStacks(RegistryAccess registryAccess) {
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

    public static class OutputItem {

        private static final Codec<OutputItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TagKey.codec(Registries.ITEM).optionalFieldOf("tag").forGetter(output -> output.item.right()),
            BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("item").forGetter(output -> output.item.left()),
            Codec.INT.optionalFieldOf("count", 1).forGetter(output -> output.count),
            Codec.FLOAT.optionalFieldOf("chance", 1f).forGetter(output -> output.chance),
            Codec.BOOL.optionalFieldOf("optional", false).forGetter(output -> output.optional)
        ).apply(instance, OutputItem::of));
        private final Either<Item, TagKey<Item>> item;
        private final int count;
        private final float chance;
        private final boolean optional;

        public static OutputItem of(Item item, int count, float chance, boolean optional) {
            return new OutputItem(Either.left(item), count, chance, optional);
        }

        public static OutputItem of(TagKey<Item> tag, int count, float chance, boolean optional) {
            return new OutputItem(Either.right(tag), count, chance, optional);
        }
        public static OutputItem of(Optional<TagKey<Item>> tag, Optional<Item> item, int count, float chance, boolean optional) {
            if (tag.isPresent()) {
                return new OutputItem(Either.right(tag.get()), count, chance, optional);
            }

            if (item.isPresent()) {
                return new OutputItem(Either.left(item.get()), count, chance, optional);
            }

            throw new IllegalStateException("either tag or item need to be present");
        }

        public OutputItem(Either<Item, TagKey<Item>> item, int count, float chance, boolean optional) {
            this.item = item;
            this.count = count;
            this.chance = chance;
            this.optional = optional;
        }

        public boolean isPresent() {
            return getItem() != null;
        }

        @Nullable
        public Item getItem() {
            return item.left().or(() -> TagUtil.getOptionalItem(item.right().get())).orElse(null);
        }

        public ItemStack getItemStack() {
            Item item = getItem();
            if (item != null) {
                return new ItemStack(item, count);
            }

            return ItemStack.EMPTY;
        }

        @Nullable
        public TagKey<Item> getTag() {
            if (!isTag()) {
                return null;
            }

            return item.right().get();
        }

        public boolean isTag() {
            return item.right().isPresent();
        }

        public boolean isItem() {
            return item.left().isPresent();
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

    public static class Serializer implements RecipeSerializer<SagMillingRecipe> {

        Codec<SagMillingRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(recipe -> recipe.input),
            OutputItem.CODEC.listOf().fieldOf("outputs").forGetter(recipe -> recipe.outputs),
            Codec.INT.fieldOf("energy").forGetter(recipe -> recipe.energy),
            Codec.STRING.xmap(v -> BonusType.valueOf(v.toUpperCase(Locale.ROOT)), e -> e.name().toLowerCase(Locale.ROOT)).optionalFieldOf("bonus", BonusType.MULTIPLY_OUTPUT).forGetter(recipe -> recipe.bonusType)
        ).apply(instance, SagMillingRecipe::new));

        @Override
        public Codec<SagMillingRecipe> codec() {
            return CODEC;
        }

        @Override
        @Nullable
        public SagMillingRecipe fromNetwork(FriendlyByteBuf buffer) {
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
                        //if (!optional && ForgeRegistries.ITEMS.tags().getTag(tag).isEmpty()) {
                        //    EnderIO.LOGGER.error("Sag milling recipe {} is missing a required output tag {}", recipeId, id);
                        //    throw new RuntimeException("Sag milling recipe is missing a required output tag.");
                        //}

                        outputs.add(OutputItem.of(tag, count, chance, optional));
                    } else {
                        Item item = BuiltInRegistries.ITEM.get(id);

                        // Check the required items are present.
                        if (item == null && !optional) {
                            EnderIO.LOGGER.error("Sag milling recipe is missing a required output item {}", id);
                            return null;
                        }

                        outputs.add(OutputItem.of(item, count, chance, optional));
                    }
                }

                return new SagMillingRecipe(input, outputs, energy, bonusType);
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error reading sag milling recipe to packet.", ex);
                return null;
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
                        buffer.writeResourceLocation(item.getTag().location());
                    } else {
                        buffer.writeResourceLocation(BuiltInRegistries.ITEM.getKey(item.getItem()));
                    }

                    buffer.writeInt(item.count);
                    buffer.writeFloat(item.chance);
                    buffer.writeBoolean(item.optional);
                }
            } catch (Exception ex) {
                EnderIO.LOGGER.error("Error writing sag milling recipe to packet.", ex);
                throw ex;
            }
        }
    }
}
