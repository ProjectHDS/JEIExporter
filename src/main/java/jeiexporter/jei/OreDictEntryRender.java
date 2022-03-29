package jeiexporter.jei;

import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author youyihj
 */
public class OreDictEntryRender implements IIngredientRenderer<OreDictEntry> {
    @Override
    public void render(Minecraft minecraft, int xPosition, int yPosition, @Nullable OreDictEntry ingredient) {
        if (ingredient != null) {
            minecraft.getRenderItem().renderItemAndEffectIntoGUI(ingredient.getRepresentationItem(), xPosition, yPosition);
        }
    }

    @Override
    public List<String> getTooltip(Minecraft minecraft, OreDictEntry ingredient, ITooltipFlag tooltipFlag) {
        return ingredient.getRepresentationItem().getTooltip(minecraft.player, tooltipFlag);
    }
}
