package fr.keyser.n.fsm.automat;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import fr.keyser.n.fsm.EventProcessingStatus;

/**
 * Permet d'exécuter une tache sur le même thread, et d'avoir accés à un status
 * 
 * @author pakeyser
 *
 */
class AutomatExecutor implements Executor {

    private final BlockingQueue<Runnable> events;

    private final AtomicInteger processing = new AtomicInteger(0);

    private final ThreadLocal<EventProcessingStatus> status = new ThreadLocal<>();

    public AutomatExecutor() {
	this(0);
    }

    public AutomatExecutor(int capacity) {
	this.events = (capacity <= 0) ? new LinkedBlockingQueue<>() : new ArrayBlockingQueue<>(capacity);
    }

    @Override
    public void execute(Runnable command) {
	this.events.add(command);
	int current = processing.getAndIncrement();
	if (current == 0)
	    processingLoop();
    }

    private void processingLoop() {
	try {
	    status.set(new EventProcessingStatus());
	    do {
		Runnable run = this.events.poll();
		run.run();
	    } while (processing.decrementAndGet() > 0);
	} finally {
	    status.remove();
	}
    }

    public EventProcessingStatus getStatus() {
	return status.get();
    }

}
