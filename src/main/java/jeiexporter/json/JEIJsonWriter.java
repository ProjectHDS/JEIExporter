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
import java.util.stream.Collectors;

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

            Collection<? extends IGuiIngredient<ItemStack>> inputItems = items.stream().filter(item -> item.isInput()).collect(Collectors.toList());
            Collection<? extends IGuiIngredient<ItemStack>> outputItems = items.stream().filter(item -> !item.isInput()).collect(Collectors.toList());

            Collection<? extends IGuiIngredient<FluidStack>> inputFluids = fluids.stream().filter(fluid -> fluid.isInput()).collect(Collectors.toList());
            Collection<? extends IGuiIngredient<FluidStack>> outputFluids = fluids.stream().filter(fluid -> !fluid.isInput()).collect(Collectors.toList());

            this.jsonWriter.name("input");
            this.jsonWriter.beginObject();
                this.jsonWriter.name("items");
                this.jsonWriter.beginArray();
                    for (IGuiIngredient<ItemStack> ingredient : inputItems)
                        writeItem(ingredient);
                    for (IGuiIngredient<FluidStack> ingredient : inputFluids)
                        writeFluid(ingredient);
                this.jsonWriter.endArray();
            this.jsonWriter.endObject();

            this.jsonWriter.name("output");
            this.jsonWriter.beginObject();
                this.jsonWriter.name("items");
                this.jsonWriter.beginArray();
                    for (IGuiIngredient<ItemStack> ingredient : outputItems)
                        writeItem(ingredient);
                    for (IGuiIngredient<FluidStack> ingredient : outputFluids)
                        writeFluid(ingredient);
                this.jsonWriter.endArray();
            this.jsonWriter.endObject();
        this.jsonWriter.endObject();
    }

    public void writeItem(IGuiIngredient<ItemStack> ingredient) throws IOException {
        Adapters.item.write(this.jsonWriter, ingredient);
    }

    public void writeFluid(IGuiIngredient<FluidStack> ingredient) throws IOException {
        Adapters.fluid.write(this.jsonWriter, ingredient);
    }

    public void close() throws IOException {
        this.jsonWriter.endArray();
        this.jsonWriter.endObject();
        this.jsonWriter.flush();
        this.jsonWriter.close();
    }
}
