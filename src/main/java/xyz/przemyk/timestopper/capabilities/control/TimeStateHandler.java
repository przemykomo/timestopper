package xyz.przemyk.timestopper.capabilities.control;

public class TimeStateHandler implements ITimeStateHandler {

    private TimeState timeState = TimeState.NORMAL;

    @Override
    public void setState(TimeState state) {
        timeState = state;
    }

    @Override
    public TimeState getState() {
        return timeState;
    }
}
