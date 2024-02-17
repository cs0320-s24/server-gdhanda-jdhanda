package edu.brown.cs.student.csv;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.server.handlers.csvhandlers.CSVDatasource;
import edu.brown.cs.student.main.server.handlers.csvhandlers.CSVSharedSource;
import edu.brown.cs.student.main.server.handlers.csvhandlers.LoadCSVHandler;
import edu.brown.cs.student.main.server.handlers.csvhandlers.SearchCSVHandler;
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
 * Test class to run UNIT tests on the seachcsv endpoint and handler.
 */
public class SearchCSVTests {

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
  private CSVDatasource sharedState;

  /**
   * Set up the load and search handlers necessary.
   */
  @BeforeEach
  public void setup() {
    sharedState = new CSVSharedSource();
    Spark.get("/loadcsv", new LoadCSVHandler(this.sharedState));
    Spark.get("/searchcsv", new SearchCSVHandler(this.sharedState));
    Spark.awaitInitialization(); // don't continue until the server is listening

    // New Moshi adapter for responses and requests
    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
  }

  /**
   * Clean up after tests.
   */
  @AfterEach
  public void tearDown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/loadcsv");
    Spark.unmap("/searchcsv");
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
   * Test a successful search through all columns.
   *
   * @throws IOException
   */
  @Test
  public void testSearchCSVSuccess_All() throws IOException {
    // Set up the request, make the request
    HttpURLConnection loadConnection =
        tryRequest("loadcsv?filepath=data/server/RI-Town-Income-Data.csv&header=true");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("success", responseBody.get("result"));

    // Set up the request, make the request
    HttpURLConnection loadConnection2 = tryRequest("searchcsv?value=Providence");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection2.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody2 =
        adapter.fromJson(new Buffer().readFrom(loadConnection2.getInputStream()));
    assertEquals("success", responseBody2.get("result"));

    loadConnection.disconnect();
  }

  /**
   * Test a successful search when specifying the header name.
   *
   * @throws IOException
   */
  @Test
  public void testSearchCSVSuccess_Header() throws IOException {
    // Set up the request, make the request
    HttpURLConnection loadConnection =
        tryRequest("loadcsv?filepath=data/server/RI-Town-Income-Data.csv&header=true");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("success", responseBody.get("result"));

    // Set up the request, make the request
    HttpURLConnection loadConnection2 = tryRequest("searchcsv?value=Providence&header=City/Town");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection2.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody2 =
        adapter.fromJson(new Buffer().readFrom(loadConnection2.getInputStream()));
    assertEquals("success", responseBody2.get("result"));

    loadConnection.disconnect();
  }

  /**
   * Test a successful search when specifying the header index.
   *
   * @throws IOException
   */
  @Test
  public void testSearchCSVSuccess_Index() throws IOException {
    // Set up the request, make the request
    HttpURLConnection loadConnection =
        tryRequest("loadcsv?filepath=data/server/RI-Town-Income-Data.csv&header=true");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("success", responseBody.get("result"));

    // Set up the request, make the request
    HttpURLConnection loadConnection2 = tryRequest("searchcsv?value=Providence&index=0");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection2.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody2 =
        adapter.fromJson(new Buffer().readFrom(loadConnection2.getInputStream()));
    assertEquals("success", responseBody2.get("result"));

    loadConnection.disconnect();
  }

  /**
   * Test for a search that will not find anything.
   *
   * @throws IOException
   */
  @Test
  public void testSearchCSVFail_NotFound() throws IOException {
    // Set up the request, make the request
    HttpURLConnection loadConnection =
        tryRequest("loadcsv?filepath=data/server/RI-Town-Income-Data.csv&header=true");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("success", responseBody.get("result"));

    // Set up the request, make the request
    HttpURLConnection loadConnection2 = tryRequest("searchcsv?value=water");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection2.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody2 =
        adapter.fromJson(new Buffer().readFrom(loadConnection2.getInputStream()));
    assertEquals("success", responseBody2.get("result"));
    assertEquals("[]", responseBody2.get("data").toString());

    loadConnection.disconnect();
  }

  /**
   * Test for a search that will fail due to a header that doesn't exist.
   *
   * @throws IOException
   */
  @Test
  public void testSearchCSVFail_BadHeader() throws IOException {
    // Set up the request, make the request
    HttpURLConnection loadConnection =
        tryRequest("loadcsv?filepath=data/server/RI-Town-Income-Data.csv&header=true");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("success", responseBody.get("result"));

    // Set up the request, make the request
    HttpURLConnection loadConnection2 = tryRequest("searchcsv?value=Providence&header=none");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection2.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody2 =
        adapter.fromJson(new Buffer().readFrom(loadConnection2.getInputStream()));
    assertEquals("error", responseBody2.get("result"));
    assertEquals("HeaderNotFoundException", responseBody2.get("exception"));

    loadConnection.disconnect();
  }

  /**
   * Test for a failed search in an index that is invalid.
   *
   * @throws IOException
   */
  @Test
  public void testSearchCSVFail_BadIndex() throws IOException {
    // Set up the request, make the request
    HttpURLConnection loadConnection =
        tryRequest("loadcsv?filepath=data/server/RI-Town-Income-Data.csv&header=true");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("success", responseBody.get("result"));

    // Set up the request, make the request
    HttpURLConnection loadConnection2 = tryRequest("searchcsv?value=Providence&index=-10");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection2.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody2 =
        adapter.fromJson(new Buffer().readFrom(loadConnection2.getInputStream()));
    assertEquals("error", responseBody2.get("result"));
    assertEquals("InvalidIndexException", responseBody2.get("exception"));

    loadConnection.disconnect();
  }

  /**
   * Test for a fail that specifies both a header and index.
   *
   * @throws IOException
   */
  @Test
  public void testSearchCSVFail_HeaderAndIndex() throws IOException {
    // Set up the request, make the request
    HttpURLConnection loadConnection =
        tryRequest("loadcsv?filepath=data/server/RI-Town-Income-Data.csv&header=true");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals("success", responseBody.get("result"));

    // Set up the request, make the request
    HttpURLConnection loadConnection2 =
        tryRequest("searchcsv?value=Providence&index=0&header=City/Town");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, loadConnection2.getResponseCode());
    // Get the expected response: a success
    Map<String, Object> responseBody2 =
        adapter.fromJson(new Buffer().readFrom(loadConnection2.getInputStream()));
    assertEquals("error", responseBody2.get("result"));
    assertEquals("Invalid parameters specified!", responseBody2.get("error_type"));

    loadConnection.disconnect();
  }
}
