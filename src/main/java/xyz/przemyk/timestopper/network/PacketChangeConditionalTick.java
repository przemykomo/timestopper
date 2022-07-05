package xyz.przemyk.timestopper.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import xyz.przemyk.timestopper.capabilities.tick.ConditionalTickHandlerProvider;

import java.util.function.Supplier;

public class PacketChangeConditionalTick {

    private final boolean canTick;
    private final int entityID;

    public PacketChangeConditionalTick(boolean canTick, int entityUUID) {
        this.canTick = canTick;
        this.entityID = entityUUID;
    }

    public PacketChangeConditionalTick(FriendlyByteBuf buffer) {
        canTick = buffer.readBoolean();
        entityID = buffer.readVarInt();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBoolean(canTick);
        buffer.writeVarInt(entityID);
    }

    @SuppressWarnings("ConstantConditions")
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            Entity entity = Minecraft.getInstance().level.getEntity(entityID);
            if (entity != null) {
                entity.getCapability(ConditionalTickHandlerProvider.CONDITIONAL_TICK_CAP).ifPresent(handler -> handler.canTick = this.canTick);
            }
        });

        context.setPacketHandled(true);
    }
}
