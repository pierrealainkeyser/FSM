package fr.keyser.n.fsm.listener.choice;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Choices {

    private final Map<String, Supplier<Boolean>> choices = new HashMap<>();

    public Choices with(String index, Supplier<Boolean> supplier) {
	choices.put(index, supplier);
	return this;
    }

    public boolean test(Choice choice) {
	Supplier<Boolean> sup = choices.get(choice.getKey());
	if (sup != null)
	    return sup.get();
	else
	    return false;
    }

}
