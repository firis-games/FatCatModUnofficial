package fatcat.ai;

import fatcat.EntityFatCat;
import fatcat.EntityFatCat.StatusChangeReason;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/* 恋愛度が高ければ子供を作る */
public class EntityAIFatCatMate extends EntityAIBase {
    private EntityFatCat cat;
    private EntityFatCat mate;
    private World worldObj;
    private int matingTimeout;
    private int tick;
    //private static final int CAT_MIN_WEIGHT = 4000;
    
    public EntityAIFatCatMate(EntityFatCat cat) {
    	this.cat = cat;
        this.worldObj = cat.getEntityWorld();
        this.setMutexBits(15);
	}
 
	@Override
	public boolean shouldExecute() {
//		System.out.println("EntityAIFatCatMate(shouldExecute): cs="+checkSufficientMating(cat));
		boolean exec = true;
        if (cat.getRNG().nextInt(500) != 0)
        {
            exec = false;
        }
        else if (cat.isMating) {
        	exec = false;
        }
        else if (!checkSufficientMating(cat))
        {
            exec = false;
        }

        if (exec || cat.tryMating)
        {
        	int range = 12;
        	AxisAlignedBB area = new AxisAlignedBB(new BlockPos(this.cat.posX, this.cat.posY, this.cat.posZ));
        	area = area.expand(-range, 0, -range).expand(range, 3.0D, range);
        	
        	EntityFatCat entity = (EntityFatCat) this.worldObj.findNearestEntityWithinAABB(EntityFatCat.class, area, this.cat);

        	exec = checkSufficientMating(mate);
       		this.mate = entity;
       		
//       		System.out.println("EntityAIFatCatMate(shouldExecute): exec="+exec+",");
        }
        
        //エラーチェック
        if (this.cat == null || this.mate == null || this.cat.getOwnerId() == null || this.mate.getOwnerId() == null) {
        	exec = false;
        }
        
        return exec;
	}
	
	@Override
	public void startExecuting() {
		this.matingTimeout = 300;
		this.tick = 0;
        this.cat.isMating = true;
        this.cat.setAISit(false);
        if (!this.mate.isMating) {
        	this.mate.tryMating = true;
        }
	}
	
	@Override
	public void resetTask()
    {
        this.cat.isMating = false;
        this.mate.isMating = false;
        this.cat.tryMating = false;
        this.cat.setAISit(true);
        this.mate = null;
    }
	
	@Override
    public boolean shouldContinueExecuting()
    {
        return this.matingTimeout >= 0 && cat.isMating && checkSufficientMating(cat) && checkSufficientMating(mate);
    }
	
	@Override
    public void updateTask()
    {
        --this.matingTimeout;
        this.cat.getLookHelper().setLookPositionWithEntity(this.mate, 10.0F, 30.0F);
        //this.cat.getNavigator().tryMoveToEntityLiving(this.mate, 0.2F);
        
        if (tick % 50 == 0) {
        	cat.generateRandomParticles(EnumParticleTypes.HEART);
        }
//    	System.out.println("EntityAIFatCatMate(updateTask): tick="+tick);
        
        //ねこのサイズで近くの判定がうまくいかない
        //からだの大きさを考慮してサイズで判定してみる
        //if (this.cat.getDistanceSqToEntity(this.mate) > 2.25D)
       	if (this.cat.getDistanceSq(this.mate) > this.mate.width*2 + this.cat.width*2)
        {
            this.cat.getNavigator().tryMoveToEntityLiving(this.mate, 0.25D);
        }
        else if (this.matingTimeout <= 0 && this.mate.isMating)
        {
            this.giveBirth();
        }
        this.tick++;
    }

	private boolean checkSufficientMating(EntityFatCat cat) {
//		System.out.println("EntityAIFatCatMate(checkSufficientMating): weight="+cat.getWeight()+",loveness="+cat.getLoveness());
		return (cat != null) && (!cat.isChild()) && (cat.getLoveness() >= EntityFatCat.LOVENESS_MAX);
	}

    private void giveBirth()
    {
//    	System.out.println("EntityAIFatCatMate(shouldExecute): getBirth");
        EntityFatCat child = this.cat.createChild(this.mate);
        child.setLocationAndAngles(this.cat.posX, this.cat.posY, this.cat.posZ, 0.0F, 0.0F);
        worldObj.spawnEntity(child);
        cat.setLoveness(0, StatusChangeReason.Spawn);
        mate.setLoveness(0, StatusChangeReason.Spawn);
    }
}
