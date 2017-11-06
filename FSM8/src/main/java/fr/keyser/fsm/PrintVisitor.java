package fr.keyser.fsm;

import java.io.PrintWriter;

/**
 * Permet d'afficher des informations sur l'automate
 * 
 * @author pakeyser
 *
 * @param <S>
 * @param <E>
 */
public class PrintVisitor<S, E> implements FSMVisitor<S, E> {

	private final PrintWriter out;

	public PrintVisitor(PrintWriter out) {
		this.out = out;
	}

	@Override
	public void initial(S initial, OnEnterAction<S, E, ?> enter, OnExitAction<S, E, ?> exit) {
		out.println("INITIAL|" + initial);
		printActions(enter, exit);
	}

	private void printActions(OnEnterAction<S, E, ?> enter, OnExitAction<S, E, ?> exit) {
		if (enter != null)
			out.println(" /enter " + enter);
		if (exit != null)
			out.println(" /exit  " + exit);
	}

	@Override
	public void state(S state, OnEnterAction<S, E, ?> enter, OnExitAction<S, E, ?> exit, boolean terminal) {
		if (terminal)
			out.print("TERMINAL|");
		else
			out.print("STATE|");
		out.println(state);
		printActions(enter, exit);
	}

	@Override
	public void transition(S from, E event, OnTransitionAction<S, E, ?> action, S destination) {
		out.print(" @" + event + " -> " + destination);
		if (action != null)
			out.print(" /transition " + action);
		out.println();
	}
}
