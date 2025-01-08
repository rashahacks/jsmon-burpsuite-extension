import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class JsmonContextMenu implements ContextMenuItemsProvider {

    private final MontoyaApi api;
    private final JsmonBurpExtension extension;
    private final JTextArea logArea;



    public JsmonContextMenu(JsmonBurpExtension extension) {
        this.extension = extension;
        this.api = extension.getApi();
        this.logArea = extension.getLogArea();
    }


    public List<Component> provideMenuItems(ContextMenuEvent event) {

        List<HttpRequestResponse> selectedMessages = event.selectedRequestResponses();
        if (selectedMessages.isEmpty()) {
            return null;
        }
        JButton sendToJsmon = new JButton("Send");
        sendToJsmon.addActionListener(e -> handleSelectedMessage(selectedMessages));

        return List.of(sendToJsmon);

    }

    private void handleSelectedMessage(List<HttpRequestResponse> selectedMessages) {
           String apiKey = extension.getApiKey();
           String wkspId = extension.getWkspId();
       try {
           String backendEndpoint = String.format("https://api.jsmon.sh/api/v2/uploadUrl?wkspId=%s", wkspId);
           HttpClient client = HttpClient.newHttpClient();

           for (HttpRequestResponse message : selectedMessages) {
               String url = message.request().url().toString();

               try {
                   String jsonPayload = String.format("{\"url\": \"%s\"}", url);
                   logArea.append(jsonPayload+"\n");
                   HttpRequest request = HttpRequest.newBuilder()
                           .uri(URI.create(backendEndpoint))
                           .header("Content-Type", "application/json")
                           .header("X-Jsmon-Key", apiKey)
                           .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                           .build();

                   HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                   //logArea.append(apiKey +" -> wkspId " + wkspId + " -> " +request);
                   if (response.statusCode() == 200) {
                       logArea.append(url + " sent successfully!! Response: " + response.statusCode() + "\n");
                   } else {
                       logArea.append("Failed to send " + url + "! Response code: " + response.statusCode() + "\n");
                   }
               } catch (Exception e) {
                   logArea.append("Error proceesing " + url + " \n");
               }

           }
       }catch(Exception e){
           logArea.append("Error Sending Url!!");
       }
    }
}
