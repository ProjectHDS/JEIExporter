package jeiexporter.handler.ingredient;

import java.util.List;

/**
 * @author youyihj
 */
public class SimpleIngredient<T> implements IIngredient<T> {
    private final List<T> members;
    private final double x, y;

    public SimpleIngredient(List<T> members, double x, double y) {
        this.members = members;
        this.x = x;
        this.y = y;
    }

    @Override
    public List<T> members() {
        return members;
    }

    @Override
    public double getXPosition() {
        return x;
    }

    @Override
    public double getYPosition() {
        return y;
    }
}
