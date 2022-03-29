package jeiexporter.handler;

import jeiexporter.jei.OreDictEntry;
import net.minecraft.client.Minecraft;

import java.util.List;

/**
 * @author youyihj
 */
public class OreDictHandler implements IIngredientHandler<OreDictEntry> {

    private final ItemHandler itemHandler;

    public OreDictHandler(ItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    @Override
    public String getDisplayName(OreDictEntry ingredient) {
        return itemHandler.getDisplayName(ingredient.getRepresentationItem());
    }

    @Override
    public String getInternalId(OreDictEntry ingredient) {
        return ingredient.getName();
    }

    @Override
    public int getAmount(OreDictEntry ingredient) {
        return ingredient.getAmount();
    }

    @Override
    public void drawIngredient(Minecraft minecraft, OreDictEntry ingredient) {
        itemHandler.drawIngredient(minecraft, ingredient.getRepresentationItem());
    }

    @Override
    public List<String> getTooltip(Minecraft minecraft, OreDictEntry ingredient) {
        return itemHandler.getTooltip(minecraft, ingredient.getRepresentationItem());
    }

    @Override
    public int getImageWidth() {
        return itemHandler.getImageWidth();
    }

    @Override
    public int getImageHeight() {
        return itemHandler.getImageHeight();
    }

    @Override
    public String getType() {
        return "oredict";
    }
}
