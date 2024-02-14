package com.nest_digital.vitals.stepdef;

import com.nest_digital.vitals.VitalsApplication;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;


@SpringBootApplication
public class sendVitalDataStepDef {
    private RestTemplate restTemplate = new RestTemplate();
    private ResponseEntity<String> response;
    private WebTestClient client;

    @When("I make a POST request to start the {string} endpoint")
    public void i_make_a_post_request_to_start_the_endpoint(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        response = restTemplate.exchange(endpoint, HttpMethod.POST, request, String.class);
    }

    @When("I make a POST request to stop the {string} endpoint")
    public void i_make_a_post_request_to_stop_the_endpoint(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        response = restTemplate.exchange(endpoint, HttpMethod.POST, request, String.class);
    }

    @Given("The App is Running")
    public void theAppIsRunning() {
        try {
            SpringApplication.run(VitalsApplication.class);
        }catch (Exception e) {
            System.out.println(e);
        }
    }

    @When("I make a POST request to the {string} endpoint")
    public void iMakeAPOSTRequestToTheEndpoint(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        response = restTemplate.exchange(endpoint, HttpMethod.POST, request, String.class);
    }

    @When("I make a POST request to the start again on {string} endpoint")
    public void iMakeAPOSTRequestToTheStartAgainOnEndpoint(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        response = restTemplate.exchange(endpoint, HttpMethod.POST, request, String.class);
    }

    @When("I send a GET request to {string}")
    public void iSendAGETRequestTo(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        response = restTemplate.exchange(endpoint, HttpMethod.GET, request, String.class);
    }

    @Then("I should receive a response with a status of {int} and the vital data")
    public void iShouldReceiveAResponseWithAStatusOfAndTheVitalData(int status) {
        String expectedData = "{\"serialId\":\"aaa123\",\"locationId\":\"2\",\"spo2\":\"96\",\"bodyTemperature\":\"38\",\"bloodPressure\":\"120/80\",\"heartRate\":\"90\",\"respiratoryRate\":\"12\"}";
        assertEquals(status, response.getStatusCodeValue());
        assertEquals(expectedData, response.getBody());
        System.out.println(response.getBody());
    }

    @Then("I should receive a response with a status of {int}")
    public void iShouldReceiveAResponseWithAStatusOf(int status) {
        assertEquals(status, response.getStatusCodeValue());
        assertEquals("{\"status\":\"Started sending vital data to Kafka\"}", response.getBody());
        System.out.println(response.getBody());
    }

    @Then("I should receive a response with a status of {int} while attempting to start sending vitaldata then already started")
    public void iShouldReceiveAResponseWithAStatusOfWhileAttemptingToStartSendingVitaldataThenAlreadyStarted(int status) {
        assertEquals(status, response.getStatusCodeValue());
        assertEquals("{\"status\":\"ALREADY RUNNING\"}", response.getBody());
        System.out.println(response.getBody());
    }

    @Then("I should receive a response with a status of {int} when stop sending data")
    public void iShouldReceiveAResponseWithAStatusOfWhenStopSendingData(int status) {
        assertEquals(status, response.getStatusCodeValue());
        assertEquals("{\"status\":\"Stopped sending vital data to Kafka\"}", response.getBody());
        System.out.println(response.getBody());
    }

    @Then("I should receive a response with a status of {int} and while attempting to stop sending vital data when not started")
    public void iShouldReceiveAResponseWithAStatusOfAndWhileAttemptingToStopSendingVitalDataWhenNotStarted(int status) {
        assertEquals(status, response.getStatusCodeValue());
        assertEquals("{\"status\":\"Vital data sending was not started\"}", response.getBody());
        System.out.println(response.getBody());
    }

    @Then("I should receive a response with a status of {int} and the vital data null when system down")
    public void iShouldReceiveAResponseWithAStatusOfAndTheVitalDataNullWhenSystemDown(int status) {
        System.out.println(status);
        assertEquals(status, response.getStatusCodeValue());

        assertEquals("{\"serialId\":null,\"locationId\":null,\"spo2\":null,\"bodyTemperature\":null,\"bloodPressure\":null,\"heartRate\":null,\"respiratoryRate\":null}", response.getBody());


    }

    @When("I send a GET request to {string} endpoint")
    public void iSendAGETRequestToEndpoint(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        response = restTemplate.exchange(endpoint, HttpMethod.GET, request, String.class);

    }

    @When("vital data stopped using {string} endpoint")
    public void vitalDataStoppedUsingEndpoint(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
    }

    @Given("I make a POST request to start fr view the {string} endpoint")
    public void iMakeAPOSTRequestToStartFrViewTheEndpoint(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        response = restTemplate.exchange(endpoint, HttpMethod.POST, request, String.class);

    }

    @When("I make a POST request to  immediately start the {string} endpoint")
    public void iMakeAPOSTRequestToImmediatelyStartTheEndpoint(String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(null, headers);
        response = restTemplate.exchange(endpoint, HttpMethod.POST, request, String.class);

    }

    @Then("I should receive a response with a status of {int}  while immediately starting")
    public void iShouldReceiveAResponseWithAStatusOfWhileImmediatelyStarting(int status) {
        assertEquals(status, response.getStatusCodeValue());
    }


    @Then("I should receive a stream of live vital data, updated every second")
    public void iShouldReceiveAStreamOfLiveVitalDataUpdatedEverySecond() {
        client.get().uri("/liveVital").exchange()
                .expectStatus().isOk();
    }

    @When("I make a GET request to {string} to endpoint {string}")
    public void iMakeAGETRequestToToEndpoint(String url, String endpoint) {
        client = WebTestClient.bindToServer().baseUrl(url).build();
        client.get().uri(endpoint).exchange();

    }

    @When("I make a POST request to edit the {string} parameters {string}")
    public void iMakeAPOSTRequestToEditTheParametersBloodPressureSystolicDiastolicHeartRateRespiratoryRateSpoTemperaturePatientAkshayJoseph(String endpoint, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        response = restTemplate.exchange(endpoint, HttpMethod.POST, request, String.class);
    }

    @Then("I should receive a response with a status of {int} and message {string}")
    public void iShouldReceiveAResponseWithAStatusOfAndMessage(int status, String message) {
        assertEquals(status, response.getStatusCodeValue());
        assertEquals(message, response.getBody());
        System.out.println(response.getBody());
    }

    @When("I make a POST request to edit the {string} interval {string}")
    public void iMakeAPOSTRequestToEditTheIntervalInterval(String endpoint, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        response = restTemplate.exchange(endpoint, HttpMethod.POST, request, String.class);
    }


    @Then("I should receive a response with a status of {int} and the message for interval {string}")
    public void iShouldReceiveAResponseWithAStatusOfAndTheMessageForInterval(int status, String message) {
        assertEquals(status, response.getStatusCodeValue());
        assertEquals(message, response.getBody());
        System.out.println(response.getBody());
    }


    @When("I make a POST request to edit the {string} location id and serial id {string}")
    public void iMakeAPOSTRequestToEditTheLocationIdAndSerialId(String endpoint, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        response = restTemplate.exchange(endpoint, HttpMethod.POST, request, String.class);


    }
}




