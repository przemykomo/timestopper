package xyz.przemyk.timestopper.mixin;

import net.minecraft.commands.CommandSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.przemyk.timestopper.TimeStopperMod;

import javax.annotation.Nullable;

@SuppressWarnings("UnstableApiUsage")
@Mixin(Entity.class)
public abstract class MixinEntity extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> implements Nameable, EntityAccess, CommandSource, net.minecraftforge.common.extensions.IForgeEntity {

    protected MixinEntity(Class<Entity> baseClass) {
        super(baseClass);
    }

    @Shadow private float yRot;
    @Shadow private float xRot;
    @Shadow public float yRotO;
    @Shadow public float xRotO;
    @Shadow @Nullable private Entity vehicle;

    @SuppressWarnings("unused")
    public void turn(double yaw, double pitch) {
        if (TimeStopperMod.canUpdateEntity((Entity) (Object) this)) {
            double d0 = pitch * 0.15D;
            double d1 = yaw * 0.15D;
            this.xRot = (float)((double)this.xRot + d0);
            this.yRot = (float)((double)this.yRot + d1);
            this.xRot = Mth.clamp(this.xRot, -90.0F, 90.0F);
            this.xRotO = (float)((double)this.xRotO + d0);
            this.yRotO = (float)((double)this.yRotO + d1);
            this.xRotO = Mth.clamp(this.xRotO, -90.0F, 90.0F);
            if (this.vehicle != null) {
                this.vehicle.onPassengerTurned((Entity) (Object) this);
            }
        }
    }
}
