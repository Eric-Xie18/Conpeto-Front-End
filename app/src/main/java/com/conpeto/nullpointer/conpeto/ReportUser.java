package com.conpeto.nullpointer.conpeto;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ReportUser extends AppCompatActivity {

    private String userID,name,cat,ID,Lat,Long,dets,userIDs,reportID,count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_user);

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

        //report intents
        count = getIntent().getStringExtra("report_count");
        reportID = getIntent().getStringExtra("reportID");

        //BACK BUTTON
        final Button goBack = findViewById(R.id.go_Back);
        goBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent showGroupInfo= new Intent(ReportUser.this, ShowGroupInfo.class);

                showGroupInfo.putExtra("name",name);
                showGroupInfo.putExtra("groupID",ID);
                showGroupInfo.putExtra("category",cat);
                showGroupInfo.putExtra("details",dets);
                showGroupInfo.putExtra("userIDs",userIDs);
                showGroupInfo.putExtra("Lat",Lat);
                showGroupInfo.putExtra("Long",Long);
                showGroupInfo.putExtra("user_ID",userID);

                ReportUser.this.startActivity(showGroupInfo);
            }
        });

         Report report = new Report();
         report.execute();

    }

    private class Report extends AsyncTask<Void, Integer, String> {

        protected String doInBackground(Void... params) {

            //New report count
            int newcount = Integer.parseInt(count)+1;
            String body = " {\"id\":\"" + reportID + "\",\"reportCount\":" + newcount + "}";
            String url = "http://null-pointers.herokuapp.com/user";
            String response;
            HttpClient http = new HttpClient(url,"PUT");
            response = http.sendRequest(body);
            return response;
        }
        protected void onProgressUpdate(Integer...params){
            super.onProgressUpdate();
        }

        protected void onPostExecute(String result) {

            String msg1 = "User successfully reported";
            String msg2 = " Sorry the user you are trying to report doesn't exist!";
            String msg3 = "Unknown error occurred";

            TextView message = (TextView) findViewById(R.id.msg);
            message.setTypeface(null, Typeface.BOLD);
            if(result.contains("Report Count Updated"))
                message.setText(msg1);
            else if(result.contains("User Not Found")||result.contains("User Does Not Exist"))
                message.setText(msg2);
            else
                message.setText(msg3);

        }
    }
}
