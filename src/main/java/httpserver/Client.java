package httpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Client class to connect with the Server.
 */
public class Client {

    //The server endpoint url path
    private static String path = "http://localhost:8080/server/request";

    /**
     * Get the server path.
     * @return the path.
     */
    public static String getPath() {
        return path;
    }

    /**
     * Set the path.
     * @param path The path.
     */
    public static void setPath(String path) {
        Client.path = path;
    }

    /**
     * Takes in a JSON string request, sends it to the server and returns the response.
     * @param request The JSON string.
     * @return The JSON reply from the server.
     */
    public String makeRequest(String request) {
        HttpURLConnection con = null;
        try {
            URL url = new URL(path);
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.addRequestProperty("request", request);
            int responseCode = con.getResponseCode();

            // read from buffer
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            StringBuffer stringBuffer = new StringBuffer();

            while ((line = br.readLine()) != null) {
                stringBuffer.append(line);
            }

            br.close();

            return "Response Code: " + responseCode + ", contents: " + stringBuffer.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (con != null) {
                con.disconnect();
            }
        }

        return "ClientError";

    }

}
