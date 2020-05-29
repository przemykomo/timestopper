package xyz.przemyk.timestopper.entities;

import net.minecraft.util.math.Vec3d;

import java.util.*;

public class ThrownTimeStopperData {

    public List<UUID> stoppedEntitiesID = new ArrayList<>();
    public Map<UUID, Vec3d> savedMotion = new HashMap<>();
    public Integer timeLeft = 0;
}
