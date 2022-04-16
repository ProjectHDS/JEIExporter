package jeiexporter.proxy;

import jeiexporter.handler.RecipeConverterFactories;
import jeiexporter.handler.jer.MobDropExtraData;
import jeresources.jei.mob.MobWrapper;
import net.minecraftforge.fml.common.Loader;

public class CommonProxy {
    public void registerKeyBindings() {

    }

    public void registerHandlers() {
        if (Loader.isModLoaded("jeresources")) {
            RecipeConverterFactories.register(MobWrapper.class, MobDropExtraData::new);
        }
    }
}
