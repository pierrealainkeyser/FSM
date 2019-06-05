package fr.keyser.n.fsm.listener.choice;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class Choices {

    private final Map<String, Function<Choice, Boolean>> choices = new HashMap<>();

    public Choices with(String index, Supplier<Boolean> supplier) {
	return with(index, m -> supplier.get());
    }

    public Choices with(String index, Function<Choice, Boolean> supplier) {
	choices.put(index, supplier);
	return this;
    }

    public boolean test(Choice choice) {
	Function<Choice, Boolean> sup = choices.get(choice.getKey());
	if (sup != null)
	    return sup.apply(choice);
	else
	    return false;
    }

}
