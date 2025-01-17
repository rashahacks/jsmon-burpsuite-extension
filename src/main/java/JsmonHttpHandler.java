import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.HttpHeader;
import java.net.http.HttpRequest;
import burp.api.montoya.*;



import javax.swing.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.*;


public class JsmonHttpHandler implements HttpHandler {
    private final Semaphore semaphore;
    private final JsmonBurpExtension extension;
    private final MontoyaApi api;
    private final JTextArea logArea;
    public JsmonHttpHandler(JsmonBurpExtension extension) {
        this.extension = extension;
        api = extension.getApi();
        logArea = extension.getLogArea();
        this.semaphore = new Semaphore(5);

    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent httpRequestToBeSent) {

        return RequestToBeSentAction.continueWith(httpRequestToBeSent);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived httpResponseReceived) {
        ArrayList<String> scopeVariable = extension.getScopeVariable();
        burp.api.montoya.http.message.requests.HttpRequest request =  httpResponseReceived.initiatingRequest();

          String url = request.url();
          HttpHeader contentType = httpResponseReceived.header("Content-Type");
          boolean containsScopeWord = scopeVariable.stream().anyMatch(url::contains);
          if( (url.contains(".js")) || (contentType!=null && contentType.value().contains("javascript"))){

              if(containsScopeWord) {
                  logArea.append(url+"\n");
                  Thread.startVirtualThread(() ->
                  {
                      try {
                          semaphore.acquire();
                          sendToBackend(url, extension.getApiKey(), extension.getWkspId());
                      } catch (InterruptedException e) {
                          Thread.currentThread().interrupt();
                          logArea.append("Exception Occured At VT block");
                      } finally {

                          semaphore.release();
                      }
                  });
              }
            }


        return ResponseReceivedAction.continueWith(httpResponseReceived);
    }

    private void sendToBackend(String url, String apiKey, String wkspId) {

        boolean allowAutomateScan = extension.getAutomaticScan();
       // api.logging().logToOutput(allowAutomateScan?"YES":"NO");


        if(allowAutomateScan) {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                api.logging().logToOutput("API key is not set. Please configure your API key.\n");
                return;
            }
            try {

                String backendEndpoint = String.format("https://api.jsmon.sh/api/v2/uploadUrl?wkspId=%s", wkspId);
                HttpClient client = HttpClient.newHttpClient();


                String jsonPayload = String.format("{\"url\": \"%s\"}", url);
                long currentTime = System.currentTimeMillis()/1000;
                logArea.append(jsonPayload + " "+ currentTime + "\n");


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
