package jeiexporter.proxy;

import jeiexporter.handler.RecipeExtraDataWrapperFactories;
import jeiexporter.handler.jer.MobDropExtraData;
import jeresources.jei.mob.MobWrapper;
import net.minecraftforge.fml.common.Loader;

public class CommonProxy {
    public void registerKeyBindings() {

    }

    public void registerHandlers() {
        if (Loader.isModLoaded("jeresources")) {
            RecipeExtraDataWrapperFactories.register(MobWrapper.class, MobDropExtraData::new);
        }
    }
}
