package xyz.przemyk.timestopper.capabilities.control;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TimeStateHandlerProvider implements ICapabilitySerializable<CompoundTag> {

    public static final Capability<TimeStateHandler> TIME_STATE_CAP = CapabilityManager.get(new CapabilityToken<>() {});
    private TimeStateHandler timeStateHandler = null;
    private final LazyOptional<TimeStateHandler> timeStateHandlerLazyOptional = LazyOptional.of(this::createTimeStateHandler);

    private TimeStateHandler createTimeStateHandler() {
        if (timeStateHandler == null) {
            timeStateHandler = new TimeStateHandler();
        }
        return timeStateHandler;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == TIME_STATE_CAP) {
            return timeStateHandlerLazyOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        createTimeStateHandler().save(compoundTag);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        createTimeStateHandler().load(compoundTag);
    }

    public void invalidate() {
        timeStateHandlerLazyOptional.invalidate();
    }
}
