package ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {
    private static final EventManager instance = new EventManager();
    private final Map<String, List<Runnable>> eventListeners = new HashMap<>();

    // Singleton Pattern
    private EventManager() {}

    public static EventManager getInstance() {
        return instance;
    }

    // 이벤트 리스너 추가
    public void subscribe(String eventType, Runnable listener) {
        eventListeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    // 특정 이벤트 발생
    public void notifyListeners(String eventType) {
        List<Runnable> listeners = eventListeners.get(eventType);
        if (listeners != null) {
            for (Runnable listener : listeners) {
                listener.run();
            }
        }
    }

    // 등록된 리스너 출력 (디버깅용)
    public void printListeners(String eventType) {
        List<Runnable> listeners = eventListeners.get(eventType);
        if (listeners != null) {
            System.out.println("Listeners for event: " + eventType);
            for (Runnable listener : listeners) {
                System.out.println(listener);
            }
        } else {
            System.out.println("No listeners for event: " + eventType);
        }
    }


}

