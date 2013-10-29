package com.example.definitiongame;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Leaderboard extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Sets up the leaderboard UI
		setContentView(R.layout.leaderboard);
		TextView leaderboard = (TextView) findViewById(R.id.leaderboard);
		leaderboard.setTextSize(20);
		leaderboard.setText("Loading Leaderboard...");

		// Runs an async task to populate the leaderboard
		populateLeaderboard lb = new populateLeaderboard();
		lb.execute();

		// Sets a listener for the back button to close this activity
		Button back = (Button) findViewById(R.id.button1);
		back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	class populateLeaderboard extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... name) {
			String stringResponse = "There was an error";
			String leaderBoardString = "";
			try {
				// Uses the ip address because localhost was giving me issues
				String url = "http://192.168.56.1:8080/";
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(url);
				HttpResponse response;

				try {
					// Gets the http response and stores it as a url
					response = client.execute(request);
					stringResponse = EntityUtils.toString(response.getEntity());

					// Sets the response to a JSONObject
					JSONObject jsonResponse = new JSONObject(stringResponse);
					String iterationString = stringResponse;
					String tempName;
					String tempScore;
					// Creates a treemap so that the values are order
					TreeMap<Integer, String> map = new TreeMap<Integer, String>();

					// Parses the json
					for (int i = 0; i < jsonResponse.length(); i++) {
						iterationString = iterationString
								.substring(iterationString.indexOf("\"") + 1);
						tempName = iterationString.substring(0,
								iterationString.indexOf("\""));
						tempScore = iterationString.substring(
								iterationString.indexOf("score\":") + 7,
								iterationString.indexOf("}"));
						iterationString = iterationString
								.substring(iterationString.indexOf("}") + 2);
						tempScore = tempScore.replace("\"", "");

						// Handles a situation in which two users have the same
						// score
						if (map.containsKey(Integer.parseInt(tempScore))) {
							map.put(Integer.parseInt(tempScore),
									tempName
											+ ", "
											+ map.get(Integer
													.parseInt(tempScore)));
						} else {
							map.put(Integer.parseInt(tempScore), tempName);
						}
					}
					// Creates a map in the reverse order so that only the
					// highest 10 scores are added
					Map<Integer, String> reverseOrderedMap = map
							.descendingMap();

					Integer length = 0;
					Iterator<Entry<Integer, String>> it = reverseOrderedMap.entrySet().iterator();
					while (it.hasNext()) {
						if (length != 0) {
							leaderBoardString += ";";
						}
						Map.Entry<Integer, String> pairs = (Entry<Integer, String>) it.next();
						System.out.println(pairs.getKey() + " = "
								+ pairs.getValue());
						it.remove(); // avoids a ConcurrentModificationException
						leaderBoardString += pairs.getValue() + ": "
								+ pairs.getKey() + "\n";
						// Breaks if 10 users are added
						if (length == 9) {
							break;
						}
						length++;
					}

				} catch (ParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return leaderBoardString;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// Updates the leaderboard once the async task finishes
			TextView leaderboard = (TextView) findViewById(R.id.leaderboard);
			leaderboard.setText("");
			String[] list = result.split(";");
			for (int i = 0; i < list.length; i++) {
				leaderboard.append(list[i]);
			}
		}
	}
}