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
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class US07_GradeLevels {
    RequestSpecification reqSpec;
    String id;
    String name;
    String shortName;
    String [] schoolId = {"6390f3207a3bcb6a7ac977f9"};

    Map<String, Object> gradeLevelMan =new HashMap<>();
    Faker faker= new Faker();

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
    public void createGradeLevels(){

        name= faker.harryPotter().character();
        shortName=("BC"+ faker.address().buildingNumber());
        gradeLevelMan.put("name", name);
        gradeLevelMan.put("shortName", shortName);
        gradeLevelMan.put("order","48");
        gradeLevelMan.put("schoolIds", schoolId);


        id =
                given()
                        .spec(reqSpec)
                        .body(gradeLevelMan)
                        //.log().body()

                        .when()
                        .post("/school-service/api/grade-levels")

                        .then()
                        //.log().body()
                        .statusCode(201)
                        .extract().path("id")
                ;
    }

    @Test(dependsOnMethods = "createGradeLevels")
    public void createGradeLevelsNeg(){

        gradeLevelMan.put("name", name);
        gradeLevelMan.put("shortName", shortName);
        gradeLevelMan.put("order","48");
        gradeLevelMan.put("schoolIds", schoolId);

                given()
                        .spec(reqSpec)
                        .body(gradeLevelMan)
                        //.log().body()

                        .when()
                        .post("/school-service/api/grade-levels")

                        .then()
                        //.log().body()
                        .statusCode(400)
                        .body("message", containsString("already"))
        ;
    }
    @Test(dependsOnMethods = "createGradeLevelsNeg")
    public void updateGradeLevels(){

        gradeLevelMan.put("name", name+"_edit");
        gradeLevelMan.put("id", id);

        given()
                .spec(reqSpec)
                .body(gradeLevelMan)
                //.log().body()

                .when()
                .put("/school-service/api/grade-levels")

                .then()
                //.log().body()
                .statusCode(200)
                .body("name", equalTo(name+"_edit"))
        ;
    }
    @Test(dependsOnMethods = "updateGradeLevels")
    public void deleteGradeLevel(){

        given()
                .spec(reqSpec)
                .pathParam("id", id)

                .when()
                .delete("/school-service/api/grade-levels/{id}")

                .then()
                //.log().body()
                .statusCode(200)
        ;
    }
    @Test(dependsOnMethods = "deleteGradeLevel")
    public void deleteGradeLevelNeg(){

        given()
                .spec(reqSpec)
                .pathParam("id", id)

                .when()
                .delete("/school-service/api/grade-levels/{id}")

                .then()
                //.log().body()
                .statusCode(400)
        ;
    }
}
