/*
 * The application needs to have the permission to write to external storage
 * if the output file is written to the external storage, and also the
 * permission to record audio. These permissions must be set in the
 * application's AndroidManifest.xml file, with something like:
 *
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.RECORD_AUDIO" />
 *
 */
package com.example.jamsession.audiorecorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import Objects.Jam;
import Objects.Song;
import Objects.User;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.example.jamsession.HomeActivity;
import com.example.jamsession.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;



public class AudioRecorder extends Activity
{
    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;
    private static String mUniqueFileName = null;
    private static String mFolderName = null;

    private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;

    private PlayButton   mPlayButton = null;
    private MediaPlayer   mPlayer = null;
    private ArrayList<MediaPlayer> jamPlayers = null;
    private ArrayList<String> fileNames = null;
    private JamButton mJamButton = null;
    
    private Jam myJam = null;
    private User currentUser;

    private void onRecord(boolean start) 
    {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) 
    {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() 
    {
    	System.out.println("Start Playing");
        mPlayer = new MediaPlayer();
        
        try 
        {	
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed for first media player");
        }
    	System.out.println("My Media player finished");
    	jamPlayers.clear();
        for(String file : fileNames)
        {
        	try
        	{
	        	MediaPlayer mp = new MediaPlayer();
	        	mp.setDataSource(file);
	        	mp.prepare();
	        	jamPlayers.add(mp);
	        	System.out.println("Prepared " + file);
        	 } catch (IOException e) {
                 Log.e(LOG_TAG, "prepare() failed");
             }
        }
    	System.out.println("Other media players finished");

        mPlayer.start();
        for(MediaPlayer mp : jamPlayers)
        {
        	System.out.println("Starting other");
        	mp.start();
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
        for(MediaPlayer mp : jamPlayers)
        {
        	mp.release();
        	mp = null;
        }
    }

    private void startRecording() 
    {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try 
        {
            mRecorder.prepare();
        } catch (IOException e) 
        {
            Log.e(LOG_TAG, "prepare() failed");
            Log.e(LOG_TAG, "ioexception: " + e.getMessage());
            
        }
        
        jamPlayers.clear();
        for(String file : fileNames)
        {
        	try
        	{
	        	MediaPlayer mp = new MediaPlayer();
	        	mp.setDataSource(file);
	        	mp.prepare();
	        	jamPlayers.add(mp);
	        	System.out.println("Prepared " + file);
        	 } catch (IOException e) {
                 Log.e(LOG_TAG, "prepare() failed");
             }
        }

        mRecorder.start();
        
        for(MediaPlayer mp : jamPlayers)
        {
        	System.out.println("Starting other");
        	mp.start();
        }
    }

    private void stopRecording() 
    {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        for(MediaPlayer mp : jamPlayers)
        {
        	mp.release();
        	mp = null;
        }
    }

    class RecordButton extends Button 
    {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() 
        {
            public void onClick(View v) 
            {
                onRecord(mStartRecording);
                if (mStartRecording) 
                {
                    setText("End");
                } 
                else 
                {
                    setText("Record");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) 
        {
            super(ctx);
            setText("Record");
            setOnClickListener(clicker);
        }
    }

    class PlayButton extends Button 
    {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() 
        {
            public void onClick(View v) 
            {
                onPlay(mStartPlaying);
                if (mStartPlaying) 
                {
                    setText("Stop");
                } 
                else 
                {
                    setText("Play");
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Context ctx) 
        {
            super(ctx);
            setText("Play");
            setOnClickListener(clicker);
        }
    }
    class JamButton extends Button 
    {
    	
        OnClickListener clicker = new OnClickListener() 
        {
            public void onClick(View v) 
            {
            	System.out.println("JAM!");
            	
        		setContentView(R.layout.collaborator_select);
        		if(isOnline())
        		{
	        		AsyncHttpClient client = new AsyncHttpClient();
	    			RequestParams params = new RequestParams();
	    			client.get("http://floating-taiga-3502.herokuapp.com/allUsers", params, new AsyncHttpResponseHandler() 
	    			{
	    			    @SuppressLint("NewApi")
	    				@Override
	    			    public void onSuccess(String response) 
	    			    {
	    			        JSONTokener parser = new JSONTokener(response);
	    			        ArrayList<User> users = new ArrayList<User>();
	    			        try {
	    						JSONObject json = new JSONObject (parser);
	    						Iterator itr = json.keys();
	    						while(itr.hasNext())
	    						{
	    							Object ob = itr.next();
	    							JSONTokener jtk = new JSONTokener(json.get(ob.toString()).toString());
	    							JSONObject userJSON = new JSONObject(jtk);
	    							
	    							User user = new User((String) userJSON.get("name"), (String) userJSON.get("email"), (int) userJSON.get("id"));
	    							users.add(user);
	    						}
	    						setListAdapter(users);
	    					} catch (JSONException e) {
	    						e.printStackTrace();
	    					}
	    			    }
	    			});
        		}
            }
        };

        public JamButton(Context ctx) 
        {
            super(ctx);
            setText("Jam!");
            setOnClickListener(clicker);
        }
    }
    public AudioRecorder() 
    {
    	mFolderName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/";
    }

    @Override
    public void onCreate(Bundle icicle) 
    {    	
        super.onCreate(icicle);
        RelativeLayout rl = new RelativeLayout(this);
        rl.setBackgroundColor(Color.parseColor("#a2d8fd"));
        rl.setGravity(Gravity.TOP);
        
        mRecordButton = new RecordButton(this);
        mRecordButton.setId(9001);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        rl.addView(mRecordButton, lp);

        mPlayButton = new PlayButton(this);
        lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.LEFT_OF, mRecordButton.getId());
     //   lp.addRule(RelativeLayout.ALIGN_TOP, mRecordButton.getId());
        rl.addView(mPlayButton, lp);
        
        mJamButton = new JamButton(this);
        lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.RIGHT_OF, mRecordButton.getId());
    //    lp.addRule(RelativeLayout.ALIGN_TOP, mRecordButton.getId());
        rl.addView(mJamButton, lp);
        setContentView(rl);
        	
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        currentUser = new User(extras.getInt("userID"));
        //set unique filename
    	SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssZ");
    	String date = sdf.format(new Date());
    	mUniqueFileName = Integer.toString(currentUser.getId()) + "-" +  date + "-" + "audiorecord.3gp";
    	System.out.println(mUniqueFileName);
    	mFileName += mUniqueFileName;
    	
    	jamPlayers = new ArrayList<MediaPlayer>();
    	fileNames = new ArrayList<String>();
    	
        if(extras.getBoolean("isJam"))
        {        	
        	System.out.println("Adding to jam");
        	myJam = new Jam(extras.getInt("jamID"));
        	setUpCollaboration(extras.getInt("jamID"));
        	//this will add to a collaboration.  Get the rest of the songs and play them at the same time
        	//get extra for jam id
//        	myJam = new Jam(/*jam id, user id, ttl? */);
        }
        else
        {
        	//create a new jam, details will be filled in once the jam is sent to the server
        	myJam = new Jam();
        } 
    }
	public boolean isOnline() 
	{
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo ni = cm.getActiveNetworkInfo();
	    if (ni!=null && ni.isAvailable() && ni.isConnected()) {
	        return true;
	    } else {
	        return false; 
	    }
	}
    public void setUpCollaboration(int jamID)
    {
    	System.out.println("Setting up Collaboration");
    	if(isOnline())
		{
			AsyncHttpClient client = new AsyncHttpClient();
			RequestParams params = new RequestParams();
			params.put("jamID", Integer.toString(jamID));
			System.out.println("Making Request");
			client.get("http://floating-taiga-3502.herokuapp.com/getSongsForJam", params, new AsyncHttpResponseHandler() 
			{
			    @SuppressLint("NewApi")
				@Override
			    public void onSuccess(String response) 
			    {
		        	System.out.println("Got songs for jam");
			        JSONTokener parser = new JSONTokener(response);
			        try {
						JSONObject json = new JSONObject (parser);
						Iterator itr = json.keys();
						ArrayList<Song> songs = new ArrayList<Song>();
						while(itr.hasNext())
						{
							Object ob = itr.next(); //key for the next json object
							
							JSONTokener jtk = new JSONTokener(json.get(ob.toString()).toString());
							JSONObject songJSON = new JSONObject(jtk);
						
							Song song = new Song((int)songJSON.get("id"));//id for a song
							song.setFilepath((String)songJSON.get("filename"));
							System.out.println("Got a song dudezzzzzz" + json.get(ob.toString()));
							songs.add(song);
						}
						for(final Song s : songs)
						{
							System.out.println("Song: " + Integer.toString(s.getId()));
							AsyncHttpClient client2 = new AsyncHttpClient();
							RequestParams params2 = new RequestParams();
							params2.put("songID", Integer.toString(s.getId()));
							String[] allowedContentTypes = new String[] { "audio/mpeg" };
							client2.get("http://floating-taiga-3502.herokuapp.com/getSong", params2, new BinaryHttpResponseHandler(allowedContentTypes) {
							    @Override
							    public void onSuccess(byte[] fileData) 
							    {
							    	try 
							    	{
							    		// Do something with the file
							    		String filename = AudioRecorder.mFolderName + s.getFilepath();
							    		FileOutputStream fos = new FileOutputStream(filename);
										fos.write(fileData);
										fos.close();
										addFile(filename);
									} catch (IOException e) {
										e.printStackTrace();
									}
							    }
							});
							
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
			    }
			});
		}

    }
    public void addFile(String filename)
    {
    	System.out.println("Adding file: " + filename);
    	this.fileNames.add(filename);
    }
    @Override
    public void onPause() 
    {
        super.onPause();
        if (mRecorder != null) 
        {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) 
        {
            mPlayer.release();
            mPlayer = null;
        }
        for(MediaPlayer mp : jamPlayers)
        {
        	if(mp != null)
        	{
	        	mp.release();
	        	mp = null;
        	}
        }
    }
    public void setListAdapter(ArrayList<User> users)
	{
		ArrayAdapter<User> adapter = new ArrayAdapter<User>(this.getApplicationContext(),
				android.R.layout.simple_list_item_1, users);
		final ListView listview = (ListView) findViewById(R.id.collaboratorListView);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() 
		{
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	        {
	        	User collaborator = (User) listview.getItemAtPosition(position);
	        	System.out.println(collaborator.toString());
	        	//create a collaboration with this user
	        	if(isOnline())
	    		{
	        		System.out.println("My Jam: " + Integer.toString(myJam.getId()));
	        		if(myJam.getId() == 0)
	        		{
	        			System.out.println("Id = 0");
			        	AsyncHttpClient client = new AsyncHttpClient();
		        		RequestParams params = new RequestParams();
		        		params.put("userID", Integer.toString(currentUser.getId()));
		        		params.put("collaboratorID", Integer.toString(collaborator.getId()));
		        		params.put("ttl", "5");//MAGIC VALUE let the user change this in the future
		        		File file = new File(mFileName);
		        		try 
		        		{
							params.put("song", file);
						} catch (FileNotFoundException e) 
						{
							e.printStackTrace();
						}
		        		params.put("filename", mUniqueFileName);
		        		//need to send the file, userid, jamID?, ttl(5), filename
		        		//	#params = userID, ttl, song, filename
		        		
		        		client.post("http://floating-taiga-3502.herokuapp.com/createJam", params, new AsyncHttpResponseHandler() 
		        		{
		        		    @Override
		        		    public void onSuccess(String response) 
		        		    {
		        		    	System.out.println("Success!!!");
		        		    	boolean success = false;
		        		        int id = 0;
		        		        
		        		        JSONTokener parser = new JSONTokener(response);
		        		    	JSONObject json;
								try 
								{
									json = new JSONObject (parser);	
			    					success = (boolean) json.get("success");
			    					id = (int) json.get("jamID");
			    					System.out.println("Success: " + success);
			    					System.out.println("JamID: " + id);
			    					toHomeScreen();
								} 
								catch (JSONException e) 
								{
									e.printStackTrace();
								}
		        		    }
		        		});
			    }
	        	else
	        	{
	        		System.out.println("Adding to Jam");
	        		AsyncHttpClient client = new AsyncHttpClient();
	        		RequestParams params = new RequestParams();
	        		params.put("userID", Integer.toString(currentUser.getId()));
	        		params.put("collaboratorID", Integer.toString(collaborator.getId()));
	        		params.put("ttl", "5");//MAGIC VALUE let the user change this in the future
	        		params.put("jamID", Integer.toString(myJam.getId()));
	        		File file = new File(mFileName);
	        		try 
	        		{
						params.put("song", file);
					} catch (FileNotFoundException e) 
					{
						e.printStackTrace();
					}
	        		params.put("filename", mUniqueFileName);
	        		System.out.println("Got to post");
	        		client.post("http://floating-taiga-3502.herokuapp.com/updateJam", params, new AsyncHttpResponseHandler() 
	        		{
	        		    public void onSuccess(String response) 
	        		    {
	        		    	System.out.println("Success!!!");
	        		    	boolean success = false;
	        		        int id = 0;
	        		        
	        		        JSONTokener parser = new JSONTokener(response);
	        		    	JSONObject json;
							try 
							{
								json = new JSONObject (parser);	
		    					success = (boolean) json.get("success");
		    					id = (int) json.get("jamID");
		    					System.out.println("Success: " + success);
		    					System.out.println("JamID: " + id);
		    					toHomeScreen();
							} 
							catch (JSONException e) 
							{
								e.printStackTrace();
							}
	        		    }
	        		});
		        }
	        }
	        }
	    });
	}
    public void toHomeScreen()
    {
    	Intent intent = new Intent(this.getBaseContext(), HomeActivity.class);
		intent.putExtra("userID", currentUser.getId());
		startActivity(intent);
    }
}