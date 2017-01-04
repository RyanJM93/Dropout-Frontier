package com.rjm.dropout.frontier.tasks;

public abstract class NotifyingThread extends Thread {
	private ILambdaCallNoArgs _onComplete;

	public final void setOnThreadComplete(
			final ILambdaCallNoArgs callOnComplete) {
		_onComplete = callOnComplete;
	}

	@Override
	public final void run() {
		try {
			doRun();
		} finally {
			if (_onComplete != null) {
				_onComplete.call();
			}
		}
	}

	public abstract void doRun();
}