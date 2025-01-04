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
        api.logging().logToOutput("Extension initialized!!");

        JPanel mainPanel = createMainPanel();

        api.userInterface().registerSuiteTab("Jsmon", mainPanel);

        JsmonHttpHandler handler = new JsmonHttpHandler(this);
        api.http().registerHttpHandler(handler);


    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createInputPanel(), BorderLayout.NORTH);
        return panel;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel label = new JLabel("API Key:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(label, gbc);

        JTextField apiKeyField = new JTextField(30);
        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(apiKeyField, gbc);

        JLabel labelWksp = new JLabel("Workspace ID:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(labelWksp, gbc);

        JTextField wkspIdField = new JTextField(30);
        gbc.gridx = 1;
        gbc.gridy = 1;
        inputPanel.add(wkspIdField, gbc);

        JButton saveButton = new JButton("Save");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        saveButton.addActionListener(e -> {
            String apikey = apiKeyField.getText().trim();
            String wkspid = wkspIdField.getText().trim();

            if (!apikey.isEmpty() && !wkspid.isEmpty()) {
                apiKey = apikey;
                wkspId = wkspid;
                JOptionPane.showMessageDialog(null, "API key saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {

                JOptionPane.showMessageDialog(inputPanel, "Both fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        inputPanel.add(saveButton, gbc);

        JLabel logLabel = new JLabel("Logs:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(logLabel, gbc);

        JTextArea logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        inputPanel.add(scrollPane, gbc);


        this.logArea = logArea;

        return inputPanel;
    }

    private JTextArea logArea;




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

    public JTextArea getLogArea(){
        return logArea;
    }
}
