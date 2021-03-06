/**
 * 
 */
package fatcat.ai;

import fatcat.EntityFatCat;
import fatcat.FatCatMod;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityAIEatBlock extends EntityAIBase {
	
	private EntityFatCat cat;
	private World world;
	private float frequency;
	private Vec3d closestPos;
	private int giveuptime;

	public EntityAIEatBlock(EntityFatCat cat) {
		this.cat = cat;
		this.world = cat.getEntityWorld();
		this.frequency = 0.25f;
        this.setMutexBits(11);
//		this.frequency = 0.01F;
	}
	
	@Override
	public boolean shouldExecute() {
		if (this.cat.getRNG().nextFloat() > frequency)  {
			return false;
		}
		else if (this.cat.isInSleep() || this.cat.getLeashed() || !this.cat.isHungry()) {
			return false;
		}
		else if (closestPos != null) {
			return false;
		}
		else {
			findBlock();

			return (this.closestPos != null);
		}
	}

	private void findBlock() {

		double closestPosDistance = 100.0D;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					Vec3d pos = new Vec3d(MathHelper.floor(cat.posX+x-8), 
							MathHelper.floor(cat.posY+y-1), 
							MathHelper.floor(cat.posZ+z-8));
					double d = cat.getDistance(pos.x, pos.y, pos.z);
					if (checkBlock(pos) && (d > 1.0D) && (d < closestPosDistance)) {
						FatCatMod.proxy.log(this.world, "EntityAIEatBlock: found %s", pos.toString());
						this.closestPos = pos;
						closestPosDistance = d;
					}
				}
			}
			if (closestPosDistance < 100.0D) {
				return;
			}
		}
	}


    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        if (this.giveuptime > 0 && checkBlock(closestPos)) {
        	return true;
        }
        else {
        	return false;
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.giveuptime = 50;
        this.cat.setAISit(false);
        this.cat.setSitting(false);
        
        //食べ物の位置へ
        this.cat.getNavigator().tryMoveToXYZ(this.closestPos.x, this.closestPos.y + 1, this.closestPos.z, 1.0F);
        
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.cat.setAISit(true);
        this.closestPos = null;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        this.cat.getLookHelper().setLookPosition(this.closestPos.x+0.5, this.closestPos.y, this.closestPos.z+0.5, 10.0F, (float)this.cat.getVerticalFaceSpeed());
        if ((this.giveuptime%10) == 0) {
        	this.cat.getNavigator().tryMoveToXYZ(this.closestPos.x+0.5, this.closestPos.y+1, this.closestPos.z+0.5, 0.5f);
        }
        if (cat.getDistanceSqToCenter(new BlockPos(closestPos)) < 1.0D) {
        	this.cat.eatBlockBounus(world.getBlockState(new BlockPos(closestPos.x, closestPos.y, closestPos.z)).getBlock());
        	this.world.destroyBlock(new BlockPos(closestPos.x, closestPos.y, closestPos.z), false);
        	this.giveuptime = 0;
        }
        --this.giveuptime;
    }
    
    private boolean checkBlock(Vec3d pos) throws RuntimeException {
    	BlockPos blockPos = new BlockPos(pos.x, pos.y, pos.z);
    	Block block = world.getBlockState(blockPos).getBlock();
    	if (block == null || pos == null) {
    		return false;
    	}
    	if (!world.isAirBlock(blockPos.add(0, 1, 0))) {
    		return false;
    	}
    	
    	if (block == Blocks.POTATOES || block == Blocks.TALLGRASS || block == Blocks.BROWN_MUSHROOM_BLOCK ||
    		block == Blocks.RED_MUSHROOM_BLOCK || block == Blocks.CARROTS || block == Blocks.WHEAT ||
    		block == Blocks.REEDS || block == Blocks.MELON_BLOCK) {
    		return true;
    	}
    	
		return false;
    }

}
