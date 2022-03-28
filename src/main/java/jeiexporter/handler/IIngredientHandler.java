package jeiexporter.handler;

import net.minecraft.client.Minecraft;

import java.util.List;

/**
 * @author youyihj
 */
public interface IIngredientHandler<T> {
    String getDisplayName(T ingredient);

    String getInternalId(T ingredient);

    int getAmount(T ingredient);

    void drawIngredient(Minecraft minecraft, T ingredient);

    List<String> getTooltip(Minecraft minecraft, T ingredient);

    int getImageWidth();

    int getImageHeight();

    String getType();

    default String getTag(T ingredient) {
        return "";
    }
}
