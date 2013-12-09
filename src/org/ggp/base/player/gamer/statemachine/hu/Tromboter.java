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
		output.append(mymachine.getRoles());
		output.append("\n");
		output.append(moves);
		output.append("\n");
		output.append(++i);
		output.append("\n\n");
		//GamerLogger.emitToConsole(output.toString());

		/*
		while (true) {
			if (System.currentTimeMillis() > timeout - 500) {
				break;
			}
		}*/

		try {
			List<Integer> nodeScores = new ArrayList<Integer>();
			for (List<Move> ownMove:mymachine.getLegalJointMoves(getCurrentState())){
				try {
					nodeScores.add(new Integer(getNodeScore(getStateMachine().getNextState(getCurrentState(), ownMove))));
				} catch (TransitionDefinitionException e) {
					e.printStackTrace();
				}
			}
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

	private boolean first_move = true;

	// Returns maximum reachable score
	private int getNodeScore(MachineState state) throws MoveDefinitionException, GoalDefinitionException {
		StateMachine mymachine = getStateMachine();
		List<Move> moves = mymachine.getLegalMoves(state, getRole());
		int myscore = 0;
		Move selection = moves.get(0);
		if (mymachine.isTerminal(state)) {
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


}