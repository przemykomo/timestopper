package xyz.przemyk.timestopper.entities;

import net.minecraft.entity.EntityType;
import net.minecraftforge.registries.ObjectHolder;
import xyz.przemyk.timestopper.TimeStopperMod;
import xyz.przemyk.timestopper.entities.active.ActiveTimeStopperEntity;
import xyz.przemyk.timestopper.entities.thrown.ThrownTimeStopperEntity;

public class ModEntities {
    @ObjectHolder(TimeStopperMod.MODID + ":active_timestopper")
    public static final EntityType<ActiveTimeStopperEntity> ACTIVE_TIME_STOPPER = null;

    @ObjectHolder(TimeStopperMod.MODID + ":thrown_timestopper")
    public static final EntityType<ThrownTimeStopperEntity> THROWN_TIME_STOPPER = null;
}
