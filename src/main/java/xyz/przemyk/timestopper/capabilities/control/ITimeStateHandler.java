package xyz.przemyk.timestopper.capabilities.control;

public interface ITimeStateHandler {
    void setState(TimeState state);
    TimeState getState();
}
