package com.example.jamsession;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import Objects.User;
import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class FriendsList extends ActionBarActivity 
{
	//TODO Create an async task to update friend requests, approvals, etc
	private User currentUser;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_list);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		Bundle extras = getIntent().getExtras();
		currentUser = new User(extras.getInt("userID"));
		
		//TODO get friends
		updateFriendsList();
		
	}
	public void updateFriendsList(View v)
	{
		updateFriendsList();
	}
	public void updateFriendsList()
	{
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
							Object ob = itr.next(); //key for the next json object
							JSONTokener jtk = new JSONTokener(json.get(ob.toString()).toString());
							JSONObject userJSON = new JSONObject(jtk);
							
							User user = new User((String) userJSON.get("name"), (String) userJSON.get("email"), (int) userJSON.get("id"));
							users.add(user);
						}
						setListAdapter(users);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
			});
		}
	}
	public void setListAdapter(ArrayList<User> users)
	{
		ArrayAdapter<User> adapter = new ArrayAdapter<User>(this.getApplicationContext(),
				android.R.layout.simple_list_item_1, users);
		final ListView listview = (ListView) findViewById(R.id.friendsListView);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() 
		{
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
	        {
	        	User user = (User) listview.getItemAtPosition(position);
	        	System.out.println(user.toString());
	        }
	    });
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
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friends_list, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_friends_list,
					container, false);
			return rootView;
		}
	}

}
