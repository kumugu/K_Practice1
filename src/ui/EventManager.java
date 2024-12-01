package ui;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private static final EventManager instance = new EventManager();
    private final List<Runnable> listeners = new ArrayList<>();

    // Singleton Pattern
    private EventManager() {}

    public static EventManager getInstance() {
        return instance;
    }

    // 이벤트 리스너 추가
    public void subscribe(Runnable listener) {
        listeners.add(listener);
    }

    // 이벤트 발생
    public void notifyListeners() {
        for (Runnable listener : listeners) {
            listener.run();
        }
    }
}
