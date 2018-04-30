package fr.keyser.bus;

public class PluggableBus implements Bus {

    private Bus delegated;

    @Override
    public void forward(Object event) {
	Bus delegated = this.delegated;
	if (delegated != null)
	    delegated.forward(event);

    }

    public void setDelegated(Bus delegated) {
        this.delegated = delegated;
    }

}
