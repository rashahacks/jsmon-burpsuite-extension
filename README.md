# JSMON Burpsuite Extension

Built using the MONTOYA API, this extension will send the URLs ending with '.js' to our backend server. It will allow users to easily send JavaScript file URLs from intercepted HTTP requests to directly hit the /uploadUrl endpoint with WKSPID in the query, user APIKEY in the headers with the url in the body.

## Installation

 1. Clone the repo
  
  ```bash
  git clone https://github.com/your-username/jsmon-burp-extension.git
  ```
2. In burpsuite extension, click on add extension and select JAVA as extension type. Upload the '.jar' file generated from the cloned repo.
3. In the extension add your apikey and wkspId, click on save and then your are ready with the setup.

Note : Make sure that both the API Key and WKSPID are correctly set up to allow the extension to send JavaScript URLs to the backend.
   
