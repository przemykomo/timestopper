package xyz.przemyk.timestopper.mixin;

import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.util.INameable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.przemyk.timestopper.entities.active.ActiveTimeStopperEntity;

import javax.annotation.Nullable;

import static xyz.przemyk.timestopper.entities.active.ActiveTimeStopperEntity.scan;

@Mixin(Entity.class)
public abstract class MixinEntity extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> implements INameable, ICommandSource, net.minecraftforge.common.extensions.IForgeEntity {

    protected MixinEntity(Class<Entity> baseClass) {
        super(baseClass);
    }

    @SuppressWarnings("FieldMayBeFinal")
    @Shadow private boolean canUpdate = true;

    @Shadow public World world;

    @Shadow
    private Vector3d field_233557_ao_; //position

    @Override
    public boolean canUpdate() {
        return world.getEntitiesWithinAABB(ActiveTimeStopperEntity.class, scan.offset(field_233557_ao_)).isEmpty() && canUpdate;
    }

    @Shadow public float rotationYaw;
    @Shadow public float rotationPitch;
    @Shadow public float prevRotationYaw;
    @Shadow public float prevRotationPitch;
    @Shadow @Nullable private Entity ridingEntity;

    @SuppressWarnings("unused")
    @OnlyIn(Dist.CLIENT)
    public void rotateTowards(double yaw, double pitch) {
        if (world.getEntitiesWithinAABB(ActiveTimeStopperEntity.class, scan.offset(field_233557_ao_)).isEmpty()) {
            double d0 = pitch * 0.15D;
            double d1 = yaw * 0.15D;
            this.rotationPitch = (float)((double)this.rotationPitch + d0);
            this.rotationYaw = (float)((double)this.rotationYaw + d1);
            this.rotationPitch = MathHelper.clamp(this.rotationPitch, -90.0F, 90.0F);
            this.prevRotationPitch = (float)((double)this.prevRotationPitch + d0);
            this.prevRotationYaw = (float)((double)this.prevRotationYaw + d1);
            this.prevRotationPitch = MathHelper.clamp(this.prevRotationPitch, -90.0F, 90.0F);
            if (this.ridingEntity != null) {
                this.ridingEntity.applyOrientationToEntity(getEntity());
            }
        }
    }

    //TODO: overwrite move method so it moves faster/slower when time flows at different rate?
}
