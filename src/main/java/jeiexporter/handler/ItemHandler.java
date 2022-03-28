package jeiexporter.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * @author youyihj
 */
public class ItemHandler implements IIngredientHandler<ItemStack> {

    @Override
    public String getDisplayName(ItemStack ingredient) {
        return ingredient.getDisplayName();
    }

    @Override
    public String getInternalId(ItemStack ingredient) {
        if (ingredient == null) return "";
        String regName = ingredient.getItem().getRegistryName() + ":" + ingredient.getMetadata();
        if (ingredient.hasTagCompound())
            regName += ":" + Integer.toHexString(ingredient.getTagCompound().toString().hashCode());
        return regName;
    }

    @Override
    public int getAmount(ItemStack ingredient) {
        return ingredient.getCount();
    }

    @Override
    public void drawIngredient(Minecraft minecraft, ItemStack ingredient) {
        minecraft.getRenderItem().renderItemAndEffectIntoGUI(ingredient, 0, 0);
    }

    @Override
    public List<String> getTooltip(Minecraft minecraft, ItemStack ingredient) {
        return ingredient.getTooltip(null, ITooltipFlag.TooltipFlags.NORMAL);
    }

    @Override
    public int getImageHeight() {
        return 32;
    }

    @Override
    public int getImageWidth() {
        return 32;
    }

    @Override
    public String getType() {
        return "item";
    }

    @Override
    public String getTag(ItemStack ingredient) {
        if (ingredient.hasTagCompound()) {
            return ingredient.getTagCompound().toString();
        }
        return "";
    }
}
