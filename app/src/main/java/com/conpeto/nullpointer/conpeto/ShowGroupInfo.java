package com.conpeto.nullpointer.conpeto;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import android.view.LayoutInflater;
import android.widget.Toast;

import java.util.ArrayList;


public class ShowGroupInfo extends AppCompatActivity {

    private String userID;
    private String name, ID, dets, Lat, Long, userIDs, cat;
    private ListView lv;
    private String ownerID = null;
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
        String result = "\nGroup Name: " + name + "\n\nGroup Description: " + dets + "\n\nCategory: " + cat + "\n\n\n LIST OF GROUP MEMBERS:";
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

        final Button mapView = findViewById(R.id.map_view);
        mapView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent viewGroup = new Intent(ShowGroupInfo.this, SelectAndSelfMap.class);
                viewGroup.putExtra("lat", Lat);
                viewGroup.putExtra("long", Long);
                ShowGroupInfo.this.startActivity(viewGroup);
            }
        });
        final Button delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                  Delete delete = new Delete();
                  delete.execute();
            }
        });


        CheckGroup checkGroup = new CheckGroup();
        checkGroup.execute();

    }

    @Override
    public void onBackPressed() {
        Intent viewGroup = new Intent(ShowGroupInfo.this, ViewGroup.class);
        viewGroup.putExtra("user_ID", userID);
        ShowGroupInfo.this.startActivity(viewGroup);
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
            ownerID = users.get(0);
            Log.e("response:", res.toString());
            return res.toString();
        }

        protected void onProgressUpdate(Integer... parms) {

            super.onProgressUpdate();
        }
        protected void onPostExecute(String result){

            ArrayList <String> userNames = new ArrayList<>();
            ArrayList <String> RepCnts = new ArrayList<>();

            int start=0,end=0,index=0;

            while(true){
                start = result.indexOf("user",index);
                end = result.indexOf("email",start+7);
                if(start == -1 || end == -1)
                    break;
                userNames.add(result.substring(start + 7, end - 3));
                index = end;
                start = result.indexOf("report_count",index);
                end = result.indexOf(",\"",start+13);
                if(start == -1 || end == -1)
                    break;
                RepCnts.add(result.substring(start + 14, end));

            }

            final String [] report_count = RepCnts.toArray(new String[RepCnts.size()]);

            class CustomAdapter extends BaseAdapter implements ListAdapter {
                private ArrayList<String> list = new ArrayList<String>();
                private Context context;
                private CustomAdapter(ArrayList<String> list, Context context) {
                    this.list = list;
                    this.context = context;
                }

                @Override
                public int getCount() {
                    return list.size();
                }

                @Override
                public Object getItem(int pos) {
                    return list.get(pos);
                }

                @Override
                public long getItemId(int pos) {
                    return 0;
                }

                @Override
                public View getView(final int position, View convertView, android.view.ViewGroup parent) {
                    View view = convertView;
                    if (view == null) {
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        view = inflater.inflate(R.layout.list_item_report, null);
                    }

                    //Handle TextView and display string from your list
                    TextView listItemText = (TextView)view.findViewById(R.id.name);
                    listItemText.setText(list.get(position));
                    //Handle buttons and add onClickListeners
                    Button report = (Button) view.findViewById(R.id.report);

                    report.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(ShowGroupInfo.this,"Pressed " + list.get(position),Toast.LENGTH_SHORT).show();
                            Intent reportUser = new Intent(ShowGroupInfo.this, ReportUser.class);

                            //Intents to report user
                            reportUser.putExtra("report_count",report_count[position]);
                            reportUser.putExtra("reportID",users.get(position));

                            //send intents that enable coming back to this page
                            reportUser.putExtra("name",name);
                            reportUser.putExtra("groupID",ID);
                            reportUser.putExtra("category",cat);
                            reportUser.putExtra("details",dets);
                            reportUser.putExtra("userIDs",userIDs);
                            reportUser.putExtra("Lat",Lat);
                            reportUser.putExtra("Long",Long);
                            reportUser.putExtra("user_ID",userID);
                            Log.e("rep: ",report_count[position]);
                            ShowGroupInfo.this.startActivity(reportUser);

                        }
                    });
                    return view;
                }
            }
                lv = (ListView) findViewById(R.id.list_view);
                CustomAdapter adapter = new CustomAdapter(userNames, ShowGroupInfo.this);
                lv.setAdapter(adapter);

        }
    }

    private class Delete extends AsyncTask<Void, Integer, String> {

        protected String doInBackground(Void... params) {

            if(!ownerID.equals(userID))
                return "You don't have permission to delete this group";

            else{
                String url = "http://null-pointers.herokuapp.com/group/?group=" + name +"&id="+userID;
                String response;
                HttpClient http = new HttpClient(url,"DELETE");
                response = http.sendRequest("");
                if(response.contains("Group Deleted"))
                    return "Group successfully deleted";
                else
                    return "The group you are trying to delete was not found";

            }
        }
        protected void onProgressUpdate(Integer...params){
            super.onProgressUpdate();
        }

        protected void onPostExecute(String result) {

            Toast.makeText(ShowGroupInfo.this, result,
                    Toast.LENGTH_LONG).show();

            if (result.equals("Group successfully deleted")) {
                Intent viewGroup = new Intent(ShowGroupInfo.this, ViewGroup.class);
                viewGroup.putExtra("user_ID", userID);
                ShowGroupInfo.this.startActivity(viewGroup);
            }
        }

        }
    }



