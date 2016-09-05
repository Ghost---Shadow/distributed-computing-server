import java.io.IOException;

import org.distributed.server.Server;

public class Main {

	public static void main(String[] args) throws IOException {
		Server server = new Server();
		server.runServer();
	}

}
