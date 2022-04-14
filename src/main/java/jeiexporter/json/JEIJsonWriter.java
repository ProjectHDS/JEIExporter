package jeiexporter.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;
import jeiexporter.handler.IIngredientHandler;
import jeiexporter.handler.IRecipeExtraDataWrapper;
import jeiexporter.handler.IngredientHandlers;
import jeiexporter.handler.RecipeExtraDataWrapperFactories;
import jeiexporter.jei.JEIConfig;
import jeiexporter.jei.OreDictEntry;
import jeiexporter.render.IconList;
import jeiexporter.util.JeiHacks;
import mezz.jei.Internal;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
        Optional<IRecipeExtraDataWrapper> extraDataWrapper = RecipeExtraDataWrapperFactories.buildWrapper(recipeWrapper);
        this.jsonWriter.beginObject();
        if (extraDataWrapper.isPresent()) {
            Map<String, JsonElement> recipeExtraData = extraDataWrapper.get().getRecipeExtraData();
            for (Map.Entry<String, JsonElement> entry : recipeExtraData.entrySet()) {
                writeEntry(entry);
            }
        }
        this.jsonWriter.name("input");
        this.jsonWriter.beginObject();
        this.jsonWriter.name("items");
        this.jsonWriter.beginArray();
        for (IIngredientType<?> type : JEIConfig.getIngredientRegistry().getRegisteredIngredientTypes()) {
            IGuiIngredientGroup<?> group = layout.getIngredientsGroup(type);
            for (IGuiIngredient<?> ingredient : group.getGuiIngredients().values()) {
                if (ingredient.isInput()) {
                    writeIngredient(ingredient, extraDataWrapper.orElse(null));
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
                    writeIngredient(ingredient, extraDataWrapper.orElse(null));
                }
            }
        }
        this.jsonWriter.endArray();
        this.jsonWriter.endObject();
        this.jsonWriter.endObject();
    }

    public <T> void writeIngredient(IGuiIngredient<T> ingredient, IRecipeExtraDataWrapper extraDataWrapper) throws IOException {
        this.jsonWriter.beginObject();
        if (!ingredient.getAllIngredients().isEmpty() && ingredient.getAllIngredients().get(0) != null) {
            IIngredientHandler<T> handler = IngredientHandlers.getHandlerByIngredient(ingredient.getAllIngredients().get(0));
            jsonWriter.name("amount").value(handler.getAmount(ingredient.getAllIngredients().get(0)));
        } else {
            jsonWriter.name("amount").value(0);
        }
        Rectangle rect = JeiHacks.getRect(ingredient);
        jsonWriter.name("x").value(rect.getX());
        jsonWriter.name("y").value(rect.getY());
        jsonWriter.name("stacks").beginArray();
        for (Object element : getIngredients(ingredient)) {
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
        if (extraDataWrapper != null) {
            Map<String, JsonElement> ingredientExtraData = extraDataWrapper.getIngredientExtraData(ingredient);
            for (Map.Entry<String, JsonElement> entry : ingredientExtraData.entrySet()) {
                writeEntry(entry);
            }
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
    private <T> List<?> getIngredients(IGuiIngredient<T> ingredient) {
        List<T> allIngredients = ingredient.getAllIngredients();
        T displayedIngredient = ingredient.getDisplayedIngredient();
        if (displayedIngredient instanceof ItemStack) {
            List<ItemStack> allItems = new ArrayList<>((List<ItemStack>) allIngredients);
            allItems.removeIf(stack -> stack == null || stack.isEmpty());
            int count = ((ItemStack) displayedIngredient).getCount();
            String oreDict = Internal.getStackHelper().getOreDictEquivalent(allItems);
            if (oreDict != null) {
                return Collections.singletonList(new OreDictEntry(oreDict, count));
            }
        }
        return allIngredients;
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
