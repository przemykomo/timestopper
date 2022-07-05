package xyz.przemyk.timestopper.capabilities.tick;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConditionalTickHandlerProvider implements ICapabilitySerializable<CompoundTag> {

    public static final Capability<ConditionalTickHandler> CONDITIONAL_TICK_CAP = CapabilityManager.get(new CapabilityToken<>() {});

    private ConditionalTickHandler conditionalTickHandler = null;
    private final LazyOptional<ConditionalTickHandler> handlerLazyOptional = LazyOptional.of(() -> conditionalTickHandler);

    private ConditionalTickHandler createConditionalTickHandler() {
        if (conditionalTickHandler == null) {
            conditionalTickHandler = new ConditionalTickHandler();
        }
        return conditionalTickHandler;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CONDITIONAL_TICK_CAP) {
            return handlerLazyOptional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        createConditionalTickHandler().save(compoundTag);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        createConditionalTickHandler().load(compoundTag);
    }

    public void invalidate() {
        handlerLazyOptional.invalidate();
    }
}
