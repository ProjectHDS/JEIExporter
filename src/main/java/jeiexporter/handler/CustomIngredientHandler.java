package jeiexporter.handler;

import jeiexporter.config.ConfigHandler;
import jeiexporter.jei.JEIConfig;
import jeiexporter.util.LogHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.recipe.IIngredientType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.ToIntFunction;

/**
 * @author youyihj
 */
public class CustomIngredientHandler<T> implements IIngredientHandler<T> {

    private final IIngredientHelper<T> helper;
    private final IIngredientRenderer<T> renderer;
    private final ToIntFunction<T> amountSupplier;
    private final IIngredientType<T> type;
    private int imageWidth = 0;
    private int imageHeight = 0;

    public CustomIngredientHandler(IIngredientType<T> type) {
        this.helper = JEIConfig.getIngredientRegistry().getIngredientHelper(type);
        this.renderer = JEIConfig.getIngredientRegistry().getIngredientRenderer(type);
        this.amountSupplier = initAmountSupplier(type);
        this.type = type;
        for (String ingredientIconSize : ConfigHandler.ingredientIconSizes) {
            String[] split = ingredientIconSize.split(";", 3);
            if (split[0].equals(type.getIngredientClass().getCanonicalName())) {
                this.imageWidth = Integer.parseInt(split[1]);
                this.imageHeight = Integer.parseInt(split[2]);
                break;
            }
        }
    }

    @Override
    public String getDisplayName(T ingredient) {
        return helper.getDisplayName(ingredient);
    }

    @Override
    public String getInternalId(T ingredient) {
        return helper.getUniqueId(ingredient);
    }

    @Override
    public int getAmount(T ingredient) {
        return amountSupplier.applyAsInt(ingredient);
    }

    @Override
    public void drawIngredient(Minecraft minecraft, T ingredient) {
        renderer.render(minecraft, 0, 0, ingredient);
    }

    @Override
    public List<String> getTooltip(Minecraft minecraft, T ingredient) {
        return renderer.getTooltip(minecraft, ingredient, ITooltipFlag.TooltipFlags.NORMAL);
    }

    @Override
    public int getImageWidth() {
        return imageWidth;
    }

    @Override
    public int getImageHeight() {
        return imageHeight;
    }

    @Override
    public String getType() {
        return type.getIngredientClass().getCanonicalName();
    }

    private ToIntFunction<T> initAmountSupplier(IIngredientType<T> type) {
        for (String entry : ConfigHandler.ingredientAmountSupplier) {
            String[] entries = entry.split(";", 2);
            Class<? extends T> ingredientClass = type.getIngredientClass();
            if (entries[0].equals(ingredientClass.getCanonicalName())) {
                if (entries[1].endsWith("()")) {
                    String methodName = entries[1].substring(0, entries[1].length() - 2);
                    Method method = ObfuscationReflectionHelper.findMethod(ingredientClass, methodName, int.class);
                    return (ingredient) -> {
                        try {
                            return (int) method.invoke(ingredient);
                        } catch (IllegalAccessException e) {
                            LogHelper.warn(method + " is not public");
                        } catch (InvocationTargetException e) {
                            LogHelper.warn("Failed to invoke " + method);
                        }
                        return 1;
                    };
                } else {
                    Field field = ObfuscationReflectionHelper.findField(ingredientClass, entries[1]);
                    return (ingredient) -> {
                        try {
                            return field.getInt(ingredient);
                        } catch (IllegalAccessException e) {
                            LogHelper.warn("Failed to get " + field);
                        }
                        return 1;
                    };
                }
            }
        }
        return ingredient -> 1;
    }
}
