import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Stein-Otto Svorstøl on 3/22/14.
 */
public class Server {
    private static ServerSocket server;
    public static ArrayList<ClientHandler> clients;

    public static void main(String args[]){
        clients = new ArrayList<ClientHandler>();
        try {server = new ServerSocket(9000); }
        catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            ClientHandler clientHandler;
            System.out.println(clients);
            try {
                Socket c = server.accept();
                if (c != null)
                    clientHandler = new ClientHandler(c); // Accept connection and create a thread
                else
                    continue;
                if (clientHandler != null && !clients.contains(clientHandler))
                    clients.add(clientHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean uniqueUsername(String username){
        if(clients.size() == 0)
            return true;
        for (int i = 0; i < clients.size(); i++){
            if (clients.get(i).username == username)
                return false;
        }
        return true;
    }

    public static void sendMessage(JSONObject message){
        for (int i = 0; i < clients.size(); i++){
            clients.get(i).send(message);
        }
    }
}