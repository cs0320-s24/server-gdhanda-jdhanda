package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.handlers.BroadbandHandler;
import edu.brown.cs.student.main.server.handlers.LoadCSVHandler;
import edu.brown.cs.student.main.server.handlers.SearchCSVHandler;
import edu.brown.cs.student.main.server.handlers.ViewCSVHandler;
import spark.Spark;

public class Server {

  public static void main(String[] args) {
    int port = 3232;
    Spark.port(port);
    /*
       Setting CORS headers to allow cross-origin requests from the client; this is necessary for the client to
       be able to make requests to the server.

       By setting the Access-Control-Allow-Origin header to "*", we allow requests from any origin.
       This is not a good idea in real-world applications, since it opens up your server to cross-origin requests
       from any website. Instead, you should set this header to the origin of your client, or a list of origins
       that you trust.

       By setting the Access-Control-Allow-Methods header to "*", we allow requests with any HTTP method.
       Again, it's generally better to be more specific here and only allow the methods you need, but for
       this demo we'll allow all methods.

       We recommend you learn more about CORS with these resources:
           - https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS
           - https://portswigger.net/web-security/cors
    */
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // Setting up the handler for the GET /____csv and /broadband endpoints
    Spark.get("loadcsv", new LoadCSVHandler());
    Spark.get("searchcsv", new SearchCSVHandler());
    Spark.get("viewcsv", new ViewCSVHandler());
    Spark.get("broadband", new BroadbandHandler());
    Spark.init();
    Spark.awaitInitialization();

    System.out.println("Server started at http://localhost:" + port);
  }
}
