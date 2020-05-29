package xyz.przemyk.timestopper.entities;

import net.minecraft.util.math.Vec3d;

import java.util.*;

public class ThrownTimeStopperData {
    public List<UUID> stoppedEntitiesID = new ArrayList<>();
    public Map<UUID, Vec3d> savedMotion = new HashMap<>();
    public Map<UUID, Vec3d> savedPosition = new HashMap<>();
    public Map<UUID, Float> savedRotation = new HashMap<>();
    Integer timeLeft = 0;
}
