import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Stein-Otto Svorstøl on 3/22/14.
 * Main program for the client application
 */
public class Client {
    private static String username = null;
    protected static Socket socket = null;
    private static PrintWriter input;
    private static Scanner scanner;
    private static JSONParser parser;
    private static MessageListener messageListener;

    public static void main(String args[]){
        scanner = new Scanner(System.in);
        try {
            login();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String inputLine = null;
        while (true){
            if (scanner.hasNextLine())
                inputLine = scanner.nextLine();
            if (inputLine.equals("logout")){
                logout();
            } else
                sendMessage(inputLine);
        }
    }

    private static void login() throws IOException {
        System.out.println("Hostname: ");
        String host = scanner.nextLine();
        socket = new Socket(host, 9000); // Connect
        messageListener = new MessageListener();
        input = new PrintWriter(socket.getOutputStream(), true); // Handles what we send to the server
        System.out.println("Enter a username: ");
        Client.username = scanner.nextLine();
        JSONObject login = new JSONObject();
        login.put("request", "login");
        login.put("username", username);
        send(login);
    }

    private static void sendMessage(String message) {
        JSONObject request = new JSONObject();
        request.put("request", "message");
        request.put("username", Client.username);
        request.put("message", message);
        send(request);
    }

    private static void logout(){
        JSONObject logout = new JSONObject();
        logout.put("request", "logout");
        send(logout);
        JSONObject response = MessageListener.response;
        if (response.get("error") != null)
            System.out.println((String) response.get("error"));
        else{
            System.out.println("You're logged out.");
            System.exit(0);
        }
    }

    private static void send(JSONObject request){
        input.println(request.toJSONString());
        JSONObject response = MessageListener.response; // Parse what we get back
    }





}
