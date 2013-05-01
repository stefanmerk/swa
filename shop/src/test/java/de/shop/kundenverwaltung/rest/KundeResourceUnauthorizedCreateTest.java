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
	private static final String UsernameKunde = "Hans";
	private static final String UserPassword = "hans";
	private static final String nachname = "Klappt";
	private static final String vorname ="Nicht";
	private static final String email ="Schade@Web.de";
	private static final String password ="ueberfluessig";
	private static final String geschlecht ="m";
	private static final String plz ="76199";
	private static final String ort ="Karlsruhe";
	private static final String strasse = "kernstr";
	private static final String hausnr ="16";
	
	@Test
	public void KundeOhneRechtCreate(){	
		LOGGER.finer("BEGINN");
	final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()	             		          
	          .add("nachname", nachname)
	          .add("vorname", vorname)
	          .add("email", email)
	          .add("geschlecht", geschlecht)
	          .add("password", password)
	          .add("adresse", getJsonBuilderFactory().createObjectBuilder()
  		                  .add("plz", plz)
  		                  .add("ort", ort)
  		                  .add("strasse", strasse)
  		                  .add("hausnr", hausnr)
  		                  .build())
            .build();

// When
final Response response = given().contentType(APPLICATION_JSON)
               .body(jsonObject.toString())
               .auth()
               .basic(UsernameKunde, UserPassword)
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
	
	

