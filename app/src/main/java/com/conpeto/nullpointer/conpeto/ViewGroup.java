package com.conpeto.nullpointer.conpeto;
import java.util.ArrayList;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.AdapterView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ViewGroup extends AppCompatActivity {
    private String userID;
    private ListView lv;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);

        userID = getIntent().getStringExtra("user_ID");

        final Button goBack = findViewById(R.id.go_Back);
        goBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent viewGroup = new Intent(ViewGroup.this, PostLogin.class);
                viewGroup.putExtra("user_ID", userID);
                ViewGroup.this.startActivity(viewGroup);
            }
        });

        CheckGroup checkGroup = new CheckGroup();
        checkGroup.execute();
    }


    private class CheckGroup extends AsyncTask<Void, Integer, String> {

        protected String doInBackground(Void... params) {
            StringBuilder urlBuilder = new StringBuilder("http://null-pointers.herokuapp.com/group");
            urlBuilder.append("?id=");
            urlBuilder.append(userID);
            String urlString = urlBuilder.toString();
            StringBuffer response = new StringBuffer();
            System.out.println("The URL is" + urlString);
            try {
                Log.e("before connection", "let's start");
                URL url = new URL(urlString);
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                System.out.println("After connection\n");
                client.setRequestMethod("GET");
                // append the content in JSON format

                int responseCode = client.getResponseCode();
                System.out.println("\nSending 'GET' request to URL : " + url);
                System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(client.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

            } catch (MalformedURLException E) {
                Log.e("URL", "The URL is not correct");
            } catch (IOException E) {
            }

            Log.e("group response is:", response.toString());
            return response.toString();
        }

        protected void onProgressUpdate(Integer... parms) {

            super.onProgressUpdate();
        }

        protected void onPostExecute(String result) {

            //Indexes used for result substring calculation
            int index = 0, i;

            //Strings to store group attribute values
            String id = "", name = "", cat = "", dets = "", users = "", Lat = "", Long = "";

            //Array List for all groups
            ArrayList<Group> groups = new ArrayList<>();
            ArrayList<String> GroupNames = new ArrayList<>();
            //Group attribute identifiers
            String idTag = "_id";
            String nameTag = "group";
            String detsTag = "group_details";
            String userTag = "user_ids";
            String catTag = "group_category";
            String latTag = "group_latitude";
            String longTag = "group_longitude";
            while (index < result.length()) {
                //Extract group id
                i = result.indexOf(idTag, index);
                i = i + 6;
                index = result.indexOf("\",", i + 1);
                id = result.substring(i, index);
                //Extract group name
                i = result.indexOf(nameTag, index);
                i = i + 8;
                index = result.indexOf("\",", i + 1);
                name = result.substring(i, index);
                //Extract group details
                i = result.indexOf(detsTag, index);
                i = i + 16;
                index = result.indexOf("\",", i + 1);
                dets = result.substring(i, index);
                //Extract user IDs
                i = result.indexOf(userTag, index);
                i = i + 10;
                index = result.indexOf("],\"", i + 1);
                users = result.substring(i, index + 1);
                //Extract category
                i = result.indexOf(catTag, index);
                i = i + 17;
                index = result.indexOf("\",", i + 1);
                cat = result.substring(i, index);
                //Extract lat
                i = result.indexOf(latTag, index);
                i = i + 16;
                index = result.indexOf(",", i + 1);
                Lat = result.substring(i, index - 1);
                //Extract longitude
                i = result.indexOf(longTag, index);
                i = i + 17;
                index = result.indexOf("}", i + 1);
                Long = result.substring(i, index);

                index = index + 2;
                groups.add(new Group(id, name, cat, dets, users, Lat, Long));

            }
            final Group[] Groups = groups.toArray(new Group[groups.size()]);
            for (int j = 0; j < groups.size(); j++) {
                GroupNames.add(Groups[j].getName());
            }
            lv = (ListView) findViewById(R.id.list_view);
            adapter = new ArrayAdapter<String>(ViewGroup.this, R.layout.list_item, R.id.name, GroupNames);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //toast added for debugging
                    Toast.makeText(ViewGroup.this,"Pressed " + Groups[position].getName(),Toast.LENGTH_SHORT).show();
                    Intent next = new Intent(ViewGroup.this, ShowGroupInfo.class);
                    next.putExtra("name",Groups[position].getName());
                    next.putExtra("groupID",Groups[position].getID());
                    next.putExtra("category",Groups[position].getCategory());
                    next.putExtra("details",Groups[position].getDetails());
                    next.putExtra("userIDs",Groups[position].getUserIDs());
                    next.putExtra("Lat",Groups[position].getLat());
                    next.putExtra("Long",Groups[position].getLong());
                    next.putExtra("user_ID",userID);
                    ViewGroup.this.startActivity(next);
                }
            });
        }
    }
}
