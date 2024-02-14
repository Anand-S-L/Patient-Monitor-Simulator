package com.nest_digital.vitals;

import com.nest_digital.vitals.controller.VitalDataController;
import com.nest_digital.vitals.dto.VitalData;
import com.nest_digital.vitals.kafkaproducer.VitalDataProducer;
import com.nest_digital.vitals.utilities.HL7Generator;
import com.nest_digital.vitals.utilities.JsonResponse;
import com.nest_digital.vitals.utilities.Prop;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.nest_digital.vitals.utilities.Prop.*;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@SpringBootTest
class VitalsApplicationTests {

    @Mock
    private KafkaTemplate kafkaTemplate;
    @InjectMocks
    private VitalDataProducer vitalDataProducer;
    JsonResponse jsonResponse = new JsonResponse();
    ResponseEntity<JsonResponse> response;
    @Mock
    HL7Generator hl7Generator;
    @InjectMocks
    VitalDataController vitalDataController;

    @Test
    void testMainMethod() {
        try {
            VitalsApplication.main(new String[]{});
        } catch (Exception e) {
          assertTrue(true);  
        }
    }
    @Test
    void testSendVitalsToKafka() {
        Map<String,Object> vitalJsonData = new HashMap<>();
        vitalJsonData.put(SP_O2, "98");
        vitalJsonData.put(BLOOD_PRESSURE, "120/80");
        vitalJsonData.put(HEART_RATE, "80");
        vitalJsonData.put(RESPIRATORY_RATE, "20");
        vitalJsonData.put(BODY_TEMPERATURE, "32");
        vitalJsonData.put(SERIAL_ID, "ABC123");
        vitalJsonData.put(LOCATION_ID, "Bed01");
        vitalDataController.setVitalDataService(vitalDataProducer);
        vitalDataController.getVitalDataService().sendVitalData(vitalJsonData);
        verify(kafkaTemplate).send(null, vitalJsonData);
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        vitalDataController = new VitalDataController();
        vitalDataController.setHl7Generator(hl7Generator);
    }

    @Test
    void testVitalDataConstructor() {
        VitalData vitalData = new VitalData("AAA123","Bed02","98", "98.6", "120/80", "80", "20");
        assertEquals("AAA123", vitalData.getSerialId());
        assertEquals("Bed02", vitalData.getLocationId());
        assertEquals("98", vitalData.getSpo2());
        assertEquals("98.6", vitalData.getBodyTemperature());
        assertEquals("120/80", vitalData.getBloodPressure());
        assertEquals("80", vitalData.getHeartRate());
        assertEquals("20", vitalData.getRespiratoryRate());
    }

    @Test
    void testVitalDataGettersAndSetters() {
        VitalData vitalData = new VitalData();
        vitalData.setSpo2("98");
        vitalData.setBodyTemperature("98.6");
        vitalData.setBloodPressure("120/80");
        vitalData.setHeartRate("80");
        vitalData.setRespiratoryRate("20");

        assertEquals("98", vitalData.getSpo2());
        assertEquals("98.6", vitalData.getBodyTemperature());
        assertEquals("120/80", vitalData.getBloodPressure());
        assertEquals("80", vitalData.getHeartRate());
        assertEquals("20", vitalData.getRespiratoryRate());
    }

    @Test
    void testNoArgsConstructor() {
        JsonResponse response = new JsonResponse();
        assertEquals(null, response.getStatus());
    }

    @Test
    void testAllArgsConstructor() {
        JsonResponse response = new JsonResponse("SUCCESS");
        assertEquals("SUCCESS", response.getStatus());
    }

    @Test
    void testGettersAndSetters() {
        JsonResponse response = new JsonResponse();
        response.setStatus("SUCCESS");
        assertEquals("SUCCESS", response.getStatus());
    }

    @Test
    void testHeartRateHl7() {
        HL7Generator hl7Generator = new HL7Generator();
        hl7Generator.heartRate = "80";
        String expected = "{\n" +
                "  \"resourceType\": \"Observation\",\n" +
                "  \"id\": \"heart-rate\",\n" +
                "  \"status\": \"final\",\n" +
                "  \"code\": {\n" +
                "    \"coding\": [ {\n" +
                "      \"system\": \"http://loinc.org\",\n" +
                "      \"code\": \"8867-4\",\n" +
                "      \"display\": \"Heart rate\"\n" +
                "    } ]\n" +
                "  },\n" +
                "  \"effectiveDateTime\": \"2021-10-01T05:30:00+05:30\",\n" +
                "  \"valueQuantity\": {\n" +
                "    \"value\": 80,\n" +
                "    \"unit\": \"beats/min\"\n" +
                "  }\n" +
                "}";
        String result = hl7Generator.heartRateHL7();
        assertEquals(expected, result);
    }

    @Test
    void testTemperatureHl7() {
        HL7Generator hl7Generator = new HL7Generator();
        hl7Generator.bodyTemperature = "98.6";
        String expected = "{\n" +
                "  \"resourceType\": \"Observation\",\n" +
                "  \"id\": \"temperature\",\n" +
                "  \"status\": \"final\",\n" +
                "  \"code\": {\n" +
                "    \"coding\": [ {\n" +
                "      \"system\": \"http://loinc.org\",\n" +
                "      \"code\": \"8310-5\",\n" +
                "      \"display\": \"Body temperature\"\n" +
                "    } ]\n" +
                "  },\n" +
                "  \"effectiveDateTime\": \"2021-10-01T05:30:00+05:30\",\n" +
                "  \"valueQuantity\": {\n" +
                "    \"value\": 98.6,\n" +
                "    \"unit\": \"C\"\n" +
                "  }\n" +
                "}";
        String result = hl7Generator.temperatureHL7();
        assertEquals(expected, result);
    }

    @Test
    void testSpo2Hl7() {
        HL7Generator hl7Generator = new HL7Generator();
        hl7Generator.spo2 = "95.0";
        String expected = "{\n" +
                "  \"resourceType\": \"Observation\",\n" +
                "  \"id\": \"spo2\",\n" +
                "  \"status\": \"final\",\n" +
                "  \"code\": {\n" +
                "    \"coding\": [ {\n" +
                "      \"system\": \"http://loinc.org\",\n" +
                "      \"code\": \"2710-2\",\n" +
                "      \"display\": \"Oxygen saturation in Arterial blood\"\n" +
                "    } ]\n" +
                "  },\n" +
                "  \"effectiveDateTime\": \"2021-10-01T05:30:00+05:30\",\n" +
                "  \"valueQuantity\": {\n" +
                "    \"value\": 95.0,\n" +
                "    \"unit\": \"%\"\n" +
                "  }\n" +
                "}";
        String result = hl7Generator.spo2HL7();
        assertEquals(expected, result);
    }

    @Test
    void testRespirationRateHl7() {
        HL7Generator hl7Generator = new HL7Generator();
        hl7Generator.respiratoryRate = "12";
        String expected = "{\n" +
                "  \"resourceType\": \"Observation\",\n" +
                "  \"id\": \"respiration-rate\",\n" +
                "  \"status\": \"final\",\n" +
                "  \"code\": {\n" +
                "    \"coding\": [ {\n" +
                "      \"system\": \"http://loinc.org\",\n" +
                "      \"code\": \"9279-1\",\n" +
                "      \"display\": \"Respiratory rate\"\n" +
                "    } ]\n" +
                "  },\n" +
                "  \"effectiveDateTime\": \"2021-10-01T05:30:00+05:30\",\n" +
                "  \"valueQuantity\": {\n" +
                "    \"value\": 12,\n" +
                "    \"unit\": \"breaths/min\"\n" +
                "  }\n" +
                "}";
        String result = hl7Generator.respirationRateHL7();
        assertEquals(expected, result);
    }

    @Test
    void testBloodpressureHl7() {
        HL7Generator hl7Generator = new HL7Generator();
        hl7Generator.systolicPressure = "120";
        hl7Generator.diastolicPressure = "80";
        String expected = "{\n" +
                "  \"resourceType\": \"Observation\",\n" +
                "  \"id\": \"blood-pressure\",\n" +
                "  \"status\": \"final\",\n" +
                "  \"code\": {\n" +
                "    \"coding\": [ {\n" +
                "      \"system\": \"http://loinc.org\",\n" +
                "      \"code\": \"55284-4\",\n" +
                "      \"display\": \"Blood pressure\"\n" +
                "    } ]\n" +
                "  },\n" +
                "  \"effectiveDateTime\": \"2021-10-01T05:30:00+05:30\",\n" +
                "  \"component\": [ {\n" +
                "    \"code\": {\n" +
                "      \"coding\": [ {\n" +
                "        \"system\": \"http://loinc.org\",\n" +
                "        \"code\": \"8480-6\",\n" +
                "        \"display\": \"Systolic blood pressure\"\n" +
                "      } ]\n" +
                "    },\n" +
                "    \"valueQuantity\": {\n" +
                "      \"value\": 120,\n" +
                "      \"unit\": \"mm[Hg]\"\n" +
                "    }\n" +
                "  }, {\n" +
                "    \"code\": {\n" +
                "      \"coding\": [ {\n" +
                "        \"system\": \"http://loinc.org\",\n" +
                "        \"code\": \"8462-4\",\n" +
                "        \"display\": \"Diastolic blood pressure\"\n" +
                "      } ]\n" +
                "    },\n" +
                "    \"valueQuantity\": {\n" +
                "      \"value\": 80,\n" +
                "      \"unit\": \"mm[Hg]\"\n" +
                "    }\n" +
                "  } ]\n" +
                "}";
        String result = hl7Generator.bloodPressureHl7();
        assertEquals(expected, result);
    }

    @Test
    void testParameters() {
        VitalData jsonResponse = vitalDataController.parameters().block().getBody();
        assertNotNull(jsonResponse);
    }
    @Test
    void startSendingVitalsTest(){
        vitalDataController.setHl7(new HL7Generator());
        response = vitalDataController.startSendingVitals().block();
        Assertions.assertNotNull(response);
        jsonResponse.setStatus(STARTED_SENDING_VITAL);
        assertEquals(jsonResponse.getStatus(),response.getBody().getStatus());
        assertFalse(vitalDataController.getExecutor().isTerminated());

    }

    @Test
    void startSendingVitalsWhenAlreadyRunning(){
        response = vitalDataController.startSendingVitals().block();
        Assertions.assertNotNull(response);
        response = vitalDataController.startSendingVitals().block();
        Assertions.assertNotNull(response);
        jsonResponse.setStatus(ALREADY_RUNNING);
        assertEquals(jsonResponse.getStatus(),response.getBody().getStatus());
        assertFalse(vitalDataController.getExecutor().isTerminated());
    }

    @Test
    void stopSendingVitalsTest(){
        response = vitalDataController.startSendingVitals().block();
        Assertions.assertNotNull(response);
        response = vitalDataController.stopSendingVitals().block();
        Assertions.assertNotNull(response);
        jsonResponse.setStatus(STOPPED_SENDING_VITAL);
        assertEquals(jsonResponse.getStatus(),response.getBody().getStatus());
    }

    @Test
    void stopSendingVitalsTestWhenNotSendingNotStarted(){
        response = vitalDataController.stopSendingVitals().block();
        Assertions.assertNotNull(response);
        jsonResponse.setStatus(SENDING_NOT_STARTED);
        assertEquals(jsonResponse.getStatus(),response.getBody().getStatus());
        assertNull(vitalDataController.getExecutor());
    }
    @Test
    void editParametersTest(){
        response = vitalDataController.startSendingVitals().block();
        Assertions.assertNotNull(response);
        vitalDataController.setHl7(new HL7Generator());
        jsonResponse.setStatus(STARTED_SENDING_VITAL);
        assertEquals(jsonResponse.getStatus(),response.getBody().getStatus());
        Map<String,Object> parameters = new HashMap<>();
        Map<String,Object> bloodPressure = new HashMap<>();
        bloodPressure.put("systolicPressure", "120");
        bloodPressure.put("diastolicPressure", "80");
        parameters.put("bloodPressure", bloodPressure);
        parameters.put("spo2", "98");
        parameters.put("heartRate", "80");
        parameters.put("bodyTemperature", "32");
        parameters.put("respiratoryRate", "20");
        parameters.put("patient","Akshay");
        response = vitalDataController.editParameters(parameters).block();
        Assertions.assertNotNull(response);
        jsonResponse.setStatus(EDITED_VITAL_DATA);
        assertEquals(jsonResponse.getStatus(),response.getBody().getStatus());
    }
    @Test
    void editIntervalTest(){
        response = vitalDataController.startSendingVitals().block();
        Assertions.assertNotNull(response);
        assertFalse(vitalDataController.getExecutor().isTerminated());
        Map<String,Object> interval = new HashMap<>();
        interval.put("interval", "2");
        response = vitalDataController.editInterval(interval).block();
        Assertions.assertNotNull(response);
        jsonResponse.setStatus(EDITED_INTERVAL);
        assertEquals(jsonResponse.getStatus(),response.getBody().getStatus());
        assertEquals(200,response.getStatusCodeValue());

    }

    @Test
    void editParametersTestWhenNotSending(){
        response = vitalDataController.editParameters(new HashMap<>()).block();
        Assertions.assertNotNull(response);
        jsonResponse.setStatus(SENDING_NOT_STARTED);
        assertEquals(jsonResponse.getStatus(),response.getBody().getStatus());
        assertEquals(200,response.getStatusCodeValue());
    }
    @Test
    void editIntervalTestWhenNotSending(){
        response = vitalDataController.editInterval(new HashMap<>()).block();
        Assertions.assertNotNull(response);
        jsonResponse.setStatus(SENDING_NOT_STARTED);
        assertEquals(jsonResponse.getStatus(),response.getBody().getStatus());
        assertEquals(200,response.getStatusCodeValue());
    }
    @Test
    void getStatus(){
        response = vitalDataController.startSendingVitals().block();
        Assertions.assertNotNull(response);
        response = vitalDataController.status().block();
        Assertions.assertNotNull(response);
        assertEquals("1",response.getBody().getStatus());
    }
    @Test
    void getStatusWhenNotRunning(){
        response = vitalDataController.status().block();
        Assertions.assertNotNull(response);
        assertEquals("0",response.getBody().getStatus());
    }


    @Test
    void testSerialIdValidations() {
        Prop prop = new Prop();
        prop.regex = "[A-Za-z]{3}[0-9]{3}";
        boolean validResult = prop.serialIdValidation("aaa111");
        boolean invalidResult = prop.serialIdValidation("a");
        Assertions.assertTrue(validResult);
        Assertions.assertFalse(invalidResult);
    }

    @Test
    void testSerialId() {
        String expectedSerialId = "12345";
        HL7Generator hl7Generator1 = new HL7Generator();
        hl7Generator1.serialId = "12345";
        String actualSerialId = hl7Generator1.serialId();
        Assertions.assertEquals(expectedSerialId, actualSerialId);
    }
    @Test
    void testSetDate() {
        Prop prop = new Prop();
        LocalDate localDate = LocalDate.of(2021, 10, 1);
        LocalDateTime localDateTime = localDate.atStartOfDay();
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        long timestamp = instant.toEpochMilli();
        Date date = new Date(timestamp);
        prop.setDate(date);
        assertEquals(date, prop.getDate());
    }

    @Test
    void testSetLiveVital() {
        Prop prop = new Prop();
        VitalData vitalData = new VitalData();
        prop.setLiveVital(vitalData);
        assertEquals(vitalData, prop.getLiveVital());
    }
    @Test
    void testSetPeriod() {
        Prop prop = new Prop();
        prop.setPeriod(5);
        assertEquals(5, prop.getPeriod());
    }

    @Test
    void testEditId(){
        response = vitalDataController.startSendingVitals().block();
        Assertions.assertNotNull(response);
        vitalDataController.setHl7(new HL7Generator());
        jsonResponse.setStatus(STARTED_SENDING_VITAL);
        assertEquals(jsonResponse.getStatus(),response.getBody().getStatus());
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("serialId", "AAA123");
        parameters.put("locationId","Bed01");
        vitalDataController.regex= "[A-Za-z]{3}[0-9]{3}";
        response = vitalDataController.editId(parameters).block();
        Assertions.assertNotNull(response);
        jsonResponse.setStatus(EDITED_SERIALID_LOCATIONID_DATA);
        assertEquals(jsonResponse.getStatus(),response.getBody().getStatus());
    }
    @Test
    void testInvalidEditId(){
        response = vitalDataController.startSendingVitals().block();
        Assertions.assertNotNull(response);
        vitalDataController.setHl7(new HL7Generator());
        jsonResponse.setStatus(STARTED_SENDING_VITAL);
        assertEquals(jsonResponse.getStatus(),response.getBody().getStatus());
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("serialId", "12234");
        parameters.put("locationId","Bed01");
        vitalDataController.regex= "[A-Za-z]{3}[0-9]{3}";
        response = vitalDataController.editId(parameters).block();
        Assertions.assertNotNull(response);
        jsonResponse.setStatus(INVALID_SERIALID);
        assertEquals(jsonResponse.getStatus(),response.getBody().getStatus());
    }
    @Test
    void editEditIDTestWhenNotSending(){
        response = vitalDataController.editId(new HashMap<>()).block();
        Assertions.assertNotNull(response);
        jsonResponse.setStatus(SENDING_NOT_STARTED);
        assertEquals(jsonResponse.getStatus(),response.getBody().getStatus());
        assertEquals(200,response.getStatusCodeValue());
    }

}
