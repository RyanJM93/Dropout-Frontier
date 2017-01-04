package com.rjm.dropout.frontier.tasks;
import com.rjm.dropout.frontier.ProgressDialog;
import com.rjm.dropout.frontier.ProgressDialogView;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Cursor;

public abstract class FrontierTask<T> {

	protected String _currentState;

	protected ProgressDialog _progressDialog;

	private NotifyingThread _spawnedThread;

	private ILambdaCallNoArgs _onComplete;

	private T _returningValue;
	
	public StringProperty messageProperty = new SimpleStringProperty();
	public DoubleProperty progressProperty = new SimpleDoubleProperty();

	private void createWaitDialog() {
		
		if (_progressDialog == null) {
			synchronized (_syncObject) {
				if (_progressDialog == null) {

					_progressDialog = null;
					try {
						_progressDialog = new ProgressDialogView().getController();
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (_progressDialog == null) {
						System.out.println("Unable to load Task Progress Dialog!");

						return;
					}

					_progressDialog.setTask(this);
				}
			}
		}
	}
	
	private boolean wait = true;
	void setWait(boolean value){
		wait = value;
	}

	public T execute() {
		
		_returningValue = null;

			
		if(wait){
			createWaitDialog();
			_progressDialog.getStage().getScene().setCursor(Cursor.WAIT);
		}
		
		
		_spawnedThread = new FrontierThread(new Runnable() {
			@Override
			public void run() {
				_returningValue = runInBackground();
			}
		});

		if (_progressDialog != null) {
			_spawnedThread.setOnThreadComplete(() -> {
				runOnUIThread(() -> {
					
					_progressDialog.getStage().getScene().setCursor(Cursor.DEFAULT);
					
					_progressDialog.getStage().close();
				});

				if (_onComplete != null) {
					_onComplete.call();
				}
			});
			
			_progressDialog.getStage().show();
		}

		_spawnedThread.setDaemon(true);


		_spawnedThread.start();

		return _returningValue;
	}

	public void setOnTaskComplete(ILambdaCallNoArgs onComplete) {
		_onComplete = onComplete;
	}

	private final static Object _syncObject = new Object();

	protected void runOnUIThread(ILambdaCallNoArgs func) {
		if (func != null) {
			synchronized (_syncObject) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						try {
							func.call();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		}
	}

	protected abstract String getTaskTitle();

	protected abstract T runInBackground();
	
	protected void setMessage(String message){
		runOnUIThread(() -> {
			messageProperty.set(message);
		});
	}
	
	protected void setProgress(double progress){
		runOnUIThread(() -> {
			progressProperty.set(progress);
		});
	}
}
