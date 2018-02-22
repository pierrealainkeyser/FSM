package fr.keyser.fsm;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

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

    private final AtomicReference<InnerState> processing = new AtomicReference<>(new InnerState(0, false));

    private static class InnerState {

	private final int processing;

	private final boolean buffering;

	public InnerState(int processing, boolean buffering) {
	    this.processing = processing;
	    this.buffering = buffering;
	}

	public InnerState increment() {
	    return new InnerState(processing + 1, buffering);
	}

	public InnerState decrement() {
	    return new InnerState(processing - 1, buffering);
	}

	public InnerState buffer() {
	    return new InnerState(processing, true);
	}

	public InnerState unbuffer() {
	    return new InnerState(processing, false);
	}

	public boolean hasToLoop() {
	    return processing > 0 && !buffering;
	}
	
	public boolean hasToStart() {
	    return processing == 1 && !buffering;
	}

    }

    public SequentialExecutor() {
	this(0);
    }

    public SequentialExecutor(int capacity) {
	this.events = (capacity <= 0) ? new LinkedBlockingQueue<>() : new ArrayBlockingQueue<>(capacity);
    }

    @Override
    public void execute(Runnable command) {
	this.events.add(command);
	InnerState is = processing.updateAndGet(InnerState::increment);
	if (is.hasToStart())
	    processingLoop();
    }

    /**
     * Passe en mode "buffering"
     */
    public void bufferize() {
	execute(() -> processing.updateAndGet(InnerState::buffer));
    }

    /**
     * Quitte le mode "buffering"
     */
    public void unbuffer() {
	InnerState is = processing.updateAndGet(InnerState::unbuffer);
	if (is.hasToLoop())
	    processingLoop();
    }

    private void processingLoop() {

	InnerState state = null;
	do {
	    Runnable run = this.events.poll();
	    run.run();

	    state = processing.updateAndGet(InnerState::decrement);

	} while (state.hasToLoop());
    }

}
