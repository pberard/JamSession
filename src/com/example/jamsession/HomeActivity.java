package com.example.jamsession;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

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

import com.example.jamsession.audiorecorder.AudioRecorder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HomeActivity extends ActionBarActivity 
{
	private User currentUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		Bundle extras = getIntent().getExtras();
		currentUser = new User(extras.getInt("userID"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;}
		return super.onOptionsItemSelected(item);}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		public PlaceholderFragment() {}
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_home, container,
					false);
			return rootView;}}
	
	public void clickRight(View v)
	{
		Intent intent = new Intent(this.getBaseContext(), FriendsList.class);
		intent.putExtra("userID", currentUser.getId());
		startActivity(intent);
	}
	public void clickLeft(View v)
	{
		Intent intent = new Intent(this.getBaseContext(), UpdateList.class);
		intent.putExtra("userID", currentUser.getId());
		startActivity(intent);
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
	public void logout(View v)
	{
		Intent intent = new Intent(this.getBaseContext(), MainActivity.class);
		startActivity(intent);
	}
	public void clickCenter(View v)
	{
		Intent intent = new Intent(this.getBaseContext(), AudioRecorder.class);
		//add data to this intent to specify that this is for a new jam
		System.out.println("Record");
		intent.putExtra("isJam", false);
		intent.putExtra("userID", currentUser.getId());
		startActivity(intent);
	}
}
