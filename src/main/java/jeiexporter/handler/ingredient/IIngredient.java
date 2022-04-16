package jeiexporter.handler.ingredient;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author youyihj
 */
public interface IIngredient<T> {
    List<T> members();

    @Nullable
    default T firstIngredient() {
        if (members().isEmpty()) return null;
        return members().get(0);
    }

    double getXPosition();

    double getYPosition();
}
