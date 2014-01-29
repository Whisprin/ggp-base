package org.ggp.base.util.statemachine;

import java.util.Set;

import org.ggp.base.util.gdl.grammar.GdlSentence;

public class TromboterMachineState extends MachineState {

	public int nodeScore;

	public TromboterMachineState() {
        //this.contents = null;
		nodeScore = -1; // -1 == not explored yet
    }

	 public TromboterMachineState(Set<GdlSentence> contents)
    {
        //this.contents = contents;
		nodeScore = -1; // -1 == not explored yet
    }

}