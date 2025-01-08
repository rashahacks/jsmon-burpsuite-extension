import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.*;
import java.util.prefs.Preferences;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.menu.MenuItem;
import burp.api.montoya.http.message.HttpRequestResponse;

import javax.swing.*;
import java.awt.*;



public class JsmonBurpExtension implements BurpExtension{
    private MontoyaApi api;
    private String apiKey = "";
    private String wkspId = "";
    private boolean automtaicScan = true;
    @Override
    public void initialize(MontoyaApi api) {
        this.api = api;
        api.extension().setName("Jsmon");
        api.logging().logToOutput("Extension initialized  !!");

        JPanel mainPanel = createMainPanel();

        api.userInterface().registerSuiteTab("Jsmon", mainPanel);
        JsmonHttpHandler handler = new JsmonHttpHandler(this);
        api.http().registerHttpHandler(handler);

        JsmonContextMenu jsmonContextMenu = new JsmonContextMenu(this);
        api.userInterface().registerContextMenuItemsProvider(jsmonContextMenu);

    }



    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(createInputPanel());
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Preferences prefs = Preferences.userRoot().node("com.jsmon.extension");

        String storedApiKey = prefs.get("apiKey", "");
        String storedWkspId = prefs.get("wkspId", "");

        JLabel label = new JLabel("API Key:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(label, gbc);

        JTextField apiKeyField = new JTextField(storedApiKey,30);
        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(apiKeyField, gbc);

        JLabel labelWksp = new JLabel("Workspace ID:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(labelWksp, gbc);

        JTextField wkspIdField = new JTextField(storedWkspId,30);
        gbc.gridx = 1;
        gbc.gridy = 1;
        inputPanel.add(wkspIdField, gbc);

        JLabel jsUrlScanLabel = new JLabel("Automatic Scan:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 4;
        inputPanel.add(jsUrlScanLabel,gbc);

        JRadioButton yesRadioButton = new JRadioButton("Yes");
        JRadioButton noRadioButton = new JRadioButton("No");

        ButtonGroup group = new ButtonGroup();
        group.add(yesRadioButton);
        group.add(noRadioButton);


        yesRadioButton.setSelected(true);


        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.add(yesRadioButton);
        radioPanel.add(noRadioButton);

        noRadioButton.addActionListener(e -> {
            automtaicScan = false;
        });
        yesRadioButton.addActionListener(e -> {
            automtaicScan = true;
        });


        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        inputPanel.add(radioPanel, gbc);

        JButton saveButton = new JButton("Save");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth=1;
        gbc.anchor = GridBagConstraints.CENTER;
        saveButton.addActionListener(e -> {
            String apikey = apiKeyField.getText().trim();
            String wkspid = wkspIdField.getText().trim();

            if (!apikey.isEmpty() && !wkspid.isEmpty()) {
                apiKey = apikey;
                wkspId = wkspid;
                prefs.put("apiKey", apikey);
                prefs.put("wkspId", wkspid);
                logArea.append("APIKEY added "+apikey+"\n");
                logArea.append("Workspace Id added "+wkspid+"\n");
                JOptionPane.showMessageDialog(null, "API key saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {

                JOptionPane.showMessageDialog(inputPanel, "Both fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        inputPanel.add(saveButton, gbc);

        JLabel manualUrl = new JLabel("Enter URLs (line seperated)");
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(manualUrl, gbc);

        JTextArea manualUrlInput = new JTextArea(8,60);
        JScrollPane scrollForUrl = new JScrollPane(manualUrlInput);
        gbc.gridx = 0;
        gbc.gridy=6;
        gbc.anchor = GridBagConstraints.CENTER;
        inputPanel.add(scrollForUrl,gbc);

        JButton submitButton = new JButton("Submit");
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth=1;
        gbc.anchor = GridBagConstraints.CENTER;
        inputPanel.add(submitButton,gbc);

        submitButton.addActionListener(e -> {
            String allUrls[] = manualUrlInput.getText().trim().split("\n");
            //logArea.append(allUrls[0]);
            JsmonManualUrlProcessing.processManualUrls(allUrls, wkspId, apiKey, logArea);
        });

        JLabel logLabel = new JLabel("Logs:");
        gbc.gridx = 0;
        gbc.gridy =  10;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(logLabel, gbc);

        JTextArea logArea = new JTextArea(8, 90);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        inputPanel.add(scrollPane, gbc);


        this.logArea = logArea;

        return inputPanel;
    }

    private JTextArea logArea;


    public String getApiKey() {
        return apiKey;
    }

    public boolean getAutomaticScan() {
        return automtaicScan;
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


