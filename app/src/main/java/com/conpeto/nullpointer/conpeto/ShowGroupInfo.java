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
                end = result.indexOf("\"",start+13);
                if(start == -1 || end == -1)
                    break;
                RepCnts.add(result.substring(start + 13, end));

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
                            reportUser.putExtra("user_ID", userID);
                            reportUser.putExtra("report_count",report_count[position]);
                            reportUser.putExtra("user'sID",users.get(position));

                            //send intents that enable coming back to this page


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
}


