package xyz.przemyk.timestopper.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.przemyk.timestopper.capabilities.tick.CapabilityConditionalTick;

import java.util.function.Supplier;

public class PacketChangeConditionalTick {

    private final boolean canTick;
    private final int entityID;

    public PacketChangeConditionalTick(boolean canTick, int entityUUID) {
        this.canTick = canTick;
        this.entityID = entityUUID;
    }

    public PacketChangeConditionalTick(PacketBuffer buffer) {
        canTick = buffer.readBoolean();
        entityID = buffer.readVarInt();
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeBoolean(canTick);
        buffer.writeVarInt(entityID);
    }

    @SuppressWarnings("ConstantConditions")
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().world.getEntityByID(entityID);
            if (entity != null) {
                entity.getCapability(CapabilityConditionalTick.CONDITIONAL_TICK_CAPABILITY).ifPresent(h -> h.setCanTick(canTick));
            }
        });

        context.setPacketHandled(true);
    }
}
