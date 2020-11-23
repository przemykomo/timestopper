package xyz.przemyk.timestopper.capabilities.control;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityTimeControl {

    @CapabilityInject(ITimeStateHandler.class)
    public static Capability<ITimeStateHandler> TIME_CONTROL_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(ITimeStateHandler.class, new Storage(), TimeStateHandler::new);
    }

    public static class Storage implements Capability.IStorage<ITimeStateHandler> {

        @Override
        public INBT writeNBT(Capability<ITimeStateHandler> capability, ITimeStateHandler instance, Direction side) {
            CompoundNBT tag = new CompoundNBT();
            tag.putString("timeState", instance.getState().name());
            return tag;
        }

        @Override
        public void readNBT(Capability<ITimeStateHandler> capability, ITimeStateHandler instance, Direction side, INBT nbt) {
            instance.setState(TimeState.valueOf(((CompoundNBT) nbt).getString("timeState")));
        }
    }
}
