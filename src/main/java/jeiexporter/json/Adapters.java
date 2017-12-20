package jeiexporter.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import jeiexporter.jei.JEIConfig;
import jeiexporter.render.RenderFluid;
import jeiexporter.render.RenderIDrawable;
import jeiexporter.render.RenderItem;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.gui.ingredients.GuiIngredient;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class Adapters {
    public static final TypeAdapter<IRecipeCategory> drawable = new TypeAdapter<IRecipeCategory>() {
        @Override
        public void write(JsonWriter out, IRecipeCategory value) throws IOException {
            IDrawable drawable = value.getBackground();
            out.name("title").value(value.getTitle());
            out.name("width").value(drawable.getWidth());
            out.name("height").value(drawable.getHeight());
            out.name("texture").value(RenderIDrawable.render(drawable, value.getUid()));
            out.name("catalysts").beginArray();
            List<Object> catalysts = JEIConfig.getJeiRuntime().getRecipeRegistry().getRecipeCatalysts(value);
            for (Object itemStack : catalysts)
                out.value(RenderItem.render((ItemStack)itemStack));
            out.endArray();
        }

        @Override
        public IRecipeCategory read(JsonReader in) throws IOException {
            return null;
        }
    };

    public static final TypeAdapter<IGuiIngredient<ItemStack>> item = new TypeAdapter<IGuiIngredient<ItemStack>>() {
        @Override
        public void write(JsonWriter out, IGuiIngredient<ItemStack> value) throws IOException {
            out.beginObject();

            if (value.getAllIngredients().size() > 0 && value.getAllIngredients().get(0) != null) {
                out.name("amount").value(value.getAllIngredients().get(0).getCount());
            } else {
                out.name("amount").value(0);
            }
            out.name("stacks").beginArray();
            for (ItemStack itemStack : value.getAllIngredients()) {
                out.beginObject();
                out.name("name").value(RenderItem.render(itemStack));
                out.endObject();
            }
            out.endArray();

            out.endObject();
        }

        @Override
        public IGuiIngredient<ItemStack> read(JsonReader in) throws IOException {
            return null;
        }
    };

    public static final TypeAdapter<IGuiIngredient<FluidStack>> fluid = new TypeAdapter<IGuiIngredient<FluidStack>>() {
        @Override
        public void write(JsonWriter out, IGuiIngredient<FluidStack> value) throws IOException {
            out.beginObject();

            if (value.getAllIngredients().size() > 0 && value.getAllIngredients().get(0) != null) {
                out.name("amount").value(value.getAllIngredients().get(0).amount);
            } else {
                out.name("amount").value(0);
            }
            out.name("stacks").beginArray();
            for (FluidStack fluidStack : value.getAllIngredients()) {
                out.beginObject();
                out.name("name").value(RenderFluid.render(fluidStack));
                out.endObject();
            }
            out.endArray();

            out.endObject();
        }

        @Override
        public IGuiIngredient<FluidStack> read(JsonReader in) throws IOException {
            return null;
        }
    };

    private static int getInt(Field field, Object object) {
        try {
            return field.getInt(object);
        } catch (IllegalAccessException e) {
            return 0;
        }
    }
}
