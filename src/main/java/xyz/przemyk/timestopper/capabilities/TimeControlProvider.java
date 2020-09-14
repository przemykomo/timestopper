package xyz.przemyk.timestopper.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class TimeControlProvider implements ICapabilitySerializable<CompoundNBT> {

    private final TimeControl timeControl = new TimeControl();
    private final LazyOptional<ITimeControl> timeControlOptional = LazyOptional.of(() -> timeControl);

    public void invalidate() {
        timeControlOptional.invalidate();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return timeControlOptional.cast();
    }

    @Override
    public CompoundNBT serializeNBT() {
        if (CapabilityTimeControl.TIME_CONTROL_CAPABILITY == null) {
            return new CompoundNBT();
        } else {
            return (CompoundNBT) CapabilityTimeControl.TIME_CONTROL_CAPABILITY.writeNBT(timeControl, null);
        }
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (CapabilityTimeControl.TIME_CONTROL_CAPABILITY != null) {
            CapabilityTimeControl.TIME_CONTROL_CAPABILITY.readNBT(timeControl, null, nbt);
        }
    }
}
