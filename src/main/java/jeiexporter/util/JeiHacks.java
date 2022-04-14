package jeiexporter.util;

import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.gui.ingredients.GuiIngredient;
import mezz.jei.gui.ingredients.GuiIngredientGroup;
import mezz.jei.gui.recipes.RecipeLayout;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author youyihj
 */
public class JeiHacks {
    private static final Field guiIngredientRect;
    private static final Field guiIngredientGroupTooltips;
    private static final Field recipeLayoutWrapper;

    static {
        guiIngredientRect = ObfuscationReflectionHelper.findField(GuiIngredient.class, "rect");
        guiIngredientGroupTooltips = ObfuscationReflectionHelper.findField(GuiIngredientGroup.class, "tooltipCallback");
        recipeLayoutWrapper = ObfuscationReflectionHelper.findField(RecipeLayout.class, "recipeWrapper");
    }

    public static Rectangle getRect(IGuiIngredient<?> guiIngredient) {
        try {
            return (Rectangle) guiIngredientRect.get(guiIngredient);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return new Rectangle();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<String> getExtraTooltip(IGuiIngredientGroup<T> ingredientGroup, int slotIndex, IGuiIngredient<T> ingredient) {
        try {
            ITooltipCallback<T> tooltipCallback = (ITooltipCallback<T>) guiIngredientGroupTooltips.get(ingredientGroup);
            List<String> tooltips = new ArrayList<>();
            if (ingredient.getDisplayedIngredient() != null) {
                tooltipCallback.onTooltip(slotIndex, ingredient.isInput(), ingredient.getDisplayedIngredient(), tooltips);
            }
            return tooltips;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static IRecipeWrapper getRecipeWrapper(IRecipeLayout layout) {
        try {
            return ((IRecipeWrapper) recipeLayoutWrapper.get(layout));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
