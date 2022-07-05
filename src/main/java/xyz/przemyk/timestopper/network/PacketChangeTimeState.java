package xyz.przemyk.timestopper.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import xyz.przemyk.timestopper.capabilities.control.TimeState;
import xyz.przemyk.timestopper.capabilities.control.TimeStateHandlerProvider;

import java.util.UUID;
import java.util.function.Supplier;

public class PacketChangeTimeState {

    private final TimeState timeState;
    private final UUID playerUUID;

    public PacketChangeTimeState(TimeState state, UUID playerUUID) {
        this.timeState = state;
        this.playerUUID = playerUUID;
    }

    public PacketChangeTimeState(FriendlyByteBuf buffer) {
        timeState = TimeState.values()[buffer.readVarInt()];
        playerUUID = buffer.readUUID();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeVarInt(timeState.ordinal());
        buffer.writeUUID(playerUUID);
    }

    @SuppressWarnings("ConstantConditions")
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            Player playerEntity = Minecraft.getInstance().level.getPlayerByUUID(playerUUID);
            if (playerEntity != null) {
                playerEntity.getCapability(TimeStateHandlerProvider.TIME_STATE_CAP).ifPresent(handler -> handler.timeState = this.timeState);
            }
        });
        
        context.setPacketHandled(true);
    }
}
