package jeiexporter.handler;

import com.google.gson.JsonElement;
import jeiexporter.handler.ingredient.IIngredient;
import jeiexporter.handler.ingredient.JeiIngredientWrapper;
import jeiexporter.jei.JEIConfig;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author youyihj
 */
public interface IRecipeConverter {
    Map<String, JsonElement> getRecipeExtraData();

    Map<String, JsonElement> getIngredientExtraData(IIngredient<?> ingredient);

    IRecipeWrapper getRecipe();

    default List<IIngredient<?>> getIngredients(IRecipeLayout layout, boolean input) {
        List<IIngredient<?>> ingredients = new ArrayList<>();
        for (IIngredientType<?> type : JEIConfig.getIngredientRegistry().getRegisteredIngredientTypes()) {
            IGuiIngredientGroup<?> group = layout.getIngredientsGroup(type);
            for (IGuiIngredient<?> ingredient : group.getGuiIngredients().values()) {
                if (ingredient.isInput() == input) {
                    ingredients.add(new JeiIngredientWrapper<>(ingredient));
                }
            }
        }
        return ingredients;
    }

    class Default implements IRecipeConverter {

        private final IRecipeWrapper wrapper;

        public Default(IRecipeWrapper wrapper) {
            this.wrapper = wrapper;
        }

        @Override
        public Map<String, JsonElement> getRecipeExtraData() {
            return Collections.emptyMap();
        }

        @Override
        public Map<String, JsonElement> getIngredientExtraData(IIngredient<?> ingredient) {
            return Collections.emptyMap();
        }

        @Override
        public IRecipeWrapper getRecipe() {
            return wrapper;
        }
    }
}
