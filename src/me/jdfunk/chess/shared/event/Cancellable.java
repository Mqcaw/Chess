package me.jdfunk.chess.shared.event;

public interface Cancellable {
    boolean isCancelled();
    void setCancelled(boolean cancel);
}