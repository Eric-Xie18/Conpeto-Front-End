package com.conpeto.nullpointer.conpeto;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DeleteGroup extends AppCompatActivity {

    private String userID,name,creator,cat,ID,Lat,Long,dets,userIDs;
    private String deleteResult = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_group);

        //ID of user currently logged in
        userID = getIntent().getStringExtra("user_ID");
        //group name
        name = getIntent().getStringExtra("name");
        //creator of group
        creator = getIntent().getStringExtra("owner");

        //other group details (used for implementing back button)
        cat = getIntent().getStringExtra("category");
        ID = getIntent().getStringExtra("groupID");
        Lat = getIntent().getStringExtra("Lat");
        Long = getIntent().getStringExtra("Long");
        dets = getIntent().getStringExtra("details");
        userIDs = getIntent().getStringExtra("userIDs");
        final Button goBack = findViewById(R.id.go_Back);

        goBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Delete deleteResult = new Delete();
                deleteResult.execute();
            }});
    }
    private class Delete extends AsyncTask<Void, Integer, String> {

        protected String doInBackground(Void... params) {

            if(!creator.equals(userID))
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
            TextView message = (TextView) findViewById(R.id.msg);
            message.setTypeface(null, Typeface.BOLD);
            message.setText(result);
            final String result2 = result;

            Intent deleteGroup= new Intent(DeleteGroup.this, ShowGroupInfo.class);
            Intent success = new Intent(DeleteGroup.this, ViewGroup.class);
            if(!result2.equals("Group successfully deleted")){
                deleteGroup.putExtra("name",name);
                deleteGroup.putExtra("groupID",ID);
                deleteGroup.putExtra("category",cat);
                deleteGroup.putExtra("details",dets);
                deleteGroup.putExtra("userIDs",userIDs);
                deleteGroup.putExtra("Lat",Lat);
                deleteGroup.putExtra("Long",Long);
                deleteGroup.putExtra("user_ID",userID);
                DeleteGroup.this.startActivity(deleteGroup);
                finish();
            }

             else{
                   success.putExtra("user_ID",userID);
                   DeleteGroup.this.startActivity(success);
                   finish();
            }


        }
        }

    @Override
    public void onBackPressed() {
        Delete deleteResult = new Delete();
        deleteResult.execute();
    }


}
