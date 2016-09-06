package org.distributed.server;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.distributed.serializable.Task;

public class TaskQueue {
	public static final int PING = 0;
	public static final int ADD_MATRICES = 1;

	public static int globalId = 0;
	private BlockingQueue<Task> tasks;
	private BlockingQueue<Task> completedTasks;
	
	public TaskQueue() {
		tasks = new LinkedBlockingQueue<>();
		completedTasks = new LinkedBlockingQueue<>();
	}

	public void waitForFinish() {
		System.out.println("Waiting for finish");
		while (tasks.size() != 0)
			Thread.yield();
		System.out.println("Finished");
	}

	public void addTask(int type, float[][] a, float[][] b, float[][] c) {
		Task task = new Task(type, globalId++, a, b, c);
		tasks.add(task);
	}

	public Task getNextTask() throws InterruptedException {
		if (tasks.size() > 0) {
			Task task = tasks.take();
			tasks.remove(0);
			tasks.add(task);
			return task;
		} else {
			return new Task(PING, -1, null, null, null);
		}
	}

	public void removeTaskFromQueue(Task task) {
		tasks.remove(task);
	}
	
	public void addToCompletedTasks(Task task){
		completedTasks.add(task);
	}
	
	public ArrayList<Task> takeCompletedTasks() throws InterruptedException{
		ArrayList<Task> temp = new ArrayList<>();
		for(int i = 0; i < completedTasks.size(); i++){
			temp.add(completedTasks.take());
		}
		return temp;
	}

	public int incompleteSize() {
		return tasks.size();
	}

}
