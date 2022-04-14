package jeiexporter.handler;

import mezz.jei.api.recipe.IRecipeWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author youyihj
 */
public class RecipeExtraDataWrapperFactories {
    private static final Map<Class<?>, IRecipeExtraDataWrapperFactory<?>> factories = new HashMap<>();

    public static <T extends IRecipeWrapper> void register(Class<T> recipeClass, IRecipeExtraDataWrapperFactory<T> factory) {
        factories.put(recipeClass, factory);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IRecipeWrapper> Optional<IRecipeExtraDataWrapper> buildWrapper(T recipe) {
        return Optional.ofNullable(factories.get(recipe.getClass()))
                .map(factory -> ((IRecipeExtraDataWrapperFactory<T>) factory))
                .map(factory -> factory.build(recipe));
    }
}
