package org.distributed.server;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.distributed.serializable.Task;

public class TaskQueue {
	public static final int PING = 0;
	public static final int ADD_MATRICES = 1;
	public static final int MULTIPLY_MATRICES = 2;
	public static final int DOT_PRODUCT = 3;

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
			return tasks.peek();
		} else {
			return new Task(PING, -1, null, null, null);
		}
	}

	public void removeTaskFromQueue(Task task) {
		tasks.remove(task);
	}

	public void addToCompletedTasks(Task task) {
		completedTasks.add(task);

		System.out.println("Adding to completed tasks " + completedTasks.size());
	}

	public ArrayList<Task> takeCompletedTasks() throws InterruptedException {
		ArrayList<Task> temp = new ArrayList<>();
		while (completedTasks.size() > 0)
			temp.add(completedTasks.take());

		return temp;
	}

	public int incompleteSize() {
		return tasks.size();
	}

}
