package fr.keyser.pt2.prop;

public interface DirtyObserver {

    public void addListener(DirtyListener e);

    public void removeListener(DirtyListener e);
}
