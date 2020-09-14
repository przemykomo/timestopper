package xyz.przemyk.timestopper.capabilities;

public class TimeControl implements ITimeControl {

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
