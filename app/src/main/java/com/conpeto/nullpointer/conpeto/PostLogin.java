package com.conpeto.nullpointer.conpeto;


import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.conpeto.nullpointer.conpeto.Service.LocationService;
import com.conpeto.nullpointer.conpeto.Service.MyFirebaseMessagingService;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
;import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PostLogin extends AppCompatActivity  {

private static String radius = "5";
private String FCMKey = null;
private String userID = null;
private boolean getKey = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_login);
        final Button bCreateGroup = findViewById(R.id.create_Group);
        final Button bViewGroup = findViewById(R.id.view_groups);
        final Button bJoinGroup = findViewById(R.id.search_Group);
        final TextView radiusDislay = findViewById(R.id.textView2);
        Button bLogOut = findViewById(R.id.log_out_button);
        userID = getIntent().getStringExtra("user_ID");
        Spinner dropdown = findViewById(R.id.spinner1);
        String[] items = new String[]{"Last Saved Setting", "5 KM", "10 KM", "15 KM","30 KM","Any"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                               @Override
                                               public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                                   switch (i) {
                                                       case 0:
                                                           // Whatever you want to happen when the first item gets selected
                                                           radius = radius;
                                                           radiusDislay.setText("The current filter distance is" + radius+ " KM");
                                                           break;
                                                       case 1:
                                                           // Whatever you want to happen when the second item gets selected
                                                           radius = "5";
                                                           radiusDislay.setText("The current filter distance is" + radius + " KM");
                                                           break;
                                                       case 2:
                                                           // Whatever you want to happen when the thrid item gets selected
                                                           radius = "10";
                                                           radiusDislay.setText("The current filter distance is" + radius + " KM");
                                                           break;

                                                       case 3:
                                                           // Whatever you want to happen when the thrid item gets selected
                                                           radius = "15";
                                                           radiusDislay.setText("The current filter distance is" + radius + " KM");
                                                           break;

                                                       case 4:
                                                           // Whatever you want to happen when the thrid item gets selected
                                                           radius = "30";
                                                           radiusDislay.setText("The current filter distance is" + radius + " KM");
                                                           break;

                                                           case 5:
                                                           // Whatever you want to happen when the thrid item gets selected
                                                           radius = "Any";
                                                               radiusDislay.setText("The geo filter is turned off");
                                                           break;

                                                   }
                                               };
                                               @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                          radius = radius;
            };


                                           });

      FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w("Firebase instance", "getInstanceId failed", task.getException());
                    return;
                }
                // Get new Instance ID token
                String token = task.getResult().getToken();
                System.out.println("Firebase Token is " +token);
                FCMKey = token;
                getKey = true;

            }});

        bCreateGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent createGroup = new Intent(PostLogin.this,CreateGroup.class);
                createGroup.putExtra("user_ID",getIntent().getStringExtra("user_ID"));
                PostLogin.this.startActivity(createGroup);
            }
        });

        bJoinGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent searchGroup = new Intent(PostLogin.this,SearchCategories.class);
                searchGroup.putExtra("user_ID",getIntent().getStringExtra("user_ID"));
                PostLogin.this.startActivity(searchGroup);
            }
        });

        bViewGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent viewGroup = new Intent(PostLogin.this,ViewGroup.class);
                viewGroup.putExtra("user_ID",getIntent().getStringExtra("user_ID"));
                PostLogin.this.startActivity(viewGroup);
            }
        });

        bLogOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                LoginManager.getInstance().logOut();
                Intent login = new Intent(PostLogin.this,LoginActivity.class);
                PostLogin.this.startActivity(login);
                finish();
            }
        });

        Intent serviceIntent = new Intent(this, LocationService.class);
        startService(serviceIntent);
        AddFCMKey addFCMKey = new AddFCMKey();
        addFCMKey.execute();
    }
    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onBackPressed() {

    }

    public static String gerRadius(){
        return radius;
        }

     public static void modifyRadius(String newRadius){
        radius = newRadius;
        }

    private class AddFCMKey extends AsyncTask<Void, Integer, Integer> {
        protected Integer doInBackground(Void...params) {
              while(!getKey){
            }

            String urlString = "http://null-pointers.herokuapp.com/user";
            StringBuffer response = new StringBuffer();
            int responseCode = 400;
            try {
                URL url = new URL(urlString);
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("PUT");
                client.setRequestProperty("Content-Type", "application/json");
                client.setDoOutput(true);

                StringBuilder body = new StringBuilder();
                body.append("{\"id\":");
                body.append("\"");
                body.append(userID);
                body.append("\"");
                body.append(", ");
                body.append("\"fcmKey\":");
                body.append("\"");
                body.append(FCMKey);
                body.append("\"");
                body.append("}");

                String userInfo = body.toString();
                Log.d("Body is", userInfo);

                // Send post request
                DataOutputStream wr = new DataOutputStream(client.getOutputStream());
                System.out.println("before Write\n");
                wr.writeBytes(userInfo);
                wr.flush();
                wr.close();

                responseCode = client.getResponseCode();
                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Post body : " + userInfo);
                System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(client.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                if(responseCode!=200){
                    Toast.makeText(PostLogin.this, "Notification is not able to synchronize with server",
                            Toast.LENGTH_LONG).show();
                }
            } catch (MalformedURLException E) {
                Log.e("URL", "The URL is not correct");
            } catch (IOException E) {
            }


            System.out.println("onPostExecute response: " + response.toString());

            return responseCode;
        }

        protected void onProgressUpdate(Integer...parms) {
            super.onProgressUpdate();
        }

        protected void onPostExecute(Integer result) {
            //    Log.e("onPost login response: ",result);
            Log.d("FCM response code ",Integer.toString(result));
            if(result==200){
               Log.d("FCM key for sign in","FCM key added!");
            }
            else{
                Log.d("something wrong happens","FCM key cannot be added");
                return;
            }

        }
    }



}