package subsistence.common.block.machine;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import subsistence.common.block.prefab.SubsistenceTileMultiBlock;
import subsistence.common.tile.machine.TileCompost;
import subsistence.common.util.ArrayHelper;

/**
 * Created by Thlayli
 */
public class BlockCompost extends SubsistenceTileMultiBlock {

    private static final String[] NAMES = new String[]{"wood", "stone"};

    public BlockCompost() {
        super(Material.wood);
    }

    @Override
    public int[] getSubtypes() {
        return ArrayHelper.getArrayIndexes(NAMES);
    }

    @Override
    public String getNameForType(int type) {
        return ArrayHelper.safeGetArrayIndex(NAMES, type);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileCompost();
    }

    @Override
    public boolean useCustomRender() {
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fx, float fy, float fz) {

        TileCompost tileCompost = (TileCompost) world.getTileEntity(x, y, z);

        if (tileCompost != null && !world.isRemote) {
            ItemStack held = player.getHeldItem();

            if (player.isSneaking()) {
                tileCompost.lidOpen = !tileCompost.lidOpen;
                tileCompost.sendPoke();
                tileCompost.markForUpdate();
            }

            if (side == 1 && tileCompost.lidOpen) {

                if (held != null && FluidContainerRegistry.isEmptyContainer(held)) {
                    ItemStack container = FluidContainerRegistry.fillFluidContainer(tileCompost.fluid, FluidContainerRegistry.EMPTY_BUCKET);
                    FluidStack fluid = new FluidStack(FluidContainerRegistry.getFluidForFilledItem(held),FluidContainerRegistry.BUCKET_VOLUME);
                    if (tileCompost.decreaseFluid(fluid)) { //if there's enough fluid to remove a full bucket
                        if (!player.capabilities.isCreativeMode) {
                            player.setCurrentItemOrArmor(0, container); //TODO: change this to work better
                        }
                    }
                } else if (held != null && Block.getBlockFromItem(held.getItem()) != Blocks.air) {
                    ItemStack itemCopy = held.copy();
                    itemCopy.stackSize = 1;
                    if (tileCompost.addItemToStack(itemCopy)) {
                        held.stackSize--;
                        if (held.stackSize <= 0) {
                            player.setCurrentItemOrArmor(0, null);
                        }
                        tileCompost.markForUpdate();
                    }
                } else {
                    if (tileCompost.contents.length > 0) {
                        player.setCurrentItemOrArmor(0, tileCompost.removeItemFromStack());
                    }
                }
            }
        }
        return !player.isSneaking();
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        // Replace with proper textures mimicking model textures?
        switch (meta) {
            case 1: return Blocks.stone.getIcon(0, 0);
            default: return Blocks.planks.getIcon(0, 0);
        }
    }
}
