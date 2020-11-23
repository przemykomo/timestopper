package xyz.przemyk.timestopper.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.przemyk.timestopper.capabilities.control.CapabilityTimeControl;
import xyz.przemyk.timestopper.capabilities.control.TimeState;

import java.util.UUID;
import java.util.function.Supplier;

public class PacketChangeTimeState {

    private final TimeState timeState;
    private final UUID playerUUID;

    public PacketChangeTimeState(TimeState state, UUID playerUUID) {
        this.timeState = state;
        this.playerUUID = playerUUID;
    }

    public PacketChangeTimeState(PacketBuffer buffer) {
        timeState = TimeState.values()[buffer.readVarInt()];
        playerUUID = buffer.readUniqueId();
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeVarInt(timeState.ordinal());
        buffer.writeUniqueId(playerUUID);
    }

    @SuppressWarnings("ConstantConditions")
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            PlayerEntity playerEntity = Minecraft.getInstance().world.getPlayerByUuid(playerUUID);
            if (playerEntity != null) {
                playerEntity.getCapability(CapabilityTimeControl.TIME_CONTROL_CAPABILITY).ifPresent(h -> h.setState(timeState));
            }
        });
        
        context.setPacketHandled(true);
    }
}
