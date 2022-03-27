package jeiexporter.config;

import jeiexporter.reference.Reference;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Config(modid = Reference.ID)
public class ConfigHandler {

    @Config.Comment("Which categories will not be exported?")
    public static String[] categoryBlacklist = new String[0];

    @Config.Comment({
            "The supplier of ingredient amount.",
            "If a custom ingredient has amount, you must set the config, otherwise the amount data is ignored.",
            "The format is <ingredient class name>;<field name> or <ingredient class name>;<method name>()"
    })
    public static String[] ingredientAmountSupplier = new String[]{
            "mekanism.api.gas.GasStack;amount"
    };

    @Config.Comment(
            "The icon of the items of the modID list below will be used for the icon of ore dict"
    )
    public static String[] ownersOfOreDict = new String[]{
            "minecraft",
            "thermalfoundation",
            "mekanism"
    };

    @Config.Comment({
            "If a custom ingredient has an icon to export, set it to set the expected width and height of the icon.",
            "The format is <ingredient class name>;<icon width>;<icon height>"
    })
    public static String[] ingredientIconSizes = new String[]{
            "mekanism.api.gas.GasStack;16;16"
    };

    @Config.Comment({
            "Which languages to export results in?"
    })
    public static String[] exportedLanguage = new String[] {
            "en_us"
    };

    @Mod.EventBusSubscriber
    public static class ConfigSyncHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(Reference.ID)) {
                ConfigManager.sync(Reference.ID, Config.Type.INSTANCE);
            }
        }
    }
}
