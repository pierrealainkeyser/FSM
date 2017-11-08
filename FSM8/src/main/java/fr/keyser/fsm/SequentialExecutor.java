package fr.keyser.fsm;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Permet d'exécuter une tache sur le même thread. Il est possible de passer en
 * mode "buffering" avec la méthode {@link #bufferize()}, ce qui à pour effet de
 * placer les événements en attente, l'attente est libéré lors de l'appel à
 * {@link #unbuffer()}.
 * 
 * @author pakeyser
 *
 */
public class SequentialExecutor implements Executor {

    private final BlockingQueue<Runnable> events;

    private AtomicInteger processing = new AtomicInteger(0);

    private AtomicBoolean buffering = new AtomicBoolean(false);

    public SequentialExecutor() {
	this(0);
    }

    public SequentialExecutor(int capacity) {
	this.events = (capacity <= 0) ? new LinkedBlockingQueue<>() : new ArrayBlockingQueue<>(capacity);
    }

    @Override
    public void execute(Runnable command) {
	this.events.add(command);
	boolean first = processing.getAndIncrement() == 0;
	boolean processingMode = !buffering.get();
	if (first && processingMode)
	    processingLoop();
    }

    /**
     * Passe en mode "buffering"
     */
    public void bufferize() {
	execute(() -> buffering.set(true));
    }

    /**
     * Quitte le mode "buffering"
     */
    public void unbuffer() {
	if (buffering.compareAndSet(true, false) && processing.get() > 0)
	    processingLoop();
    }

    private void processingLoop() {

	boolean moreToProcess;
	boolean processingMode;
	do {
	    Runnable run = this.events.poll();
	    run.run();

	    moreToProcess = processing.decrementAndGet() > 0;
	    processingMode = !buffering.get();

	} while (moreToProcess && processingMode);
    }

}
