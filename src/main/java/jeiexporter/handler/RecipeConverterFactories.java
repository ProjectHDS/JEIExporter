package jeiexporter.handler;

import mezz.jei.api.recipe.IRecipeWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author youyihj
 */
public class RecipeConverterFactories {
    private static final Map<Class<?>, IRecipeConverterFactory<?>> factories = new HashMap<>();

    public static <T extends IRecipeWrapper> void register(Class<T> recipeClass, IRecipeConverterFactory<T> factory) {
        factories.put(recipeClass, factory);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IRecipeWrapper> IRecipeConverter buildConverter(T recipe) {
        return Optional.ofNullable(factories.get(recipe.getClass()))
                .map(factory -> ((IRecipeConverterFactory<T>) factory))
                .map(factory -> factory.build(recipe))
                .orElseGet(() -> new IRecipeConverter.Default(recipe));
    }
}
