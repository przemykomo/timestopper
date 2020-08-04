package xyz.przemyk.timestopper.mixin;

import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.util.INameable;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.przemyk.timestopper.entities.ThrownTimeStopperEntity;

import static xyz.przemyk.timestopper.entities.ThrownTimeStopperEntity.scan;

@Mixin(Entity.class)
public abstract class MixinEntity extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> implements INameable, ICommandSource, net.minecraftforge.common.extensions.IForgeEntity {

    protected MixinEntity(Class<Entity> baseClass) {
        super(baseClass);
    }

    @SuppressWarnings("FieldMayBeFinal")
    @Shadow
    private boolean canUpdate = true;

    @Shadow
    public World world;

    @Shadow
    private Vector3d field_233557_ao_; //position

    @Override
    public boolean canUpdate() {
        return world.getEntitiesWithinAABB(ThrownTimeStopperEntity.class, scan.offset(field_233557_ao_)).isEmpty() && canUpdate;
    }
}
