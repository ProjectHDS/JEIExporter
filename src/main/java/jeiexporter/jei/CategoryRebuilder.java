package jeiexporter.jei;

import mezz.jei.JustEnoughItems;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.startup.ProxyCommonClient;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.*;

/**
 * @author youyihj
 */
public class CategoryRebuilder implements IRecipeCategoryRegistration {
    private final List<IRecipeCategory> categories = new ArrayList<>();

    @Override
    public void addRecipeCategories(IRecipeCategory... recipeCategories) {
        Collections.addAll(categories, recipeCategories);
    }

    @Override
    public IJeiHelpers getJeiHelpers() {
        return JEIConfig.getHelpers();
    }

    public List<IRecipeCategory> getCategories() {
        return categories;
    }

    public void rebuildCategory() {
        ProxyCommonClient proxy = (ProxyCommonClient) JustEnoughItems.getProxy();
        List<IModPlugin> plugins = ObfuscationReflectionHelper.getPrivateValue(ProxyCommonClient.class, proxy, "plugins");
        plugins.forEach(it -> it.registerCategories(this));
    }
}
