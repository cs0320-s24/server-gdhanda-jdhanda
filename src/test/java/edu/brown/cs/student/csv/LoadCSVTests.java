package edu.brown.cs.student.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.handlers.csvhandlers.CSVSharedSource;
import edu.brown.cs.student.main.server.handlers.csvhandlers.LoadCSVHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.annotations.BeforeClass;
import spark.Spark;

/**
 * Testing class for the load csv endpoint and handler.
 */
public class LoadCSVTests {

  /**
   * Set up the server port.
   */
  @BeforeClass
  public static void setupOnce() {
    // Pick an arbitrary free port
    Spark.port(0);
    // Eliminate logger spam in console for test suite
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root
  }

  // Helping Moshi serialize Json responses.
  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;

  /**
   * Set up the testing objects in the server.
   */
  @BeforeEach
  public void setup() {
    Spark.get("/loadcsv", new LoadCSVHandler(new CSVSharedSource()));
    Spark.awaitInitialization(); // don't continue until the server is listening

    // New Moshi adapter for responses and requests
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
  }

  /**
   * Clean up after testing.
   */
  @AfterEach
  public void tearDown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/loadcsv");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  /**
   * Helper to start a connection to a specific API endpoint/params
   *
   * <p>The "throws" clause doesn't matter below -- JUnit will fail if an exception is thrown that
   * hasn't been declared as a parameter to @Test.
   *
   * @param apiCall the call string, including endpoint (Note: this would be better if it had more
   *     structure!)
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  private HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send a request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    // The request body contains a Json object
    clientConnection.setRequestProperty("Content-Type", "application/json");
    // We're expecting a Json object in the response body
    clientConnection.setRequestProperty("Accept", "application/json");

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Test of a successful loading.
   *
   * @throws IOException
   */
  @Test
  public void testLoadCSVSuccess() throws IOException {
    // Set up the request, make the request
    HttpURLConnection loadConnection =
        tryRequest("loadcsv?filepath=data/server/RI-Town-Income-Data.csv&header=true");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("success", responseBody.get("result"));

    loadConnection.disconnect();
  }

  /**
   * Test of a failed load due to missing file.
   *
   * @throws IOException
   */
  @Test
  public void testLoadCSVFail_Missing() throws IOException {
    // Setup without any parameters (oops!)
    HttpURLConnection loadConnection = tryRequest("loadcsv");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());

    // Get the expected response: an error
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("error", responseBody.get("result"));
    assertEquals("Invalid number of parameters specified!", responseBody.get("error_type"));
    loadConnection.disconnect(); // close gracefully
  }

  /**
   * Test of a failed load due to too many args.
   *
   * @throws IOException
   */
  @Test
  public void testLoadCSVFail_TooManyArgs() throws IOException {
    // Setup without any parameters (oops!)
    HttpURLConnection loadConnection = tryRequest("loadcsv?filepath=hello&header=hi&sus=sus");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());

    // Get the expected response: an error
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("error", responseBody.get("result"));
    assertEquals("Invalid number of parameters specified!", responseBody.get("error_type"));
    loadConnection.disconnect(); // close gracefully
  }

  /**
   * Test of a failed load because the file was not in the scope of the project.
   *
   * @throws IOException
   */
  @Test
  public void testLoadCSVFail_OutsideFilepath() throws IOException {
    // Setup with bad parameters (oops)
    HttpURLConnection loadConnection = tryRequest("loadcsv?filepath=out/of/scope!&header=true");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());

    // Get the expected response: an error
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("error", responseBody.get("result"));
    assertEquals("InvalidFilepathException", responseBody.get("exception"));

    loadConnection.disconnect(); // close gracefully
  }

  /**
   * Test of a failed load because the filepath does not exist.
   * @throws IOException
   */
  @Test
  public void testLoadCSVFail_BadFilepath() throws IOException {
    // Setup with bad parameters (oops)
    HttpURLConnection loadConnection = tryRequest("loadcsv?filepath=data/nothing.csv!&header=true");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());

    // Get the expected response: an error
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("error", responseBody.get("result"));
    assertEquals("FileNotFoundException", responseBody.get("exception"));

    loadConnection.disconnect(); // close gracefully
  }

  /**
   * Test of a failed load because the header specification was malformed.
   *
   * @throws IOException
   */
  @Test
  public void testLoadCSVFail_BadHeader() throws IOException {
    // Setup with bad parameters (oops)
    HttpURLConnection loadConnection =
        tryRequest("loadcsv?filepath=data/server/RI-Town-Income-Data.csv&header=sus");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());

    // Get the expected response: an error
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

    assertEquals("error", responseBody.get("result"));
    assertEquals("Invalid header value!", responseBody.get("error_type"));
    loadConnection.disconnect(); // close gracefully
  }

  /**
   * Ensure that you can load a new CSV with one already loaded.
   * @throws IOException
   */
  @Test
  public void testLoadCSVTwice() throws IOException {
    // Setup with bad parameters (oops)
    HttpURLConnection loadConnection =
        tryRequest("loadcsv?filepath=data/server/RI-Town-Income-Data.csv&header=true");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());

    // Get the expected response: an error
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));

    assertEquals("success", responseBody.get("result"));

    // Setup with bad parameters (oops)
    HttpURLConnection loadConnection2 =
        tryRequest("loadcsv?filepath=data/census/dol_ri_earnings_disparity.csv&header=true");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection2.getResponseCode());

    // Get the expected response: an error
    Map<String, Object> responseBody2 =
        adapter.fromJson(new Buffer().readFrom(loadConnection2.getInputStream()));

    assertEquals("success", responseBody2.get("result"));
    loadConnection.disconnect(); // close gracefully
  }
}
