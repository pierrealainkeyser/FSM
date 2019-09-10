package fr.keyser.nn.fsm.builder;

import java.util.function.BiConsumer;

import fr.keyser.nn.fsm.impl.ActionFunction;
import fr.keyser.nn.fsm.impl.EventMsg;
import fr.keyser.nn.fsm.impl.Instance;
import fr.keyser.nn.fsm.impl.PayloadActionFunction;

public interface ActionNode<T> {

    default ActionNode<T> callbackEntry(BiConsumer<Instance<T>, EventMsg> callback) {
	return entry((i, event) -> {
	    callback.accept(i, event);
	    return i;
	});
    }

    default ActionNode<T> updateEntry(PayloadActionFunction<T> entry) {
	return entry(entry.asAction());
    }

    ActionNode<T> entry(ActionFunction<T> entry);

    default ActionNode<T> callbackExit(BiConsumer<Instance<T>, EventMsg> callback) {
	return exit((i, event) -> {
	    callback.accept(i, event);
	    return i;
	});
    }

    default ActionNode<T> updateExit(PayloadActionFunction<T> exit) {
	return exit(exit.asAction());
    }

    ActionNode<T> exit(ActionFunction<T> exit);

}
