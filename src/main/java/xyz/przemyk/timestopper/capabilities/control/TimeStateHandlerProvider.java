package xyz.przemyk.timestopper.capabilities.control;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class TimeStateHandlerProvider implements ICapabilitySerializable<CompoundNBT> {

    private final TimeStateHandler timeControl = new TimeStateHandler();
    private final LazyOptional<ITimeStateHandler> timeControlOptional = LazyOptional.of(() -> timeControl);

    public void invalidate() {
        timeControlOptional.invalidate();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == CapabilityTimeControl.TIME_CONTROL_CAPABILITY) {
            return timeControlOptional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        if (CapabilityTimeControl.TIME_CONTROL_CAPABILITY == null) {
            return new CompoundNBT();
        }

        return (CompoundNBT) CapabilityTimeControl.TIME_CONTROL_CAPABILITY.writeNBT(timeControl, null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (CapabilityTimeControl.TIME_CONTROL_CAPABILITY != null) {
            CapabilityTimeControl.TIME_CONTROL_CAPABILITY.readNBT(timeControl, null, nbt);
        }
    }
}
