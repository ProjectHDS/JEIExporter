package jeiexporter.handler.jer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import jeiexporter.handler.IRecipeConverter;
import jeiexporter.handler.ingredient.IIngredient;
import jeresources.jei.mob.MobWrapper;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author youyihj
 */
public class MobDropExtraData implements IRecipeConverter {
    private final MobWrapper wrapper;

    public MobDropExtraData(MobWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public Map<String, JsonElement> getRecipeExtraData() {
        for (Map.Entry<ResourceLocation, EntityEntry> entry : ForgeRegistries.ENTITIES.getEntries()) {
            if (entry.getValue().getEntityClass() == wrapper.getMob().getClass()) {
                return Collections.singletonMap("mob", new JsonPrimitive(entry.getKey().toString()));
            }
        }
        return Collections.emptyMap();
    }

    @Override
    public Map<String, JsonElement> getIngredientExtraData(IIngredient<?> ingredient) {
        Object stack = ingredient.members().get(0);
        if (stack instanceof ItemStack) {
            List<String> toolTip = wrapper.getToolTip(((ItemStack) stack));
            if (toolTip != null) {
                JsonArray array = new JsonArray();
                toolTip.forEach(array::add);
                return Collections.singletonMap("tooltip", array);
            }
        }
        return Collections.emptyMap();
    }

    @Override
    public IRecipeWrapper getRecipe() {
        return wrapper;
    }
}
