package complete;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Process the Toulouse JSON data
 * @return Toulouse stations array
 * @param url
 */
public class StaticToulouse {
		final String CITYNAME = "TOULOUSE";
		static String url = "https://data.toulouse-metropole.fr/api/records/1.0/search/?dataset=velo-toulouse&rows=1000";
		static List<Station> stationsList = new ArrayList<Station>();

		public List<Station> processData() throws JSONException, IOException {
			JSONArray stations = readJsonFromUrl(url);
			processStations(stations);
			return stationsList;		
		}
		
		public static JSONArray readJsonFromUrl(String url) throws IOException, JSONException {
			InputStream is = new URL(url).openStream();
			try {
				BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
				String jsonText = readAll(rd);
				JSONObject json = new JSONObject(jsonText);
				JSONArray stations =  (JSONArray) json.get("records");
				return stations;
			} finally {
				is.close();
			}
		}

		private static String readAll(Reader rd) throws IOException {
			StringBuilder sb = new StringBuilder();
			int cp;
			while ((cp = rd.read()) != -1) {
				sb.append((char) cp);
			}
			return sb.toString();
		}

		private static void processStations(JSONArray stations) {
			for (Object station : stations) {
				JSONObject stationJson = (JSONObject) station;
				JSONObject obj = stationJson.getJSONObject("fields");
				
				int a = (int) obj.get("num_station");
				
				String ID = Integer.toString(a);
				String name = (String) obj.get("nom");

				JSONArray geo = obj.getJSONArray("geo_point_2d");
				double lat = (Double) geo.get(0);
				double lon = (Double) geo.get(1);
				int capacity =  (Integer) obj.get("nb_bornettes");

				Station ss = new Station(ID, name, lat, lon, capacity);
				stationsList.add(ss);
			}
		}


	}

