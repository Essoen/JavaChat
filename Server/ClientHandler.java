import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Stein-Otto Svorstøl on 3/22/14.
 * Handles a single client on the server
 */
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private Thread t;
    private PrintWriter toClient;
    private BufferedReader fromClient;
    private String inputLine;

    public String username = null;

    public ClientHandler(Socket cs) {
        clientSocket = cs;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        try {
            toClient = new PrintWriter(clientSocket.getOutputStream(), true); // Where we're gonna send the data
            fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // Read request
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true){
            try { inputLine = fromClient.readLine();}
            catch (IOException e) {} // If it causes IO then the problem was that there was not data, so just don't do shit
            if(inputLine == null) { // Did not catch exception - there is a command.
                continue;
            }
            JSONObject request = parseString(inputLine);
            if (request.get("request").equals("login"))
                login(request);
            else if(request.get("request").equals("message"))
                message(request);
            else if(request.get("request").equals("logout"))
                logout(request);
        }
    }

    public void send(JSONObject response){
        toClient.println(response.toJSONString());
    }


    private void login(JSONObject request){
        JSONObject response = new JSONObject();
        response.put("response", "login");
        if ((String) request.get("username") == null) {
            response.put("error", "Invalid username!");
        }else if (!Server.uniqueUsername((String) request.get("username"))){
            response.put("error", "Username already taken!");
        }
        this.username = (String) request.get("username");
        response.put("username", (String) request.get("username"));
        send(response);
    }

    private void logout(JSONObject request){
        JSONObject response = new JSONObject();
        response.put("response", "logout");
        if (username == null){
            response.put("error", "Not logged in!");
        } else {
            response.put("username", username);
            Server.clients.remove(this);
            t.interrupt();
        }
        send(response);
    }

    private void message(JSONObject request){
        JSONObject response = new JSONObject();
        response.put("response", "message");
        if (username == null) {
            response.put("error", "You are not logged in!");
            send(response);
        } else{
            request.remove("request");
            request.put("response", "message");
            Server.sendMessage(request);
        }
    }


    private JSONObject parseString(String s){
        JSONParser p = new JSONParser();
        try {
            return (JSONObject) p.parse(s);
        } catch (ParseException e) {
            return null;
        }
    }
}