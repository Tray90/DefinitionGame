package com.example.definitiongame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LoginScreen extends Activity {
	private Integer score_;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginscreen);
		score_ = 0;

		final EditText loginBox = (EditText) findViewById(R.id.editText1);
		loginBox.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				// If the event is a key-down event on the "enter" button
				if ((arg2.getAction() == KeyEvent.ACTION_DOWN)
						&& (arg1 == KeyEvent.KEYCODE_ENTER)) {
					// Rejects possible names if they could interfere with
					// future parsing
					if (loginBox.getText().toString().contains(",")
							|| loginBox.getText().toString().contains(";")
							|| loginBox.getText().toString().contains("\\")
							|| loginBox.getText().toString().contains("\"")
							|| loginBox.getText().toString().contains(":")
							|| loginBox.getText().toString().contains("{")
							|| loginBox.getText().toString().contains("}")
							|| loginBox.getText().toString().contains("\n")
							|| loginBox.getText().toString().replace(" ", "")
									.isEmpty()) {

						// Notifies the user that they entered an invalid name
						TextView text = (TextView) findViewById(R.id.textView1);
						text.setText("Invalid Login Name, please try again");
						loginBox.setText("");
						// Runs the Async task if the user name is valid
					} else {
						getUserName gun = new getUserName();
						gun.execute(loginBox.getText().toString());
						return true;
					}
				}
				return false;
			}
		});

	}

	// This AsyncTask gets information from the server
	class getUserName extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... name) {
			String stringResponse = "There was an error";
			try {
				// I had to use my ip address because localhost was giving me
				// problems
				String url = "http://192.168.56.1:8080/";

				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(url);

				HttpResponse response;

				try {
					response = client.execute(request);
					stringResponse = EntityUtils.toString(response.getEntity());
					JSONObject jsonResponse = new JSONObject(stringResponse);
					// If the server contains the user then this sets their
					// score
					if (jsonResponse.has(name[0])) {
						JSONObject score = jsonResponse.getJSONObject(name[0]);
						score_ = score.getInt("score");
						// If the server does not contain the user this runs an
						// async task to add them
					} else {
						postName post = new postName();
						post.execute(name[0]);
						stringResponse = "";
					}

				} catch (ParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return stringResponse;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != "") {
				// Updates the main activity with the user's name and current
				// score and closes this activity
				EditText loginBox = (EditText) findViewById(R.id.editText1);
				MainActivity.setName(loginBox.getText().toString());
				MainActivity.setScore(score_);
				finish();
			}
		}
	}

	class postName extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... name) {
			String stringResponse = "There was an error";
			try {
				String url = "http://192.168.56.1:8080/";
				HttpClient client = new DefaultHttpClient();
				HttpPost request = new HttpPost(url);

				// Adds the arguements to the url, this is a new user so the score is zero
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("loginId", name[0]));
				nameValuePairs.add(new BasicNameValuePair("score", "0"));
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
						nameValuePairs);

				request.setEntity(entity);
				HttpResponse response;

				try {
					response = client.execute(request);
					stringResponse = response.toString();
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return stringResponse;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// Updates the main activity with the user's name and current
			// score and closes this activity
			EditText loginBox = (EditText) findViewById(R.id.editText1);
			MainActivity.setName(loginBox.getText().toString());
			MainActivity.setScore(0);
			finish();
		}
	}
}