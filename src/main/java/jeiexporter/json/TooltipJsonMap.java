package jeiexporter.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jeiexporter.JEIExporter;
import jeiexporter.config.ConfigHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TooltipJsonMap {
    private static Map<String, String> regToDisp = new HashMap<>();
    private static Map<String, String> dispToReg = new HashMap<>();
    private static Map<String, String> modRegToDisp = new HashMap<>();
	private static long counter = 0;
	
    public static void clear() {
        regToDisp.clear();
        dispToReg.clear();
        modRegToDisp.clear();
    }

    public static String add(ItemStack itemStack) {
        if (itemStack == null) return "";
        String regName = createRegName(itemStack);
		String dispName = "Description Error"+counter++;
		try{
			dispName = itemStack.getDisplayName();
		}catch(Exception e){}
        regToDisp.put(regName, dispName);
        dispToReg.put(dispName, regName);
        return regName;
    }

    public static String createRegName(ItemStack itemStack) {
        if (itemStack == null) return "";
        String regName = itemStack.getItem().getRegistryName() + ":" + itemStack.getMetadata();
        if (itemStack.hasTagCompound())
            regName += ":" + Integer.toHexString(itemStack.getTagCompound().toString().hashCode());
        return regName;
    }

    public static String getDispName(String regName) {
        return regToDisp.get(regName);
    }

    public static String add(FluidStack fluidStack) {
        if (fluidStack == null) return "";
        String regName = "fluid:" + fluidStack.getFluid().getName();
        String dispName = fluidStack.getLocalizedName();
        regToDisp.put(regName, dispName);
        dispToReg.put(dispName, regName);
        return regName;
    }

    public static void asJson(File location, Map<String, String> map) throws IOException {
        FileWriter writer = new FileWriter(location);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        writer.write(gson.toJson(map));
        writer.flush();
        writer.close();
    }

    public static void saveAsJson(String subFolder) throws IOException {
        asJson(new File(JEIExporter.configDir + subFolder + "/tooltipMap.json"), regToDisp);
        asJson(new File(JEIExporter.configDir + subFolder + "/lookupMap.json"), dispToReg);
        for (Map.Entry<String, ModContainer> mod : Loader.instance().getIndexedModList().entrySet()) {
            modRegToDisp.put(mod.getKey(), mod.getValue().getName());
        }
        asJson(new File(JEIExporter.configDir + subFolder + "/modList.json"), modRegToDisp);
        clear();
    }
}
