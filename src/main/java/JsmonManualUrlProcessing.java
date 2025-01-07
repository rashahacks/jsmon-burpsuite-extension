import burp.api.montoya.MontoyaApi;

import javax.swing.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JsmonManualUrlProcessing {


    public static void processManualUrls(String urls[], String wkspId, String apiKey, JTextArea logArea){


            String backendEndpoint = String.format("https://api.jsmon.sh/api/v2/uploadUrl?wkspId=%s", wkspId);

            HttpClient client = HttpClient.newHttpClient();

            for (int i = 0; i < urls.length; i++) {
                String url = urls[i];
                try {

                String jsonPayload = String.format("{\"url\": \"%s\"}", url);
                logArea.append(jsonPayload + "\n");


                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(backendEndpoint))
                        .header("Content-Type", "application/json")
                        .header("X-Jsmon-Key", apiKey)
                        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


                if (response.statusCode() == 200) {
                    logArea.append(url + " sent successfully!! Response: " + response.statusCode() + "\n");
                } else {
                    logArea.append("Failed to send " + url + "! Response code: " + response.statusCode() + "\n");
                }

            } catch(Exception e){
                logArea.append("Error proceesing " + url + " ");
            }
        }
    }

}
