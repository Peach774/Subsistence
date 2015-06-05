package subsistence.client.render.tile;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import subsistence.client.render.FoliageHandler;
import subsistence.client.render.SubsistenceTileRenderer;
import subsistence.common.block.machine.BarrelType;
import subsistence.common.tile.machine.TileBarrel;
import subsistence.common.util.RenderHelper;

import java.util.List;

public class RenderTileBarrel extends SubsistenceTileRenderer<TileBarrel> {

    private List<ItemStack> foliage = Lists.newArrayList();

    public RenderTileBarrel() {

    }

    @Override
    public void renderTileAt(TileBarrel tile, double x, double y, double z, float delta) {
        if (tile == null)
            return;

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
        GL11.glRotated(RenderHelper.getRotationAngle(tile.orientation.getOpposite()), 0, 1, 0);

        BarrelType type = tile.getType();
        type.texture.bindTexture();
        type.model.renderAllExcept("lid", "lidHandle");

        GL11.glPushMatrix();
        renderLid(tile);
        GL11.glPopMatrix();

        if (tile.itemContents != null) {
            for (int i = 0; i < tile.itemContents.length; i++) {
                if (tile.itemContents[i] != null) {
                    final ItemStack itemStack = tile.itemContents[i];

                    if (FoliageHandler.shouldRender(itemStack)) {
                        RenderHelper.renderColoredIcon(Blocks.dirt.getIcon(1, 0), TextureMap.locationBlocksTexture, Blocks.leaves.getBlockColor(), 0.35F + ((float) i * 0.35F));
                    } else if (itemStack.getItem() instanceof ItemBlock) {
                        Block block = Block.getBlockFromItem(tile.itemContents[i].getItem());
                        RenderHelper.renderColoredIcon(block.getIcon(1, 0), TextureMap.locationBlocksTexture, block.getBlockColor(), 0.35F + ((float) i * 0.35F));
                    } else {
                        RenderHelper.renderColoredIcon(itemStack.getItem().getIcon(itemStack, 0), TextureMap.locationBlocksTexture, 0xFFFFFF, 0.35F + ((float) i * 0.35F));
                    }
                }
            }
        }

        if (tile.fluidContents != null) {
            renderLiquid(tile);
        }

        GL11.glPopMatrix();
    }

    private void renderLiquid(TileBarrel tile) {
        GL11.glPushMatrix();

        final float volume = tile.getType().fluidCapacity;
        float s = 1.0F / 256.0F * 14.0F;
        float level = (float) tile.fluidContents.amount / (float) volume;

        GL11.glTranslatef(-0.40F, (TileBarrel.DIMENSION_FILL * level) - TileBarrel.DIMENSION_FILL / 2, -0.40F);
        GL11.glScalef(s / 1.0F, s / 1.0F, s / 1.0F);

        RenderHelper.renderLiquid(tile.fluidContents);

        GL11.glPopMatrix();
    }

    private void renderLid(TileBarrel tile) {
        if (tile.hasLid) {
            tile.getType().model.renderOnly("lid", "lidHandle");
        }
    }
}