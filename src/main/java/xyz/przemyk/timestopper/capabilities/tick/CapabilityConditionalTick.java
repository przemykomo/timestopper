package xyz.przemyk.timestopper.capabilities.tick;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityConditionalTick {

    @CapabilityInject(IConditionalTickHandler.class)
    public static Capability<IConditionalTickHandler> CONDITIONAL_TICK_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IConditionalTickHandler.class, new Storage(), ConditionalTickHandler::new);
    }

    public static class Storage implements Capability.IStorage<IConditionalTickHandler> {

        @Override
        public INBT writeNBT(Capability<IConditionalTickHandler> capability, IConditionalTickHandler instance, Direction side) {
            CompoundNBT tag = new CompoundNBT();
            tag.putBoolean("canTick", instance.canTick());
            return tag;
        }

        @Override
        public void readNBT(Capability<IConditionalTickHandler> capability, IConditionalTickHandler instance, Direction side, INBT nbt) {
            instance.setCanTick(((CompoundNBT) nbt).getBoolean("canTick"));
        }
    }
}
