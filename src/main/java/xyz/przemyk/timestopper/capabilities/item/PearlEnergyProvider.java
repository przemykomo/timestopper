package xyz.przemyk.timestopper.capabilities.item;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PearlEnergyProvider implements ICapabilityProvider {

    public static final Capability<PearlEnergyStorage> PEARL_ENERGY_STORAGE_CAP = CapabilityManager.get(new CapabilityToken<>() {});

    protected final PearlEnergyStorage energyStorage;
    protected final LazyOptional<PearlEnergyStorage> energyStorageLazyOptional;

    public PearlEnergyProvider(PearlEnergyStorage energyStorage) {
        this.energyStorage = energyStorage;
        this.energyStorageLazyOptional = LazyOptional.of(() -> this.energyStorage);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return PEARL_ENERGY_STORAGE_CAP.orEmpty(cap, energyStorageLazyOptional);
    }
}
