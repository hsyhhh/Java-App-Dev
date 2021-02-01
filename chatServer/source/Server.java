import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final String[] names = {"Mike", "John", "Amy", "Tom", "Jack", "William"};
    private static final String[] adjs = {"the gentle", "the handsome", "the alert", "the urbane",
                                          "the frank", "the cool", "the humorous", "the generous"};
    private static final int PORT = 8880;
    private static final int MAX_CLIENT_NUM = 20;

    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENT_NUM);

    public static void main(String[] args) throws IOException {
        try (ServerSocket s = new ServerSocket(PORT)) {
            while (true) {
                System.out.println("[SERVER] Waiting for client connection...");
                Socket client = s.accept();
                System.out.println(client);
                System.out.println("[SERVER] Connected to client");
                ClientHandler clientThread = new ClientHandler(client, clients);
                clients.add(clientThread);

                pool.execute(clientThread);
            }
        }
    }

    public static String getRandonName() {
        String name = names[(int)(Math.random() * names.length)];
        String adj = adjs[(int)(Math.random() * adjs.length)];
        return name + " " + adj;
    }
}
