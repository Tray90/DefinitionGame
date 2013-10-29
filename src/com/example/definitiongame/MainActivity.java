package com.example.definitiongame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {
	private SensorManager mSensorManager;
	private Sensor mTemperature;
	private Sensor mLight;
	private String choice1_;
	private String choice2_;
	private String choice3_;
	private String nextAnswer_;
	private String currentAnswer_;
	// These are static so that it may be changed outside of this function
	static Integer score_ = 0;
	static String loginName_;
	private String definition_;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		choice1_ = "";
		choice2_ = "";
		choice3_ = "";
		definition_ = "";
		score_ = 0;
		setContentView(R.layout.activity_main);
		getChoices myRequest = new getChoices();
		myRequest.execute();

		// Sets up sensor manager
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mTemperature = mSensorManager
				.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
		mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

		// Registers listeners
		mSensorManager.registerListener(this, mTemperature,
				SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mLight,
				SensorManager.SENSOR_DELAY_NORMAL);

		// Opens the login screen
		final Intent LoginIntent = new Intent(this, LoginScreen.class);
		startActivity(LoginIntent);

		// Initializes buttons and sets their click events
		final Button button1 = (Button) findViewById(R.id.button1);
		final Button button2 = (Button) findViewById(R.id.button2);
		final Button button3 = (Button) findViewById(R.id.button3);
		Button leaderboard = (Button) findViewById(R.id.leaderboard);
		Button logout = (Button) findViewById(R.id.logout);

		button1.setText("New Definition");
		button2.setVisibility(View.GONE);
		button3.setVisibility(View.GONE);

		// Intent for the leaderboard activity
		final Intent leaderboardIntent = new Intent(this, Leaderboard.class);

		leaderboard.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					// Opens the leaderboard when the corresponding button is
					// pressed
					startActivity(leaderboardIntent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		logout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					// Opens the leaderboard when the corresponding button is
					// pressed
					TextView score = (TextView) findViewById(R.id.score);
					score.setText("");
					startActivity(LoginIntent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		

		// These click events handle correct and incorrect answers
		button1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					TextView text = (TextView) findViewById(R.id.definition);
					TextView score = (TextView) findViewById(R.id.score);
					if (button1.getText() == currentAnswer_) {
						// Increases the score and lets the user know that they
						// were correct
						score_++;
						text.setText("Correct!");
						button1.setText("New Definition");
						button2.setVisibility(View.GONE);
						button3.setVisibility(View.GONE);
					} else if (button1.getText() == "New Definition") {
						// Updates the UI with
						// new choices
						updateUI();
					} else {
						// Outputs the correct answer if the user is wrong
						text.setText("Incorrect, the correct answer was: "
								+ currentAnswer_);
						button1.setText("New Definition");
						button2.setVisibility(View.GONE);
						button3.setVisibility(View.GONE);
					}
					score.setText("Score: " + score_.toString());

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		button2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					TextView text = (TextView) findViewById(R.id.definition);
					TextView score = (TextView) findViewById(R.id.score);
					if (button2.getText() == currentAnswer_) {
						score_++;
						text.setText("Correct!");
						button1.setText("New Definition");
						button2.setVisibility(View.GONE);
						button3.setVisibility(View.GONE);
					} else {
						text.setText("Incorrect, the correct answer was: "
								+ currentAnswer_);
						button1.setText("New Definition");
						button2.setVisibility(View.GONE);
						button3.setVisibility(View.GONE);
					}
					score.setText("Score: " + score_.toString());

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		button3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					TextView text = (TextView) findViewById(R.id.definition);
					TextView score = (TextView) findViewById(R.id.score);
					if (button3.getText() == currentAnswer_) {
						score_++;
						text.setText("Correct!");
						button1.setText("New Definition");
						button2.setVisibility(View.GONE);
						button3.setVisibility(View.GONE);
					} else {
						text.setText("Incorrect, the correct answer was: "
								+ currentAnswer_);
						button1.setText("New Definition");
						button2.setVisibility(View.GONE);
						button3.setVisibility(View.GONE);
					}
					score.setText("Score: " + score_.toString());

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		// Register a listener for the sensors on resume
		super.onResume();

		TextView score = (TextView) findViewById(R.id.score);
		score.setText("Score: " + score_.toString());
		mSensorManager.registerListener(this, mTemperature,
				SensorManager.SENSOR_DELAY_NORMAL);
		updateUI();
	}

	@Override
	protected void onPause() {
		// Unregister the sensor when the activity pauses.
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void updateUI() {

		currentAnswer_ = nextAnswer_;

		// Puts the definition in the text view
		TextView definitionView = (TextView) findViewById(R.id.definition);
		definitionView.setText(definition_);
		
		TextView score = (TextView) findViewById(R.id.score);
		try {
			score.setText(score_);
		}
		catch (Exception e) {
			
		}

		// Writes the choices in the buttons and makes them visible
		Button button1 = (Button) findViewById(R.id.button1);
		button1.setText(choice1_);
		button1.setVisibility(View.VISIBLE);
		Button button2 = (Button) findViewById(R.id.button2);
		button2.setText(choice2_);
		button2.setVisibility(View.VISIBLE);
		Button button3 = (Button) findViewById(R.id.button3);
		button3.setText(choice3_);
		button3.setVisibility(View.VISIBLE);

		// Retrieves new choices
		getChoices myRequest = new getChoices();
		myRequest.execute();
		
		postScore post = new postScore();
		post.execute();
	}

	// Accessor so that this may be changed from another activity
	static void setScore(Integer newScore) {
		score_ = newScore;
	}

	static void setName(String userName) {
		loginName_ = userName;
	}

	// Retrieves choices from RandomWordGenerator.net
	class getChoices extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... unusedString) {
			String stringResponse = "There was an error";
			try {
				String url = "http://randomwordgenerator.net/results.php";

				HttpClient client = new DefaultHttpClient();

				HttpPost request = new HttpPost(url);

				// Adds the arguements to the url
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						3);
				nameValuePairs.add(new BasicNameValuePair("length", "Any!"));
				nameValuePairs.add(new BasicNameValuePair("startwith", "Any!"));
				nameValuePairs.add(new BasicNameValuePair("amount", "3"));
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
						nameValuePairs);

				request.setEntity(entity);
				HttpResponse response;

				try {
					response = client.execute(request);
					stringResponse = parse(EntityUtils.toString(response
							.getEntity()));
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

		protected String parse(String stringToBeParsed) {
			String parsedResult = stringToBeParsed.substring(stringToBeParsed
					.indexOf("Your word(s):<br/><br/>") + 24);
			parsedResult = parsedResult.substring(0,
					parsedResult.indexOf("<br/><br/><br/>"));
			parsedResult = parsedResult.replace("<br/>", ",");
			return parsedResult;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != "There was an error") {
				String[] listResponse;
				listResponse = result.split(",");

				// Error check
				if (listResponse.length == 3) {
					// Updates the choice variables to the new choices
					choice1_ = listResponse[0];
					choice2_ = listResponse[1];
					choice3_ = listResponse[2];

					defineWord myRequest = new defineWord();

					// Creates a random number from 0 to 2, then gets the
					// definition for the corresponding word and has it defined
					Random rand = new Random();
					int n = rand.nextInt(3);

					switch (n) {
					case 0:
						myRequest.execute(choice1_);
						nextAnswer_ = choice1_;
						break;
					case 1:
						myRequest.execute(choice2_);
						nextAnswer_ = choice2_;
						break;
					default:
						myRequest.execute(choice3_);
						nextAnswer_ = choice3_;
						break;
					}

				}

				else {
					choice1_ = "Error";
					choice2_ = "Error";
					choice3_ = "Error";
					definition_ = "There has been an error, please click one of the buttons to continue.";
				}
			}
		}
	}

	// Defines a word and adds its definition to the definition_ variable
	class defineWord extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... word) {
			String definition = "There was an error";
			try {
				// Formats the url then calls httpGet on the created url
				String url = "http://dictionary.reference.com/browse/";
				url += word[0];
				url += "?s=t";

				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(url);
				HttpResponse response;

				try {
					// Gets the http response and stores it as a url
					response = client.execute(request);
					String stringResponse = EntityUtils.toString(response
							.getEntity());

					// Retrieves the definition from the response and formats it
					definition = stringResponse
							.substring(stringResponse
									.indexOf("<meta name=\"description\" content=\"") + 34);
					definition = definition.substring(0,
							definition.indexOf("\"/>") - 1);

					definition = definition.toLowerCase().replace(
							word[0] + " definition, ", "");
					definition = definition.replace(". see more", "");

					definition = definition.substring(0,
							definition.indexOf(":"));

				} catch (ParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return definition;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != "There was an error") {
				// This string occurs on undefined words so getChoices is run
				// again to get new words
				if (result.contains("definition at Dictionary.com")) {
					getChoices myRequest = new getChoices();
					myRequest.execute();
				} else
					definition_ = result;
			} else {
				getChoices myRequest = new getChoices();
				myRequest.execute();
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {

	}

	// Changes the color of the buttons' background based on the current
	// temperature
	// Also changes the background color based on the current light level
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor == mTemperature) {
			float temp = event.values[0];

			Button button1 = (Button) findViewById(R.id.button1);
			Button button2 = (Button) findViewById(R.id.button2);
			Button button3 = (Button) findViewById(R.id.button3);
			Button leaderboard = (Button) findViewById(R.id.leaderboard);
			if (temp < 0) {
				button1.setBackgroundColor(Color.BLUE);
				button2.setBackgroundColor(Color.BLUE);
				button3.setBackgroundColor(Color.BLUE);
				leaderboard.setBackgroundColor(Color.BLUE);
			} else if (temp < 15) {
				button1.setBackgroundColor(Color.CYAN);
				button2.setBackgroundColor(Color.CYAN);
				button3.setBackgroundColor(Color.CYAN);
				leaderboard.setBackgroundColor(Color.CYAN);
			} else if (temp < 30) {
				button1.setBackgroundColor(Color.YELLOW);
				button2.setBackgroundColor(Color.YELLOW);
				button3.setBackgroundColor(Color.YELLOW);
				leaderboard.setBackgroundColor(Color.YELLOW);
			} else {
				button1.setBackgroundColor(Color.RED);
				button2.setBackgroundColor(Color.RED);
				button3.setBackgroundColor(Color.RED);
				leaderboard.setBackgroundColor(Color.RED);
			}
		} else if (event.sensor == mLight) {
			float lux = event.values[0];
			View view = this.getWindow().getDecorView();
			TextView tv = (TextView) findViewById(R.id.definition);
			TextView scoreView = (TextView) findViewById(R.id.score);

			if (lux < 10) {
				view.setBackgroundColor(color.background_dark);
				tv.setTextColor(Color.WHITE);
				scoreView.setTextColor(Color.WHITE);
			} else {
				view.setBackgroundColor(color.background_light);
				tv.setTextColor(Color.BLACK);
				scoreView.setTextColor(Color.BLACK);
			}
		}

	}

	class postScore extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... name) {
			String stringResponse = "There was an error";
			try {
				String url = "http://192.168.56.1:8080/";

				HttpClient client = new DefaultHttpClient();

				HttpPost request = new HttpPost(url);

				// Adds the arguements to the url
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						2);
				nameValuePairs.add(new BasicNameValuePair("loginId", loginName_));
				nameValuePairs.add(new BasicNameValuePair("score", score_.toString()));
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
	}

}
