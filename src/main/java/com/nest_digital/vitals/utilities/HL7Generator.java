/*
 * Copyright (c) NeST Digital Pvt Ltd, 2023.
 * All Rights Reserved. Confidential.
 */
package com.nest_digital.vitals.utilities;

import ca.uhn.fhir.context.FhirContext;
import com.nest_digital.vitals.dto.VitalData;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

/**
 * HL7Generator is a utility class that generates HL7 FHIR observation resources in JSON format
 * for various vital signs (heart rate, temperature, oxygen saturation, respiratory rate, and blood pressure)
 */
@Component
public class HL7Generator extends Prop {
    private static final String UNIT_BEATS_PER_MIN = "beats/min";
    private static final String UNIT_C = "C";
    private static final String UNIT_PERCENTAGE = "%";
    private static final String UNIT_BREATHS_PER_MIN = "breaths/min";
    FhirContext ctx = FhirContext.forR4();

    // This function creates an instance of an Observation object and sets its properties
    private Observation generatingObservation(String id, Observation.ObservationStatus status, CodeableConcept code, String subject, Date date, Quantity value) {
        Observation observation = new Observation();
        observation.setId(id);
        observation.setStatus(status);
        observation.setCode(code);
        observation.setSubject(new Reference(subject));
        observation.setEffective(new DateTimeType().setValue(date));
        observation.setValue(value);
        return observation;
    }

    /**
     * This function creates an HL7 message in JSON format for a heart rate observation.
     * return the HL7 message in JSON format
     */

    public String heartRateHL7() {

        int heartbeat = Integer.parseInt(heartRate);
        CodeableConcept heartRateCode = new CodeableConcept().addCoding(new Coding().setSystem(LOINC_SYSTEM).setCode("8867-4").setDisplay("Heart rate"));
        Quantity heartRateQuantity = new Quantity().setValue(BigDecimal.valueOf(heartbeat)).setUnit(UNIT_BEATS_PER_MIN);
        Observation heartRateObservation = generatingObservation("heart-rate", Observation.ObservationStatus.FINAL, heartRateCode, patient, getDate(), heartRateQuantity);
        return ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(heartRateObservation);
    }

    /**
     * This function creates an HL7 message in JSON format for a body temperature observation.
     * return the HL7 message in JSON format
     */

    public String temperatureHL7() {
        double temperature = Double.parseDouble(bodyTemperature);
        CodeableConcept temperatureCode = new CodeableConcept().addCoding(new Coding().setSystem(LOINC_SYSTEM).setCode("8310-5").setDisplay("Body temperature"));
        Quantity temperatureQuantity = new Quantity().setValue(temperature).setUnit(UNIT_C);
        Observation temperatureObservation = generatingObservation("temperature", Observation.ObservationStatus.FINAL, temperatureCode, patient, getDate(), temperatureQuantity);
        return ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(temperatureObservation);
    }

    /**
     * This function creates an HL7 message in JSON format for a spo2 observation.
     * return the HL7 message in JSON format
     */

    public String spo2HL7() {
        double spo2rate = Double.parseDouble(spo2);
        CodeableConcept spo2Code = new CodeableConcept().addCoding(new Coding().setSystem(LOINC_SYSTEM).setCode("2710-2").setDisplay("Oxygen saturation in Arterial blood"));
        Quantity spo2Quantity = new Quantity().setValue(spo2rate).setUnit(UNIT_PERCENTAGE);
        Observation spo2Observation = generatingObservation("spo2", Observation.ObservationStatus.FINAL, spo2Code, patient, getDate(), spo2Quantity);
        return ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(spo2Observation);
    }

    /**
     * This function creates an HL7 message in JSON format for a respiratory rate observation.
     *
     * @return the HL7 message in JSON format
     */
    public String respirationRateHL7() {
        int respirationrate = Integer.parseInt(respiratoryRate);
        CodeableConcept respirationRateCode = new CodeableConcept().addCoding(new Coding().setSystem(LOINC_SYSTEM).setCode("9279-1").setDisplay("Respiratory rate"));
        Quantity respirationRateQuantity = new Quantity().setValue(BigDecimal.valueOf(respirationrate)).setUnit(UNIT_BREATHS_PER_MIN);
        Observation respirationRateObservation = generatingObservation("respiration-rate", Observation.ObservationStatus.FINAL, respirationRateCode, patient, getDate(), respirationRateQuantity);
        return ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(respirationRateObservation);
    }

    /**
     * This function creates an HL7 message in JSON format for a blood pressure observation.
     * return the HL7 message in JSON format
     */
    public String bloodPressureHl7() {
        int systolic = Integer.parseInt(systolicPressure);
        int diastolic = Integer.parseInt(diastolicPressure);
        Observation bloodPressureObservation = new Observation();
        bloodPressureObservation.setId("blood-pressure");
        bloodPressureObservation.setStatus(Observation.ObservationStatus.FINAL);
        bloodPressureObservation.setCode(new CodeableConcept().addCoding(new Coding().setSystem(LOINC_SYSTEM).setCode("55284-4").setDisplay("Blood pressure")));
        bloodPressureObservation.setSubject(new Reference(patient));
        bloodPressureObservation.setEffective(new DateTimeType().setValue(getDate()));
        bloodPressureObservation.addComponent()
                .setCode(new CodeableConcept().addCoding(new Coding().setSystem(LOINC_SYSTEM).setCode("8480-6").setDisplay("Systolic blood pressure")))
                .setValue(new Quantity().setValue(systolic).setUnit("mm[Hg]"));
        bloodPressureObservation.addComponent()
                .setCode(new CodeableConcept().addCoding(new Coding().setSystem(LOINC_SYSTEM).setCode("8462-4").setDisplay("Diastolic blood pressure")))
                .setValue(new Quantity().setValue(diastolic).setUnit("mm[Hg]"));
        return ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(bloodPressureObservation);


    }

    /**
     * This function creates a message in JSON format for a serialId.
     * returns the serialId
     */
    public String serialId(){

        return serialId;
    }

    /**
     * This function creates a message in JSON format for a locationId.
     * returns the locationId
     */
    public String locationId(){

        return locationId;
    }

    /**
     * This function creates a VitalData object containing HL7 messages in JSON format for various vital signs.
     *
     * @return the VitalData object
     */
    public VitalData generateVitalData() {
        return new VitalData(serialId(),locationId(),spo2HL7(), temperatureHL7(), bloodPressureHl7(), heartRateHL7(), respirationRateHL7());
    }

}

