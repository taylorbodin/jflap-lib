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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.StringTokenizer;

import edu.duke.cs.jflap.automata.AlphabetRetriever;
import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.ClosureTaker;
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.StatePlacer;
import edu.duke.cs.jflap.automata.Transition;

/**
 * The LambdaClosure object can be used to remove all lambda transitions from a given
 * NFA using the lambda closure property of NFA's. This module demonstrates the 
 * equivalence of lambda-NFA's to regular NFA's.
 * 
 * @author Taylor Bodin
 *
 */


public class LambdaClosure {
	/**
	 * Creates an instance of <CODE>LambdaClosure</CODE>
	 */
	public LambdaClosure() {

	}

	public void printTransitionLabels(FiniteStateAutomaton fsa) {
		Transition[] transitions = fsa.getTransitions();
		
		for (int i = 0; i < transitions.length; i++) {
			System.out.println(((FSATransition) transitions[i]).getLabel());
		}
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
	public static boolean containSameStates(State[] states1, State[] states2) {
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
	 *Copies the states, and only the states, from the srcFSA to the destFSA
	 * @param srcFSA
	 * @param destFSA
	 */
	public static void copyStates(FiniteStateAutomaton srcFSA, FiniteStateAutomaton destFSA) {
		
		//Copy the src automaton
		Automaton.become(destFSA, srcFSA);
		
		//Delete the old transitions
		Transition[] oldTransitions = destFSA.getTransitions();
		
		for (int i=0; i < oldTransitions.length; i++) {
			destFSA.removeTransition(oldTransitions[i]);
		}
		
		return;
	}

	/**
	 *Returns an array of states reachable from a given state on a given terminal in one hop
	 * @param state
	 * 			The from state
	 * @param terminal
	 * 			The terminal or transition label of interest
	 * @param fsa
	 * 			The finite state machine containing the states and transitions
	 * @return
	 */
	public State[] getStatesReachableOnTerminal(State state, String terminal, FiniteStateAutomaton fsa) {
		Transition[] allTransitions = fsa.getTransitions();
		ArrayList<State> states = new ArrayList<State>();
		
		for (int i = 0; i < allTransitions.length; i++) {
			String label = ((FSATransition) allTransitions[i]).getLabel();
			State toState = allTransitions[i].getToState();
			State fromState = allTransitions[i].getFromState();
			
			//If the transition is out of the state and has the specified label
			if (label.equals(terminal) && fromState.equals(state)){
				states.add(toState);
			}
		}
		
		return (State[]) states.toArray(new State[0]);
	}
	
	/**
	 *Implements the algorithm by which Lambda transitions are removed for a given state
	 * works by: 
	 * 1. Calculate the set of states for the closure of the given state over lambda transitions
	 * 2. Calculate the set of states for one transition from the previous set of states 
	 * 		over a given terminal
	 * 3. Calculate the set of states for the closure of the previous set of states over lambda
	 * 		transitions
	 * 
	 * @param state
	 * 			State on which the algorithm will be performed
	 * @param terminal
	 * 			The character used as the terminal in step 2
	 * @param fsa
	 * 			The FSA which contains the state
	 * @return
	 * 		  toStates is the set of states described in step 3
	 */
	public State[] processStateOnTerminal (State state, String terminal, FiniteStateAutomaton fsa){
		State[] closureStates = null; // First set in step 1
		ArrayList<State> transitionStates = new ArrayList<State>(); // First set in step 2
		ArrayList<State> toStates = new ArrayList<State>(); // Final Set in step 3
		
		// Step 1
		
		closureStates = ClosureTaker.getClosure(state, fsa);
	
		// Step 2
		
		State[] reachableStates = null;
		
		for (int i = 0; i < closureStates.length; i++) {
			reachableStates = getStatesReachableOnTerminal(closureStates[i],terminal,fsa);
			for (int j = 0; j < reachableStates.length; j++) {
				transitionStates.add(reachableStates[j]);
			}
		}
		
		// Step 3
		Iterator<State> it = transitionStates.iterator();
		State[] nextClosureStates = null;
		
		while (it.hasNext()) {
			State nextState = it.next();
			nextClosureStates = ClosureTaker.getClosure(nextState,fsa);
			
			for (int i = 0; i < nextClosureStates.length; i++) {
				if (!toStates.contains(nextClosureStates[i])) {
					toStates.add(nextClosureStates[i]);
				}
			}
		}
		
		return (State[]) toStates.toArray(new State[0]);
	}
	
	/**
	 *Adds Transitions from <CODE>fromState</CODE> to the <CODE>toStates</CODE> with the label for the fsa given
	 * @param fromState 
	 * 			State from which the transitions will be added
	 * @param toStates
	 * 			States to which transitions will be added
	 * @param label
	 * 			Label for the transitions
	 * @param fsa
	 * 			The FSA on which these transitions will be added
	 */
	public static void addTransitions(State fromState, State[] toStates, String label, FiniteStateAutomaton fsa) {
		
		for (int i = 0; i < toStates.length; i++) {
			FSATransition transition = new FSATransition(fromState, toStates[i], label);
			fsa.addTransition(transition);
		}
	}
	
	public void removeLambdaTrans(FiniteStateAutomaton fsa) {
		Transition[] transitions = fsa.getTransitions();
		for (int i = 0; i < transitions.length; i++) {
			if (((FSATransition) transitions[i]).getLabel().equals("")) {
				fsa.removeTransition(transitions[i]);
			}
		}
	}
	
	/**
	 *Returns an equivalent NFA without lambda transfers 
	 * 
	 * @param srcFSA
	 * 			The NFA with lambda transitions that the transform is being applied on
	 * 			NOTE: I'm trying to make this non-destructive and I'm  not sure if I'm
	 * 					achieving that 
	 * @return destFSA
	 * 			The FSA with lambda transitions removed
	 */
	public void transform(FiniteStateAutomaton srcFSA, FiniteStateAutomaton destFSA) {
		
		// Setup variables
		
		FiniteStateAutomaton fsa = new FiniteStateAutomaton();
		Automaton.become(fsa, srcFSA);
		
		// remove multiple character labels. 
		if (FSALabelHandler.hasMultipleCharacterLabels(fsa)) {
			FSALabelHandler.removeMultipleCharacterLabelsFromAutomaton(fsa);
		}
		
		State[] states = fsa.getStates();
		State initState = fsa.getInitialState();
		State[] toStates = null;
		
		FSAAlphabetRetriever ret = new FSAAlphabetRetriever();
		String[] alphabet = ret.getAlphabet(fsa);
		
		// Check for the special case where the initial state has lambda transitions
		// to a final state
		
		State[] initialClosure = ClosureTaker.getClosure(initState, fsa);
		for (int i = 0; i < initialClosure.length; i++) {
			if (fsa.isFinalState(initialClosure[i])) {
				fsa.addFinalState(initState);
				break;
			}
		}
		
		// Calculate new transfer functions for each state on each terminal and add
		
		for (int i = 0; i < states.length; i++) {
			for (int j = 0; j < alphabet.length; j++) {
				if (!alphabet[j].equals("")) { //Don't want to run this on nulls
					toStates = null;
					toStates = processStateOnTerminal(states[i], alphabet[j], fsa);
					addTransitions(states[i], toStates, alphabet[j], fsa);
				}
			}
		}
		
		removeLambdaTrans(fsa);
		
		Automaton.become(destFSA, fsa);
		
		return;
	}

}
