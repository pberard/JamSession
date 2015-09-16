package com.example.jamsession;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import Objects.Jam;
import Objects.User;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.jamsession.audiorecorder.AudioRecorder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class UpdateList extends ActionBarActivity 
{
	private User currentUser;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_list);

		if (savedInstanceState == null) 
		{
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		//Get list of updates from the server
		Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        currentUser = new User(extras.getInt("userID"));
        updateJams();
        getAllJams();
	}
	
	public void updateJams(View v)
	{
		updateJams();
		getAllJams();
	}
	public void updateJams()
	{
		System.out.println("Update Jams");
		if(isOnline())
		{
			AsyncHttpClient client = new AsyncHttpClient();
			RequestParams params = new RequestParams();
			params.put("userID", Integer.toString(currentUser.getId()));
			client.get("http://floating-taiga-3502.herokuapp.com/getUpdates", params, new AsyncHttpResponseHandler() 
			{
			    @SuppressLint("NewApi")
				@Override
			    public void onSuccess(String response) 
			    {
			        JSONTokener parser = new JSONTokener(response);
			        ArrayList<Jam> jams = new ArrayList<Jam>();
			        try {
						JSONObject json = new JSONObject (parser);
						Iterator itr = json.keys();
						
						while(itr.hasNext())
						{
							Object ob = itr.next(); //key for the next json object
							JSONTokener jtk = new JSONTokener(json.get(ob.toString()).toString());
							JSONObject jamJSON = new JSONObject(jtk);
							
							//parse through jam json
							Jam jam = new Jam((int) jamJSON.get("id"), (int) jamJSON.get("ttl"), (int) jamJSON.get("user_id"));
							User user = new User((int) jamJSON.get("user_id"));
							user.setName((String)jamJSON.get("user_name"));
							jam.setUser(user);
							jams.add(jam);
						}
						setNewUpdateListAdapter(jams);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
			});
		}
	}
	public void getAllJams()
	{
		System.out.println("Update All Jams");
		if(isOnline())
		{
			AsyncHttpClient client = new AsyncHttpClient();
			RequestParams params = new RequestParams();
			params.put("userID", Integer.toString(currentUser.getId()));
			client.get("http://floating-taiga-3502.herokuapp.com/getAllUpdates", params, new AsyncHttpResponseHandler() 
			{
			    @SuppressLint("NewApi")
				@Override
			    public void onSuccess(String response) 
			    {
			        JSONTokener parser = new JSONTokener(response);
			        ArrayList<Jam> jams = new ArrayList<Jam>();
			        try {
						JSONObject json = new JSONObject (parser);
						Iterator itr = json.keys();
						
						while(itr.hasNext())
						{
							Object ob = itr.next(); //key for the next json object
							JSONTokener jtk = new JSONTokener(json.get(ob.toString()).toString());
							JSONObject jamJSON = new JSONObject(jtk);
							
							//parse through jam json
							Jam jam = new Jam((int) jamJSON.get("id"), (int) jamJSON.get("ttl"), (int) jamJSON.get("user_id"));
							User user = new User((int) jamJSON.get("user_id"));
							user.setName((String)jamJSON.get("user_name"));
							jam.setUser(user);
							jams.add(jam);
						}
						setAllUpdateListAdapter(jams);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
			});
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
	public void setNewUpdateListAdapter(ArrayList<Jam> jams)
	{
		System.out.println("Set Jam List Adapter" + jams.toString());
		ArrayAdapter<Jam> adapter = new ArrayAdapter<Jam>(this.getApplicationContext(),
				android.R.layout.simple_list_item_1, jams);
		final ListView listview = (ListView) findViewById(R.id.newUpdatesListView);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() 
		{
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	        {
	        	//When one of the things on the list is clicked, create a new audioRecorder that will add to a jam
	        	Jam jam = (Jam) listview.getItemAtPosition(position);
	        	collaborate(jam);
	        }
	    });
	}
	public void setAllUpdateListAdapter(ArrayList<Jam> jams)
	{
		System.out.println("Set All List Adapter" + jams.toString());
		ArrayAdapter<Jam> adapter = new ArrayAdapter<Jam>(this.getApplicationContext(),
				android.R.layout.simple_list_item_1, jams);
		final ListView listview = (ListView) findViewById(R.id.allUpdateListView);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() 
		{
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	        {
	        	//When one of the things on the list is clicked, create a new audioRecorder that will add to a jam
	        	Jam jam = (Jam) listview.getItemAtPosition(position);
	        	collaborate(jam);
	        }
	    });
	}
	public void collaborate(Jam jam)
	{
		Intent intent = new Intent(this.getBaseContext(), AudioRecorder.class);
		//add data to this intent to specify that this is for a new jam
		intent.putExtra("isJam", true);
		intent.putExtra("userID", currentUser.getId());
		intent.putExtra("jamID", jam.getId());
		startActivity(intent);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.update_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_update_list,
					container, false);
			return rootView;
		}
	}
}
