package com.meertens.affection_sender;

import java.io.FileNotFoundException;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class HelloWidgetConfig extends Activity {

	private Button configConfirmButton;
	
	private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private static final String FILENAME = "klef.txt";
    private static final int CONTACT_PICKER_RESULT = 1337;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Initialization code
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);
		setContentView(R.layout.config);
		
		// Add on click listener to the confirmation button.
		configConfirmButton = (Button) findViewById(R.id.exitconfig);
		configConfirmButton.setOnClickListener(configExitOnClickListener);

        // Add on click listener to the contact select button
        configConfirmButton = (Button) findViewById(R.id.contactbutton);
        configConfirmButton.setOnClickListener(configContactOnClickListener);

		// Read the text from the file. 
		try {
			String[] messagesAr = AffectionInOutOperations.readFromFile(FILENAME).split("\n");
			String totalString = "";
			for (String singleMessage : messagesAr)
			{
				totalString+= singleMessage + "\n";
			}
			EditText text = (EditText) findViewById(R.id.editText1);
			text.setText(totalString.toString().substring(0, totalString.length()-1)); // Remove the last newline
		} catch (FileNotFoundException e1) {
			EditText text = (EditText) findViewById(R.id.editText1);
			text.setText("");
		}
	    
	    // Read the number from the file
	    try {
	    	String numberString = AffectionInOutOperations.readFromFile("number.txt");
			EditText number = (EditText) findViewById(R.id.phone);
		    number.setText(numberString.toString());
		} catch (FileNotFoundException e) {
			EditText text = (EditText) findViewById(R.id.editText1);
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
			String kleffeString = ((EditText) findViewById(R.id.editText1)).getText().toString();
			String phoneString = ((EditText)findViewById(R.id.phone)).getText().toString();

			// Write the found strings to the memory
			AffectionInOutOperations.writeToFile(HelloWidgetConfig.FILENAME, kleffeString);
			AffectionInOutOperations.writeToFile("number.txt", phoneString);
			
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
        Log.i("OnResult","Got result");
        // Check which request we're responding to
        if (requestCode == CONTACT_PICKER_RESULT) {
            Log.i("OnResult","Got result 2");
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Log.i("OnResult","Got result 3");
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
                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
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
}