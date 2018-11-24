package com.conpeto.nullpointer.conpeto;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import android.view.LayoutInflater;
import java.util.ArrayList;


public class ShowGroupInfo extends AppCompatActivity {

    private String userID;
    private String name, ID, dets, Lat, Long, userIDs, cat;
    private ListView lv;
    ArrayAdapter<String> adapter;
    ArrayList<String> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_group_info);

        //ID of user currently logged in
        userID = getIntent().getStringExtra("user_ID");

        //details related to group
        name = getIntent().getStringExtra("name");
        cat = getIntent().getStringExtra("category");
        ID = getIntent().getStringExtra("groupID");
        Lat = getIntent().getStringExtra("Lat");
        Long = getIntent().getStringExtra("Long");
        dets = getIntent().getStringExtra("details");
        userIDs = getIntent().getStringExtra("userIDs");

        int index = 2,i=0;
        while (index < userIDs.length() && i<userIDs.length())
        {
            i = userIDs.indexOf("\"",index);
            users.add(userIDs.substring(index,i));
            index = i + 3;
        }

        TextView info = (TextView) findViewById(R.id.group_info);
        String result = "\nGroup Name: " + name + "\n\nGroup Description: " + dets + "\n\nCategory: " + cat + "\n";
        info.setTypeface(null, Typeface.BOLD);
        info.setText(result);
        final Button goBack = findViewById(R.id.go_Back);
        goBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent viewGroup = new Intent(ShowGroupInfo.this, ViewGroup.class);
                viewGroup.putExtra("user_ID", userID);
                ShowGroupInfo.this.startActivity(viewGroup);
            }
        });

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
        protected void onPostExecute(String result){

                ArrayList<String> userNames = new ArrayList<>();
                int start =0,end =0;
                userNames.add(result);
               // for(int index = 0; index<result.length(),)
                lv = (ListView) findViewById(R.id.list_view);
                adapter = new ArrayAdapter<String>(ShowGroupInfo.this, R.layout.list_item, R.id.name,userNames);
                lv.setAdapter(adapter);

            }

        }
}


