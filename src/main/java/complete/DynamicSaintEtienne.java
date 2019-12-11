package complete;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.jena.query.DatasetAccessor;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DynamicSaintEtienne {
	private static Model model;
	String exNS = "http://www.example.com/";
	private static final String FUESKI_LOCAL_ENDPOINT = "http://localhost:3030/bicycleStations/update";

	 
	private static final String DATABASE_TTL = "E:\\CPS2\\Year_2\\Semantic_Web\\Jena\\extractdata\\src\\main\\java\\extractdata\\full.ttl";

	public static void main(String args[]) throws IOException, JSONException {
		String url = "https://saint-etienne-gbfs.klervi.net/gbfs/en/station_status.json";
		JSONObject json = readJsonFromUrl(url);
		JSONObject data = (JSONObject) json.get("data");
		JSONArray stations = (JSONArray) data.get("stations");
		processStationDyna(stations);
	}

	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
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

	private static void processStationDyna(JSONArray stations) {

//		DatasetAccessor getAccessor = DatasetAccessorFactory.createHTTP(FUESKI_LOCAL_ENDPOINT);
//		model = getAccessor.getModel();

//		String stationURIPrefix = NsPrefix.getLocalNS() + "Station";
//		for (Object station : stations) {
//
//			JSONObject stationJson = (JSONObject) station;
//			String ID = (String) stationJson.get("station_id");
//			int nava = (Integer) stationJson.get("num_bikes_available");
//			int ndisa = (Integer) stationJson.get("num_bikes_disabled");
//			int ndocava = (Integer) stationJson.get("num_docks_available");
//			int ninstall = (Integer) stationJson.get("is_installed");
//			int nrenting = (Integer) stationJson.get("is_renting");
//			int nreturn = (Integer) stationJson.get("is_returning");
//			String nupdatetime = String.valueOf((Integer) stationJson.get("last_reported"));
//
//			// Match Station Resource by ID
//			Resource StationDyna = model.getResource(stationURIPrefix + ":SAINT-ETIENNE:" + ID);
//
//			Property pnava = model.createProperty(NsPrefix.getExNS() + "num_bikes_available");
//			// Can also create statements directly .. .
//			Statement statement_pnava = model.createLiteralStatement(StationDyna, pnava, nava); // resource,property,resource
//																								// or string or
//			// but remember to add the created statement to the model
//			model.add(statement_pnava);
//
//			Property pndisa = model.createProperty(NsPrefix.getExNS() + "num_bikes_disabled");
//			Statement statement_pndisa = model.createLiteralStatement(StationDyna, pndisa, ndisa);
//			model.add(statement_pndisa);
//
//			Property pndocava = model.createProperty(NsPrefix.getExNS() + "num_docks_available");
//			Statement statement_pndocava = model.createLiteralStatement(StationDyna, pndocava, ndocava);
//			model.add(statement_pndocava);
//
//			Property pninstall = model.createProperty(NsPrefix.getExNS() + "is_installed");
//			Statement statement_pninstall = model.createLiteralStatement(StationDyna, pninstall, ninstall);
//			model.add(statement_pninstall);
//
//			Property pnrenting = model.createProperty(NsPrefix.getExNS() + "is_renting");
//			Statement statement_pnrenting = model.createLiteralStatement(StationDyna, pnrenting, nrenting);
//			model.add(statement_pnrenting);
//
//			Property pnreturn = model.createProperty(NsPrefix.getExNS() + "is_returning");
//			Statement statement_pnreturn = model.createLiteralStatement(StationDyna, pnreturn, nreturn);
//			model.add(statement_pnreturn);
//
//			Property pnupdatetime = model.createProperty(NsPrefix.getExNS() + "last_reported");
//			Statement statement_pnupdatetime = model.createLiteralStatement(StationDyna, pnupdatetime, nupdatetime);
//			model.add(statement_pnupdatetime);

		updateModel(stations);

//		}

//		DatasetAccessor putAccessor = DatasetAccessorFactory.createHTTP(FUESKI_LOCAL_ENDPOINT);
//		putAccessor.putModel(model);

//		try {
//			model.write(new FileOutputStream(new File(DATABASE_TTL)), "TURTLE");
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

//		model.write(System.out, "turtle");

	}

	public static void updateModel(JSONArray stations) {
		String stationURIPrefix = NsPrefix.getLocalNS() + "Station";
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date today = new Date();
		String todayDate = formatter.format(today);
		
		for (Object station : stations) {

			JSONObject stationJson = (JSONObject) station;
			String ID = (String) stationJson.get("station_id");
			int nava = (Integer) stationJson.get("num_bikes_available");
			int ndisa = (Integer) stationJson.get("num_bikes_disabled");
			int ndocava = (Integer) stationJson.get("num_docks_available");
			int ninstall = (Integer) stationJson.get("is_installed");
			int nrenting = (Integer) stationJson.get("is_renting");
			int nreturn = (Integer) stationJson.get("is_returning");
			String nupdatetime = String.valueOf((Integer) stationJson.get("last_reported"));

			String iri = stationURIPrefix + ":SAINT-ETIENNE:" + ID;
			System.out.println(FUESKI_LOCAL_ENDPOINT);
			String query = "PREFIX ex: <http://www.example.org/>\r\n"
					+ "PREFIX geo: <https://www.w3.org/2003/01/geo/wgs84_pos#>\r\n"
					+ "PREFIX schema: <http://schema.org/>\r\n" 
					+ "prefix xsd: <http://www.w3.org/2001/XMLSchema#> \r\n"
					+ "INSERT DATA { <" +  iri + "> ex:hasAvailability [\r\n" 
					+ "                                            a           ex:Availability; \r\n"
					+ "					                           ex:datetime \"" + todayDate + "\"^^xsd:dateTime;\r\n"
					+ "					                           ex:availableBikes \""  +nava + "\";\r\n" 
					+ "                                            ] .\r\n" 
					+ "		}";
			
			System.out.println(query);
			
			UpdateRequest update  = UpdateFactory.create(query);
	        UpdateProcessor qexec = UpdateExecutionFactory.createRemote(update, FUESKI_LOCAL_ENDPOINT);
	        qexec.execute();			

		}
	}
}
