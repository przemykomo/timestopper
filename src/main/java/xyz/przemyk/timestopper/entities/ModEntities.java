package xyz.przemyk.timestopper.entities;

import net.minecraft.entity.EntityType;
import net.minecraftforge.registries.ObjectHolder;
import xyz.przemyk.timestopper.TimeStopperMod;

public class ModEntities {
    @ObjectHolder(TimeStopperMod.MODID + ":thrown_timestopper")
    public static final EntityType<ThrownTimeStopperEntity> THROWN_TIME_STOPPER = null;
}
