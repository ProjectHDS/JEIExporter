package jeiexporter.handler;

import mezz.jei.api.recipe.IRecipeWrapper;

/**
 * @author youyihj
 */
@FunctionalInterface
public interface IRecipeConverterFactory<T extends IRecipeWrapper> {
    IRecipeConverter build(T recipe);
}
