import burp.api.montoya.MontoyaApi;

import javax.swing.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;

public class JsmonManualUrlProcessing {


    public static void processManualUrls(String urls[], String wkspId, String apiKey, JTextArea logArea){

        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {


            @Override
            protected Void doInBackground() throws Exception {

                String backendEndpoint = String.format("https://api.jsmon.sh/api/v2/uploadUrl?wkspId=%s", wkspId);

                HttpClient client = HttpClient.newHttpClient();

                for (int i = 0; i < urls.length; i++) {
                    String url = urls[i];
                    try {

                        String jsonPayload = String.format("{\"url\": \"%s\"}", url);
                        publish(jsonPayload + "\n");


                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(backendEndpoint))
                                .header("Content-Type", "application/json")
                                .header("X-Jsmon-Key", apiKey)
                                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                                .build();

                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


                        if (response.statusCode() == 200) {
                           publish(url + " sent successfully!! Response: " + response.statusCode() + "\n");
                        } else {
                           publish("Failed to send " + url + "! Response code: " + response.statusCode() + "\n");
                        }

                    } catch (Exception e) {
                        publish("Error proceesing " + url + " ");
                    }

                }

                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                for (String chunk : chunks) {
                    logArea.append(chunk);
                }
            }

            @Override
            protected void done() {

                try {
                    get();
                    logArea.append("All URLs successfully Uploaded !\n");
                } catch (InterruptedException | ExecutionException e) {
                    logArea.append("Error processing URLs in the background.\n");
                }
            }
        };

        worker.execute();

        }
    }


