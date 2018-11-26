package com.conpeto.nullpointer.conpeto;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class JoinGroupInfo extends AppCompatActivity {

    //User details
    private String radius, userCat, userID;
    private double userLat, userLong;
    ArrayList<String> users = new ArrayList<>();
    //Group details
    private String name, ID, dets, Lat, Long, userIDs, cat;

    private ListView lv;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group_info);

        //user details
        radius = getIntent().getStringExtra("radius");
        userCat = getIntent().getStringExtra("userCat");
        userID = getIntent().getStringExtra("user_ID");
        userLat = getIntent().getDoubleExtra("userLat", 0);
        userLong = getIntent().getDoubleExtra("userLong", 0);

        //details related to group
        name = getIntent().getStringExtra("name");
        cat = getIntent().getStringExtra("category");
        ID = getIntent().getStringExtra("groupID");
        Lat = getIntent().getStringExtra("Lat");
        Long = getIntent().getStringExtra("Long");
        dets = getIntent().getStringExtra("details");
        userIDs = getIntent().getStringExtra("userIDs");

        final Button goBack = findViewById(R.id.go_Back);
        goBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent prev = new Intent(JoinGroupInfo.this, GroupList.class);
                //user ID
                prev.putExtra("user_ID", userID);
                prev.putExtra("userLat", userLat);
                prev.putExtra("userLong", userLong);
                prev.putExtra("radius", radius);
                prev.putExtra("userCat", userCat);

                JoinGroupInfo.this.startActivity(prev);
            }
        });
        final Button joinGroup = findViewById(R.id.join);
        goBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AddUserToGroup joinGroup = new AddUserToGroup();
                joinGroup.execute();
            }
        });

        int index = 2, i = 0;
        while (index < userIDs.length() && i < userIDs.length()) {
            i = userIDs.indexOf("\"", index);
            users.add(userIDs.substring(index, i));
            index = i + 3;
        }

        TextView info = (TextView) findViewById(R.id.group_info);
        String result = "\nGroup Name: " + name + "\n\nGroup Description: " + dets + "\n\nCategory: " + cat + "\n\n\n LIST OF GROUP MEMBERS:";
        info.setTypeface(null, Typeface.BOLD);
        info.setText(result);


        CheckGroup checkGroup = new CheckGroup();
        checkGroup.execute();
    }

    private class CheckGroup extends AsyncTask<Void, Integer, String> {

        protected String doInBackground(Void... params) {
            StringBuilder res = new StringBuilder();
            StringBuilder urlBuilder = new StringBuilder("http://null-pointers.herokuapp.com/user?id=");
            String url, response;

            for (int i = 0; i < users.size(); ++i) {

                url = urlBuilder + users.get(i);
                Log.e("url: ", url);
                HttpClient http = new HttpClient(url, "GET");
                response = http.sendRequest("");
                if (response != null)
                    res.append(response);
            }
            Log.e("response:", res.toString());
            return res.toString();
        }

        protected void onProgressUpdate(Integer... parms) {

            super.onProgressUpdate();
        }

        protected void onPostExecute(String result) {
            ArrayList <String> userNames = new ArrayList<>();
            int start=0,end=0,index=0;

            while(true) {
                start = result.indexOf("user", index);
                end = result.indexOf("email", start + 7);
                if (start == -1 || end == -1)
                    break;
                userNames.add(result.substring(start + 7, end - 3));
                index = end + 1;

            }
            lv = (ListView) findViewById(R.id.list_view);
            adapter = new ArrayAdapter<String>(JoinGroupInfo.this, R.layout.list_item, R.id.name, userNames);
            lv.setAdapter(adapter);
        }
    }
    private class AddUserToGroup extends AsyncTask<Void, Integer, Boolean> {

        protected Boolean doInBackground(Void... params) {
            boolean success = false;
            String response = null;
            String urlString = "http://null-pointers.herokuapp.com/group";
            String method = "PUT";
            // append the content in JSON format
            StringBuilder body = new StringBuilder();
            body.append("{\"user\":");
            body.append("\"");
            body.append(userID);
            body.append("\"");
            body.append(", ");
            body.append("\"group\":");
            body.append("\"");
            body.append(name);
            body.append("\"");
            body.append("}");
            String bodyInfo = body.toString();

            HttpClient httpClient = new HttpClient(urlString,method);
            response = httpClient.sendRequest(bodyInfo);

            if (response.contains("Added")||(response.contains("Already"))) {
                success = true;
            }


            Log.e("Join group response:", response.toString());
            return success;
        }

        protected void onProgressUpdate(Integer...params){
            super.onProgressUpdate();
        }

        protected void onPostExecute(Boolean result) {
            // add toast message for added and not added
            // Intent goBack = new Intent(JoinGroup.this,PostLogin.class);
            if(!result) {
                Toast.makeText(JoinGroupInfo.this, "No such group or user was found!",
                        Toast.LENGTH_LONG).show();
            }
            else{

                //goBack.putExtra("user_ID",userID);
                Toast.makeText(JoinGroupInfo.this, "Join the group successfully!",
                        Toast.LENGTH_LONG).show();
                //JoinGroup.this.startActivity(goBack);

            }
        }

    }
}

