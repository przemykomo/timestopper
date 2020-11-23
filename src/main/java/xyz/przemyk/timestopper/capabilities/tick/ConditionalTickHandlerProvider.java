package xyz.przemyk.timestopper.capabilities.tick;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConditionalTickHandlerProvider implements ICapabilitySerializable<CompoundNBT> {

    private final ConditionalTickHandler conditionalTickHandler = new ConditionalTickHandler();
    private final LazyOptional<IConditionalTickHandler> handlerLazyOptional = LazyOptional.of(() -> conditionalTickHandler);

    public void invalidate() {
        handlerLazyOptional.invalidate();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityConditionalTick.CONDITIONAL_TICK_CAPABILITY) {
            return handlerLazyOptional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        if (CapabilityConditionalTick.CONDITIONAL_TICK_CAPABILITY == null) {
            return new CompoundNBT();
        }

        return (CompoundNBT) CapabilityConditionalTick.CONDITIONAL_TICK_CAPABILITY.writeNBT(conditionalTickHandler, null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (CapabilityConditionalTick.CONDITIONAL_TICK_CAPABILITY != null) {
            CapabilityConditionalTick.CONDITIONAL_TICK_CAPABILITY.readNBT(conditionalTickHandler, null, nbt);
        }
    }
}
