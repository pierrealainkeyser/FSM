package fr.keyser.nn.fsm.builder;

public interface Container<T> extends NodeContainer<T> {

    Choice<T> choice(String choice);

    default Region<T> region(String region) {
	return region(region, 1);
    }

    Region<T> region(String region, int times);

    Terminal<T> terminal(String terminal);

}
