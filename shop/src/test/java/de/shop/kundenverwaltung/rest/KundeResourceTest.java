package de.shop.kundenverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.KUNDEN_ID_PATH;
import static de.shop.util.TestConstants.KUNDEN_ID_PATH_PARAM;
import static de.shop.util.TestConstants.KUNDEN_PATH;
import static de.shop.util.TestConstants.LOCATION;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

import javax.json.JsonObject;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import de.shop.util.AbstractResourceTest;

@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class KundeResourceTest extends AbstractResourceTest {

private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final Long KUNDE_ID_VORHANDEN = Long.valueOf(101);
	private static final String NEUER_NACHNAME = "Nachnameneu";
	private static final String NEUER_VORNAME = "Vorname";
	private static final String NEUE_EMAIL = NEUER_NACHNAME + "@test.de";
	private static final String GESCHLECHT = "w";
	private static final String NEUE_PLZ = "76133";
	private static final String NEUER_ORT = "Karlsruhe";
	private static final String NEUE_STRASSE = "Testweg";
	private static final String NEUE_HAUSNR = "1";
	
	
	@Test
	public void findKundeById() {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long kundeId = KUNDE_ID_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
				                         .get(KUNDEN_ID_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("kid").longValue(), is(kundeId.longValue()));
			//assertThat(jsonObject.getString("kundeUri"), is(notNullValue()));
		}

		LOGGER.finer("ENDE");
	}
	
	@Test
	public void createKunde() {
		LOGGER.finer("BEGINN");
		
		// Given
		final String nachname = NEUER_NACHNAME;
		final String vorname = NEUER_VORNAME;
		final String email = NEUE_EMAIL;
		final String plz = NEUE_PLZ;
		final String ort = NEUER_ORT;
		final String strasse = NEUE_STRASSE;
		final String hausnr = NEUE_HAUSNR;
		final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		final String geschlecht = GESCHLECHT;
		
		
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
                                         .basic(username, password)
                                         .post(KUNDEN_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_CREATED));
		final String location = response.getHeader(LOCATION);
		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		final Long id = Long.valueOf(idStr);
		assertThat(id.longValue() > 0, is(true));

		LOGGER.finer("ENDE");
	}
}


