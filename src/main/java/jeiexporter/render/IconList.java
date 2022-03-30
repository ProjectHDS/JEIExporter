package jeiexporter.render;

import com.google.common.io.Files;
import jeiexporter.JEIExporter;
import jeiexporter.handler.IIngredientHandler;
import jeiexporter.handler.IngredientHandlers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author youyihj
 */
public class IconList {
    private static final Map<String, Object> ingredients = new HashMap<>();

    public static <T> void add(T ingredient) {
        IIngredientHandler<T> ingredientHandler = IngredientHandlers.getHandlerByIngredient(ingredient);
        int height = ingredientHandler.getImageHeight();
        int width = ingredientHandler.getImageWidth();
        if (height != 0 && width != 0) {
            ingredients.put(ingredientHandler.getInternalId(ingredient), ingredient);
        }
    }

    public static void clear() {
        ingredients.clear();
    }

    @SuppressWarnings({"UnstableApiUsage", "ResultOfMethodCallIgnored"})
    public static void renderIngredients() {
        int index = 0;
        int size = ingredients.size();
        for (Map.Entry<String, Object> entry : ingredients.entrySet()) {
            index++;
            String id = entry.getKey();
            Object ingredient = entry.getValue();
            int finalIndex = index;
            Loading.render(() -> new Loading.Context(
                            "Exporting ingredient icon",
                            "Exporting " + ingredient,
                            (finalIndex * 1F) / size,
                            String.format("%s/%s", finalIndex, size),
                            0.0f
                    )
            );

            IIngredientHandler<Object> handler = IngredientHandlers.getHandlerByIngredient(ingredient);
            File file = new File("exports/items/" + handler.getType() + "/" + id.replace(":", "__") + ".png");
            int height = handler.getImageHeight();
            int width = handler.getImageWidth();
            RenderHelper.setupRenderState(Math.max(height, width));
            GlStateManager.pushMatrix();
            GlStateManager.clearColor(0, 0, 0, 0);
            GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            handler.drawIngredient(Minecraft.getMinecraft(), ingredient);
            GlStateManager.popMatrix();
            BufferedImage img = RenderHelper.createFlipped(RenderHelper.readPixels(width, height));
            try {
                Files.createParentDirs(file);
                file.createNewFile();
                ImageIO.write(img, "PNG", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            RenderHelper.tearDownRenderState();
        }
    }
}
