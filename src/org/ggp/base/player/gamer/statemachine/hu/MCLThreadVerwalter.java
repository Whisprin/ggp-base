package org.ggp.base.player.gamer.statemachine.hu;

import java.util.ArrayList;
import java.util.List;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;

public class MCLThreadVerwalter extends Thread {

	Tromboter player;
	MachineState state;
	StateMachine mymachine;
	ArrayList<MCL_thread> threads;
	Double[] scores;
	boolean run = true;

	MCLThreadVerwalter(Double[] scores, StateMachine mymachine, Tromboter player){
		super();

		this.mymachine = mymachine;
		this.player = player;
		this.scores = scores;

		state = player.getCurrentState();
	}

	@Override public void run(){

		try {

		int i = 0;
		for (List<Move> ownMove:mymachine.getLegalJointMoves(player.getCurrentState())){
			// do thread magic here
			MCL_thread t = new MCL_thread( scores[i], mymachine,mymachine.getNextState(state, ownMove), player);
			t.start();
			threads.add(t);
			i++;
		}

		} catch (MoveDefinitionException | TransitionDefinitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// thread verwaltung
		/*boolean allDone = false;
		while (!allDone) {
			allDone = true;
			for (double score:scores) {
				if (score == -1) {
					allDone = false;
				}
			}
		}*/
		while(run);


	}


	void stopMinions(){
		for(int i=0; i < threads.size(); i++){
			threads.get(i).stopThread();
		}
	}

	  void stopThread(){
		  run = false;
	  }
}
