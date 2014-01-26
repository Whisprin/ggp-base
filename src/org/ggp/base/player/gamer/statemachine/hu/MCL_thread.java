package org.ggp.base.player.gamer.statemachine.hu;

import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;

public class MCL_thread extends Thread
{


  MCL_thread(int monteScores[], int i,Move ownMove, StateMachine mymachine){
    super();
    
    // initalize parameters
    
    
  }

  @Override public void run()
  {
      // do
  
      // run monteZott
      
      // write monteZott's return value in too the array or determine average
      
      // while (is enough time || iterated enough)
      
      //exit run => close thread    
  }

  
  
  private int monteZott(StateMachine mymachine, MachineState state) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
  {
    MachineState finalState = mymachine.performDepthCharge(state, new int[1]);
    return mymachine.getGoal(finalState, getRole());
  }
  
}
