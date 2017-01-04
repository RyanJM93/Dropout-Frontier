package com.rjm.dropout.frontier.tasks;

public class FrontierThread extends NotifyingThread {
	private Runnable _runThis;

	public FrontierThread(Runnable runThis) {
		_runThis = runThis;
	}

	@Override
	public void doRun() {
		_runThis.run();
	}

}
