package jeiexporter.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import jeiexporter.handler.IngredientHandlers;
import jeiexporter.jei.JEIConfig;
import jeiexporter.render.RenderIDrawable;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.item.ItemStack;

import java.io.IOException;
import java.util.List;

public class Adapters {
    public static final TypeAdapter<IRecipeCategory<?>> drawable = new TypeAdapter<IRecipeCategory<?>>() {
        @Override
        public void write(JsonWriter out, IRecipeCategory<?> value) throws IOException {
            IDrawable drawable = value.getBackground();
            out.name("width").value(drawable.getWidth());
            out.name("height").value(drawable.getHeight());
            out.name("texture").value(RenderIDrawable.render(drawable, value.getUid()));
            out.name("catalysts").beginArray();
            List<Object> catalysts = JEIConfig.getJeiRuntime().getRecipeRegistry().getRecipeCatalysts(value);
            for (Object itemStack : catalysts) {
                if (itemStack instanceof ItemStack) {
                    out.value(IngredientHandlers.getHandlerByIngredient(itemStack).getInternalId(itemStack));
                } else {
                    out.value(itemStack.toString());
                }
            }
            out.endArray();
        }

        @Override
        public IRecipeCategory<?> read(JsonReader in) throws IOException {
            return null;
        }
    };
}
