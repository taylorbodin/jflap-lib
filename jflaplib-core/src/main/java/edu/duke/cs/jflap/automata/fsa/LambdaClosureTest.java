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
		FSATransition lambdaTrans = new FSATransition(states[0],states[2],"LAMBDA");
		
		
		test.addTransition(trans1);
		test.addTransition(trans2);
		test.addTransition(trans3);
		test.addTransition(trans4);
		test.addTransition(lambdaTrans);
		
		test.addFinalState(states[4]);
		test.setInitialState(states[0]);
		
		LambdaClosure lc = new LambdaClosure();
		
		//TEST: Copy States
		FiniteStateAutomaton copy = new FiniteStateAutomaton();
		LambdaClosure.copyStates(test, copy);
		
		System.out.println("COPY TEST");
		System.out.println("Test States: " + test.getStates().toString());
		System.out.println("Copy States: " + copy.getStates().toString());
		System.out.println("END OF COPY TEST");
		
//		assertTrue(test.getStates().equals(copy.getStates()));
//		assertTrue(test.getFinalStates().equals(copy.getFinalStates()));
//		assertTrue(test.getInitialState().equals(copy.getInitialState()));
		
		//TEST: getStatesOnTerminal
		State[] reachTest1 = lc.getStatesReachableOnTerminal(states[0], "a", test);
		State[] reachTest2 = lc.getStatesReachableOnTerminal(states[1], "b", test);
		State[] reachTest3 = lc.getStatesReachableOnTerminal(states[0], "c", test);
		
		State[] reachResult1 = {states[1]};
		State[] reachResult2 = {states[2]};
		State[] reachResult3 = {};
		
//		assertTrue(reachResult1.equals(reachTest1));
//		assertTrue(reachResult2.equals(reachTest2));
//		assertTrue(reachResult3.equals(reachTest3));
		
		//
		System.out.println("BEFORE:");
		lc.printTransitionLabels(test);
		
		FiniteStateAutomaton testLambdaRemoved = new FiniteStateAutomaton();
		Automaton.become(testLambdaRemoved,lc.removeLambdaTrans(test));
		
		System.out.println("AFTER:");
		lc.printTransitionLabels(testLambdaRemoved);
		
		System.out.println("DONE");
	}

}
