package fr.keyser.fsm;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

public class TestSequentialExecutor {

    @Test
    public void simple() {
	SequentialExecutor seq = new SequentialExecutor();
	AtomicInteger count = new AtomicInteger();

	int nb = 10;
	for (int i = 0; i < nb; ++i)
	    seq.execute(() -> count.getAndIncrement());
	Assert.assertEquals(nb, count.get());
    }

    @Test
    public void buffering() {
	SequentialExecutor seq = new SequentialExecutor();

	seq.bufferize();
	AtomicInteger count = new AtomicInteger();
	int nb = 10;
	for (int i = 0; i < nb; ++i)
	    seq.execute(() -> count.getAndIncrement());

	Assert.assertEquals(0, count.get());
	seq.unbuffer();
	Assert.assertEquals(nb, count.get());
    }

    @Test
    public void emptyBuffering() {
	SequentialExecutor seq = new SequentialExecutor();

	// ne fait rien si pas d'evenement
	seq.bufferize();
	seq.unbuffer();

	AtomicInteger count = new AtomicInteger();
	int nb = 10;
	for (int i = 0; i < nb; ++i)
	    seq.execute(() -> count.getAndIncrement());

	Assert.assertEquals(nb, count.get());
    }
}
