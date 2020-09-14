package xyz.przemyk.timestopper.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityTimeControl {

    @CapabilityInject(ITimeControl.class)
    public static Capability<ITimeControl> TIME_CONTROL_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(ITimeControl.class, new Storage(), TimeControl::new);
    }

    public static class Storage implements Capability.IStorage<ITimeControl> {

        @Override
        public INBT writeNBT(Capability<ITimeControl> capability, ITimeControl instance, Direction side) {
            CompoundNBT tag = new CompoundNBT();
            tag.putString("timeState", instance.getState().name());
            return tag;
        }

        @Override
        public void readNBT(Capability<ITimeControl> capability, ITimeControl instance, Direction side, INBT nbt) {
            instance.setState(TimeState.valueOf(((CompoundNBT) nbt).getString("timeState")));
        }
    }
}
