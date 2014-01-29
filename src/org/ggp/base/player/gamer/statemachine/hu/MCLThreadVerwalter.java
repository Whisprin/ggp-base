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
	ArrayList<MCL_thread> threads = new ArrayList<MCL_thread>();
	MutableDouble[] scores;
	boolean run = true;

	long timeout;
	int  numberOfThreads;

	MCLThreadVerwalter(MutableDouble[] scores, StateMachine mymachine, Tromboter player, long timeout, int numberOfThreads){
		super();

		this.mymachine = mymachine;
		this.player = player;
		this.scores = scores;
		this.timeout = timeout;
		this.numberOfThreads = numberOfThreads;

		state = player.getCurrentState();
	}

	@Override public void run(){

		try {

		long timeframe = timeout - System.currentTimeMillis();

		if(scores.length < numberOfThreads){
			numberOfThreads = scores.length;
		}

		// Zeitspanne / (#Moves / #Threads)
		long threadRunTime = timeframe / (long)Math.ceil((double)(scores.length)/numberOfThreads);

		List<List<Move>> ownMoves = mymachine.getLegalJointMoves(player.getCurrentState());

		for(int j=1; j <= Math.ceil((double)(scores.length)/numberOfThreads); j++){

			System.out.println(j);

			for(int k = 0; k < numberOfThreads; k++)
			{
				int index = ((j-1)*numberOfThreads+k)%scores.length;
				MCL_thread t = new MCL_thread( scores[index], mymachine,mymachine.getNextState(state, ownMoves.get(index)), player);
				t.start();
				threads.add(t);
			}

			long time = System.currentTimeMillis();

			while(((System.currentTimeMillis() - time) < threadRunTime) && run){
				// pfui
				Thread.sleep(threadRunTime/10);
			}

			for(MCL_thread t : threads){
				t.stopThread();
			}

			while(!threads.isEmpty()){
				threads.get(0).join();
				threads.remove(0);
			}

			if(!run) return;

		}

		} catch (MoveDefinitionException | TransitionDefinitionException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
