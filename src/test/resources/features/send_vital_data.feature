Feature: Vital Data Controller As a user I want to be able to start and stop sending vital data to Kafka and view configured Data

  Scenario: Attempt to stop sending vital data when not started
    Given  The App is Running
    When I make a POST request to the "http://localhost:8081/stop" endpoint
    Then I should receive a response with a status of 200 and while attempting to stop sending vital data when not started

  Scenario: View vital data when system is down
    Given vital data stopped using "http://localhost:8081/stop" endpoint
    When I send a GET request to "http://localhost:8081/parameters" endpoint
    Then I should receive a response with a status of 200 and the vital data null when system down

  Scenario: Start sending vital data
    When I make a POST request to start the "http://localhost:8081/start" endpoint
    Then I should receive a response with a status of 200

  Scenario: Attempt to start sending vital data when already started
    When I make a POST request to the start again on "http://localhost:8081/start" endpoint
    Then I should receive a response with a status of 200 while attempting to start sending vitaldata then already started

  Scenario: Stop sending vital data
    When I make a POST request to stop the "http://localhost:8081/stop" endpoint
    Then I should receive a response with a status of 200 when stop sending data

  Scenario: Immediately start sending vital data after stop
    When I make a POST request to  immediately start the "http://localhost:8081/start" endpoint
    Then I should receive a response with a status of 200  while immediately starting

  Scenario: View vital data
    Given I make a POST request to start fr view the "http://localhost:8081/start" endpoint
    When I send a GET request to "http://localhost:8081/parameters"
    Then I should receive a response with a status of 200 and the vital data

  Scenario: Edit parameters
    Given I make a POST request to start fr view the "http://localhost:8081/start" endpoint
    When I make a POST request to edit the "http://localhost:8081/editParameters" parameters "{ \"bloodPressure\":{ \"systolicPressure\":124, \"diastolicPressure\":72}, \"heartRate\":76, \"respiratoryRate\":62, \"spo2\":98, \"bodyTemperature\":34, \"patient\":\"okay Joseph\"} "
    Then I should receive a response with a status of 200 and message "{\"status\":\"New vital data has been added\"}"

  Scenario: Edit interval
    When I make a POST request to edit the "http://localhost:8081/editInterval" interval "{ \"interval\":2}"
    Then I should receive a response with a status of 200 and the message for interval "{\"status\":\"Interval has been edited\"}"

  Scenario: Edit Id's
    Given I make a POST request to start fr view the "http://localhost:8081/start" endpoint
    When I make a POST request to edit the "http://localhost:8081/editId" location id and serial id "{\"serialId\":\"asd123\",\"locationId\":3}"

    Then I should receive a response with a status of 200 and message "{\"status\":\"New SerialId and LocationId has been added\"}"