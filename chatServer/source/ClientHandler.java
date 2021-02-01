import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private String name;
    private ArrayList<ClientHandler> clients;

    public ClientHandler(Socket clientSocket, ArrayList<ClientHandler> clients) throws IOException {
        this.client = clientSocket;
        this.clients = clients;
    }
    @Override
    public void run() {
        try {
            name = Server.getRandonName();
            System.out.println("[SERVER] The name of new client is " + name);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(),true);
            out.println("Please enter command 'name' to get your name!");
            while (true) {
                String request = in.readLine();
                if (request == null) break;
                if (request.contains("name")) {
                    out.println("Server says: Your name is " + name);
                } else if (request.startsWith("say")) {
                    int firstSpace = request.indexOf(" ");
                    if (firstSpace != -1) {
                        outToAll(name + " says: " + request.substring(firstSpace+1));
                    }
                } else {
                    out.println();
                }
            }
            System.out.println("[SERVER] Client " + name + " is closing...");
        } catch (IOException e) {
//            e.printStackTrace();
            clients.remove(client);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void outToAll(String msg) {
        for (ClientHandler aClient : clients) {
            aClient.out.println(msg);
        }
    }
}
