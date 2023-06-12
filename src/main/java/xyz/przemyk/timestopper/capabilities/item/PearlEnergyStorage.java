package xyz.przemyk.timestopper.capabilities.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public record PearlEnergyStorage(ItemStack itemStack) {

    private CompoundTag getTag() {
        if (!itemStack.hasTag()) {
            CompoundTag tag = new CompoundTag();
            itemStack.setTag(tag);
        }
        return itemStack.getTag();
    }

    public int getEnergy() {
        return getTag().getInt("Energy");
    }

    public void setEnergy(int energy) {
        getTag().putInt("Energy", energy);
    }
}
