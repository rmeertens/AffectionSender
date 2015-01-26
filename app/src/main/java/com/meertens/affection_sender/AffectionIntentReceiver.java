package com.meertens.affection_sender;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.meertens.affection_sender.R;

public class AffectionIntentReceiver extends BroadcastReceiver {
	private static final String FILENAME = "klef.txt";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals("com.meertens.SEND_MESSAGE")){
			updateWidgetPictureAndButtonListener(context);
		}
		else if(intent.getAction().equals("com.meertens.CHANGE_MESSAGES"))
		{
			// Start the configuration screen. 
			Intent i = new Intent(context,HelloWidgetConfig.class);
			i.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
		    context.startActivity(i);
		}
	}

	private void updateWidgetPictureAndButtonListener(Context context) {
		// TODO What does this do???
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_demo);
		
	    boolean gotNumberAndMessages = true;
		
		// Load the phone number 
	    String phoneNumber = null ;
	    try {
	    	phoneNumber  = AffectionInOutOperations.readFromFile("number.txt");
		} catch (FileNotFoundException e) {
			Toast toast2 = Toast.makeText(context, "Go to options (click image) to set love", Toast.LENGTH_SHORT);
		    toast2.show();
			gotNumberAndMessages = false;
		}
	    

	    // Load loving messages
	    ArrayList<String> messages = new ArrayList<String>();
		try {
			String[] messagesAr = AffectionInOutOperations.readFromFile(FILENAME).split("\n");
			messages = new ArrayList<String>(Arrays.asList(messagesAr));
		} catch (FileNotFoundException e) {
			Toast toast2 = Toast.makeText(context, "Go to options (click image) to set number", Toast.LENGTH_SHORT);
		    toast2.show();
			gotNumberAndMessages = false;
		}
	    
	    // Check if we have all information. If yes: proceed to send text. 
	    if(gotNumberAndMessages)
	    {
	    	
		    // Actually send the text
	    	Random r = new Random();
	    	try{
			    String message = messages.get(r.nextInt(messages.size()));
		    	sendText(phoneNumber,message, context);
		    	// Show that we are sending something
			    Toast toast = Toast.makeText(context, "Sending love...", Toast.LENGTH_SHORT);
			    toast.show();
				
	    	}
	    	catch(IllegalArgumentException e)
	    	{
	    		Toast toast = Toast.makeText(context, "Unable to send love, make sure your settings are correct.", Toast.LENGTH_SHORT);
			    toast.show();
	    	}
	    	
	    }
	    
		//REMEMBER TO ALWAYS REFRESH YOUR BUTTON CLICK LISTENERS!!!
		remoteViews.setOnClickPendingIntent(R.id.widget_button, AffectionCommandProvider.buildButtonPendingIntent(context));
		AffectionCommandProvider.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
	}
	
	private void sendText(String phoneNumber, String message, Context context)
	{
		// TODO move this to another class...
		// Send the text
	    SmsManager smsManager = SmsManager.getDefault();
	    smsManager.sendTextMessage(phoneNumber, null, message.substring(0, Math.min(message.length(), 159)), null, null);
	    
		// Add the text to the messages app of your phone 	    
	    ContentValues values = new ContentValues(); 
	    values.put("address", phoneNumber); 
	    values.put("body", message); 
	    context.getContentResolver().insert(Uri.parse("content://sms/sent"), values);		
	}

}