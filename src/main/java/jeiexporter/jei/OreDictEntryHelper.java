package jeiexporter.jei;

import mezz.jei.api.ingredients.IIngredientHelper;

import javax.annotation.Nullable;

/**
 * @author youyihj
 */
public class OreDictEntryHelper implements IIngredientHelper<OreDictEntry> {
    @Nullable
    @Override
    public OreDictEntry getMatch(Iterable<OreDictEntry> ingredients, OreDictEntry ingredientToMatch) {
        for (OreDictEntry ingredient : ingredients) {
            if (ingredientToMatch.getName().equals(ingredient.getName())) {
                return ingredient;
            }
        }
        return null;
    }

    @Override
    public String getDisplayName(OreDictEntry ingredient) {
        return ingredient.getName();
    }

    @Override
    public String getUniqueId(OreDictEntry ingredient) {
        return "oredict:" + ingredient.getName();
    }

    @Override
    public String getWildcardId(OreDictEntry ingredient) {
        return getUniqueId(ingredient);
    }

    @Override
    public String getModId(OreDictEntry ingredient) {
        return "forge";
    }

    @Override
    public String getResourceId(OreDictEntry ingredient) {
        return "forge";
    }

    @Override
    public OreDictEntry copyIngredient(OreDictEntry ingredient) {
        return ingredient;
    }

    @Override
    public String getErrorInfo(@Nullable OreDictEntry ingredient) {
        return "error: oredict ingredient";
    }
}
