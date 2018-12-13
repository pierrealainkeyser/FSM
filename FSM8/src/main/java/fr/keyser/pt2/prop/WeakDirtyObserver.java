package fr.keyser.pt2.prop;

import java.util.WeakHashMap;

public class WeakDirtyObserver implements DirtyListener, DirtyObserver {

    private WeakHashMap<DirtyListener, String> listeners = new WeakHashMap<>();

    @Override
    public void addListener(DirtyListener e) {
	listeners.put(e, "");
    }

    @Override
    public void removeListener(DirtyListener e) {
	listeners.remove(e);
    }

    @Override
    public void setDirty() {
	listeners.keySet().forEach(DirtyListener::setDirty);
    }
}
