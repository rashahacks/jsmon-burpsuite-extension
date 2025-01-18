# Jsmon Burpsuite Extension

The Burpsuite extension helps to send the JS files coming in the proxy directly to the Jsmon. You can see the uploaded JS URLs, the corresponding JS Intelligence results and Keys & Secrets results in the app (<a href="https://jsmon.sh">Jsmon.sh</a>).

## Installation Ways:

### From the Releases

1. Go to <a href="https://github.com/rashahacks/jsmon-burpsuite-extension/releases">releases</a> and download the .jar file.
![image](https://github.com/user-attachments/assets/c342d215-3efc-4596-86bf-c10f971eef2d)
2. Open burpsuite, go to Extensions, click on Add button, load the .jar file and click on Next.
![image](https://github.com/user-attachments/assets/5355c260-4303-49d0-9b55-3afbb771e510)

### From out/artifacts folder

1. Clone the repository
```
git clone https://github.com/rashahacks/jsmon-burpsuite-extension
```
2. Add .jar file in out/artifacts folder to burpsuite extensions
![image](https://github.com/user-attachments/assets/5355c260-4303-49d0-9b55-3afbb771e510)

## Usage
Add the correct workspace ID (wkspId) and the API key from your account into Jsmon.
<img width="1484" alt="Jsmon API" src="https://github.com/user-attachments/assets/d168a15b-b172-4d5e-b4b4-dffb7b52c9fe" />


![image](https://github.com/user-attachments/assets/30b090f4-d41b-43d5-8015-a222a037477d)

1. Automate Scan: Allows to send the JS URLs coming in HTTP history automatically to jsmon.
2. Manually submit URLs. Copy the JS URLs, put in all the URLs into the "Enter URLs" prompt box and click on Submit button.
3. Right click and send to Jsmon: You can select multiple JS URLs and send to Jsmon by right click and hover to Extensions button.
