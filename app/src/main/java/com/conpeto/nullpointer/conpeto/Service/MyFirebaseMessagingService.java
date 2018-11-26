package com.conpeto.nullpointer.conpeto.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.conpeto.nullpointer.conpeto.CreateGroup;
import com.conpeto.nullpointer.conpeto.LoginActivity;
import com.conpeto.nullpointer.conpeto.PostLogin;
import com.conpeto.nullpointer.conpeto.R;
import com.facebook.AccessToken;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarException;

public class MyFirebaseMessagingService  extends FirebaseMessagingService {
    String TAG = "Firebase message";
    private String userID = null;
    private String FCMToken = null;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

     String messageData = remoteMessage.getData().toString().substring(9,remoteMessage.getData().toString().length()-1);

        Log.d(TAG + "extracted data", messageData);
        if (remoteMessage != null && remoteMessage.getData()!=null) {
            Log.d(TAG, "New message received from the firebase!");
            Log.d("Message is ", remoteMessage.getData().toString());


            if (LocationService.getLocation().equals(null)) {
                Log.d(TAG, "Location service unavailable!");
                return;
            }

            try {

                double log;
                double lat;
                JSONObject jsonBody;
                 Log.d(TAG,"Building JSON");
                jsonBody = new JSONObject(messageData);
                log =  jsonBody.getDouble("group_longitude");
                lat =  jsonBody.getDouble("group_latitude");
                Location groupLoc = new Location("");
                groupLoc.setLatitude(lat);
                groupLoc.setLongitude(log);
                Location currentLoc = LocationService.getLocation();
                float distance = currentLoc.distanceTo(groupLoc) / 1000;
                JSONArray userList = jsonBody.getJSONArray("user_ids");

                String creator = (String)userList.get(0);
                String groupName = jsonBody.getString("group");
                String groupCategory = jsonBody.getString("group_category");
                String groupDetails = jsonBody.getString("group_details");
                String messageBody = groupCategory +
                        " details: " + groupDetails;
                String title = "A new group: " + groupName;

                if(creator.contains(AccessToken.getCurrentAccessToken().getUserId())){
                    return;
                }

                if (PostLogin.gerRadius().equals("Any")) {
                    sendNotification(messageBody, title);
                } else if (distance <= Float.parseFloat(PostLogin.gerRadius())) {
                     Log.d(TAG, "Building Notification!");
                    sendNotification(messageBody, title);

                }

            } catch (JSONException e) { Log.d(TAG,"JSON exception");
            }
            ;

            //}

        }

    }
    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody, String title) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           Log.d("Firebase higer"," So now what?");
        }

        notificationManager.notify(0, notificationBuilder.build());

    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        FCMToken = token;
        sendRegistrationToServer(token);
    }


    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        AccessToken fbToken = AccessToken.getCurrentAccessToken();
        if (!fbToken.equals(null) && !fbToken.isExpired() ) {
            userID = fbToken.getUserId();
        }
    }

    private class SendToken extends AsyncTask<Void, Integer, Integer> {
        protected Integer doInBackground(Void... params) {
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
                    body.append(FCMToken);
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
                        Toast.makeText(MyFirebaseMessagingService.this, "Notification is not able to synchronize with server",
                                Toast.LENGTH_LONG).show();
                    }
            } catch (MalformedURLException E) {
                Log.e("URL", "The URL is not correct");
            } catch (IOException E) {
            }


            System.out.println("onPostExecute response: " + response.toString());

            return responseCode;

        }


        protected void onProgressUpdate(Integer... parms) {
            super.onProgressUpdate();
        }

        protected void onPostExecute(Integer result) {

        }



    }
}




