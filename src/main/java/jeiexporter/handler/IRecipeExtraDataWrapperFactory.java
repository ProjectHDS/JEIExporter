package jeiexporter.handler;

import mezz.jei.api.recipe.IRecipeWrapper;

/**
 * @author youyihj
 */
@FunctionalInterface
public interface IRecipeExtraDataWrapperFactory<T extends IRecipeWrapper> {
    IRecipeExtraDataWrapper build(T recipe);
}
