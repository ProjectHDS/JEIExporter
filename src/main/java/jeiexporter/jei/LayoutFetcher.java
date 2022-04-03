package jeiexporter.jei;

import jeiexporter.config.ConfigHandler;
import jeiexporter.render.Loading;
import jeiexporter.util.LogHelper;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.gui.Focus;
import mezz.jei.gui.recipes.IRecipeGuiLogic;
import mezz.jei.gui.recipes.IRecipeLogicStateListener;
import mezz.jei.gui.recipes.RecipeGuiLogic;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<String> categoryUids = JEIConfig.recipeCategoryUids();
        this.logic.setCategoryFocus(categoryUids);
        this.logic.setRecipesPerPage(1);
        String startCategory = this.logic.getSelectedRecipeCategory().getUid();
        int index = 0;
        do {
            int recipesNumber = 0;
            index++;
            IRecipeCategory selectedCategory = this.logic.getSelectedRecipeCategory();
            String selectedCategoryUid = selectedCategory.getUid();
            int finalIndex = index;
            if (!ArrayUtils.contains(ConfigHandler.categoryBlacklist, selectedCategoryUid)) {
                List<IRecipeLayout> layouts = new ArrayList<>();
                LogHelper.info("Begin fetching category: " + selectedCategoryUid);
                do {
                    recipesNumber++;
                    int finalRecipesNumber = recipesNumber;
                    layouts.addAll(this.logic.getRecipeLayouts(0, 0, 0));
                    this.logic.nextPage();
                    Loading.render(() -> new Loading.Context(
                            "Fetching all jei recipes",
                            "Fetching " + selectedCategory.getUid() + " (" + finalRecipesNumber + " recipes fetched)",
                            finalIndex * 1.0f / categoryUids.size(),
                            String.format("%s/%s", finalIndex, categoryUids.size()),
                            0.0f
                    ));
                } while (!this.logic.getPageString().startsWith("1/"));
                map.put(selectedCategory, layouts);
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
