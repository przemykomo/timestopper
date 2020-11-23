package xyz.przemyk.timestopper.mixin;

import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.util.INameable;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.przemyk.timestopper.TimeStopperMod;

import javax.annotation.Nullable;

@Mixin(Entity.class)
public abstract class MixinEntity extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> implements INameable, ICommandSource, net.minecraftforge.common.extensions.IForgeEntity {

    protected MixinEntity(Class<Entity> baseClass) {
        super(baseClass);
    }

    @SuppressWarnings("FieldMayBeFinal")
    @Shadow private boolean canUpdate = true;

    @Override
    public boolean canUpdate() {
        return TimeStopperMod.canUpdateEntity(getEntity()) && canUpdate;
    }

    @Shadow public float rotationYaw;
    @Shadow public float rotationPitch;
    @Shadow public float prevRotationYaw;
    @Shadow public float prevRotationPitch;
    @Shadow @Nullable private Entity ridingEntity;

    @SuppressWarnings("unused")
    @OnlyIn(Dist.CLIENT)
    public void rotateTowards(double yaw, double pitch) {
        if (TimeStopperMod.canUpdateEntity(getEntity())) {
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
}
