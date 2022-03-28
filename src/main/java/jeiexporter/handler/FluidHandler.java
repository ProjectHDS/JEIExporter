package jeiexporter.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author youyihj
 */
public class FluidHandler implements IIngredientHandler<FluidStack> {
    @Override
    public String getDisplayName(FluidStack ingredient) {
        return ingredient.getLocalizedName();
    }

    @Override
    public String getInternalId(FluidStack ingredient) {
        return ingredient.getFluid().getName();
    }

    @Override
    public int getAmount(FluidStack ingredient) {
        return ingredient.amount;
    }

    @Override
    public void drawIngredient(Minecraft minecraft, FluidStack ingredient) {
        if (ingredient == null) return;
        TextureMap textureMapBlocks = Minecraft.getMinecraft().getTextureMapBlocks();
        ResourceLocation fluidStill = ingredient.getFluid().getStill(ingredient);
        TextureAtlasSprite fluidStillSprite = null;
        if (fluidStill != null) {
            fluidStillSprite = textureMapBlocks.getTextureExtry(fluidStill.toString());
        }
        if (fluidStillSprite == null) {
            fluidStillSprite = textureMapBlocks.getMissingSprite();
        }

        minecraft.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        int colour = ingredient.getFluid().getColor(ingredient);
        float red = (colour >> 16 & 0xFF) / 255.0F;
        float green = (colour >> 8 & 0xFF) / 255.0F;
        float blue = (colour & 0xFF) / 255.0F;
        GlStateManager.color(red, green, blue, 1.0F);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(0, 16, 0).tex(fluidStillSprite.getMinU(), fluidStillSprite.getMaxV()).endVertex();
        buffer.pos(16, 16, 0).tex(fluidStillSprite.getMaxU(), fluidStillSprite.getMaxV()).endVertex();
        buffer.pos(16, 0, 0).tex(fluidStillSprite.getMaxU(), fluidStillSprite.getMinV()).endVertex();
        buffer.pos(0, 0, 0).tex(fluidStillSprite.getMinU(), fluidStillSprite.getMinV()).endVertex();
        tessellator.draw();
    }

    @Override
    public List<String> getTooltip(Minecraft minecraft, FluidStack ingredient) {
        return Collections.singletonList(ingredient.getLocalizedName());
    }

    @Override
    public String getTag(FluidStack ingredient) {
        return Optional.ofNullable(ingredient.tag).map(NBTTagCompound::toString).orElse("");
    }

    @Override
    public String getType() {
        return "fluid";
    }

    @Override
    public int getImageWidth() {
        return 16;
    }

    @Override
    public int getImageHeight() {
        return 16;
    }
}
