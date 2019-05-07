package fr.keyser.n.fsm.listener.choice;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class Choices {

    private final Map<String, Function<Map<String, Object>, Boolean>> choices = new HashMap<>();

    public Choices with(String index, Supplier<Boolean> supplier) {
	return with(index, m -> supplier.get());
    }

    public Choices with(String index, Function<Map<String, Object>, Boolean> supplier) {
	choices.put(index, supplier);
	return this;
    }

    public boolean test(Choice choice) {
	Function<Map<String, Object>, Boolean> sup = choices.get(choice.getKey());
	if (sup != null)
	    return sup.apply(choice.getProps());
	else
	    return false;
    }

}
