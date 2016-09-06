import java.io.IOException;
import java.util.ArrayList;

import org.distributed.serializable.Task;
import org.distributed.server.Server;
import org.distributed.server.TaskQueue;

public class Main {

	public static void main(String[] args) throws IOException {
		Server server = Server.getInstance();
		server.runServer();

		TaskQueue taskQueue = server.getTaskQueue();
		float[][] a = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
		float[][] b = { { 9, 8, 7 }, { 6, 5, 4 }, { 3, 2, 1 } };
		float[][] c = new float[3][3];
		taskQueue.addTask(TaskQueue.ADD_MATRICES, a, b, c);
		taskQueue.addTask(TaskQueue.MULTIPLY_MATRICES, a, b, c);
		taskQueue.addTask(TaskQueue.DOT_PRODUCT, a, b, c);
		taskQueue.waitForFinish();
		try {
			ArrayList<Task> tasks = taskQueue.takeCompletedTasks();
			System.out.println("Completed tasks size" + tasks.size());
			for (Task t : tasks) {
				t.print();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
