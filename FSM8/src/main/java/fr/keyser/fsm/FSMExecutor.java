package fr.keyser.fsm;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Permet d'exécuter une tache sur le même thread
 * 
 * @author pakeyser
 *
 */
public class FSMExecutor implements Executor {

    private final BlockingQueue<Runnable> events;

    private AtomicInteger processing = new AtomicInteger(0);

    public FSMExecutor() {
	this(0);
    }

    public FSMExecutor(int capacity) {
	this.events = (capacity <= 0) ? new LinkedBlockingQueue<>() : new ArrayBlockingQueue<>(capacity);
    }

    @Override
    public void execute(Runnable command) {
	this.events.add(command);
	if (processing.getAndIncrement() == 0) {

	    do {
		Runnable run = this.events.poll();
		run.run();
	    } while (processing.decrementAndGet() > 0);

	}
    }

}
