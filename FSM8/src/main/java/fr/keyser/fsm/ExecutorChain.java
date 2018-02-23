package fr.keyser.fsm;

import java.util.concurrent.Executor;

public class ExecutorChain implements Executor {

    private final Executor first;

    private final Executor second;

    public ExecutorChain(Executor first, Executor second) {
	this.first = first;
	this.second = second;
    }

    @Override
    public void execute(Runnable command) {
	Runnable wrapped = () -> second.execute(command);
	first.execute(wrapped);

    }
}
