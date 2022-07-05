package xyz.przemyk.timestopper.capabilities.tick;

import net.minecraft.nbt.CompoundTag;

public class ConditionalTickHandler {

    public boolean canTick = true;

    public void copyFrom(ConditionalTickHandler source) {
        canTick = source.canTick;
    }

    public void save(CompoundTag compoundTag) {
        compoundTag.putBoolean("canTick", canTick);
    }

    public void load(CompoundTag compoundTag) {
        canTick = compoundTag.getBoolean("canTick");
    }
}
