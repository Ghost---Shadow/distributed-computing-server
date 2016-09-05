package org.distributed.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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

	// Maximum number of clients to handle at a time
	private final static int NUMBER_OF_CLIENTS = 10;
	private ExecutorService executorService;

	private static Server server;
	private static TaskQueue taskQueue;

	private static String REQUEST = "request";
	private static String REPLY = "reply";

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
		int serverPort = 5555;
		try {
			System.out.println("Starting Server");
			serverSocket = new ServerSocket(serverPort);

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
			System.out.println("Error starting Server on " + serverPort);
			e.printStackTrace();
		}
	}

	// Call the method when you want to stop your server
	private void stopServer() {
		// Stop the executor service.
		executorService.shutdownNow();
		try {
			// Stop accepting requests.
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
				Task task = new Task(TaskQueue.PING,-1,null,null,null);
				objectOut.writeObject(task);
				Task t1 = (Task) objectIn.readObject();
				t1.print();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			//temp();
		}
		
		
		public void temp(){
			System.out.println("-------------------------------------------------");
			try {
				//DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				DataInputStream in = new DataInputStream(socket.getInputStream());
				ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());

				if (in.readUTF().equals(REQUEST)) {
					System.out.println("Got request from "+socket.getInetAddress());
					//Task task = taskQueue.getNextTask();
					Task task = new Task(TaskQueue.PING,-1,null,null,null);
					System.out.println("Sending task");
					objectOut.writeObject(task);
					objectOut.flush();
				} else if (in.readUTF().equals(REPLY)) {
					System.out.println("Got reply from "+socket.getInetAddress());
					Task task = (Task) objectIn.readObject();
					System.out.println(task.id+" "+task.type);
					if (task.id != -1)
						taskQueue.removeTaskFromQueue(task);
					else
						System.out.println("Ping from client "+socket.getInetAddress());
				}				
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("-------------------------------------------------");
		}
	}
	
	

	public TaskQueue getTaskQueue() {
		return taskQueue;
	}
}
