/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.datagrid.hotrod.query.domain;

import org.infinispan.protostream.annotations.ProtoField;

/**
 * @author Adrian Nistor
 */
public class Forecast {

   private int id;

   private String location;

   private int year;

   private int month;

   private int day;

   /**
    * Rain in cm.
    */
   private float rain;

   /**
    * Snow in mm.
    */
   private float snowfall;

   /**
    * Temperature in Celsius degrees.
    */
   private float temperature;

   /**
    * Relative humidity.
    */
   private float humidity;

   @ProtoField(number = 1, required = true)
   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   @ProtoField(number = 2, required = true)
   public String getLocation() {
      return location;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   @ProtoField(number = 3, required = true)
   public int getYear() {
      return year;
   }

   public void setYear(int year) {
      this.year = year;
   }

   @ProtoField(number = 4, required = true)
   public int getMonth() {
      return month;
   }

   public void setMonth(int month) {
      this.month = month;
   }

   @ProtoField(number = 5, required = true)
   public int getDay() {
      return day;
   }

   public void setDay(int day) {
      this.day = day;
   }

   @ProtoField(number = 6, defaultValue = "0")
   public float getRain() {
      return rain;
   }

   public void setRain(float rain) {
      this.rain = rain;
   }

   @ProtoField(number = 7, defaultValue = "0")
   public float getSnowfall() {
      return snowfall;
   }

   public void setSnowfall(float snowfall) {
      this.snowfall = snowfall;
   }

   @ProtoField(number = 8, required = true)
   public float getTemperature() {
      return temperature;
   }

   public void setTemperature(float temperature) {
      this.temperature = temperature;
   }

   @ProtoField(number = 9, required = true)
   public float getHumidity() {
      return humidity;
   }

   public void setHumidity(float humidity) {
      this.humidity = humidity;
   }

   @Override
   public String toString() {
      return "Forecast{" +
            "id=" + id +
            ", location='" + location + '\'' +
            ", year=" + year +
            ", month=" + month +
            ", day=" + day +
            ", rain=" + rain +
            ", snowfall=" + snowfall +
            ", temperature=" + temperature +
            ", humidity=" + humidity +
            '}';
   }
}
