package fr.keyser.fsm;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Génére les {@link FSMInstanceKey} à partir d'un UUID aléatoire
 * 
 * @author pakeyser
 *
 */
public class UUIDFSMInstanceKeyBuilder implements Supplier<FSMInstanceKey> {

    public static final UUIDFSMInstanceKeyBuilder INSTANCE = new UUIDFSMInstanceKeyBuilder("SHARED");

    private final FSMKey fsm;

    public UUIDFSMInstanceKeyBuilder(String key) {
	this.fsm = new FSMKey(key);
    }

    @Override
    public FSMInstanceKey get() {
	return new FSMInstanceKey(fsm, UUID.randomUUID().toString());
    }

}
