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

// yeah!

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
	int i = 0;

	boolean justOneTime = true;
	long finish_by = 0;
	@Override
	public Move stateMachineSelectMove(long timeout) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException
	{
		// We get the current start time
		long start = System.currentTimeMillis();

		finish_by = timeout - 2000;

		StateMachine mymachine = getStateMachine();

		/**
		 * We put in memory the list of legal moves from the
		 * current state. The goal of every stateMachineSelectMove()
		 * is to return one of these moves. The choice of which
		 * Move to play is the goal of GGP.
		 */
		List<Move> moves = mymachine.getLegalMoves(getCurrentState(), getRole());
		Move selection = moves.get(0);

		// Log
		StringBuilder output = new StringBuilder();
		output.append(mymachine.getRoles());
		output.append("\n");
		output.append(moves);
		output.append("\n");
		output.append(++i);
		output.append("\n\n");
		//GamerLogger.emitToConsole(output.toString());
		int i = 0;

		int depth = 100;
		try {
			List<Integer> nodeScores = new ArrayList<Integer>();
			for (List<Move> ownMove:mymachine.getLegalJointMoves(getCurrentState())){
				try {
					int score = new Integer(minimax(mymachine, mymachine.getNextState(getCurrentState(), ownMove), depth, false));
					if (moves.size() != 1) {
						GamerLogger.emitToConsole(new Integer(++i).toString() + ": " +new Integer(score).toString() + "\n");
					}
					nodeScores.add(score);
				} catch (TransitionDefinitionException e) {
					e.printStackTrace();
				}
			}
			GamerLogger.emitToConsole("\n");
			if (moves.size() != 1) {
				int myscore = Collections.max(nodeScores);
				try {
					selection = mymachine.getLegalMoves(getCurrentState(), getRole()).get(nodeScores.indexOf(myscore));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (MoveDefinitionException e) {
			e.printStackTrace();
		}

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

	// This is the default Sample Panel
	@Override
	public DetailPanel getDetailPanel() {
		return new SimpleDetailPanel();
	}

	/*
	private boolean first_move = true;

	// Returns maximum reachable score
	private int getNodeScore(MachineState state) throws MoveDefinitionException, GoalDefinitionException {
		StateMachine mymachine = getStateMachine();
		List<Move> moves = mymachine.getLegalMoves(state, getRole());
		int myscore = 0;
		Move selection = moves.get(0);
		if (System.currentTimeMillis() > finish_by || mymachine.isTerminal(state)) {
			return mymachine.getGoal(state, getRole());
		}
		List<Integer> nodeScores = new ArrayList<Integer>();
		for (List<Move> ownMove:mymachine.getLegalJointMoves(state)){
			try {
				nodeScores.add(new Integer(getNodeScore(getStateMachine().getNextState(state, ownMove))));
			} catch (TransitionDefinitionException e) {
				e.printStackTrace();
			}
		}
		if (moves.size() != 1) {
			myscore = Collections.max(nodeScores);
		} else {
			myscore = Collections.min(nodeScores);
		}
		return myscore;
	}
	*/

	private int minimax(StateMachine mymachine, MachineState state, int depth, boolean maximizingPlayer) throws GoalDefinitionException, MoveDefinitionException, TransitionDefinitionException {
		if (depth == 0 || mymachine.isTerminal(state)) {
			//return heuristic_value;
			return mymachine.getGoal(state, getRole());
		} else if (System.currentTimeMillis() >= finish_by){
			return monteZott(mymachine, state);
		}
		if (maximizingPlayer) {
			int bestValue = -1;
			int val = -1;
			for (List<Move> ownMove:mymachine.getLegalJointMoves(state)){
				try {
					val = minimax(mymachine, mymachine.getNextState(state, ownMove), depth - 1, false);
				} catch (TransitionDefinitionException e) {
					e.printStackTrace();
				}
				bestValue = Math.max(bestValue, val);
				if (bestValue == 100) {
					break;
				}
			}
			return bestValue;
		} else {
			int worstValue = 101;
			int val = 101;
			for (List<Move> ownMove:mymachine.getLegalJointMoves(state)){
				try {
					val = minimax(mymachine, mymachine.getNextState(state, ownMove), depth - 1, true);
				} catch (TransitionDefinitionException e) {
					e.printStackTrace();
				}
				worstValue = Math.min(worstValue, val);
				if (worstValue == 0) {
					break;
				}
			}
			return worstValue;
		}
	}

	private int monteZott(StateMachine mymachine, MachineState state) throws TransitionDefinitionException, MoveDefinitionException, GoalDefinitionException {
		MachineState finalState = mymachine.performDepthCharge(state, new int[1]);
		return mymachine.getGoal(finalState, getRole());
	}


}