package de.shop.artikelverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH_PARAM;
import static de.shop.util.TestConstants.ARTIKEL_PATH;
//import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.Set;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.FixMethodOrder;
//import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import de.shop.util.AbstractResourceTest;
//import de.shop.util.ConcurrentUpdate;

@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class ArtikelResourceTest extends AbstractResourceTest {
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final Long ARTIKEL_ID_VORHANDEN = Long.valueOf(301);
	
	private static final String BEZEICHNUNG_NEU = "Hemd";
	private static final Double PREIS_NEU = Double.valueOf(12.00);
	private static final String VERFUEGBARKEIT_NEU = "j";
	private static final Long ARTIKEL_ID_UPDATE = Long.valueOf(301);
	private static final String NEUE_BEZEICHNUNG = "Hose";
	
	

@Test
public void findArtikelById() {
	LOGGER.finer("BEGINN");
	
	//Given
	final Long artikelId = ARTIKEL_ID_VORHANDEN;
	
	//When
	final Response response = given().header(ACCEPT, APPLICATION_JSON)
								.pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
								.get(ARTIKEL_ID_PATH);
	//Then
	assertThat(response.getStatusCode(), is(HTTP_OK));
	
	try (final JsonReader jsonReader =
				getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("aid").longValue(), is(artikelId.longValue()));
	}
	LOGGER.finer("ENDE");
	}


@Test
public void createArtikel() {
	LOGGER.finer("BEGINN");
	
	//Given
	
	final String bezeichnung = BEZEICHNUNG_NEU;
	final Double preis = PREIS_NEU;
	final String verfuegbarkeit = VERFUEGBARKEIT_NEU;
	final String username = USERNAME_ADMIN;
	final String password = PASSWORD_ADMIN;
	
	final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
									.add("bezeichnung", bezeichnung)
									.add("preis", preis)
									.add("verfuegbarkeit", verfuegbarkeit)
									.build();
	//When
	final Response response = given().contentType(APPLICATION_JSON)
				.body(jsonObject.toString())
				.auth().basic(username, password)
				.post(ARTIKEL_PATH);
	//Then
	assertThat(response.getStatusCode(), is(HTTP_CREATED));
}

@Test
public void updateArtikel() {
	LOGGER.finer("BEGINN");
	
	//Given
	final Long artikelId = ARTIKEL_ID_UPDATE;
	final String neueBezeichnung = NEUE_BEZEICHNUNG;
	final String username = USERNAME_ADMIN;
	final String password = PASSWORD_ADMIN;
	
	// When
			Response response = given().header(ACCEPT, APPLICATION_JSON)
					                   .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
	                                   .get(ARTIKEL_ID_PATH);
	
	
			JsonObject jsonObject;
			try (final JsonReader jsonReader =
					              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
				jsonObject = jsonReader.readObject();
			}
			
			assertThat(jsonObject.getJsonNumber("aid").longValue(), is(artikelId.longValue()));
			
	    	// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
	    	final JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
	    	final Set<String> keys = jsonObject.keySet();
	    	for (String k : keys) {
	    		if ("bezeichnung".equals(k)) {
	    			job.add("bezeichnung", neueBezeichnung);
	    		}
	    		else {
	    			job.add(k, jsonObject.get(k));
	    		}
	    	}
	    	jsonObject = job.build();
	
			response = given().contentType(APPLICATION_JSON)
			          .body(jsonObject.toString())
                    .auth()
                    .basic(username, password)
                    .put(ARTIKEL_PATH);
	
	// Then
	assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));

}

}

