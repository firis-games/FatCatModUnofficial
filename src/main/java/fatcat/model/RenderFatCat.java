package fatcat.model;

import java.math.BigDecimal;

import org.lwjgl.opengl.GL11;

import fatcat.EntityFatCat;
import fatcat.FatCatMod;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderFatCat extends RenderLiving<EntityFatCat> {
	
	public static int SKIN_COUNT = 3;

	public RenderFatCat(RenderManager manager) {
		super(manager, new ModelFatCat(), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFatCat cat) {
		int type = ((EntityFatCat)cat).getSkinType();
		String location = FatCatMod.instance.skinMap.get(type);
		if (null != location) {
			return new ResourceLocation(location);
		} else {
			return new ResourceLocation(FatCatMod.instance.skinMap.get(0));
		}
	}
	
	@Override
	protected void renderLeash(EntityFatCat entityLivingIn, double x, double y, double z, float entityYaw, float partialTicks)
    {
		//リードの高さを調整
		super.renderLeash(entityLivingIn, x, y - 0.4D, z, entityYaw, partialTicks);
    }
	
	@Override
	public void doRender(EntityFatCat cat, double x, double y, double z, float entityYaw, float partialTicks) {
		
		// adjust shadow size
		this.shadowSize = (new BigDecimal(((EntityFatCat)cat).getWeight() / 9000.0F)).setScale(1, BigDecimal.ROUND_DOWN).floatValue();
		this.shadowSize = Math.max(0.1F, shadowSize);
		
		//Render
		super.doRender(cat, x, y, z, entityYaw, partialTicks);
	}
	
	@Override
	/*
	 * Sent rotation of body
	 * @see net.minecraft.client.renderer.entity.RendererLivingEntity#rotateCorpse(net.minecraft.entity.EntityLivingBase, float, float, float)
	 */
    protected void rotateCorpse(EntityFatCat entity, float p_77043_2_, float p_77043_3_, float p_77043_4_)
    {
		EntityFatCat cat = (EntityFatCat)entity;

        if (cat.isEntityAlive() && cat.getPose() == EntityFatCat.Pose.Brushing)
        {
            GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
        }
        else
        {
            super.rotateCorpse(cat, p_77043_2_, p_77043_3_, p_77043_4_);
        }
    }

	@Override
    /**
     * Sets a simple glTranslate on a LivingEntity.
     */
    protected void renderLivingAt(EntityFatCat entityLivingBaseIn, double x, double y, double z)
    {
		EntityFatCat cat = (EntityFatCat)entityLivingBaseIn;
		
        if (cat.isEntityAlive() && cat.getPose() == EntityFatCat.Pose.Brushing)
        {
            super.renderLivingAt(entityLivingBaseIn, x, y + 0.25F, z);
        }
        else
        {
            super.renderLivingAt(entityLivingBaseIn, x, y, z);
        }
    }
}
