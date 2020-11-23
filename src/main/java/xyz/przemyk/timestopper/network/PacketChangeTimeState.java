package xyz.przemyk.timestopper.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xyz.przemyk.timestopper.capabilities.CapabilityTimeControl;
import xyz.przemyk.timestopper.capabilities.TimeState;

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
        ctx.get().enqueueWork(() ->
                Minecraft.getInstance().world.getPlayerByUuid(playerUUID).getCapability(CapabilityTimeControl.TIME_CONTROL_CAPABILITY).ifPresent(h -> h.setState(timeState)));
        ctx.get().setPacketHandled(true);
    }
}
