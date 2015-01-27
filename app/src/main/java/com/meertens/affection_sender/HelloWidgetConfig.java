package com.meertens.affection_sender;


import android.app.Activity;
import android.app.Fragment;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TableLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.meertens.affection_sender.R;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class HelloWidgetConfig extends Activity {


	
	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private static final String FILENAME = "klef.txt";
    private static final int CONTACT_PICKER_RESULT = 1337;
    ArrayAdapter<String> adapter;

    public void saveToFile() {
        String kleffeString = "";
        for(int i=0 ; i<adapter.getCount() ; i++){
            String obj = adapter.getItem(i);
            kleffeString+= obj+"\n";

        }

        String phoneString = ((EditText)findViewById(R.id.phone)).getText().toString();

        // Write the found strings to the memory
        AffectionInOutOperations.writeToFile(HelloWidgetConfig.FILENAME, kleffeString);
        AffectionInOutOperations.writeToFile("number.txt", phoneString);
    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// Initialization code
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);
		setContentView(R.layout.config);
		
		// Add on click listener to the confirmation button.
        Button exitConfigScreenButton = (Button) findViewById(R.id.exitconfig);
        exitConfigScreenButton.setOnClickListener(configExitOnClickListener);

        // Add on click listener to the contact select button
        ImageButton contactButton= (ImageButton) findViewById(R.id.contactbutton);
        contactButton.setOnClickListener(configContactOnClickListener);

        ImageButton addMessage = (ImageButton) findViewById(R.id.add_message_button);
        addMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the message from the field, and clear this field
                EditText newLoveMessageField = (EditText)findViewById(R.id.new_message_field);
                String newMessage = newLoveMessageField.getText().toString();
                newLoveMessageField.setText("");

                // Add the message to the overview with messages
                adapter.add(newMessage);
            }
        });

        ListView listView = (ListView) findViewById(R.id.tablelayout);

		// Read the text from the file. 
		try {
            // All all messages to an arraylist
			String[] messagesAr = AffectionInOutOperations.readFromFile(FILENAME).split("\n");
            ArrayList<String> allMessages = new ArrayList<String>();
			for (String singleMessage : messagesAr)
			{
                allMessages.add(singleMessage);
            }

            // Create the adapter and add it to the listview
            adapter = new LoveAdapter(this,allMessages);
            listView.setAdapter(adapter);

		} catch (FileNotFoundException e1) {
            adapter = new LoveAdapter(this,new ArrayList<String>());
            listView.setAdapter(adapter);
		}
	    
	    // Read the number from the file
	    try {
	    	String numberString = AffectionInOutOperations.readFromFile("number.txt");
			EditText number = (EditText) findViewById(R.id.phone);
		    number.setText(numberString.toString());
		} catch (FileNotFoundException e) {
			EditText text = (EditText) findViewById(R.id.phone);
			text.setText("");
		}
	    
	    // TODO Roland: Apparantly important for placing it
	     Intent intent = getIntent();
	     Bundle extras = intent.getExtras();
	     if (extras != null) {
	         mAppWidgetId = extras.getInt(
	                 AppWidgetManager.EXTRA_APPWIDGET_ID,
	                 AppWidgetManager.INVALID_APPWIDGET_ID);
	     }
	  
	     // TODO If they gave us an intent without the widget id, just bail.
	     if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
	    	 //Log.i("hello widget config","Did not receive a widget id");
	         //finish();
	     }

	}

	// The on click listener for the return to homescreen button.
	private Button.OnClickListener configExitOnClickListener = new Button.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// Get the text from the text field with all love strings
            saveToFile();
			
			// Return to the home screen
			Intent resultValue = new Intent();
			resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
			setResult(RESULT_OK, resultValue);
			finish();
		}

    };

    // The on click listener for the contact button.
    private Button.OnClickListener configContactOnClickListener = new Button.OnClickListener() {


        @Override
        public void onClick(View arg0) {
            Intent pickContactIntent = new Intent( Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI );
            pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);

            startActivityForResult(pickContactIntent, CONTACT_PICKER_RESULT);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == CONTACT_PICKER_RESULT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
                Uri contactUri = data.getData();
                // We only need the NUMBER column, because there will be only one row in the result
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column);
                Log.i("Selected cntact", number);
                AffectionInOutOperations.writeToFile("number.txt", number);
                ((EditText)findViewById(R.id.phone)).setText(number);

            }
        }
    }

    /**
     * This class makes the ad request and loads the ad.
     */
    public static class AdFragment extends Fragment {

        private AdView mAdView;

        public AdFragment() {
        }

        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);

            // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
            // values/strings.xml.
            mAdView = (AdView) getView().findViewById(R.id.adView);

            // Create an ad request. Check logcat output for the hashed device ID to
            // get test ads on a physical device. e.g.
            // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_ad, container, false);
        }

        /** Called when leaving the activity */
        @Override
        public void onPause() {
            if (mAdView != null) {
                mAdView.pause();
            }
            super.onPause();
        }

        /** Called when returning to the activity */
        @Override
        public void onResume() {
            super.onResume();
            if (mAdView != null) {
                mAdView.resume();
            }
        }

        /** Called before the activity is destroyed */
        @Override
        public void onDestroy() {
            if (mAdView != null) {
                mAdView.destroy();
            }
            super.onDestroy();
        }

    }
}