package me.jdfunk.chess.shared.events;

public abstract class Event {
    private boolean isCancelled;

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public boolean isCancelled() {
        return isCancelled;
    }
}
