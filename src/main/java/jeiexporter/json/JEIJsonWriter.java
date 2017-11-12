package jeiexporter.json;

import com.google.gson.stream.JsonWriter;
import jeiexporter.config.ConfigHandler;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public class JEIJsonWriter {
    private JsonWriter jsonWriter;
    private static String dir;

    public static String getDir() {
        if (dir != null) return dir;
        dir = ConfigHandler.getConfigDir().getAbsolutePath() + "/exports/recipes/";
        new File(dir).mkdir();
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

        Collection<? extends IGuiIngredient<ItemStack>> items = layout.getItemStacks().getGuiIngredients().values();
        Collection<? extends IGuiIngredient<FluidStack>> fluids = layout.getFluidStacks().getGuiIngredients().values();

        this.jsonWriter.name("input");
        this.jsonWriter.beginObject();

        this.jsonWriter.name("items");
        this.jsonWriter.beginArray();
        for (IGuiIngredient<ItemStack> ingredient : items)
            writeInputItem(ingredient);
        this.jsonWriter.endArray();

        this.jsonWriter.name("fluids");
        this.jsonWriter.beginArray();
        for (IGuiIngredient<FluidStack> ingredient : fluids)
            writeInputFluid(ingredient);
        this.jsonWriter.endArray();

        this.jsonWriter.endObject();

        this.jsonWriter.name("output");
        this.jsonWriter.beginObject();

        this.jsonWriter.name("items");
        this.jsonWriter.beginArray();
        for (IGuiIngredient<ItemStack> ingredient : items)
            writeOutputItem(ingredient);
        this.jsonWriter.endArray();

        this.jsonWriter.name("fluids");
        this.jsonWriter.beginArray();
        for (IGuiIngredient<FluidStack> ingredient : fluids)
            writeOutputFluid(ingredient);
        this.jsonWriter.endArray();

        this.jsonWriter.endObject();
        this.jsonWriter.endObject();
    }

    public void writeInputItem(IGuiIngredient<ItemStack> ingredient) throws IOException {
        Adapters.itemInput.write(this.jsonWriter, ingredient);
    }

    public void writeInputFluid(IGuiIngredient<FluidStack> ingredient) throws IOException {
        Adapters.fluidInput.write(this.jsonWriter, ingredient);
    }

    public void writeOutputItem(IGuiIngredient<ItemStack> ingredient) throws IOException {
        Adapters.itemOutput.write(this.jsonWriter, ingredient);
    }

    public void writeOutputFluid(IGuiIngredient<FluidStack> ingredient) throws IOException {
        Adapters.fluidOutput.write(this.jsonWriter, ingredient);
    }

    public void close() throws IOException {
        this.jsonWriter.endArray();
        this.jsonWriter.endObject();
        this.jsonWriter.flush();
        this.jsonWriter.close();
    }
}
