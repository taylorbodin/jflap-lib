package edu.duke.cs.jflap.automata.fsa;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Test;

import edu.duke.cs.jflap.automata.Automaton;
import edu.duke.cs.jflap.automata.State;
import edu.duke.cs.jflap.automata.Transition;

public class LambdaClosureTest {

	@Test
	public void testRemoveLambdaTrans() {
		
		//Set up NFA with Lambda Transitions
		
		FiniteStateAutomaton test = new FiniteStateAutomaton();
		
		for (int i = 0; i < 5; i++) {
			test.createState(new Point(i,i));
		}
		
		State[] states = test.getStates();
		
		FSATransition trans1 = new FSATransition(states[0],states[1],"a");
		FSATransition trans2 = new FSATransition(states[1],states[2],"b");
		FSATransition trans3 = new FSATransition(states[2],states[3],"a");
		FSATransition trans4 = new FSATransition(states[3],states[4],"b");
		FSATransition lambdaTrans = new FSATransition(states[0],states[2],"");
		
		
		test.addTransition(trans1);
		test.addTransition(trans2);
		test.addTransition(trans3);
		test.addTransition(trans4);
		test.addTransition(lambdaTrans);
		
		test.addFinalState(states[4]);
		test.setInitialState(states[0]);
		
		LambdaClosure lc = new LambdaClosure();
		
		//TEST: Copy States
		/* I can't seem to make a test for this one that works well.
		 * By setting a breakpoint and checking what I got out of copy
		 * I know that when fed the correct stuff it copy produces a 
		 * copy with just the states.
		 */
		
		FiniteStateAutomaton copy = new FiniteStateAutomaton();
		
		LambdaClosure.copyStates(test, copy);
		
		//TEST: getStatesOnTerminal
		/* Again, It was just easier to set a breakpoint and compare. 
		 * .equals() doesn't work
		 */
		
		State[] reachTest1 = lc.getStatesReachableOnTerminal(states[0], "a", test);
		State[] reachTest2 = lc.getStatesReachableOnTerminal(states[1], "b", test);
		State[] reachTest3 = lc.getStatesReachableOnTerminal(states[0], "c", test);
		
		State[] reachResult1 = {states[1]};
		State[] reachResult2 = {states[2]};
		State[] reachResult3 = {};
		
		//TEST: addTransitions
		// Works well so long as you're referencing the correct states
		State[] copyStates = copy.getStates();
		lc.addTransitions(copyStates[0], copyStates, "b", copy);
		
		
		//TEST: processStateOnTerminal
		State[] processStates = lc.processStateOnTerminal(states[0], "a", test);
		
		//TEST: removeLambdaTrans
		
		FiniteStateAutomaton testLambdaRemoved = new FiniteStateAutomaton();
		lc.transform(test, testLambdaRemoved);
		
		System.out.println("Test Object with Lambda Transitions");
		System.out.println(test+"\r");
		
		System.out.println("Result FSA with Lambda Transitions Removed");
		System.out.println(testLambdaRemoved);
	}

}
