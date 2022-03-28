package jeiexporter.jei;

import mezz.jei.api.*;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.gui.GuiHelper;
import mezz.jei.ingredients.IngredientRegistry;
import mezz.jei.runtime.JeiRuntime;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@JEIPlugin
public class JEIConfig implements IModPlugin {
    private static IJeiRuntime jeiRuntime;
    private static IJeiHelpers helpers;
    private static IngredientRegistry ingredientRegistry;

    @Override
    public void register(IModRegistry registry) {
        ingredientRegistry = (IngredientRegistry)registry.getIngredientRegistry();
        helpers = registry.getJeiHelpers();
    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        JEIConfig.jeiRuntime = jeiRuntime;
    }

    public static IJeiRuntime getJeiRuntime() {
        return jeiRuntime;
    }

    public static IngredientRegistry getIngredientRegistry() {
        return ingredientRegistry;
    }

    public static List<String> recipeCategoryUids() {
        List<String> list = new ArrayList<>();
        for (IRecipeCategory category : jeiRuntime.getRecipeRegistry().getRecipeCategories())
            list.add(category.getUid());
        return list;
    }

    public static IJeiHelpers getHelpers() {
        return helpers;
    }
}
