package xyz.przemyk.timestopper.capabilities.control;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import xyz.przemyk.timestopper.TimeStopperMod;

public enum TimeState {
    NORMAL,
    STOPPED,
    FAST,
    SLOW;

    public MutableComponent toTextComponent() {
        return switch (this) {
            case NORMAL -> Component.translatable("info." + TimeStopperMod.MODID + ".normal_speed");
            case FAST -> Component.translatable("info." + TimeStopperMod.MODID + ".fast_speed");
            case SLOW -> Component.translatable("info." + TimeStopperMod.MODID + ".slow_speed");
            case STOPPED -> Component.translatable("info." + TimeStopperMod.MODID + ".stopped_time");
        };
    }
}
