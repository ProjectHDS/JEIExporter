package jeiexporter.handler;

import com.google.gson.JsonElement;
import mezz.jei.api.gui.IGuiIngredient;

import java.util.Map;

/**
 * @author youyihj
 */
public interface IRecipeExtraDataWrapper {
    Map<String, JsonElement> getRecipeExtraData();

    Map<String, JsonElement> getIngredientExtraData(IGuiIngredient<?> ingredient);
}
