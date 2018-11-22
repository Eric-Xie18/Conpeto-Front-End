package com.conpeto.nullpointer.conpeto;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CreateGroup extends AppCompatActivity {

    private String userID;
    private ArrayList<String> groupInfo;
    private String category = "Music and Arts";
    private String location = null;
    int PLACE_PICKER_REQUEST = 1;
    private Place place = null;
    private double longitude = 0;
    private double latitude = 0;
    private TextView placeLocation;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        userID = getIntent().getStringExtra("user_ID");
        final Button back = (Button) findViewById(R.id.button_Cancel);
        final Button submit = (Button) findViewById(R.id.button_Submit);
        final Button pickPlace = (Button)findViewById(R.id.pick_place);
        placeLocation = (TextView) findViewById(R.id.textView3);
         //get the spinner from the xml.
         Spinner dropdown = findViewById(R.id.ddCategory);
//create a list of items for the spinner.
         String[] items = new String[]{ "Music and Arts", "Sport", "Food and Conversation"};
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
         ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
         dropdown.setAdapter(adapter);
         dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                 switch (i) {
                     case 0:
                         category = "Music and Arts";
                         break;
                     case 1:
                         // Whatever you want to happen when the second item gets selected
                         category = "Sport";
                         break;
                     case 2:
                         // Whatever you want to happen when the thrid item gets selected
                         category = "Food and Conversation";
                         break;
                 }
             };

             @Override
             public void onNothingSelected(AdapterView<?> adapterView) {

             };


         });


        System.out.println("In create group: The radius is" + PostLogin.gerRadius());
        pickPlace.setOnClickListener( new View.OnClickListener(){
                public void onClick(View v) {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    try {
                        startActivityForResult(builder.build(CreateGroup.this), PLACE_PICKER_REQUEST);
                    }catch(GooglePlayServicesRepairableException e){}
                    catch(GooglePlayServicesNotAvailableException e){}

            }});



        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent goBack = new Intent(CreateGroup.this, PostLogin.class);
                goBack.putExtra("user_ID", userID);
                CreateGroup.this.startActivity(goBack);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText group_Name = (EditText) findViewById(R.id.group_Name);
                String name = group_Name.getText().toString();
                System.out.println("group name is" + name);


                Pattern p = Pattern.compile("[^a-z 0-9 ]", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(group_Name.getText().toString());

                // need a third case where non a~Z and 0~9 character appears
                if ("".equals(name) || "".equals(group_Name.getText().toString())) {
                    Toast.makeText(CreateGroup.this, "You must specify either the group name to proceed.",
                            Toast.LENGTH_LONG).show();
                }
                else if (m.find()){
                    Toast.makeText(CreateGroup.this, "The group name can only contain letters a-z, number 0-9 and white spaces.",
                            Toast.LENGTH_LONG).show();
                }
                else if(location==null){
                    Toast.makeText(CreateGroup.this, "Please pick a location for your group activity",
                            Toast.LENGTH_LONG).show();

                }
                else {
                    // user_id
                    // group_name
                    // group_Details
                    // group_Category
                    // “group_latitude”:”123”, “group_longitude”:”123”
                   EditText groupDetails = (EditText)findViewById(R.id.group_details);
                   String details = groupDetails.getText().toString();
                   if(details.equals(null)||details.equals("")){
                       details = " ";
                   }

                    groupInfo = new ArrayList<String>();
                    groupInfo.add(userID);
                    groupInfo.add(name);
                    groupInfo.add(details);
                    groupInfo.add(category);
                    groupInfo.add(Double.toString(latitude));
                    groupInfo.add(Double.toString(longitude));
                    NewGroup createGroup = new NewGroup();
                    createGroup.execute();
                }
            }
        });

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(data, this);
                if (place == null) {
                    String toastMsg = String.format("Can't get the place");
                    Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                } else {
                    String toastMsg = String.format("Place: %s", place.getName());
                    Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                    longitude = place.getLatLng().longitude;
                    latitude = place.getLatLng().latitude;
                    location = place.getName().toString();
                    placeLocation.setText(location);

                }
            }
        }
    }
    protected void onResume(){
         super.onResume();
         System.out.println("Resume Create group: The radius is" + PostLogin.gerRadius());


    }

    private class NewGroup extends AsyncTask<Void, Integer, Integer> {
        protected Integer doInBackground(Void... params) {
            String urlString = "http://null-pointers.herokuapp.com/group";
            StringBuffer response = new StringBuffer();
            int responseCode = 400;
            try {
                URL url = new URL(urlString);
                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                System.out.println("After connection\n");
                client.setRequestProperty("Content-Type", "application/json");
                client.setRequestMethod("POST");
                client.setDoOutput(true);

                // append the content in JSON format
                // user_id
                // group_name
                // group_Details
                // group_Category
                // “group_latitude”:”123”, “group_longitude”:”123”
                // {“id”:”12345”, “group_name”:”new_group”, “group_details”:
                //“new_group_details”, “group_category”: “Sport”, “group_latitude”:”123”, “group_longitude”:”123”}

                StringBuilder body = new StringBuilder();
                body.append("{\"id\":");
                body.append("\"");
                body.append(groupInfo.get(0));
                body.append("\"");
                body.append(", ");
                body.append("\"group_name\":");
                body.append("\"");
                body.append(groupInfo.get(1));
                body.append("\"");
                body.append(", ");
                body.append("\"group_details\":");
                body.append("\"");
                body.append(groupInfo.get(2));
                body.append("\"");
                body.append(", ");
                body.append("\"group_category\":");
                body.append("\"");
                body.append(groupInfo.get(3));
                body.append("\"");
                body.append(", ");
                body.append("\"group_latitude\":");
                body.append("\"");
                body.append(groupInfo.get(4));
                body.append("\"");
                body.append(", ");
                body.append("\"group_longitude\":");
                body.append("\"");
                body.append(groupInfo.get(5));
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
            // add toast message for added and not added
            if(result==200){
            Intent goBack = new Intent(CreateGroup.this, PostLogin.class);
            Toast.makeText(CreateGroup.this, "Group Created!",
                    Toast.LENGTH_LONG).show();
        }
          else if(result==409){
                Toast.makeText(CreateGroup.this, "Group already exists, please use the other name",
                        Toast.LENGTH_LONG).show();
            }

            else{
                Toast.makeText(CreateGroup.this, "Create group failed",
                        Toast.LENGTH_LONG).show();

            }

    };
}
}