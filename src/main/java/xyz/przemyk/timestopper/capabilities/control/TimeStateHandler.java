package xyz.przemyk.timestopper.capabilities.control;

import net.minecraft.nbt.CompoundTag;

public class TimeStateHandler {

    public TimeState timeState = TimeState.NORMAL;

    public void copyFrom(TimeStateHandler source) {
        timeState = source.timeState;
    }

    public void save(CompoundTag compoundTag) {
        compoundTag.putString("timeState", timeState.name());
    }

    public void load(CompoundTag compoundTag) {
        timeState = TimeState.valueOf(compoundTag.getString("timeState"));
    }
}
