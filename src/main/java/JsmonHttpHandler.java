import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.*;


import javax.swing.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;



public class JsmonHttpHandler implements HttpHandler {



    private final JsmonBurpExtension extension;
    private final MontoyaApi api;
    private final JTextArea logArea;
    public JsmonHttpHandler(JsmonBurpExtension extension) {
        this.extension = extension;
        api = extension.getApi();
        logArea = extension.getLogArea();

    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent httpRequestToBeSent) {

        String url = httpRequestToBeSent.url().toString();

        if(url.endsWith("js")){
            System.out.println(url);
            Thread.startVirtualThread(() -> sendToBackend(url, extension.getApiKey(), extension.getWkspId()));
        }

        return RequestToBeSentAction.continueWith(httpRequestToBeSent);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived httpResponseReceived) {
        return null;
    }

    private void sendToBackend(String url, String apiKey, String wkspId) {

        boolean allowAutomateScan = extension.getAutomaticScan();

        if(allowAutomateScan) {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                api.logging().logToOutput("API key is not set. Please configure your API key.");
                return;
            }
            try {

                String backendEndpoint = String.format("https://api.jsmon.sh/api/v2/uploadUrl?wkspId=%s", wkspId);
                HttpClient client = HttpClient.newHttpClient();


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


            } catch (Exception e) {
                api.logging().logToOutput("Error sending URL: " + e);
            }
        }
    }

}
