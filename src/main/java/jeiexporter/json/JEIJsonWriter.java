package jeiexporter.json;

import com.google.gson.stream.JsonWriter;
import jeiexporter.JEIExporter;
import jeiexporter.handler.IIngredientHandler;
import jeiexporter.handler.IngredientHandlers;
import jeiexporter.jei.JEIConfig;
import jeiexporter.render.IconList;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeCategory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JEIJsonWriter {
    private JsonWriter jsonWriter;
    private static String dir;

    public static String getDir() {
        if (dir != null) return dir;
        dir = "exports/recipes/";
        new File(dir).mkdirs();
        return dir;
    }

    public JEIJsonWriter(String filename) throws IOException {
        this.jsonWriter = new JsonWriter(new FileWriter(getDir() + filename + ".json"));
        this.jsonWriter.setIndent("  ");
    }

    public void writeTitle(IRecipeCategory category) throws IOException {
        this.jsonWriter.beginObject();
        Adapters.drawable.write(this.jsonWriter, category);
        this.jsonWriter.name("recipes");
        this.jsonWriter.beginArray();
    }

    public void writeLayout(IRecipeLayout layout) throws IOException {
        this.jsonWriter.beginObject();
        this.jsonWriter.name("input");
        this.jsonWriter.beginObject();
        this.jsonWriter.name("items");
        this.jsonWriter.beginArray();
        for (IIngredientType<?> type : JEIConfig.getIngredientRegistry().getRegisteredIngredientTypes()) {
            IGuiIngredientGroup<?> group = layout.getIngredientsGroup(type);
            for (IGuiIngredient<?> ingredient : group.getGuiIngredients().values()) {
                if (ingredient.isInput()) {
                    writeIngredient(ingredient);
                }
            }
        }
        this.jsonWriter.endArray();
        this.jsonWriter.endObject();

        this.jsonWriter.name("output");
        this.jsonWriter.beginObject();
        this.jsonWriter.name("items");
        this.jsonWriter.beginArray();
        for (IIngredientType<?> type : JEIConfig.getIngredientRegistry().getRegisteredIngredientTypes()) {
            IGuiIngredientGroup<?> group = layout.getIngredientsGroup(type);
            for (IGuiIngredient<?> ingredient : group.getGuiIngredients().values()) {
                if (!ingredient.isInput()) {
                    writeIngredient(ingredient);
                }
            }
        }
        this.jsonWriter.endArray();
        this.jsonWriter.endObject();
        this.jsonWriter.endObject();
    }

    public <T> void writeIngredient(IGuiIngredient<T> ingredient) throws IOException {
        this.jsonWriter.beginObject();
        if (!ingredient.getAllIngredients().isEmpty() && ingredient.getAllIngredients().get(0) != null) {
            IIngredientHandler<T> handler = IngredientHandlers.getHandlerByIngredient(ingredient.getAllIngredients().get(0));
            jsonWriter.name("amount").value(handler.getAmount(ingredient.getAllIngredients().get(0)));
        } else {
            jsonWriter.name("amount").value(0);
        }
        jsonWriter.name("stacks").beginArray();
        for (T element : ingredient.getAllIngredients()) {
            if (element == null) continue;
            IIngredientHandler<T> handler = IngredientHandlers.getHandlerByIngredient(element);
            jsonWriter.beginObject();
            jsonWriter.name("type").value(handler.getType());
            jsonWriter.name("name").value(handler.getInternalId(element));
            NameMap.add(element);
            IconList.add(element);
            jsonWriter.endObject();
        }
        jsonWriter.endArray();
        jsonWriter.endObject();
    }

    public void close() throws IOException {
        this.jsonWriter.endArray();
        this.jsonWriter.endObject();
        this.jsonWriter.flush();
        this.jsonWriter.close();
    }
}
