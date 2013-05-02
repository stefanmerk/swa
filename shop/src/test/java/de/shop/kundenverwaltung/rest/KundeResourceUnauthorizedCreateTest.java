package de.shop.kundenverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.KUNDEN_PATH;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static java.net.HttpURLConnection.HTTP_CREATED;
import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

import javax.json.JsonObject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import de.shop.util.AbstractResourceTest;

@RunWith(Arquillian.class)
public class KundeResourceUnauthorizedCreateTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private static final String USERKUNDE = "Hans";
	private static final String USERPASSWORD = "hans";
	private static final String NACHNAME = "Klappt";
	private static final String VORNAME = "Nicht";
	private static final String EMAIL = "Schade@Web.de";
	private static final String PASSWORD = "ueberfluessig";
	private static final String GESCHLECHT = "m";
	private static final String PLZ = "76199";
	private static final String ORT = "Karlsruhe";
	private static final String STRASSE = "kernstr";
	private static final String HAUSNR = "16";
	
	@Test
	public void kundeOhneRechtCreate() {	
		LOGGER.finer("BEGINN");
	final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()	             		          
	          .add("nachname", NACHNAME)
	          .add("vorname", VORNAME)
	          .add("email", EMAIL)
	          .add("geschlecht", GESCHLECHT)
	          .add("password", PASSWORD)
	          .add("adresse", getJsonBuilderFactory().createObjectBuilder()
  		                  .add("plz", PLZ)
  		                  .add("ort", ORT)
  		                  .add("strasse", STRASSE)
  		                  .add("hausnr", HAUSNR)
  		                  .build())
            .build();

// When
final Response response = given().contentType(APPLICATION_JSON)
               .body(jsonObject.toString())
               .auth()
               .basic(USERKUNDE, USERPASSWORD)
               .post(KUNDEN_PATH);

// Then

assertThat(response.getStatusCode(), is(HTTP_UNAUTHORIZED));
// WIR LEGEN NUN DAS OBJEKT MIT KORREKTEN DATEN IN DER DB AB!
final Response response2 = given().contentType(APPLICATION_JSON)
.body(jsonObject.toString())
.auth()
.basic("mitarbeiter", "mitarbeiter")
.post(KUNDEN_PATH);
assertThat(response2.getStatusCode(), is(HTTP_CREATED));
}
	
}
	
	

