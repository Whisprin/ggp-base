package org.ggp.base.player.gamer.statemachine.hu;

class MutableDouble {

	private double content;

	public MutableDouble(double d) {

			content = d;
	}

	void set(double d) {

		content = d;
	}

	double get() {

		return content;
	}
}

