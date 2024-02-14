/*
 * Copyright (c) NeST Digital Pvt Ltd, 2023.
 * All Rights Reserved. Confidential.
 */
package com.nest_digital.vitals.utilities;

import com.nest_digital.vitals.dto.VitalData;
import com.nest_digital.vitals.kafkaproducer.VitalDataProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class Prop {
  @Value("${heartratevalue}")
  public String heartRate;
  @Value("${respirationratevalue}")
  public String respiratoryRate;
  @Value("${oxygensaturationvalue}")
  public String spo2;
  @Value("${systolicvalue}")
  public String systolicPressure;
  @Value("${distolicvalue}")
  public String diastolicPressure;
  @Value("${Temperaturevalue}")
  public String bodyTemperature;
  @Value("${patientinfo}")
  public String patient;
  @Value("${bloodpressurevalue}")
  public String bloodPressure;
  @Value("${serialid}")
  public String serialId;
  @Value("${locationid}")
  public String locationId;
  @Value("${regularexpression}")
  public String regex;
  private int period = 1;
  public static final int INITIAL_DELAY = 0;
  public static final TimeUnit SECONDS = TimeUnit.SECONDS;
  public static final String SP_O2 = "spo2";
  public static final String BODY_TEMPERATURE = "bodyTemperature";
  public static final String BLOOD_PRESSURE = "bloodPressure";
  public static final String SYSTOLIC_PRESSURE = "systolicPressure";
  public static final String DIASTOLIC_PRESSURE = "diastolicPressure";
  public static final String HEART_RATE = "heartRate";
  public static final String RESPIRATORY_RATE = "respiratoryRate";
  public static final String SERIAL_ID = "serialId";
  public static final String LOCATION_ID = "locationId";
  public static final String ALREADY_RUNNING = "ALREADY RUNNING";
  public static final String STARTED_SENDING_VITAL = "Started sending vital data to Kafka";
  public static final String LOINC_SYSTEM = "http://loinc.org";
  public static final String STOPPED_SENDING_VITAL = "Stopped sending vital data to Kafka";
  public static final String SENDING_NOT_STARTED = "Vital data sending was not started";
  public static final String EDITED_VITAL_DATA = "New vital data has been added";
  public static final String EDITED_INTERVAL = "Interval has been edited";
  public static final String INVALID_SERIALID="Invalid Serial ID";
  public static final String EDITED_SERIALID_LOCATIONID_DATA="New SerialId and LocationId has been added";
  LocalDate localDate = LocalDate.of(2021, 10, 1);
  LocalDateTime localDateTime = localDate.atStartOfDay();
  Instant instant = localDateTime.toInstant(ZoneOffset.UTC);

  long timestamp = instant.toEpochMilli();
  private Date date = new Date(timestamp);
  private VitalData liveVital = new VitalData();

  public boolean serialIdValidation(String serialId){
    return serialId.matches(regex);
  }
  public void setVitalDataService(VitalDataProducer vitalDataService) {
    // intentionally empty
  }

  public void setHl7Generator(HL7Generator hl7Generator) {
    // Implementation pending
  }

  public int getPeriod() {
    return period;
  }

  public void setPeriod(int period) {
    this.period = period;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public VitalData getLiveVital() {
    return liveVital;
  }

  public void setLiveVital(VitalData liveVital) {
    this.liveVital = liveVital;
  }
}
