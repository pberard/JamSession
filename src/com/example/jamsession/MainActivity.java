package com.example.jamsession;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	public void createAccount(View v)
	{
		System.out.println("Create Account");
		
		setContentView(R.layout.activity_main_login);
	}
	public void back(View v)
	{
		setContentView(R.layout.activity_main);
	}
	
	public void addUser(View V)
	{
		System.out.println("Add User");
		EditText name = (EditText)findViewById(R.id.name);
		EditText email = (EditText)findViewById(R.id.email);
		EditText pass1 = (EditText)findViewById(R.id.password1);
		EditText pass2 = (EditText)findViewById(R.id.password2);
		
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("name", name.getText().toString());
		params.put("email", email.getText().toString());
		params.put("password1", pass1.getText().toString());
		params.put("password2", pass2.getText().toString());
		
		client.get("http://floating-taiga-3502.herokuapp.com/createAccount", params, new AsyncHttpResponseHandler() 
		{
		    @Override
		    public void onSuccess(String response) 
		    {
		    	boolean success = false;
		        int id = 0;
		        String error = "";
		        JSONTokener parser = new JSONTokener(response);
		        try {
					JSONObject json = new JSONObject (parser);
					success = (boolean) json.get("success");
					System.out.println("Success: " + success);
					id = (int) json.get("userID");
					error = (String) json.get("error");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        if(success)
		        {
		    		loginSuccess(id);
		        }
		        else
		        {
			        createAccountFailure(error);
			    }
		    }
		});
	}
	private void createAccountFailure(String error)
	{
		TextView err = (TextView)findViewById(R.id.error);
		err.setText(error);
	}
	private void loginSuccess(int id)
	{
		Intent intent = new Intent(this.getBaseContext(), HomeActivity.class);
		intent.putExtra("userID", id);
		startActivity(intent);
	}
	public void login(View v)
	{
		System.out.println("Login User");
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		
		EditText email = (EditText)findViewById(R.id.loginEmail);
		EditText password = (EditText)findViewById(R.id.loginPassword);

		params.put("email", email.getText().toString());
		params.put("password", password.getText().toString());
//		params.put("email", "pberard@carthage.edu");
//		params.put("password", "pass");
		client.get("http://floating-taiga-3502.herokuapp.com/login", params, new AsyncHttpResponseHandler() 
		{
		    @Override
		    public void onSuccess(String response) 
		    {
		    	//parse some json
		       // System.out.println(response);
		        boolean success = false;
		        int id = 0;
		        String error = "";
		        JSONTokener parser = new JSONTokener(response);
		        try {
					JSONObject json = new JSONObject (parser);
					success = (boolean) json.get("success");
					id = (int) json.get("userID");
					error = (String) json.get("error");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        if(success)
		        {
		    		loginSuccess(id);
		        }
		        else
		        {
			        loginFailure(error);
			    }
		    }
		});
	}
	private void loginFailure(String error)
	{
		TextView err = (TextView)findViewById(R.id.loginError);
		err.setText(error);
	}
}
