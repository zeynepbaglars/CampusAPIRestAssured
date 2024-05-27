package CampusAPI;


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
import static org.hamcrest.Matchers.equalTo;

public class US04_Fields {

    RequestSpecification reqSpec;
    String id;
    String name = "Ress61";
    String type= "STRING";
    String schoolId = "6390f3207a3bcb6a7ac977f9";
    Map<String, Object> fieldType =new HashMap<>();

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
    public void createField(){

        fieldType.put("name", name);
        fieldType.put("schoolId", schoolId);
        fieldType.put("type",type);

        id =
                given()
                        .spec(reqSpec)
                        .body(fieldType)
                        //.log().body()

                        .when()
                        .post("/school-service/api/entity-field")

                        .then()
                        //.log().body()
                        .statusCode(201)
                        .extract().path("id")
        ;
    }

    @Test (dependsOnMethods = "createField")
    public void createFieldNegative(){

        fieldType.put("name", name);
        fieldType.put("schoolId", schoolId);
        fieldType.put("type",type);

        given()
                .spec(reqSpec)
                .body(fieldType)
                //.log().body()

                .when()
                .post("/school-service/api/entity-field")

                .then()
                //.log().body()
                .statusCode(400) // Bug
        ;
    }

    @Test (dependsOnMethods = "createField")
    public void updateField(){

        name="Merve1818";
        fieldType.put("id",id);
        fieldType.put("name", name);
        fieldType.put("schoolId", schoolId);
        fieldType.put("type",type);

        given()
                .spec(reqSpec)
                .body(fieldType)
                //.log().body()

                .when()
                .put("/school-service/api/entity-field")

                .then()
                //.log().body()
                .statusCode(200)
                .body("name", equalTo(name))
        ;
    }

    @Test(dependsOnMethods = "updateField")
    public void deleteField(){

        given()

                .spec(reqSpec)
                .pathParam("id", id)
                //.log().body()

                .when()
                .delete("/school-service/api/entity-field/{id}")

                .then()
                //.log().body()
                .statusCode(204)
        ;
    }



    @Test (dependsOnMethods = "deleteField" )
    public void deleteFieldNegative(){

        given()

                .spec(reqSpec)
                .pathParam("id", id)
                //.log().uri()

                .when()
                .delete("/school-service/api/entity-field/{id}")

                .then()
                //.log().body()
                .statusCode(400)
                .body("message", equalTo("EntityField not found"))
        ;
    }
}

