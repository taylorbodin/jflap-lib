package edu.duke.cs.jflap.automata.fsa;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Test;

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
		State[] terminalTest = null;
		
		FSATransition trans1 = new FSATransition(states[0],states[1],"a");
		FSATransition trans2 = new FSATransition(states[1],states[2],"b");
		FSATransition trans3 = new FSATransition(states[2],states[3],"a");
		FSATransition trans4 = new FSATransition(states[3],states[4],"b");
		FSATransition lambdaTrans = new FSATransition(states[0],states[4],"LAMBDA");
		
		
		test.addTransition(trans1);
		test.addTransition(trans2);
		test.addTransition(trans3);
		test.addTransition(trans4);
		test.addTransition(lambdaTrans);
		
		test.addFinalState(states[4]);
		test.setInitialState(states[0]);
		
		LambdaClosure lc = new LambdaClosure();
		
		FiniteStateAutomaton testLambdaRemoved = lc.removeLambdaTrans(test);
		System.out.println("DONE");
	}

}
