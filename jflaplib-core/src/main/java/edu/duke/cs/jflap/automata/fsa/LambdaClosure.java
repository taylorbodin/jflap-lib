/*
 *  JFLAP - Formal Languages and Automata Package
 * 
 * 
 *  Susan H. Rodger
 *  Computer Science Department
 *  Duke University
 *  August 27, 2009

 *  Copyright (c) 2002-2009
 *  All rights reserved.

 *  JFLAP is open source software. Please see the LICENSE for terms.
 *
 */

package edu.duke.cs.jflap.automata.fsa;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import edu.duke.cs.jflap.automata.AlphabetRetriever;
import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.AutomatonChecker;
import edu.duke.cs.jflap.automata.ClosureTaker;
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.StatePlacer;
import edu.duke.cs.jflap.automata.Transition;
import edu.duke.cs.jflap.automata.mealy.MooreMachine;

/**
 * The LambdaClosure object can be used to remove all lambda transitions from a given
 * NFA using the lambda closure property of NFA's. This module demonstrates the 
 * equivalence of lambda-NFA's to regular NFA's.
 * 
 * @author Taylor Bodin
 *
 */

/**
 * @author Taylor
 *
 */
public class LambdaClosure {
	/**
	 * Creates an instance of <CODE>LambdaClosure</CODE>
	 */
	public LambdaClosure() {

	}

	/**
	 * Returns true if one or more of the states in <CODE>states</CODE> are
	 * final.
	 * 
	 * @param states
	 *            the set of states
	 * @param automaton
	 *            the automaton that contains <CODE>states</CODE>
	 * @return true if one or more of the states in <CODE>states</CODE> are
	 *         final
	 */
	public boolean hasFinalState(State[] states, Automaton automaton) {
		for (int k = 0; k < states.length; k++) {
			if (automaton.isFinalState(states[k]))
				return true;
		}
		return false;
	}
	
	/**
	 * Returns all states reachable on <CODE>terminal</CODE> from <CODE>states</CODE>,
	 * including the closure of all reachable states.
	 * 
	 * @param terminal
	 *            the terminal (alphabet character)
	 * @param states
	 *            the set of states that we are checking to see if they have
	 *            transitions on <CODE>terminal</CODE>.
	 * @param automaton
	 *            the automaton.
	 * @return all states reachable on <CODE>terminal</CODE> from <CODE>states</CODE>,
	 *         including the closure of all reachable states.
	 */
	public State[] getStatesOnTerminal(String terminal, State[] states,
			Automaton automaton) {
		ArrayList list = new ArrayList();
		for (int k = 0; k < states.length; k++) {
			State state = states[k];
			Transition[] transitions = automaton.getTransitionsFromState(state);
			for (int i = 0; i < transitions.length; i++) {
				FSATransition transition = (FSATransition) transitions[i];
				if (transition.getLabel().equals(terminal)) {
					State toState = transition.getToState();
					State[] closure = ClosureTaker.getClosure(toState,
							automaton);
					for (int j = 0; j < closure.length; j++) {
						if (!list.contains(closure[j])) {
							list.add(closure[j]);
						}
					}
				}
			}
		}
		return (State[]) list.toArray(new State[0]);
	}

	/**
	 * Returns a string representation of <CODE>states</CODE>.
	 * 
	 * @param states
	 *            the set of states.
	 * @return a string representation of <CODE>states</CODE>.
	 */
	public String getStringForStates(State[] states) {
		StringBuffer buffer = new StringBuffer();
		for (int k = 0; k < states.length - 1; k++) {
			buffer.append(Integer.toString(states[k].getID()));
			buffer.append(",");
		}
		buffer.append(Integer.toString(states[states.length - 1].getID()));
		return buffer.toString();
	}

	/**
	 * Returns true if <CODE>states1</CODE> and <CODE>states2</CODE> are
	 * identical (i.e. they contain exactly the same states, and no extras).
	 * 
	 * @param states1
	 *            a set of states
	 * @param states2
	 *            a set of states
	 * @return true if <CODE>states1</CODE> and <CODE>states2</CODE> are
	 *         identical (i.e. they contain exactly the same states, and no
	 *         extras).
	 */
	public boolean containSameStates(State[] states1, State[] states2) {
		int len1 = states1.length;
		int len2 = states2.length;
		if (len1 != len2)
			return false;
		
	    Arrays.sort(states1, new Comparator<State>(){
	    	    public int compare(State s, State t){
	                return s.hashCode() - t.hashCode();	        	
	    	    }
	    }); 
	    Arrays.sort(states2, new Comparator<State>(){
	    	    public int compare(State s, State t){
	                return s.hashCode() - t.hashCode();	        	
	    	    }
	    }); 
		
		for (int k = 0; k < states1.length; k++) {
//			if (!containsState(states1[k], states2))
//				return false;
			if (states1[k] != states2[k])
				return false;
		}
		return true;
	}

	/**
	 * Creates a state in <CODE>dfa</CODE>, labeled with the set of states
	 * in <CODE>states</CODE>, which are all states from <CODE>nfa</CODE>.
	 * 
	 * @param dfa
	 *            the dfa. the automaton the state is added to
	 * @param states
	 *            the set of states from the nfa that this created state in the
	 *            dfa will represent
	 * @param nfa
	 *            the nfa
	 * @return the created state
	 */
	public State createStateWithStates(Automaton dfa, State[] states,
			Automaton nfa) {
		StatePlacer sp = new StatePlacer();
		State state = dfa.createState(sp.getPointForState(dfa));
		state.setLabel(getStringForStates(states));
		if (hasFinalState(states, nfa)) {
			dfa.addFinalState(state);
		}
		return state;
	}

	/**
	 * Returns the State array mapped to <CODE>state</CODE>.
	 * 
	 * @param state
	 *            the state.
	 * @param automaton
	 *            the nfa whose states are represented by <CODE>state</CODE>.
	 * @return the State array mapped to <CODE>state</CODE>.
	 */
	public State[] getStatesForState(State state, Automaton automaton) {
		if (state.getLabel() == null)
			return new State[0];
		StringTokenizer tokenizer = new StringTokenizer(state.getLabel(),
				" \t\n\r\f,q");
		ArrayList states = new ArrayList();
		while (tokenizer.hasMoreTokens())
			states.add(automaton.getStateWithID(Integer.parseInt(tokenizer
					.nextToken())));
		return (State[]) states.toArray(new State[0]);
	}
	
	/**
	 * Returns the State mapped to <CODE>states</CODE>.
	 * 
	 * @param states
	 *            the states
	 * @param dfa
	 *            the automaton that contains a state that is mapped to <CODE>states</CODE>
	 * @return the State mapped to <CODE>states</CODE>.
	 */
	public State getStateForStates(State[] states, Automaton dfa, Automaton nfa) {
		State[] dfaStates = dfa.getStates();
		for (int k = 0; k < dfaStates.length; k++) {
			State[] nfaStates = getStatesForState(dfaStates[k], nfa);
			if (containSameStates(nfaStates, states)) {
				return dfaStates[k];
			}
		}
		return null;
	}
	
	/**
	 *TODO: Returns a list of States created by expanding <CODE>state</CODE> in
	 * <CODE>dfa</CODE>. <CODE>state<CODE> is a state in <CODE>dfa</CODE>
	 * that represents a set of states in <CODE>nfa</CODE>. This method adds
	 * transitions to <CODE>dfa</CODE> from <CODE>state</CODE> on all
	 * terminals in the alphabet of <CODE>nfa</CODE> for which it is relevant.
	 * For each letter in the alphabet, you determine the reachable states (from
	 * <CODE>nfa</CODE>) from the set of states represented by <CODE>state</CODE>.
	 * You then create a state in <CODE>dfa</CODE> that represents all these
	 * reachable states and add a transition to DFA connecting <CODE>state</CODE>
	 * and this newly created state.
	 * 
	 * @param state
	 *            the state from dfa
	 * @param nfa
	 *            the nfa being converted to a dfa
	 * @param dfa
	 *            the dfa being built from the conversion
	 * @return a list of States created by expanding <CODE>state</CODE>.
	 */
	public ArrayList expandState(State state, Automaton nfa, Automaton dfa) {
		ArrayList list = new ArrayList();
		AlphabetRetriever far = new FSAAlphabetRetriever();
		String[] alphabet = far.getAlphabet(nfa);
		/** for each letter in the alphabet. */
		for (int k = 0; k < alphabet.length; k++) {
			/**
			 * get states reachable on terminal from all states represented by
			 * state.
			 */
			State[] states = getStatesOnTerminal(alphabet[k],
					getStatesForState(state, nfa), nfa);
			/** if any reachable states on terminal. */
			if (states.length > 0) {
				/**
				 * get state from dfa that represents list of reachable states
				 * in nfa.
				 */
				State toState = getStateForStates(states, dfa, nfa);
				/** if no such state. */
				if (toState == null) {
					/** create state, add to list */
					toState = createStateWithStates(dfa, states, nfa);
					// StatePlacer sp = new StatePlacer();
					// Point point = sp.getPointForState(dfa);
					// toState = dfa.createState(point);
					// toState.setLabel(getStringForStates(states));
					// if(hasFinalState(states, nfa)) {
					// dfa.addFinalState(toState);
					// }
					list.add(toState);
				}
				/**
				 * add transition to dfa from state to state that represents
				 * reachables states on terminal from nfa.
				 */
				Transition transition = new FSATransition(state, toState,
						alphabet[k]);
				dfa.addTransition(transition);
			}
		}
		return list;
	}

	
	/**
	 *TODO: Returns a 
	 * 
	 * @param automaton
	 * 			The Automaton on which the lambda transitions will be removed
	 *            
	 * @return fsa
	 * 			The FSA with lambda transitions removed
	 */
	public FiniteStateAutomaton removeLambdaTrans(FiniteStateAutomaton srcAutomaton) {
		
		// remove multiple character labels. 
		if (FSALabelHandler.hasMultipleCharacterLabels(srcAutomaton)) {
			FSALabelHandler.removeMultipleCharacterLabelsFromAutomaton(srcAutomaton);
		}
		
		// Initialize the destination automaton
		FiniteStateAutomaton destAutomaton = new FiniteStateAutomaton();
		
		// Copy the source automaton
		destAutomaton.become(destAutomaton, srcAutomaton);
		
		// Remove old transitions from state to state
		Transition[] oldTransitions = destAutomaton.getTransitions();
		
		for(int i = 0; i < oldTransitions.length; i++) {
			destAutomaton.removeTransition(oldTransitions[i]);
		}
		
		// Calculate the transition function for the new Automaton
		
		State[] states = destAutomaton.getStates();
		
		for(int i = 0; i < states.length; i++) {
			State[] lambdaStates = ClosureTaker.getClosure(states[i], srcAutomaton);
			
				AlphabetRetriever far = new FSAAlphabetRetriever();
				String[] alphabet = far.getAlphabet(srcAutomaton);
				/** for each letter in the alphabet. */
				for (int j = 0; j < alphabet.length; j++) {
					
					 //get states reachable on terminal from all lambda reachable states
					 
					State[] reachableStates = getStatesOnTerminal(alphabet[j], lambdaStates, srcAutomaton);
					
					// if any reachable states on terminal.
					if (reachableStates.length > 0) {
						
						State toState = getStateForStates(states, destAutomaton, srcAutomaton);
						// if no such state.
						if (toState == null) {
						break;
						}
						
						
						 //add transition to the destination Automaton
						Transition transition = new FSATransition(states[i], toState,
								alphabet[j]);
						destAutomaton.addTransition(transition);
					}
				}
			}
		
		// Check to see if the initial state should be a final state (accepts a null string)
		// I know it's not pretty
		State initialState = destAutomaton.getInitialState();
		State[] initialLambdaTransitions = ClosureTaker.getClosure(initialState, srcAutomaton);
		
		for(int i = 0; i < initialLambdaTransitions.length; i++) {
			if (destAutomaton.isFinalState(initialLambdaTransitions[i])) {
				destAutomaton.addFinalState(initialState);
			}
		}
		
		
		return destAutomaton;
	}

}
