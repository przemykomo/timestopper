package xyz.przemyk.timestopper.capabilities.tick;

public interface IConditionalTickHandler {
    boolean canTick();
    void setCanTick(boolean canTick);
    void switchState();
}
