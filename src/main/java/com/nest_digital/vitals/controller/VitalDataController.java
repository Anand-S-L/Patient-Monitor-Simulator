/*
 * Copyright (c) NeST Digital Pvt Ltd, 2023.
 * All Rights Reserved. Confidential.
 */
package com.nest_digital.vitals.controller;

import com.nest_digital.vitals.dto.VitalData;
import com.nest_digital.vitals.kafkaproducer.VitalDataProducer;
import com.nest_digital.vitals.utilities.HL7Generator;
import com.nest_digital.vitals.utilities.JsonResponse;
import com.nest_digital.vitals.utilities.Prop;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Class: VitalDataController
 * Author: Aston Martin
 * Description: This class handles the generation and sending of vital data to a Kafka topic
 * it also have endpoints to start and stop sending vital data and view vital parameters
 */

@Service
@Controller
@CrossOrigin
@Getter
@Setter
public class VitalDataController extends Prop {
  @Autowired
  private VitalDataProducer vitalDataService;
  private ScheduledExecutorService executor;
  @Autowired
  private HL7Generator hl7;
  @Autowired
  private SimpMessagingTemplate simpMessagingTemplate;

  Runnable task = () -> {
    setLiveVital(hl7.generateVitalData());
    JSONObject spo2 = new JSONObject(getLiveVital().getSpo2());
    JSONObject bp = new JSONObject(getLiveVital().getBloodPressure());
    JSONObject heartRate = new JSONObject(getLiveVital().getHeartRate());
    JSONObject respirationRate = new JSONObject(getLiveVital().getRespiratoryRate());
    JSONObject temperature = new JSONObject(getLiveVital().getBodyTemperature());
    String serialId = getLiveVital().getSerialId();
    String locationId = getLiveVital().getLocationId();
    JSONObject vitalJsonData = new JSONObject();
    vitalJsonData.put(SP_O2, spo2);
    vitalJsonData.put(BLOOD_PRESSURE, bp);
    vitalJsonData.put(HEART_RATE, heartRate);
    vitalJsonData.put(RESPIRATORY_RATE, respirationRate);
    vitalJsonData.put(BODY_TEMPERATURE, temperature);
    vitalJsonData.put(SERIAL_ID, serialId);
    vitalJsonData.put(LOCATION_ID, locationId);
    simpMessagingTemplate.convertAndSend("/topic/messages", vitalJsonData.toMap());
    vitalDataService.sendVitalData(vitalJsonData.toMap());
  };


  /**
   * Method: startSendingVitals()
   * Description: starts sending vital data to kafka topic at fixed rate of 1 secon
   *
   * @return Mono<ResponseEntity < JsonResponse>>
   */

  @PostMapping("/start")
  public Mono<ResponseEntity<JsonResponse>> startSendingVitals() {
    JsonResponse jsonResponse = new JsonResponse();
    if (executor != null && !executor.isTerminated()) {
      jsonResponse.setStatus(ALREADY_RUNNING);
      return Mono.just(ResponseEntity.ok(jsonResponse));
    }
    executor = Executors.newScheduledThreadPool(1);
    executor.scheduleAtFixedRate(task, INITIAL_DELAY, getPeriod(), SECONDS);
    jsonResponse.setStatus(STARTED_SENDING_VITAL);
    return Mono.just(ResponseEntity.ok(jsonResponse));
  }


  /**
   * Method: stopSendingVitals()
   * Description: stops sending vital data to kafka topic
   *
   * @return Mono<ResponseEntity < JsonResponse>>
   */
  @PostMapping("/stop")
  public Mono<ResponseEntity<JsonResponse>> stopSendingVitals() {
    JsonResponse jsonResponse = new JsonResponse();
    if (executor != null) {
      executor.shutdown();
      jsonResponse.setStatus(STOPPED_SENDING_VITAL);
      return Mono.just(ResponseEntity.ok(jsonResponse));
    }
    jsonResponse.setStatus(SENDING_NOT_STARTED);
    return Mono.just(ResponseEntity.ok(jsonResponse));
  }

  /**
   * Method: view()
   * Description: returns vital data
   *
   * @return VitalData
   */
  @GetMapping("/parameters")
  public Mono<ResponseEntity<VitalData>> parameters() {
    if (executor == null || executor.isTerminated()) {
      return Mono.just(ResponseEntity.ok(new VitalData(null, null, null, null, null, null, null)));
    }
    VitalData vitalData = new VitalData(serialId, locationId, spo2, bodyTemperature, bloodPressure, heartRate, respiratoryRate);
    return Mono.just(ResponseEntity.ok(vitalData));
  }

  /**
   * Method: editId()
   * Description: edits serial id and location id data with the new values
   *
   * @return Mono<ResponseEntity < JsonResponse>>
   */
  @PostMapping("/editId")
  public Mono<ResponseEntity<JsonResponse>> editId(@RequestBody Map<String, Object> newSerialId) {
    JsonResponse jsonResponse = new JsonResponse();
    if (executor == null || executor.isTerminated()) {
      jsonResponse.setStatus(SENDING_NOT_STARTED);
      return Mono.just(ResponseEntity.ok(jsonResponse));
    }
    if (newSerialId.get(SERIAL_ID) != null) {
      String serialId = newSerialId.get(SERIAL_ID).toString();
      if (serialIdValidation(serialId)) {
        hl7.serialId = serialId;
      } else {
        jsonResponse.setStatus(INVALID_SERIALID);
        return Mono.just(ResponseEntity.ok(jsonResponse));
      }
    }
    if (newSerialId.get(LOCATION_ID) != null) {
      hl7.locationId = newSerialId.get(LOCATION_ID).toString();
    }
    jsonResponse.setStatus(EDITED_SERIALID_LOCATIONID_DATA);
    return Mono.just(ResponseEntity.ok(jsonResponse));
  }


  /**
   * Method: editVitals()
   * Description: edits vital data with the new values
   *
   * @return Mono<ResponseEntity < JsonResponse>>
   */
    @PostMapping("/editParameters")
    public Mono<ResponseEntity<JsonResponse>> editParameters(@RequestBody Map<String, Object> newVitalData) {
      JsonResponse jsonResponse = new JsonResponse();
      if (executor == null || executor.isTerminated()) {
        jsonResponse.setStatus(SENDING_NOT_STARTED);
        return Mono.just(ResponseEntity.ok(jsonResponse));
      }
      if (newVitalData.get(BLOOD_PRESSURE) != null) {
        Map<Object, Object> bp = (Map<Object, Object>) newVitalData.get("bloodPressure");
        if(bp.get(SYSTOLIC_PRESSURE) != null)
         hl7.systolicPressure = bp.get(SYSTOLIC_PRESSURE).toString();
        if(bp.get(DIASTOLIC_PRESSURE) != null)
         hl7.diastolicPressure = bp.get(DIASTOLIC_PRESSURE).toString();
        hl7.bloodPressure = hl7.systolicPressure + "/" + hl7.diastolicPressure;
      }
      if (newVitalData.get(HEART_RATE) != null) {
        hl7.heartRate = newVitalData.get(HEART_RATE).toString();
      }
      if (newVitalData.get(RESPIRATORY_RATE) != null) {
        hl7.respiratoryRate = newVitalData.get(RESPIRATORY_RATE).toString();
      }
      if (newVitalData.get(SP_O2) != null) {
        hl7.spo2 = newVitalData.get(SP_O2).toString();
      }
      if (newVitalData.get(BODY_TEMPERATURE) != null) {
        hl7.bodyTemperature = newVitalData.get(BODY_TEMPERATURE).toString();
      }
      if (newVitalData.get("patient") != null) {
        hl7.patient = newVitalData.get("patient").toString();

      }
      hl7.setDate(new Date());
      jsonResponse.setStatus(EDITED_VITAL_DATA);
      return Mono.just(ResponseEntity.ok(jsonResponse));
    }

  /**
   * Method: editInterval()
   * Description: edits the interval at which vital data is sent to kafka topic
   *
   * @return Mono<ResponseEntity < JsonResponse>>
   */
  @PostMapping("/editInterval")
  public Mono<ResponseEntity<JsonResponse>> editInterval(@RequestBody Map<String, Object> newInterval) {
    JsonResponse jsonResponse = new JsonResponse();
    if (executor == null || executor.isTerminated()) {
      jsonResponse.setStatus(SENDING_NOT_STARTED);
      return Mono.just(ResponseEntity.ok(jsonResponse));
    }
    int interval = Integer.parseInt(newInterval.get("interval").toString());
    setPeriod((interval <= 0) ? 1 : interval);
    executor.shutdown();
    executor = Executors.newScheduledThreadPool(1);
    executor.scheduleAtFixedRate(task, INITIAL_DELAY, getPeriod(), SECONDS);
    jsonResponse.setStatus(EDITED_INTERVAL);
    return Mono.just(ResponseEntity.ok(jsonResponse));
  }

  /**
   * Method: status()
   * Description: returns the status of the kafka producer
   *
   * @return Mono<ResponseEntity < JsonResponse>>
   */
  @GetMapping("/status")
  public Mono<ResponseEntity<JsonResponse>> status() {
    JsonResponse jsonResponse = new JsonResponse();
    if (executor == null || executor.isShutdown()) {
      jsonResponse.setStatus("0");
      return Mono.just(ResponseEntity.ok(jsonResponse));
    }
    jsonResponse.setStatus("1");
    return Mono.just(ResponseEntity.ok(jsonResponse));
  }

}
