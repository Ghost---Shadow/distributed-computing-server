package org.distributed.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.distributed.serializable.Task;

public class Server {
	private ServerSocket serverSocket;

	private final static int NUMBER_OF_CLIENTS = 10;
	private ExecutorService executorService;

	private static Server server;
	private static TaskQueue taskQueue;
	public static int PORT = 80;

	private Server() {
		executorService = Executors.newFixedThreadPool(NUMBER_OF_CLIENTS);
		taskQueue = new TaskQueue();
	}

	public static Server getInstance() {
		if (server == null)
			server = new Server();
		return server;
	}

	public void runServer() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println("Starting Server");
					serverSocket = new ServerSocket(PORT);

					while (true) {
						System.out.println("Waiting for request");
						try {
							Socket s = serverSocket.accept();
							System.out.println("Processing request");
							executorService.submit(new ServiceRequest(s));
						} catch (IOException ioe) {
							System.out.println("Error accepting connection");
							ioe.printStackTrace();
						}
					}
				} catch (IOException e) {
					System.out.println("Error starting Server on " + PORT);
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void stopServer() {
		executorService.shutdownNow();
		try {
			serverSocket.close();
		} catch (IOException e) {
			System.out.println("Error in server shutdown");
			e.printStackTrace();
		}
		System.exit(0);
	}

	class ServiceRequest implements Runnable {
		private Socket socket;

		public ServiceRequest(Socket connection) {
			this.socket = connection;
		}

		public void run() {
			try {
				ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());
				System.out.println("Number of tasks: " + taskQueue.incompleteSize());
				System.out.println(socket.getInetAddress().toString());
				Task task = taskQueue.getNextTask();
				objectOut.writeObject(task);
				Task completedTask = (Task) objectIn.readObject();
				if (completedTask.type != TaskQueue.PING) {
					taskQueue.addToCompletedTasks(completedTask);
					taskQueue.removeTaskFromQueue(task);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Request processed");
		}
	}

	public TaskQueue getTaskQueue() {
		return taskQueue;
	}
}
