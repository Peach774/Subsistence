package subsistence.common.block.prefab.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import subsistence.common.block.prefab.SubsistenceMultiBlock;

public class SubsistenceItemMultiBlock extends ItemBlock {

    public SubsistenceItemMultiBlock(Block block) {
        super(block);

        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "." + ((SubsistenceMultiBlock) this.field_150939_a).getNameForType(stack.getItemDamage());
    }
}
