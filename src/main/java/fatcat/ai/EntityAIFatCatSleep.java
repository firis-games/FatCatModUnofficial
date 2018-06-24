package fatcat.ai;

import fatcat.EntityFatCat;
import fatcat.EntityFatCat.StatusChangeReason;
import net.minecraft.entity.ai.EntityAIBase;

/* Try to sleep */
public class EntityAIFatCatSleep extends EntityAIBase {
	private EntityFatCat cat;
	//private World world;
	public boolean tryWakeup = false;
	
	public EntityAIFatCatSleep(EntityFatCat cat) {
		this.cat = cat;
		//this.world = cat.worldObj;
		this.setMutexBits(16);
	}

	@Override
	public boolean shouldExecute() {
//		System.out.println("EntityAIFatCatSleep: shouldExec="+(this.cat.getTiredness()));
		return (this.cat.getTiredness() >= EntityFatCat.TIREDNESS_MAX);
	}


    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
	@Override
    public boolean continueExecuting()
    {
//		System.out.println("EntityAIFatCatSleep: continueExecuting="+(this.cat.getTiredness() > 0));
        return (this.cat.getTiredness() > 0 && !tryWakeup);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
	@Override
    public void startExecuting()
    {
        this.cat.setAISit(true);
        this.cat.setFace(EntityFatCat.Face.Sleep);
    }

    /**
     * Resets the task
     */
	@Override
    public void resetTask()
    {
        this.cat.setAISit(false);
        this.cat.setFace(EntityFatCat.Face.None);
        this.tryWakeup = false;
    }

    /**
     * Updates the task
     */
	@Override
    public void updateTask()
    {
    	this.cat.setTiredness(cat.getTiredness()-1, StatusChangeReason.Sleep);
    }
}
