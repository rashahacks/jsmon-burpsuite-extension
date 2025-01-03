import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.requests.HttpRequest;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class JsmonHttpHandler implements HttpHandler {

    private final JsmonBurpExtension extension;
    private final MontoyaApi api;

    public JsmonHttpHandler(JsmonBurpExtension extension) {
        this.extension = extension;
        api = extension.getApi();

    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent httpRequestToBeSent) {

        String url = httpRequestToBeSent.url().toString();
        //System.out.println("URLS: "+url);
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
        if (apiKey == null || apiKey.trim().isEmpty()) {
            api.logging().logToOutput("API key is not set. Please configure your API key.");
            return;
        }
        try {
            String backendEndpoint = String.format("https://api-dev.jsmon.sh/api/v2/uploadUrl?wkspId=%s", wkspId);
            api.logging().logToOutput("Sending URL: " + url + " to backend");
            api.logging().logToOutput(url + " - " + apiKey + " - " + wkspId);
            URL backendUrl = new URL(backendEndpoint);
            HttpURLConnection connection = (HttpURLConnection) backendUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("X-Jsmon-Key",apiKey);
            connection.setDoOutput(true);

            String jsonPayload = String.format("{\"url\": \"%s\"}", url);


            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonPayload.getBytes());
                os.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                api.logging().logToOutput("URL sent successfully!");
            } else {
                api.logging().logToOutput("Failed to send URL. Response code: " + responseCode);
            }

            connection.disconnect();

        }catch(Exception e){
            api.logging().logToOutput("Error sending URL: " + e);
        }
    }
}
