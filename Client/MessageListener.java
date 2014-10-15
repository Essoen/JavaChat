import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Stein-Otto Svorstøl on 3/22/14.
 * Controls the response from the server
 * Prints any messages to the user
 */
public class MessageListener implements Runnable {
    Thread t;
    protected static JSONObject response;
    JSONParser parser = new JSONParser();

    public MessageListener(){
        t = new Thread(this, "MessageListener");
        t.start();
    }

    @Override
    public void run() {
        while(true) {
            try {
                response = (JSONObject) parser.parse(new BufferedReader(new InputStreamReader(Client.socket.getInputStream())).readLine());
                if (((String) response.get("response")).equals("message")) {
                    System.out.println((String) response.get("username")+": " + (String) response.get("message"));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
