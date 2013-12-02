package org.ggp.base.player.gamer.statemachine.hu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ggp.base.apps.player.detail.DetailPanel;
import org.ggp.base.apps.player.detail.SimpleDetailPanel;
import org.ggp.base.player.gamer.event.GamerSelectedMoveEvent;
import org.ggp.base.player.gamer.exception.GamePreviewException;
import org.ggp.base.player.gamer.statemachine.StateMachineGamer;
import org.ggp.base.util.game.Game;
import org.ggp.base.util.logging.GamerLogger;
import org.ggp.base.util.statemachine.MachineState;
import org.ggp.base.util.statemachine.Move;
import org.ggp.base.util.statemachine.StateMachine;
import org.ggp.base.util.statemachine.cache.CachedStateMachine;
import org.ggp.base.util.statemachine.exceptions.GoalDefinitionException;
import org.ggp.base.util.statemachine.exceptions.MoveDefinitionException;
import org.ggp.base.util.statemachine.exceptions.TransitionDefinitionException;
import org.ggp.base.util.statemachine.implementation.prover.ProverStateMachine;

/**
 * SampleLegalGamer is a minimal gamer which always plays the first
 * legal move it identifies, regardless of the state of the game.
 *
 * For your first players, you should extend the class SampleGamer
 * The only function that you are required to override is :
 * public Move stateMachineSelectMove(long timeout)
 *
 */
public final class Tromboter extends StateMachineGamer
{
	/**
	 * This function is called at the start of each round
	 * You are required to return the Move your player will play
	 * before the timeout.
	 *
	 */
	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
		// We get the current start time
		long start = System.currentTimeMillis();
		StateMachine mymachine = getStateMachine();

		/**
		 * We put in memory the list of legal moves from the
		 * current state. The goal of every stateMachineSelectMove()
		 * is to return one of these moves. The choice of which
		 * Move to play is the goal of GGP.
		 */
		List<Move> moves = mymachine.getLegalMoves(getCurrentState(), getRole());
		Move selection = moves.get(moves.size()-1);

		// Log
		StringBuilder output = new StringBuilder();
		output.append(getRole());
		output.append("\n");
		GamerLogger.emitToConsole(output.toString());

		while (true) {
			if (System.currentTimeMillis() > timeout - 500) {
		        break;
			}
			selection = mymachine.getRandomMove(getCurrentState(), getRole());
		}



		/*
		for (Move amove:getStateMachine().getLegalMoves(getCurrentState(), getRole()) {
			for (Role arole:getStateMachine().getRoles()) {
			    try {
		            MachineState finalState = mymachine.performDepthCharge(mymachine.getRandomNextState(theState, getRole(), myMove), depth);
		            return theMachine.getGoal(finalState, getRole());
		        } catch (Exception e) {
		            e.printStackTrace();
		            return 0;
		        }
			}
		}
		*/

		// We get the end time
		// It is mandatory that stop<timeout

		long stop = System.currentTimeMillis();

		/**
		 * These are functions used by other parts of the GGP codebase
		 * You shouldn't worry about them, just make sure that you have
		 * moves, selection, stop and start defined in the same way as
		 * this example, and copy-paste these two lines in your player
		 */
		notifyObservers(new GamerSelectedMoveEvent(moves, selection, stop - start));
		return selection;
	}

	@Override
	public StateMachine getInitialStateMachine() {
		return new CachedStateMachine(new ProverStateMachine());
	}

	@Override
	public void stateMachineMetaGame(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void stateMachineStop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stateMachineAbort() {
		// TODO Auto-generated method stub

	}

	@Override
	public void preview(Game g, long timeout) throws GamePreviewException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		return "Tromboter";
	}

	// This is the defaul Sample Panel
	@Override
	public DetailPanel getDetailPanel() {
		return new SimpleDetailPanel();
	}

	// Returns maximum reachable score
	private int getMaxNodeScore(MachineState state) {
		// TODO: return move
		if (getStateMachine().isTerminal(state)) {
			try {
				return getStateMachine().getGoal(state, getRole());
			} catch (GoalDefinitionException e) {
				e.printStackTrace();
				return 0;
			}
		}
		try {
			List<Integer> nodeScores = new ArrayList<Integer>();
			for (List<Move> amove:getStateMachine().getLegalJointMoves(state)) {
				//MachineState newstate = getStateMachine().performDepthCharge(getStateMachine().getNextState(state, amove), );
				// TODO: Unterscheidung zwischen Rollen: min/max
				try {
					nodeScores.add(getMaxNodeScore(getStateMachine().getNextState(state, amove)));
				} catch (TransitionDefinitionException e) {
					e.printStackTrace();
				}
			}
			return Collections.max(nodeScores);
		} catch (MoveDefinitionException e) {
			e.printStackTrace();
			return 0;
		}
	}


}