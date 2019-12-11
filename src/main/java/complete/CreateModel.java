package complete;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.jena.query.DatasetAccessor;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.json.JSONException;

public class CreateModel {
	static Model model;
	private static final String DATABASE_TTL = "E:\\CPS2\\Year_2\\Semantic_Web\\Jena\\extractdata\\src\\main\\java\\extractdata\\dbfirst.ttl";
	private static final String FUESKI_LOCAL_ENDPOINT = "http://localhost:3030/bicycleStations";

	static String stationURIPrefix;
	static String[] cities = { "SAINT-ETIENNE", "LYON", "PARIS", "TOULOUSE" };

	public static void main(String args[]) throws JSONException, IOException {
		CreateModel.sslResolve();
		CreateModel.initializeModel();
		for (String city : cities) {
			List<Station> stations = null;
			if (city == "SAINT-ETIENNE") {
				StaticSaintEtienne se = new StaticSaintEtienne();// TODO change this as take other cities have format
																	// like st-etienne
				stations = se.processData();
				addCityToModel(stations, city);
			} else if (city == "LYON") {
				StaticLyon ly = new StaticLyon();
				stations = ly.processData();
				addCityToModel(stations, city);
			} 
//			else if (city == "PARIS") {
//				StaticParis pa = new StaticParis();
//				stations = pa.processData();
//				addCityToModel(stations, city);
//			}
			else if (city == "TOULOUSE") {
				StaticToulouse pa = new StaticToulouse();
				stations = pa.processData();
				addCityToModel(stations, city);
			}

			// Save the model in fueski server
			saveToFueski();
		}
	}

	public static void sslResolve() {

		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (GeneralSecurityException e) {
		}

	}

	public static void initializeModel() {
		model = ModelFactory.createDefaultModel();
		model.setNsPrefix("ex", NsPrefix.getExNS());
		model.setNsPrefix("geo", NsPrefix.getGeoNS());
		model.setNsPrefix("local", NsPrefix.getLocalNS());
		model.setNsPrefix("onto", NsPrefix.getOntoNS());
		stationURIPrefix = NsPrefix.getLocalNS() + "Station";
	}

	public static void addCityToModel(List<Station> stations, String city) {
		for (Station station : stations) {
			Resource StationRs = model.createResource(stationURIPrefix + ":" + city + ":" + station.getID());

			Statement statement_ptype = model.createLiteralStatement(StationRs, RDF.type, RDFS.Class);
			model.add(statement_ptype);

			// Station.addProperty(FOAF.name, name);
			Statement statement_pname = model.createLiteralStatement(StationRs, FOAF.name, station.getName());
			model.add(statement_pname);

			// Station.addProperty(RDF.value, String.valueOf(capacity));
			Property pcapacity = model.createProperty(NsPrefix.getExNS() + "capacity");
			Statement statement_pcapacity = model.createLiteralStatement(StationRs, pcapacity, station.getCapacity());
			model.add(statement_pcapacity);

			Property pcity = model.createProperty(NsPrefix.getSchemaNS() + "City");
			Statement statement_pcity = model.createLiteralStatement(StationRs, pcity,  city);
			model.add(statement_pcity);

			StationRs.addLiteral(model.createProperty(NsPrefix.getGeoNS() + "lat"), station.getLat());
			StationRs.addLiteral(model.createProperty(NsPrefix.getGeoNS() + "long"), station.getLon());
		}
	}

	public static void saveToFueski() {
		try {
			// write to a file, for the debugging purpose
			model.write(new FileOutputStream(new File(DATABASE_TTL)), "TURTLE");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		DatasetAccessor accessor = DatasetAccessorFactory.createHTTP(FUESKI_LOCAL_ENDPOINT);
		accessor.putModel(model);
	}

}
