package computerdatabase;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.util.concurrent.ThreadLocalRandom;

import java.time.Duration;
import java.util.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import io.gatling.javaapi.jdbc.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static io.gatling.javaapi.jdbc.JdbcDsl.*;

public class ComputerDatabaseSimulation extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080")
            .inferHtmlResources()
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
            .acceptEncodingHeader("gzip, deflate")
            .acceptLanguageHeader("de,en-US;q=0.7,en;q=0.3")
            .upgradeInsecureRequestsHeader("1")
            .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:126.0) Gecko/20100101 Firefox/126.0");

    private Map<CharSequence, String> headers_0 = Map.of("Priority", "u=1");


    private ScenarioBuilder scn = scenario("RecordedSimulation")
            .exec(
                    http("gameState")
                            .get("/gameState")
                            .headers(headers_0)
            )
            .exec(
                    http("setPlayerAmount")
                            .post("/turn")  // Ändern Sie dies zu POST
                            .headers(headers_0)
                            .body(StringBody("3"))  // Geben Sie den Body für die POST-Anfrage an
            )
            .exec(
                    http("gameState")
                            .get("/gameState")
                            .headers(headers_0)
            )
            .exec(
                    http("NamePlayer1")
                            .post("/turn")  // Ändern Sie dies zu POST
                            .headers(headers_0)
                            .body(StringBody("1"))  // Geben Sie den Body für die POST-Anfrage an
            )
            .exec(
                    http("gameState")
                            .get("/gameState")
                            .headers(headers_0)
            )
            .exec(
                    http("NamePlayer2")
                            .post("/turn")  // Ändern Sie dies zu POST
                            .headers(headers_0)
                            .body(StringBody("2"))  // Geben Sie den Body für die POST-Anfrage an
            )
            .exec(
                    http("gameState")
                            .get("/gameState")
                            .headers(headers_0)
            )
            .exec(
                    http("NamePlayer3")
                            .post("/turn")  // Ändern Sie dies zu POST
                            .headers(headers_0)
                            .body(StringBody("3"))  // Geben Sie den Body für die POST-Anfrage an
            )
            .exec(
                    http("gameState")
                            .get("/gameState")
                            .headers(headers_0)
            )
            .exec(
                    http("PlayCard1")
                            .post("/turn")  // Ändern Sie dies zu POST
                            .headers(headers_0)
                            .body(StringBody("1"))  // Geben Sie den Body für die POST-Anfrage an
            )
            .exec(
                    http("gameState")
                            .get("/gameState")
                            .headers(headers_0)
            )
            .exec(
                    http("TargetPlayer2")
                            .post("/turn")  // Ändern Sie dies zu POST
                            .headers(headers_0)
                            .body(StringBody("2"))  // Geben Sie den Body für die POST-Anfrage an
            );

    {
        setUp(scn.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
    }
}