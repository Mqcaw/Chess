package me.jdfunk.chess.shared.events;

public interface Cancellable {
    boolean isCancelled();
    void setCancelled(boolean cancel);
}