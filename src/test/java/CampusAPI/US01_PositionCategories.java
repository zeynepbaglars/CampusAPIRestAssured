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
import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class US01_PositionCategories {

    RequestSpecification reqSpec;

    Faker faker=new Faker();
    String positionName = faker.pokemon().name();

    String positionId;

    Map<String, String> position =new HashMap<>();

    @BeforeClass
    public void Login(){

        baseURI="https://test.mersys.io";

        Map<String, String> userCredential=new HashMap<>();
        userCredential.put("username", "turkeyts");
        userCredential.put("password", "TechnoStudy123");
        userCredential.put("rememberMe", "true");

        Cookies cookies=
                given()

                        .contentType(ContentType.JSON)
                        .body(userCredential)

                        .when()
                        .post("/auth/login")

                        .then()
                        .statusCode(200)
                        .extract().response().getDetailedCookies()
                ;

        reqSpec=new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build()
        ;
    }

    @Test
    public void createPosition(){

        position.put("name", positionName );

        positionId =
                given()
                        .spec(reqSpec)
                        .body(position)
                        //.log().body()

                        .when()
                        .post("/school-service/api/position-category")

                        .then()
                        //.log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;
    }

    @Test (dependsOnMethods = "createPosition")
    public void createPositionNeg(){

        position.put("name", positionName );

        given()

                .spec(reqSpec)
                .body(position)
                //.log().body()

                .when()
                .post("/school-service/api/position-category")

                .then()
                //.log().body()
                .statusCode(400)
                .body("message", containsString("already"))
        ;
    }

    @Test (dependsOnMethods = "createPositionNeg")
    public void updatePosition(){

        positionName += faker.number().digits(3);
        position.put("id", positionId);
        position.put("name", positionName);


        given()

                .spec(reqSpec)
                .body(position)
                //.log().body()

                .when()
                .put("/school-service/api/position-category")

                .then()
                //.log().body()
                .statusCode(200)
                .body("name", equalTo(positionName))
        ;
    }

    @Test (dependsOnMethods = "updatePosition")
    public void deletePosition(){

        given()
                .spec(reqSpec)
                .pathParam("positionID", positionId)
                //.log().uri()

                .when()
                .delete("/school-service/api/position-category/{positionID}")

                .then()
                //.log().body()
                .statusCode(204)
        ;
    }

    @Test (dependsOnMethods = "deletePosition")
    public void deletePositionNeg(){

        given()
                .spec(reqSpec)
                .pathParam("positionID", positionId)
                //.log().uri()

                .when()
                .delete("/school-service/api/position-category/{positionID}")

                .then()
                //.log().body()
                .statusCode(400)
        ;
    }
}
