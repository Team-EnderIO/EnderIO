package com.enderio.conduits.data.model;

import com.enderio.EnderIOBase;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class FacadeItemModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {

    private ResourceLocation model;

    public static <T extends ModelBuilder<T>> FacadeItemModelBuilder<T> begin(T parent,
            ExistingFileHelper existingFileHelper) {
        return new FacadeItemModelBuilder<>(parent, existingFileHelper);
    }

    protected FacadeItemModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(EnderIOBase.loc("facades_item"), parent, existingFileHelper, false);
    }

    public FacadeItemModelBuilder<T> model(String name) {
        this.model = EnderIOBase.loc("block/" + name);
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        json = super.toJson(json);
        JsonObject model = new JsonObject();
        model.addProperty("parent", this.model.toString());
        json.add("model", model);
        return json;
    }
}
