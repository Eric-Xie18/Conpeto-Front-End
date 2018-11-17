package com.conpeto.nullpointer.conpeto;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpClient {
    private URL url = null;
    private String method = null;

    HttpClient(String url, String method){
        try {
              this.url = new URL(url);
              this.method = method;
        } catch(MalformedURLException E){
           Log.e("URL malformed","in HttpClient constructor");
        }
        }

    String sendRequest(String body){
        String responseContent = null;

        if("GET".equals(method)) {
            try {
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                int responseCode = client.getResponseCode();
                System.out.println("\nSending " + method + " request to URL : " + url);
                System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(client.getInputStream()));
                String inputLine;

                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                responseContent = response.toString();

            } catch (IOException E) { }
        }

        else{
            try {
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                client.setRequestProperty("Content-Type", "application/json");
                client.setRequestMethod(method);
                client.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(client.getOutputStream());
                System.out.println("before Write\n");
                wr.writeBytes(body);
                wr.flush();
                wr.close();

                int responseCode = client.getResponseCode();
                System.out.println("\nSending " + method + " request to URL : " + url);
                System.out.println("Body : " + body);
                System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(client.getInputStream()));
                String inputLine;

                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                responseContent = response.toString();

            } catch (IOException E) {
            }
        }
        System.out.println("Server response is: " + responseContent);
      return responseContent;
    }


}
