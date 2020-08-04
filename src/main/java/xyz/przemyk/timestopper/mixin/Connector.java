package xyz.przemyk.timestopper.mixin;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;
import xyz.przemyk.timestopper.TimeStopperMod;

public class Connector implements IMixinConnector {

    @Override
    public void connect() {
        Mixins.addConfiguration("assets/" + TimeStopperMod.MODID + '/' + TimeStopperMod.MODID + ".mixins.json");
    }
}
