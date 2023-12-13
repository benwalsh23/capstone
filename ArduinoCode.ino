/***************************************************
* Made by: Mark D'Cruz
* Used for Capstone Project
*
*
****************************************************/

#include "Air_Quality_Sensor.h"
#include "Firebase_Arduino_WiFiNINA.h"
#include <WiFiNINA.h>
#include <SPI.h>

AirQualitySensor sensor(A0);
int current_quality =-1;
int COUNT = 1;
int DUST_MAX;
int AIR_MAX;

//pins used:
const int analogInPin = 0;  // Analog input pin that the sensor is attached to
int sensorValue = 0; // value read from the dust sensor

//Access to Firebase
#define DATABASE_URL "*****************"
#define DATABASE_SECRET "********************"

//WiFi to be used
#define WIFI_SSID "**********"
#define WIFI_PASSWORD "*************"

#define SCHOOL "***************"
#define USER "***************"
#define PWRD "****************"

//Pin for the relay
const int relayPin = 4;

//Define Firebase data object
FirebaseData fbdo;

void setup() {
  delay(100);

  pinMode(relayPin, OUTPUT);

  String fv = WiFi.firmwareVersion();

  int status = WL_IDLE_STATUS;
  while (status != WL_CONNECTED)
  {
    //Change depending on WiFi
    //status = WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
     status = WiFi.beginEnterprise(SCHOOL, USER, PWRD);
    delay(100);
  }

  //Connect to Firebase
  Firebase.begin(DATABASE_URL, DATABASE_SECRET, SCHOOL, WIFI_PASSWORD);
  Firebase.reconnectWiFi(true);


  

  // initialize serial communications at 9600 bps:
  while (!Serial);

  delay(5000);


}


void checkLimits(){
  //Get the max settings from firebase
  //Replace * with path. Ex: /Path1/Key Name
  if (Firebase.getInt(fbdo, "**********"))
  {
    if (fbdo.dataType() == "int"){
      DUST_MAX = fbdo.intData();
    }
  }
  if (Firebase.getInt(fbdo, "********")))
  {
    if (fbdo.dataType() == "int"){
      AIR_MAX = fbdo.intData();
    }
  }
}

void fanOn(){
  //Turns the fan on
  //Sets value in firebase. Ex: Path/Key
  Firebase.setInt(fbdo, "********", 1);
}

void fanOff(){
  Firebase.setInt(fbdo, "*********", 0);
}


void loop() {
  checkLimits();
  // read the analog in value:
  sensorValue = analogRead(analogInPin);

  //good = 0 - 50
  //moderate = 51 - 100
  //unhealthy for sensitive groups = 101 - 150
  //Unhealthy = 151 - 200
  //Very unhealthy = 201 - 300
  //Dangerous = > 300

  int quality = sensor.slope();
  int air_value = 0;

  //Get the value form the air quality sensors
  if (quality == AirQualitySensor::FORCE_SIGNAL) {
    air_value = 4;
    } else if (quality == AirQualitySensor::HIGH_POLLUTION) {
      air_value = 3;
    } else if (quality == AirQualitySensor::LOW_POLLUTION) {
      air_value = 2;
    } else if (quality == AirQualitySensor::FRESH_AIR) {
      air_value = 1;
    }
  
  //Push the values to firbase
  COUNT = millis()/1000;
  if (Firebase.pushInt(fbdo, "******" + String(COUNT), sensorValue))
  {
    COUNT = COUNT + 1;
  }
  if (Firebase.pushInt(fbdo, "******" + String(COUNT), air_value))
  {
    COUNT = COUNT + 1;
  }
  
  int fanAuto;
  int fanControl;
  //Check if the fan is auto or being controlled by the user
  if (Firebase.getInt(fbdo, "******"))
  {
    if (fbdo.dataType() == "int"){
      fanControl = fbdo.intData();
    }
  }

  if (Firebase.getInt(fbdo, "******"))
  {
    if (fbdo.dataType() == "int"){
      fanAuto = fbdo.intData();
    }
  }

  //If the fan is not being controlled
  //Compare the values with the max settings
  if(fanAuto == 1){
    if(sensorValue <= DUST_MAX && air_value <= AIR_MAX){
      fanOff();
      digitalWrite(relayPin, LOW);//OFF
    } 
    else{
      digitalWrite(relayPin, HIGH);//ON
      fanOn();
    }
  }
  else {
    if(fanControl == 0){
      digitalWrite(relayPin, LOW);
    }
    else{
      digitalWrite(relayPin, HIGH);
    }
  }
  delay(1000);
}
