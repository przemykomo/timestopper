package xyz.przemyk.timestopper.capabilities.control;

import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import xyz.przemyk.timestopper.TimeStopperMod;

public enum TimeState {
    NORMAL,
    STOPPED,
    FAST,
    SLOW;

    public TextComponent toTextComponent() {
        switch (this) {
            case NORMAL:
                return new TranslationTextComponent("info." + TimeStopperMod.MODID + ".normal_speed");
            case FAST:
                return new TranslationTextComponent("info." + TimeStopperMod.MODID + ".fast_speed");
            case SLOW:
                return new TranslationTextComponent("info." + TimeStopperMod.MODID + ".slow_speed");
            case STOPPED:
                return new TranslationTextComponent("info." + TimeStopperMod.MODID + ".stopped_time");
            default:
                return new StringTextComponent("invalid time state");
        }
    }
}
