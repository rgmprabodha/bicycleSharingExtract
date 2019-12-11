package complete;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

public class StationClasses {

	public static void main(String[] args) {
		Model model = ModelFactory.createDefaultModel();

		String schemaNS = "http://schema.org";
		String localNS = "http://localhost:3030/";
		String rdfsNS = "http://www.w3.org/2000/01/rdf-schema#";

		model.setNsPrefix("local", localNS);
		model.setNsPrefix("schema", schemaNS);
		model.setNsPrefix("rdfs", rdfsNS);

		String countryURI = schemaNS + "Country";
		String cityURI = schemaNS + "City";
		String stationURI = localNS + "Station";
		String availabilityURI = localNS + "Availability";
		String hascityURI = localNS + "hasCity";
		String hasStationURI = localNS + "hasStation";
		String hasAvailabilityURI = localNS + "hasAvailability";

		Resource Country = model.createResource(countryURI);
		Resource City = model.createResource(cityURI);
		Resource Station = model.createResource(stationURI);
		Resource Availability = model.createResource(availabilityURI);

		Property hasCity = model.createProperty(hascityURI);
		Property hasStation = model.createProperty(hasStationURI);
		Property hasAvailability = model.createProperty(hasAvailabilityURI);

		Country.addProperty(hasCity, City);
		Country.addProperty(RDF.type, RDFS.Class);

		City.addProperty(hasStation, Station);
		City.addProperty(RDF.type, RDFS.Class);

		Station.addProperty(RDF.type, RDFS.Class);
		Station.addProperty(hasAvailability, Availability);

		Availability.addProperty(RDF.type, RDFS.Class);

		model.write(System.out, "turtle");

	}
}
