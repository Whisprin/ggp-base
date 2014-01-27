package org.ggp.base.player.gamer.statemachine.hu;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;


public class MCL_thread extends Thread
{

	Tromboter player;
	StateMachine mymachine;
	double i;
	MachineState state;
	boolean run = true;

  MCL_thread(double i, StateMachine mymachine, MachineState state, Tromboter player){
    super();

    // initalize parameters
    this.player = player;
    this.mymachine = mymachine;
    this.i = i;
    this.state = state;
  }

  @Override public void run()
  {
	  int value = 0;
	  int times = 0;

	  while(run){

		  // run monteZott
		  try {

			  value += monteZott();
			  times++;

		  } catch (Exception e) {

		  }

		  // write monteZott's return value in too the array or determine average
		  i = ((double)value)/times;

	  }

      //exit run => close thread
  }



  private int monteZott() throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
  {
    MachineState finalState = mymachine.performDepthCharge(state, new int[1]);
    return mymachine.getGoal(finalState, player.getRole());
  }

  void stopThread(){
	  run = false;
  }

}
