package com.travizer.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.widget.Toast;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String[] data = {
                "Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thu 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - Trapped in weather station - 23/18",
                "Sun 6/29 - Sunny - 20/7",
                "Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thu 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - Trapped in weather station - 23/18",
                "Sun 6/29 - Sunny - 20/7"
        };

        List<String> weekForecast = new ArrayList<String>(Arrays.asList(data));

        ArrayAdapter mForeCastAdapter = new ArrayAdapter(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, weekForecast);

        View v = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView;
        listView = (ListView)v.findViewById(R.id.Listview_forecast);
        listView.setAdapter(mForeCastAdapter);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh){
            FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();
            fetchWeatherTask.execute("94043,US");

            WeatherDataParser wdp = new WeatherDataParser();

            String theJason = "{\"city\":{\"id\":5375480,\"name\":\"Mountain View\",\"coord\":{\"lon\":-122.083847,\"lat\":37.386051},\"country\":\"US\",\"population\":0},\"cod\":\"200\",\"message\":0.0095,\"cnt\":7,\"list\":[{\"dt\":1455739200,\"temp\":{\"day\":18.13,\"min\":9.11,\"max\":19.36,\"night\":9.11,\"eve\":13.72,\"morn\":16.58},\"pressure\":979.48,\"humidity\":67,\"weather\":[{\"id\":502,\"main\":\"Rain\",\"description\":\"heavy intensity rain\",\"icon\":\"10d\"}],\"speed\":4.4,\"deg\":179,\"clouds\":64,\"rain\":12.9},{\"dt\":1455825600,\"temp\":{\"day\":13.64,\"min\":4.1,\"max\":13.64,\"night\":4.1,\"eve\":8.97,\"morn\":9.89},\"pressure\":989.99,\"humidity\":90,\"weather\":[{\"id\":501,\"main\":\"Rain\",\"description\":\"moderate rain\",\"icon\":\"10d\"}],\"speed\":1.51,\"deg\":221,\"clouds\":12,\"rain\":9.52},{\"dt\":1455912000,\"temp\":{\"day\":13.24,\"min\":3.22,\"max\":13.24,\"night\":7.91,\"eve\":9.89,\"morn\":3.22},\"pressure\":994.87,\"humidity\":82,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":2.01,\"deg\":197,\"clouds\":88,\"rain\":0.92},{\"dt\":1455998400,\"temp\":{\"day\":15.31,\"min\":3.54,\"max\":15.33,\"night\":3.54,\"eve\":9.38,\"morn\":6.98},\"pressure\":998.8,\"humidity\":85,\"weather\":[{\"id\":801,\"main\":\"Clouds\",\"description\":\"few clouds\",\"icon\":\"02d\"}],\"speed\":1.51,\"deg\":256,\"clouds\":20},{\"dt\":1456084800,\"temp\":{\"day\":11.94,\"min\":6.35,\"max\":16.42,\"night\":9.6,\"eve\":16.42,\"morn\":6.35},\"pressure\":1019.01,\"humidity\":0,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"sky is clear\",\"icon\":\"01d\"}],\"speed\":2.13,\"deg\":33,\"clouds\":1},{\"dt\":1456171200,\"temp\":{\"day\":13.7,\"min\":7.2,\"max\":17.97,\"night\":10.19,\"eve\":17.97,\"morn\":7.2},\"pressure\":1017.63,\"humidity\":0,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"sky is clear\",\"icon\":\"01d\"}],\"speed\":2.2,\"deg\":93,\"clouds\":16},{\"dt\":1456257600,\"temp\":{\"day\":14.44,\"min\":8.66,\"max\":18.44,\"night\":11.25,\"eve\":18.44,\"morn\":8.66},\"pressure\":1017.61,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":1.4,\"deg\":55,\"clouds\":28}]}";
            double md = wdp.getMaxTemperatureForDay(theJason, 3);

            Toast.makeText(getActivity(),Double.toString(md),Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class WeatherDataParser{
        public double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex) {
            double temperature = 0;
            try {
                JSONObject weather = new JSONObject(weatherJsonStr);
                JSONArray days = weather.getJSONArray("list");
                JSONObject dayInfo = days.getJSONObject(dayIndex);
                JSONObject temperatureInfo = dayInfo.getJSONObject("temp");
                temperature = temperatureInfo.getDouble("max");
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                return temperature;
            }
        }
    }


    public class FetchWeatherTask extends AsyncTask<String, Void, Void>{
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                //declare the URL base
                final String THE_URL_BASE = "http://api.openweathermap.org/data/2.5/forecast/daily?";

                //declare the query params
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APP_ID = "appid";

                //declare the query param values
                String format = "json";
                String untis = "metric";
                String appId = "44db6a862fba0b067b1930da0d769e98";
                int numDays = 7;

                Uri builtURI = Uri.parse(THE_URL_BASE).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, untis)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(APP_ID, appId)
                        .build();

                URL url = new URL(builtURI.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                forecastJsonStr = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }

            return null;
        }

    }

}
