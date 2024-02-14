/*
 * Copyright (c) NeST Digital Pvt Ltd, 2023.
 * All Rights Reserved. Confidential.
 */
package com.nest_digital.vitals.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/**
 * VitalsData.java
 * @author Aston-Martin
 * @since 25-01-2023
 * A class that represents the vital sign data of a patient.
 */
public class VitalData {
  private String serialId;
  private String locationId;
  private String spo2;
  private String bodyTemperature;
  private String bloodPressure;
  private String heartRate;
  private String respiratoryRate;

}
