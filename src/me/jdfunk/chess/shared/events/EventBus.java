package me.jdfunk.chess.shared.events;

import me.jdfunk.chess.shared.annotations.EventHandler;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventBus {
    private final Map<Class<?>, List<MethodHandler>> handlers = new ConcurrentHashMap<>();
    private final Map<Class<?>, List<Consumer<Event>>> subscribers = new ConcurrentHashMap<>();

    public void register(Object listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventHandler.class)
                    && method.getParameterCount() == 1
                    && Event.class.isAssignableFrom(method.getParameterTypes()[0])) {

                Class<?> eventType = method.getParameterTypes()[0];
                method.setAccessible(true);
                handlers.computeIfAbsent(eventType, c -> new CopyOnWriteArrayList<>())
                        .add(new MethodHandler(listener, method));
            }
        }
    }

    public <T extends Event> Subscription subscribeToEvent(Class<T> eventType, Consumer<T> handler) {
        return subscribe(eventType, handler);
    }

    private <T extends Event> Subscription subscribe(Class<T> eventType, Consumer<T> handler) {
        @SuppressWarnings("unchecked")
        Consumer<Event> eventConsumer = (Consumer<Event>) handler;

        subscribers.computeIfAbsent(eventType, c -> new CopyOnWriteArrayList<>())
                .add(eventConsumer);

        return () -> unsubscribe(eventType, eventConsumer);
    }

    public void publish(Event event) {
        for (Class<?> type = event.getClass();
             type != null && Event.class.isAssignableFrom(type);
             type = type.getSuperclass()) {

            // First, invoke annotation-based handlers
            List<MethodHandler> methodHandlers = handlers.get(type);
            if (methodHandlers != null) {
                for (MethodHandler mh : methodHandlers) {
                    if (event instanceof Cancellable c && c.isCancelled()) {
                        return;
                    }
                    mh.invoke(event);
                }
            }

            // Then, invoke subscribers
            List<Consumer<Event>> eventSubscribers = subscribers.get(type);
            if (eventSubscribers != null) {
                for (Consumer<Event> subscriber : eventSubscribers) {
                    if (event instanceof Cancellable c && c.isCancelled()) {
                        return;
                    }
                    try {
                        subscriber.accept(event);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    private void unsubscribe(Class<?> eventType, Consumer<Event> handler) {
        List<Consumer<Event>> list = subscribers.get(eventType);
        if (list != null) {
            list.remove(handler);
            if (list.isEmpty()) {
                subscribers.remove(eventType);
            }
        }
    }

    private record MethodHandler(Object instance, Method method) {
        void invoke(Event e) {
            try { method.invoke(instance, e); }
            catch (Exception ex) { ex.printStackTrace(); }
        }
    }
}
