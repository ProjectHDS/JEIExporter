package jeiexporter.json;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jeiexporter.config.ConfigHandler;
import jeiexporter.handler.IIngredientHandler;
import jeiexporter.handler.IngredientHandlers;
import jeiexporter.jei.JEIConfig;
import jeiexporter.render.Loading;
import mezz.jei.api.recipe.IIngredientType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import org.apache.commons.io.FileUtils;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author youyihj
 */
public class NameMap {
    private static final Map<IIngredientType<?>, Map<String, ?>> ingredients = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> void add(T ingredient) {
        Map<String, T> map = (Map<String, T>) ingredients.computeIfAbsent(JEIConfig.getIngredientRegistry().getIngredientType(ingredient), it -> new HashMap<>());
        map.put(IngredientHandlers.getHandlerByIngredient(ingredient).getInternalId(ingredient), ingredient);
    }

    public static void clear() {
        ingredients.clear();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void exportNames() {
        long lastUpdate = 0;
        Map<String, Map<String, String>> names = new HashMap<>();
        int index = 0;
        int nameSteps = ConfigHandler.exportedLanguage.length * ingredients.size();
        Language mcLanguage = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage();
        List<Language> languages = Lists.newArrayList(mcLanguage);
        Arrays.stream(ConfigHandler.exportedLanguage)
                .filter(name -> !name.equals(mcLanguage.getLanguageCode()))
                .map(name -> Minecraft.getMinecraft().getLanguageManager().getLanguage(name))
                .forEach(languages::add);
        for (Language language : languages) {
            boolean isCurrentLanguage = language.equals(mcLanguage);
            if (!isCurrentLanguage) {
                toggleLanguage(language);
            }
            for (Map.Entry<IIngredientType<?>, Map<String, ?>> iIngredientTypeMapEntry : ingredients.entrySet()) {
                if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) break;
                index++;
                Map<String, ?> ingredientMap = iIngredientTypeMapEntry.getValue();
                IIngredientType<?> type = iIngredientTypeMapEntry.getKey();
                IIngredientHandler handler = IngredientHandlers.getHandler(type);
                Set<? extends Map.Entry<String, ?>> entries = ingredientMap.entrySet();
                int size = entries.size();
                int i = 0;
                for (Map.Entry<String, ?> entry : entries) {
                    i++;
                    if (Minecraft.getSystemTime() - lastUpdate > 33) { // 30 FPS
                        Loading.render(
                                "Exporting ingredient names for " + language.getLanguageCode(),
                                String.format("Exporting %s (%s/%s)", type.getIngredientClass().getCanonicalName(), i, size),
                                (i * 1F) / size,
                                String.format("%s/%s", index, nameSteps),
                                (i * 1F) / nameSteps
                        );
                        lastUpdate = Minecraft.getSystemTime();
                    }
                    Map<String, String> entryMap = names.computeIfAbsent(entry.getKey(), it -> new HashMap<>());
                    entryMap.put(language.getLanguageCode(), handler.getDisplayName(entry.getValue()));
                    if (isCurrentLanguage) {
                        String tag = handler.getTag(entry.getValue());
                        if (!tag.isEmpty()) {
                            entryMap.put("tag", tag);
                        }
                    }
                }
            }
        }
        toggleLanguage(mcLanguage);
        try {
            asJson(new File("exports/nameMap.json"), names);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void asJson(File location, Map<String, Map<String, String>> map) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        FileUtils.write(location, gson.toJson(map), StandardCharsets.UTF_8);
    }

    private static void toggleLanguage(Language language) {
        LanguageManager languageManager = Minecraft.getMinecraft().getLanguageManager();
        languageManager.setCurrentLanguage(language);
        languageManager.onResourceManagerReload(Minecraft.getMinecraft().getResourceManager());
    }
}
