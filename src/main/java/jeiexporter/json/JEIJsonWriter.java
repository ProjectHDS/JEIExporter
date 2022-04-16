package jeiexporter.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;
import jeiexporter.handler.IIngredientHandler;
import jeiexporter.handler.IRecipeConverter;
import jeiexporter.handler.IngredientHandlers;
import jeiexporter.handler.RecipeConverterFactories;
import jeiexporter.handler.ingredient.IIngredient;
import jeiexporter.jei.OreDictEntry;
import jeiexporter.render.IconList;
import jeiexporter.util.JeiHacks;
import mezz.jei.Internal;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
        this.jsonWriter = new JsonWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(getDir() + filename + ".json")), StandardCharsets.UTF_8));
        this.jsonWriter.setIndent("  ");
    }

    public void writeTitle(IRecipeCategory category) throws IOException {
        this.jsonWriter.beginObject();
        Adapters.drawable.write(this.jsonWriter, category);
        this.jsonWriter.name("recipes");
        this.jsonWriter.beginArray();
    }

    public void writeLayout(IRecipeLayout layout) throws IOException {
        IRecipeWrapper recipeWrapper = JeiHacks.getRecipeWrapper(layout);
        IRecipeConverter converter = RecipeConverterFactories.buildConverter(recipeWrapper);
        this.jsonWriter.beginObject();
        Map<String, JsonElement> recipeExtraData = converter.getRecipeExtraData();
        for (Map.Entry<String, JsonElement> entry : recipeExtraData.entrySet()) {
            writeEntry(entry);
        }
        this.jsonWriter.name("input");
        this.jsonWriter.beginObject();
        this.jsonWriter.name("items");
        this.jsonWriter.beginArray();
        for (IIngredient<?> ingredient : converter.getIngredients(layout, true)) {
            writeIngredient(ingredient, converter);
        }
        this.jsonWriter.endArray();
        this.jsonWriter.endObject();

        this.jsonWriter.name("output");
        this.jsonWriter.beginObject();
        this.jsonWriter.name("items");
        this.jsonWriter.beginArray();
        for (IIngredient<?> ingredient : converter.getIngredients(layout, false)) {
            writeIngredient(ingredient, converter);
        }
        this.jsonWriter.endArray();
        this.jsonWriter.endObject();
        this.jsonWriter.endObject();
    }

    public <T> void writeIngredient(IIngredient<T> ingredient, IRecipeConverter converter) throws IOException {
        jsonWriter.beginObject();
        IIngredient<?> ingredient1 = convertItemsToOreDict(ingredient);
        List<?> members = ingredient1.members();
        Object firstIngredient = ingredient1.firstIngredient();
        if (firstIngredient != null) {
            IIngredientHandler<Object> handler = IngredientHandlers.getHandlerByIngredient(members.get(0));
            jsonWriter.name("amount").value(handler.getAmount(members.get(0)));
        } else {
            jsonWriter.name("amount").value(0);
        }
        jsonWriter.name("x").value(ingredient1.getXPosition());
        jsonWriter.name("y").value(ingredient1.getYPosition());
        jsonWriter.name("stacks").beginArray();
        for (Object element : members) {
            if (element == null) continue;
            IIngredientHandler<Object> handler = IngredientHandlers.getHandlerByIngredient(element);
            jsonWriter.beginObject();
            jsonWriter.name("type").value(handler.getType());
            jsonWriter.name("name").value(handler.getInternalId(element));
            NameMap.add(element);
            IconList.add(element);
            jsonWriter.endObject();
        }
        jsonWriter.endArray();
        Map<String, JsonElement> ingredientExtraData = converter.getIngredientExtraData(ingredient);
        for (Map.Entry<String, JsonElement> entry : ingredientExtraData.entrySet()) {
            writeEntry(entry);
        }
        jsonWriter.endObject();
    }

    public void close() throws IOException {
        this.jsonWriter.endArray();
        this.jsonWriter.endObject();
        this.jsonWriter.flush();
        this.jsonWriter.close();
    }

    @SuppressWarnings("unchecked")
    private IIngredient<?> convertItemsToOreDict(IIngredient<?> ingredient) {
        if (ingredient.firstIngredient() instanceof ItemStack) {
            List<ItemStack> allItems = new ArrayList<>(((List<ItemStack>) ingredient.members()));
            allItems.removeIf(stack -> stack == null || stack.isEmpty());
            int count = ((ItemStack) Objects.requireNonNull(ingredient.firstIngredient())).getCount();
            String oreDict = Internal.getStackHelper().getOreDictEquivalent(allItems);
            if (oreDict != null) {
                return ingredient.withMembers(Collections.singletonList(new OreDictEntry(oreDict, count)));
            }
        }
        return ingredient;
    }

    private void writeEntry(Map.Entry<String, JsonElement> entry) throws IOException {
        this.jsonWriter.name(entry.getKey());
        this.writeElement(entry.getValue());
    }

    private void writeElement(JsonElement element) throws IOException {
        if (element.isJsonNull()) {
            this.jsonWriter.nullValue();
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
            if (jsonPrimitive.isBoolean()) {
                this.jsonWriter.value(jsonPrimitive.getAsBoolean());
            } else if (jsonPrimitive.isNumber()) {
                this.jsonWriter.value(jsonPrimitive.getAsNumber());
            } else if (jsonPrimitive.isString()) {
                this.jsonWriter.value(jsonPrimitive.getAsString());
            }
        } else if (element.isJsonArray()) {
            this.jsonWriter.beginArray();
            for (JsonElement jsonElement : element.getAsJsonArray()) {
                this.writeElement(jsonElement);
            }
            this.jsonWriter.endArray();
        } else if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();
            this.jsonWriter.beginObject();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                writeEntry(entry);
            }
            this.jsonWriter.endObject();
        }
    }
}
