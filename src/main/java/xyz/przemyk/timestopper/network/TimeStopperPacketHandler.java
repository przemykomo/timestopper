package xyz.przemyk.timestopper.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import xyz.przemyk.timestopper.TimeStopperMod;

import java.util.Optional;

public class TimeStopperPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel INSTANCE;
    private static int ID = 0;

    public static void init() {
        INSTANCE = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(TimeStopperMod.MODID, "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );

        INSTANCE.registerMessage(ID++,
                PacketChangeTimeState.class,
                PacketChangeTimeState::toBytes,
                PacketChangeTimeState::new,
                PacketChangeTimeState::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        INSTANCE.registerMessage(ID++,
                PacketChangeConditionalTick.class,
                PacketChangeConditionalTick::toBytes,
                PacketChangeConditionalTick::new,
                PacketChangeConditionalTick::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}
