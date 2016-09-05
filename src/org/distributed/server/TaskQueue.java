package org.distributed.server;

import java.util.ArrayList;

import org.distributed.serializable.Task;

public class TaskQueue {
	public static final int PING = 0;
	public static final int ADD_MATRICES = 1;

	public static int globalId = 0;
	ArrayList<Task> tasks;

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

	public Task getNextTask() {
		if (tasks.size() > 0) {
			Task task = tasks.get(0);
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

}
