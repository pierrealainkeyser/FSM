package fr.keyser.pt2.prop;

public abstract class ObservableSupplier<T> implements DirtySupplier<T> {
    protected final WeakDirtyObserver observer = new WeakDirtyObserver();
    
    @Override
    public final void addListener(DirtyListener e) {
	observer.addListener(e);
    }
    
    @Override
    public final void removeListener(DirtyListener e) {
	observer.removeListener(e);
    }
    
}
