package jeiexporter.jei;

import jeiexporter.config.ConfigHandler;
import jeiexporter.json.JEIJsonWriter;
import jeiexporter.json.NameMap;
import jeiexporter.json.TooltipJsonMap;
import jeiexporter.render.IconList;
import jeiexporter.render.Loading;
import jeiexporter.util.LogHelper;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JEIExporter {
    //TODO add single exports

    public static void exportAll() {
        export(LayoutFetcher.getInstance().fetchAll());
    }

    private static void export(Map<IRecipeCategory, List<IRecipeLayout>> map) {
        int size = map.size();
        int index = 0;
        for (Map.Entry<IRecipeCategory, List<IRecipeLayout>> entry : map.entrySet()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) break;
            index++;
            List<IRecipeLayout> layouts = entry.getValue();
            int layoutsSize = layouts.size();
            try {
                JEIJsonWriter writer = new JEIJsonWriter(entry.getKey().getUid().replaceAll("[\\.\\s:]", "__"));
                writer.writeTitle(entry.getKey());
                for (int i = 0; i < layoutsSize; i++) {
                    if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) break;
                    int finalIndex = index;
                    int finalI = i;
                    Loading.render(() -> new Loading.Context(
                                    I18n.format("jeiexporter.title"),
                                    I18n.format("Exporting %s (%s/%s)", entry.getKey().getTitle(), finalIndex, size),
                                    (finalIndex * 1F) / size,
                                    I18n.format("%s/%s", finalI, layoutsSize),
                                    (finalI * 1F) / layoutsSize
                            )
                    );
                    writer.writeLayout(layouts.get(i));
                }
                writer.close();
                LogHelper.info("Saved category: " + entry.getKey().getTitle());
            } catch (IOException e) {
                e.printStackTrace();
                LogHelper.warn("Failed writing category: " + entry.getKey().getTitle());
            }
        }
        NameMap.exportNames();
        if (!ConfigHandler.disableIconExporting) {
            IconList.renderIngredients();
        }
        NameMap.clear();
        IconList.clear();
    }
}
