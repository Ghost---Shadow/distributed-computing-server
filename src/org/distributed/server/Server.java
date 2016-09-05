package org.distributed.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	private ServerSocket serverSocket;
	
	// Maximum number of clients to handle at a time
	private final static int NUMBER_OF_CLIENTS = 10;
	private ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_CLIENTS);	

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
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				DataInputStream in = new DataInputStream(socket.getInputStream());
				out.writeUTF("From server");
				System.out.println(in.readUTF());
				socket.close();
			} catch (IOException ioe) {
				System.out.println("Error closing client connection");
			}
		}
	}
}
