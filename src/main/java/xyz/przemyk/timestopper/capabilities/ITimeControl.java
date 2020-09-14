package xyz.przemyk.timestopper.capabilities;

public interface ITimeControl {
    void setState(TimeState state);
    TimeState getState();
}
