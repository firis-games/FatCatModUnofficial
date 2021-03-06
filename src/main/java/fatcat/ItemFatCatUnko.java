package fatcat;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemFatCatUnko extends Item {

	public ItemFatCatUnko() {
		super();
        //this.setCreativeTab(CreativeTabs.MISC);
        this.setCreativeTab(FatCatMod.FatCatModTab);
	}

    /**
     * Called when a Block is right-clicked with this Item
     */
	@Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
    	ItemStack stack = player.getHeldItem(hand);
    	
		if (applyUnko(stack, worldIn, pos, player))
        {
            if (!worldIn.isRemote)
            {
                worldIn.playEvent(2005, pos, 0);
            }

            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

	private boolean applyUnko(ItemStack stack, World worldIn, BlockPos pos, EntityPlayer player) {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (block instanceof IGrowable)
        {
            IGrowable igrowable = (IGrowable)block;

            if (igrowable.canGrow(worldIn, pos, iblockstate, worldIn.isRemote))
            {
                if (!worldIn.isRemote)
                {
                    if (igrowable.canUseBonemeal(worldIn, worldIn.rand, pos, iblockstate))
                    {
                        igrowable.grow(worldIn, worldIn.rand, pos, iblockstate);
                    }
                    stack.shrink(1);
                }

                return true;
            }
        }

        return false;
	}
}
