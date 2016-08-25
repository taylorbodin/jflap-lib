package edu.duke.cs.jflap.automata.fsa;

import java.awt.Point;

import org.junit.Test;

import edu.duke.cs.jflap.automata.State;

public class LambdaClosureTest2 {

	@Test
	public void testRemoveLambdaTrans() {
		
		//Set up NFA with Lambda Transitions
		
		FiniteStateAutomaton test = new FiniteStateAutomaton();
		
		for (int i = 0; i < 4; i++) {
			test.createState(new Point(i,i));
		}
		
		State[] states = test.getStates();
		
		FSATransition trans0_1 = new FSATransition(states[0],states[1],"");
		
		FSATransition trans1_1 = new FSATransition(states[1],states[1],"a");
		FSATransition trans1_2 = new FSATransition(states[1],states[2],"a");
		
		FSATransition trans2_3 = new FSATransition(states[2],states[3],"b");
		
		FSATransition trans3_1 = new FSATransition(states[3],states[1],"");
		
		test.addTransition(trans0_1);
		test.addTransition(trans1_1);
		test.addTransition(trans1_2);
		test.addTransition(trans2_3);
		test.addTransition(trans3_1);
		
		test.addFinalState(states[1]);
		test.setInitialState(states[0]);
		
		LambdaClosure lc = new LambdaClosure();
		
		//TEST: removeLambdaTrans
		
		FiniteStateAutomaton testLambdaRemoved = new FiniteStateAutomaton();
		lc.transform(test, testLambdaRemoved);
		
		System.out.println("Test Object with Lambda Transitions");
		System.out.println(test+"\r");
		
		System.out.println("Result FSA with Lambda Transitions Removed");
		System.out.println(testLambdaRemoved);
	}
}