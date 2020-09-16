package xyz.przemyk.timestopper.capabilities;

public interface ITimeStateHandler {
    void setState(TimeState state);
    TimeState getState();
}
