package xyz.przemyk.timestopper.capabilities.tick;

public class ConditionalTickHandler implements IConditionalTickHandler {

    private boolean canTick;

    @Override
    public boolean canTick() {
        return canTick;
    }

    @Override
    public void setCanTick(boolean canTick) {
        this.canTick = canTick;
    }

    @Override
    public void switchState() {
        canTick = !canTick;
    }
}
