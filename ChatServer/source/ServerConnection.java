import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerConnection implements Runnable{
    private Socket server;
    private BufferedReader in;

    public ServerConnection(Socket s) {
        this.server = s;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            while (true) {
                String serverResponse = in.readLine();
                if (serverResponse == null) break;
                System.out.println(serverResponse);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
