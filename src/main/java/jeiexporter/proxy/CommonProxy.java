package jeiexporter.proxy;

import com.rwtema.extrautils2.crafting.jei.JEIMachine;
import jeiexporter.handler.RecipeConverterFactories;
import jeiexporter.handler.jer.MobDropExtraData;
import jeiexporter.handler.mods.XUConverter;
import jeresources.jei.mob.MobWrapper;
import net.minecraftforge.fml.common.Loader;

public class CommonProxy {
    public void registerKeyBindings() {

    }

    public void registerHandlers() {
        if (Loader.isModLoaded("jeresources")) {
            RecipeConverterFactories.register(MobWrapper.class, MobDropExtraData::new);
        }
        if (Loader.isModLoaded("extrautils2")) {
            RecipeConverterFactories.register(JEIMachine.JEIMachineRecipe.Wrapper.class, XUConverter::new);
        }
    }
}
