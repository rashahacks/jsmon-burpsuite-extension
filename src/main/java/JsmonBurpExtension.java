import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.*;

import javax.swing.*;
import java.awt.*;



public class JsmonBurpExtension implements BurpExtension{
    private MontoyaApi api;
    private String apiKey = "";
    private String wkspId = "";
    @Override
    public void initialize(MontoyaApi api) {
        this.api = api;
        api.extension().setName("Jsmon");
        api.logging().logToOutput("Extension initialized: JSMON ==> 1355");

        JPanel mainPanel = createMainPanel();

        api.userInterface().registerSuiteTab("JSMON", mainPanel);

        JsmonHttpHandler handler = new JsmonHttpHandler(this);
        api.http().registerHttpHandler(handler);


    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createInputPanel(), BorderLayout.NORTH);
        return panel;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new FlowLayout());


        JLabel label = new JLabel("API Key:");
        inputPanel.add(label);

        JTextField apiKeyField = new JTextField(20);
        inputPanel.add(apiKeyField);

        JLabel labelWksp = new JLabel("Workspace ID:");
        inputPanel.add(labelWksp);

        JTextField wkspIdField = new JTextField(20);
        inputPanel.add(wkspIdField);


        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String apikey = apiKeyField.getText().trim();
            String wkspid = wkspIdField.getText().trim();


            if (!apikey.isEmpty() && !wkspid.isEmpty()) {
                apiKey = apikey;
                wkspId = wkspid;
                JOptionPane.showMessageDialog(null, "API key saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Show a warning message if either field is empty
                JOptionPane.showMessageDialog(inputPanel, "Both fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        });

        inputPanel.add(saveButton);

        return inputPanel;
    }

//    private void saveFunc(String apiKeyInput) {
//        if (apiKeyInput == null || apiKeyInput.trim().isEmpty()) {
//            api.logging().logToOutput("API key is empty. Please enter a valid API key.");
//            JOptionPane.showMessageDialog(null, "API key cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
//        } else {
//            this.apiKey = apiKeyInput.trim();
//            api.logging().logToOutput("API key saved: " + apiKey);
//            JOptionPane.showMessageDialog(null, "API key saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
//        }
//    }

    public String getApiKey() {
        return apiKey;
    }

    public String getWkspId(){
        return wkspId;
    }

    public MontoyaApi getApi() {
        return api;
    }
}
