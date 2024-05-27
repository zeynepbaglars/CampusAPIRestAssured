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
public class US08_Discounts {
    RequestSpecification reqSpec;
    Faker faker=new Faker();
    Map<String, String> discounts=new HashMap<>();

    String discountID;
    String descriptionName;


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
    public void createDiscount(){

        descriptionName=faker.harryPotter().character();
        discounts.put("description", descriptionName);
        discounts.put("code", faker.number().digits(4));
        discounts.put("priority", faker.number().digits(2));

        discountID=
        given()

                .spec(reqSpec)
                .body(discounts)
                //.log().body()

                .when()
                .post("/school-service/api/discounts")

                .then()
                //.log().body()
                .statusCode(201)
                .extract().path("id")
                ;
    }

    @Test (dependsOnMethods = "createDiscount")
    public void createDiscountNeg(){

        discounts.put("description", descriptionName);
        discounts.put("code", faker.number().digits(4));
        discounts.put("priority", faker.number().digits(2));

        given()

                .spec(reqSpec)
                .body(discounts)
                //.log().body()

                .when()
                .post("/school-service/api/discounts")

                .then()
                //.log().body()
                .statusCode(400)
        ;
    }

    @Test (dependsOnMethods = "createDiscount")
    public void updateDiscount(){

        discounts.put("id", discountID);
        descriptionName="Hermione Granger"+faker.number().digits(2);
        discounts.put("description", descriptionName);
        discounts.put("code", faker.number().digits(4));

        given()

                .spec(reqSpec)
                .body(discounts)
                //.log().body()

                .when()
                .put("/school-service/api/discounts")

                .then()
                //.log().body()
                .statusCode(200)
                .body("description", equalTo(descriptionName))
        ;
    }

    @Test (dependsOnMethods = "updateDiscount")
    public void deleteDiscount(){

        given()
                .spec(reqSpec)
                .pathParam("discountID", discountID)
                //.log().uri()

                .when()
                .delete("/school-service/api/discounts/{discountID}")

                .then()
                //.log().body()
                .statusCode(200)
                ;
    }


    @Test (dependsOnMethods = "deleteDiscount")
    public void deleteDiscountNeg(){

        given()
                .spec(reqSpec)
                .pathParam("discountID", discountID)
                //.log().uri()

                .when()
                .delete("/school-service/api/discounts/{discountID}")

                .then()
                //.log().body()
                .statusCode(400)
        ;
    }

}
