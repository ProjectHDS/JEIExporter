package jeiexporter.handler.ingredient;

import jeiexporter.util.JeiHacks;
import mezz.jei.api.gui.IGuiIngredient;

import java.awt.*;
import java.util.List;

/**
 * @author youyihj
 */
public class JeiIngredientWrapper<T> implements IIngredient<T> {
    private final IGuiIngredient<T> ingredient;
    private final Rectangle rectangle;

    public JeiIngredientWrapper(IGuiIngredient<T> ingredient) {
        this.ingredient = ingredient;
        this.rectangle = JeiHacks.getRect(ingredient);
    }

    @Override
    public List<T> members() {
        return ingredient.getAllIngredients();
    }

    @Override
    public double getXPosition() {
        return rectangle.getX();
    }

    @Override
    public double getYPosition() {
        return rectangle.getY();
    }
}
