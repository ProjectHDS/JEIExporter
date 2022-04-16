package jeiexporter.handler.mods;

import com.google.gson.JsonElement;
import com.rwtema.extrautils2.crafting.jei.JEIMachine;
import jeiexporter.handler.IRecipeConverter;
import jeiexporter.handler.ingredient.IIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author youyihj
 */
public class XUConverter implements IRecipeConverter {
    private final JEIMachine.JEIMachineRecipe.Wrapper wrapper;

    public XUConverter(JEIMachine.JEIMachineRecipe.Wrapper wrapper) {
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
    public List<IIngredient<?>> getIngredients(IRecipeLayout layout, boolean input) {
        GlStateManager.pushMatrix();
        wrapper.drawInfo(Minecraft.getMinecraft(), 180, 180, 90, 90);
        GlStateManager.popMatrix();
        return IRecipeConverter.super.getIngredients(layout, input);
    }

    @Override
    public IRecipeWrapper getRecipe() {
        return wrapper;
    }
}
