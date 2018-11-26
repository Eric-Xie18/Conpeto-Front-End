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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;

public class GroupList extends AppCompatActivity {
    private String radius,Category,userID;
    private double Lat,Long;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        radius = getIntent().getStringExtra("radius");
        Category = getIntent().getStringExtra("userCat");
        userID = getIntent().getStringExtra("user_ID");
        Lat = getIntent().getDoubleExtra("userLat",0);
        Long = getIntent().getDoubleExtra("userLong",0);

        final Button goBack = findViewById(R.id.go_Back);
        goBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent postLogin = new Intent(GroupList.this, SearchCategories.class);
                postLogin.putExtra("user_ID", userID);
                GroupList.this.startActivity(postLogin);
            }
        });

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

            ArrayList<Group> empty = new ArrayList<>();

            HttpClient http = new HttpClient(url1, "GET");
            usergroups = http.sendRequest("");
            if(usergroups==null)
                usergroups ="";
            Log.e("userLog",usergroups);
            StringBuilder urlBuilder2 = new StringBuilder("http://null-pointers.herokuapp.com/group/?");

            if(!Category.equals("All")&&!radius.equals("Any")){
                urlBuilder2.append("category=");
                urlBuilder2.append(Category);
                urlBuilder2.append("&longitude=");
                urlBuilder2.append(Long);
                urlBuilder2.append("&latitude=");
                urlBuilder2.append(Lat);
                urlBuilder2.append("&distance=");
                urlBuilder2.append(radius);
                String url2 = urlBuilder2.toString();
                HttpClient http2 = new HttpClient(url2, "GET");
                catlocgroups = http2.sendRequest("");
                if(catlocgroups==null)
                    return empty;
                Log.e("catLog",catlocgroups);
            }
            else if(!Category.equals("All")&&radius.equals("Any")){
                urlBuilder2.append("category=");
                urlBuilder2.append(Category);
                String url2 = urlBuilder2.toString();
                HttpClient http2 = new HttpClient(url2, "GET");
                catlocgroups = http2.sendRequest("");
                if(catlocgroups==null)
                    return empty;
                Log.e("catLoghere22",catlocgroups);
            }
            else if(Category.equals("All")&& !radius.equals("Any")){
                urlBuilder2.append("category=Sport");
                urlBuilder2.append("&longitude=");
                urlBuilder2.append(Long);
                urlBuilder2.append("&latitude=");
                urlBuilder2.append(Lat);
                urlBuilder2.append("&distance=");
                urlBuilder2.append(radius);
                String url2 = urlBuilder2.toString();
                HttpClient http2 = new HttpClient(url2, "GET");
                String catlocgroups1 = http2.sendRequest("");
                if(catlocgroups1==null)
                    catlocgroups1 = "";
                String url3 = "http://null-pointers.herokuapp.com/group/?category=Music and Arts&longitude="+Long +"&latitude="+Lat+"&distance="+radius;
                http2 = new HttpClient(url3, "GET");
                String catlocgroups2 = http2.sendRequest("");
                if(catlocgroups2==null)
                    catlocgroups2 = "";
                String url4 = "http://null-pointers.herokuapp.com/group/?category=Food and Conversation&longitude="+Long +"&latitude="+Lat+"&distance="+radius;
                http2 = new HttpClient(url4, "GET");
                String catlocgroups3 = http2.sendRequest("");
                if(catlocgroups3==null)
                    catlocgroups3 = "";

                if(catlocgroups1.equals("")&&catlocgroups2.equals("")&&catlocgroups3.equals(""))
                    return empty;
                else
                    catlocgroups = catlocgroups1 + catlocgroups2 + catlocgroups3;
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
                if(str1 == null)
                    str1 = "";
                String str2 = http4.sendRequest("");
                if(str2 == null)
                    str2 = "";
                String str3 = http5.sendRequest("");
                if(str2 == null)
                    str2 = "";
                catlocgroups = str1+str2+str3;
                if (catlocgroups.equals(""))
                     return empty;
                
                Log.e("catLogLast",catlocgroups);
            }

            ArrayList<Group> groupsToFilter = stringToList(catlocgroups);
            ArrayList<Group> groupsToRemove = stringToList(usergroups);

            for(int i=0; i<groupsToFilter.size(); ++i){
                Group g = groupsToFilter.get(i);
                for(int j=0; j<groupsToRemove.size(); ++j){

                    if(g.checkEquality(groupsToRemove.get(j)))
                        groupsToFilter.set(i,null);
                }
            }

            ArrayList <Group> groups = new ArrayList<Group>();

            for(int i=0; i<groupsToFilter.size(); ++i){
                Group g = groupsToFilter.get(i);
                if(g!=null)
                    groups.add(g);
            }
            return groups;

        }
        protected void onProgressUpdate(Group... parms) {

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

        protected void onPostExecute(ArrayList<Group> result) {

            if(!result.isEmpty()){
            ListView lv;
            ArrayAdapter<String> adapter;
            final ArrayList<Group> Final = new ArrayList<>(result);
            ArrayList <String> GroupNames = new ArrayList<>();

            for (int j = 0; j < result.size(); j++) {
                GroupNames.add(result.get(j).getName());
            }

            lv = (ListView) findViewById(R.id.list_view);
            adapter = new ArrayAdapter<String>(GroupList.this, R.layout.list_item, R.id.name, GroupNames);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //toast added for debugging
                    Intent next = new Intent(GroupList.this, JoinGroupInfo.class);

                    //group details
                    next.putExtra("name",Final.get(position).getName());
                    next.putExtra("groupID",Final.get(position).getID());
                    next.putExtra("category",Final.get(position).getCategory());
                    next.putExtra("details",Final.get(position).getDetails());
                    next.putExtra("userIDs",Final.get(position).getUserIDs());
                    next.putExtra("Lat",Final.get(position).getLat());
                    next.putExtra("Long",Final.get(position).getLong());

                    //user details
                    next.putExtra("user_ID",userID);
                    next.putExtra("userCat",Category);
                    next.putExtra("userLat",Lat);
                    next.putExtra("userLong",Long);
                    next.putExtra("radius",radius);

                    GroupList.this.startActivity(next);
                }
            });}
            else {
                ArrayList<String> GroupNames = new ArrayList<>();
                ListView lv;
                ArrayAdapter<String> adapter;
                GroupNames.add("No Groups Found");
                lv = (ListView) findViewById(R.id.list_view);
                adapter = new ArrayAdapter<String>(GroupList.this, R.layout.list_item, R.id.name, GroupNames);
                lv.setAdapter(adapter);
            }

        }
    }
    @Override
    public void onBackPressed(){
        Intent postLogin = new Intent(GroupList.this, SearchCategories.class);
        postLogin.putExtra("user_ID", userID);
        GroupList.this.startActivity(postLogin);

    }
}


