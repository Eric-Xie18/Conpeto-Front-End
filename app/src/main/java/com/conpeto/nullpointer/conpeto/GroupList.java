package com.conpeto.nullpointer.conpeto;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class GroupList extends AppCompatActivity {
    private String radius,Category,userID;
    private double Lat,Long;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        radius = getIntent().getStringExtra("radius");
        Category = getIntent().getStringExtra("cat");
        userID = getIntent().getStringExtra("user_ID");
        Lat = getIntent().getDoubleExtra("Lat",0);
        Long = getIntent().getDoubleExtra("Long",0);

        CheckGroup checkGroup = new CheckGroup();
        checkGroup.execute();
    }
    private class CheckGroup extends AsyncTask<Void, Group, ArrayList<Group>> {

        protected ArrayList<Group> doInBackground(Void... params) {

            StringBuilder urlBuilder = new StringBuilder("http://null-pointers.herokuapp.com/group");
            urlBuilder.append("?id=");
            urlBuilder.append(userID);
            String url1 = urlBuilder.toString();
            String usergroups,catlocgroups;

            HttpClient http = new HttpClient(url1, "GET");
            usergroups = http.sendRequest("");
            Log.e("userLog",usergroups);
            StringBuilder urlBuilder2 = new StringBuilder("http://null-pointers.herokuapp.com/group/?");

            if(!Category.equals("All")&&!radius.equals("Any")){
                urlBuilder2.append("category=");
                urlBuilder2.append(Category);
                urlBuilder2.append("longitude=");
                urlBuilder2.append(Long);
                urlBuilder2.append("latitude=");
                urlBuilder2.append(Lat);
                urlBuilder2.append("distance=");
                urlBuilder2.append(radius);
                String url2 = urlBuilder2.toString();
                HttpClient http2 = new HttpClient(url2, "GET");
                catlocgroups = http2.sendRequest("");
                Log.e("catLog",catlocgroups);
            }
            else if(!Category.equals("All")&&radius.equals("Any")){
                urlBuilder2.append("category=");
                urlBuilder2.append(Category);
                String url2 = urlBuilder2.toString();
                HttpClient http2 = new HttpClient(url2, "GET");
                catlocgroups = http2.sendRequest("");
                Log.e("catLog",catlocgroups);
            }
            else if(Category.equals("All")&& !radius.equals("Any")){
                urlBuilder2.append("longitude=");
                urlBuilder2.append(Long);
                urlBuilder2.append("latitude=");
                urlBuilder2.append(Lat);
                urlBuilder2.append("distance=");
                urlBuilder2.append(radius);
                String url2 = urlBuilder2.toString();
                HttpClient http2 = new HttpClient(url2, "GET");
                catlocgroups = http2.sendRequest("");
                Log.e("catLog",catlocgroups);
            }
            else{
                String urlone = "http://null-pointers.herokuapp.com/group/?category=Sport";
                String urltwo = "http://null-pointers.herokuapp.com/group/?category=Music and Arts";
                String urlthree = "http://null-pointers.herokuapp.com/group/?category=Food and Conversation";
                HttpClient http3 = new HttpClient(urlone, "GET");
                HttpClient http4 = new HttpClient(urltwo, "GET");
                HttpClient http5 = new HttpClient(urlthree, "GET");
                String str1 = http3.sendRequest("");
                String str2 = http4.sendRequest("");
                String str3 = http5.sendRequest("");
                catlocgroups = str1+str2+str3;
                Log.e("catLog",catlocgroups);
            }

            String result = catlocgroups + "#$#BREAK_HERE#$#" + usergroups;
            Log.e("LOL",result);
            return result;
        }
        protected void onProgressUpdate(Integer... parms) {

            super.onProgressUpdate();
        }

        ArrayList<Group> stringToList(String result){

            //Indexes used for result substring calculation
            int index = 0, i;

            //Strings to store group attribute values
            String id = "", name = "", cat = "", dets = "", users = "", Lat = "", Long = "";

            //Array List for all groups
            ArrayList<Group> groups = new ArrayList<>();

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

            return groups;
        }

        protected void onPostExecute(String result) {

            int j = result.indexOf("#$#BREAK_HERE#$#",0);
            String catlocgroups = result.substring(0,j);
            String usergroups = result.substring(j+16,result.length());
            Log.e("catloc",catlocgroups);
            Log.e("users",usergroups);

            ListView lv;
            ArrayAdapter<String> adapter;

            ArrayList<Group> groupsToFilter = stringToList(catlocgroups);
            ArrayList<Group> groupsToRemove = stringToList(usergroups);

           Log.e("NOW",groupsToRemove.get(0).getName());
            Log.e("NOW1",groupsToRemove.get(groupsToRemove.size()-1).getName());
            Log.e("NOW2",groupsToFilter.get(0).getName());
            Log.e("NOW3",groupsToFilter.get(groupsToFilter.size()-1).getName());
            /*lv = (ListView) findViewById(R.id.list_view);
            adapter = new ArrayAdapter<String>(GroupList.this, R.layout.list_item, R.id.name, GroupNames);
            lv.setAdapter(adapter);*/

           /* lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //toast added for debugging
                    Intent next = new Intent(GroupList.this, JoinGroupInfo.class);
                    next.putExtra("name",Groups[position].getName());
                    next.putExtra("groupID",Groups[position].getID());
                    next.putExtra("category",Groups[position].getCategory());
                    next.putExtra("details",Groups[position].getDetails());
                    next.putExtra("userIDs",Groups[position].getUserIDs());
                    next.putExtra("Lat",Groups[position].getLat());
                    next.putExtra("Long",Groups[position].getLong());
                    next.putExtra("user_ID",userID);
                    GroupList.this.startActivity(next);
                }
            });*/

        }
    }
}


