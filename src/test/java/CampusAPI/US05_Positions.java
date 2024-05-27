package CampusAPI;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class US05_Positions {

    Faker faker = new Faker();
    String positionsName;
    String id;
    String positionsShortName;
    String tenantId = "6390ef53f697997914ec20c2";

    Map<String, String> positions = new HashMap<>();


    RequestSpecification recSpec;

    String myUrlPath = "employee-position";

    @BeforeClass
    public void Login() {
        baseURI = "https://test.mersys.io/";

        Map<String, String> userCredential = new HashMap<>();
        userCredential.put("username", "turkeyts");
        userCredential.put("password", "TechnoStudy123");
        userCredential.put("rememberMe", "true");
        Cookies cookies =

                given()
                        .contentType(ContentType.JSON)
                        .body(userCredential)

                        .when()
                        .post("auth/login")

                        .then()
                        .statusCode(200)
                        .extract().response().getDetailedCookies();

        recSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }

    @Test
    public void createPositions() {
        positionsName = faker.name().firstName();
        positionsShortName = positionsName.substring(0, 3);
        positions.put("name", positionsName);
        positions.put("shortName", positionsShortName);
        positions.put("tenantId", tenantId);
        id =
                given()
                        .spec(recSpec)
                        .body(positions)
                        //.log().body()

                        .when()
                        .post("/school-service/api/" + myUrlPath)

                        .then()
                        //.log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;
    }

    @Test(dependsOnMethods = "createPositions")
    public void createPositionsNegative() {

        positions.put("name", positionsName);
        positions.put("shortName", positionsShortName);

        given()
                .spec(recSpec)
                .body(positions)
                //.log().body()

                .when()
                .post("/school-service/api/" + myUrlPath)

                .then()
                //.log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;
    }

    @Test(dependsOnMethods = "createPositionsNegative")
    public void updatePositions() {
        positionsName += "120";
        positionsName += "0a";
        positions.put("id", id);
        positions.put("name", positionsName);
        positions.put("shortName", positionsShortName);
        given()
                .spec(recSpec)
                .body(positions)
                //.log().body()

                .when()
                .put("/school-service/api/" + myUrlPath)

                .then()
                //.log().body()
                .statusCode(200)
                .body("name", equalTo(positionsName))
        ;
    }

    @Test(dependsOnMethods = "updatePositions")
    public void deletePositions() {
        given()
                .spec(recSpec)
                .pathParam("id", id)
                //.log().uri()

                .when()
                .delete("/school-service/api/"+myUrlPath+"/{id}")

                .then()
                //.log().body()
                .statusCode(204)
        ;
    }


    @Test(dependsOnMethods = "deletePositions")
    public void deletePositionsNegative() {
        given()
                .spec(recSpec)
                .pathParam("id", id)
                //.log().uri()

                .when()
                .delete("/school-service/api/"+myUrlPath+"/{id}")

                .then()
                //.log().body()
                .statusCode(204)
        ;
    }
}
