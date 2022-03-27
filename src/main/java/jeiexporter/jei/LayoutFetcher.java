package jeiexporter.jei;

import jeiexporter.config.ConfigHandler;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.gui.Focus;
import mezz.jei.gui.recipes.IRecipeGuiLogic;
import mezz.jei.gui.recipes.IRecipeLogicStateListener;
import mezz.jei.gui.recipes.RecipeGuiLogic;
import mezz.jei.recipes.RecipeRegistry;
import mezz.jei.runtime.JeiRuntime;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

public class LayoutFetcher {
    private static LayoutFetcher instance;
    private IRecipeGuiLogic logic;

    public static LayoutFetcher getInstance() {
        if (instance == null)
            instance = new LayoutFetcher();
        return instance;
    }

    private LayoutFetcher() {
        this.logic = new RecipeGuiLogic(JEIConfig.getJeiRuntime().getRecipeRegistry(), new BlankStateListener(), JEIConfig.getIngredientRegistry());
    }

    public List<IRecipeLayout> getRecipes(ItemStack itemStack) {
        List<IRecipeLayout> list = new ArrayList<>();
        this.logic.setFocus(new Focus<>(Focus.Mode.OUTPUT, itemStack));
        this.logic.setRecipesPerPage(1);
        String startCategory = this.logic.getSelectedRecipeCategory().getUid();
        do {
            do {
                list.addAll(this.logic.getRecipeLayouts(0, 0, 0));
                this.logic.nextPage();
            } while (!this.logic.getPageString().startsWith("1/"));
            this.logic.nextRecipeCategory();
        } while (!this.logic.getSelectedRecipeCategory().getUid().equals(startCategory));
        return list;
    }

    public Map<IRecipeCategory, List<IRecipeLayout>> fetchAll() {
        Map<IRecipeCategory, List<IRecipeLayout>> map = new HashMap<>();
        this.logic.setCategoryFocus(JEIConfig.recipeCategoryUids());
        this.logic.setRecipesPerPage(1);
        String startCategory = this.logic.getSelectedRecipeCategory().getUid();
        do {
            if (!ArrayUtils.contains(ConfigHandler.categoryBlacklist, this.logic.getSelectedRecipeCategory().getUid())) {
                List<IRecipeLayout> layouts = new ArrayList<>();
                do {
                    layouts.addAll(this.logic.getRecipeLayouts(0, 0, 0));
                    this.logic.nextPage();
                } while (!this.logic.getPageString().startsWith("1/"));
                map.put(this.logic.getSelectedRecipeCategory(), layouts);
            }
            this.logic.nextRecipeCategory();
        } while (!this.logic.getSelectedRecipeCategory().getUid().equals(startCategory));
        return map;
    }

    private static class BlankStateListener implements IRecipeLogicStateListener {
        @Override
        public void onStateChange() {

        }
    }
}
