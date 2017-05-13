#include <NewPing.h>
#include <Vcc.h>

//motor A connected between A01 and A02
//motor B connected between B01 and B02

int STBY = 10; //standby

//Motor A
int PWMA = 5; //Speed control 
int AIN1 = 3; //Direction
int AIN2 = 4; //Direction

//Motor B
int PWMB = 11; //Speed control
int BIN1 = 12; //Direction
int BIN2 = 13; //Direction

#define TRIGGER_PIN  9  // Arduino pin tied to trigger pin on the ultrasonic sensor.
#define ECHO_PIN     8  // Arduino pin tied to echo pin on the ultrasonic sensor.
#define MAX_DISTANCE 200 // Maximum distance we want to ping for (in centimeters). Maximum sensor distance is rated at 400-500cm.

int LEDF1 7 //front led 1
int LEDF2 6  //front led 2
//int LEDB1 6  //back led 1
//int LEDB2 4  //back led 2

char command;
char sending;
String string;
boolean ledon = false;

const float VccMin   = 0.0;           // Minimum expected Vcc level, in Volts.
const float VccMax   = 5.0;           // Maximum expected Vcc level, in Volts.
const float VccCorrection = 1.0/1.0;  // Measured Vcc by multimeter divided by reported Vcc

NewPing sonar(TRIGGER_PIN, ECHO_PIN, MAX_DISTANCE); // NewPing setup of pins and maximum distance.
Vcc vcc(VccCorrection);

void setup() {

  Serial.begin(9600);
  
  pinMode(LEDF1,OUTPUT); //led front 1
  pinMode(LEDF2,OUTPUT);  //led front 2
//  pinMode(LEDB1,OUTPUT);  //led back 1
//  pinMode(LEDB2,OUTPUT);  //led back 2
  
  pinMode(STBY, OUTPUT);

  pinMode(PWMA, OUTPUT);
  pinMode(AIN1, OUTPUT);
  pinMode(AIN2, OUTPUT);

  pinMode(PWMB, OUTPUT);
  pinMode(BIN1, OUTPUT);
  pinMode(BIN2, OUTPUT);
}

void loop() {

delay(50);                      // Wait 50ms between pings (about 20 pings/sec). 29ms should be the shortest delay between pings.
  unsigned int uS = sonar.ping(); // Send ping, get ping time in microseconds (uS).
  Serial.print("Ping: ");
  Serial.print(sonar.convert_cm(uS)); // Convert ping time to distance and print result (0 = outside set distance range, no ping echo)
  Serial.println("cm");

  sendDistanceValue();
  delay(100);
  sendBatteryPercentage();

  if (Serial.available() > 0) {
    string = "";
    }
    
    while(Serial.available() > 0) {
      command = ((byte)Serial.read());
    if(command == ':') {
      break;
    } else {
      string += command;
    }
    delay(1);
  }

   if(string == "F") {
    motorForward(255);
  }
  if(string == "B") {
    motorBack(255);
  }
  if(string == "L") {
    motorLeft(255);
  }
  if(string == "R") {
    motorRight(255);
  }
  if(string == "O") {
    longLedOn();
    ledon = true;
  } 
  if (string == "o") {
    shortLedOn();
    ledon = true;  
  }
  if(string == "s") {
    ledOff();
    ledon = false;
//    Serial.println(string); //debug
  }  
   stop();


   float percentage = vcc.Read_Perc(VccMin, VccMax);

   String str = "p" + String(percentage) + "";
   int buff_size = str.length();
   char buff[buff_size + 1];
   str.toCharArray(buff, buff_size + 1);

   Serial.print("VCC = ");
   Serial.print(percentage);
   Serial.println(" %");
   delay(1000);
   
   if (Serial.available()) {
    while (Serial.available())    
     Serial.write(p); 
   }
}

void sendBatteryPercentage() {
   //puts # before the values so our app knows what to do with the data
  Serial.print('#');
  float percentage = vcc.Read_Perc(VccMin, VccMax);//gets the percentage value of the battery
  Serial.print(percentage);
  Serial.print('~'); //used as an end of transmission character - used in app for string length
  Serial.println();
  delay(500);        //added a delay to eliminate missed transmissions
}

void sendDistanceValue() {
  //puts / before the values so our app knows what to do with the data
  Serial.print('/');
  delay(50);                      // Wait 50ms between pings (about 20 pings/sec). 29ms should be the shortest delay between pings.
  unsigned int uS = sonar.ping(); // Send ping, get ping time in microseconds (uS).
  Serial.print(sonar.convert_cm(uS)); // Convert ping time to distance and print result (0 = outside set distance range, no ping echo)
  Serial.print('~'); //used as an end of transmission character - used in app for string length
  Serial.println();
  delay(500);
}

void longLedOn() {
  //if value from bluetooth serial is 'O' turn leds ON
  digitalWrite(LEDF1,HIGH);            //switch on LED
  digitalWrite(LEDF2,HIGH);            //switch on LED    
//  digitalWrite(LEDB1,HIGH);            //switch on LED
//  digitalWrite(LEDB2,HIGH);            //switch on LED
  //delay(10);
}

void shortLedOn() {
  //if value from bluetooth serial is 'o' turn leds ON
  digitalWrite(LEDF1,80);            //switch on LED
  digitalWrite(LEDF2,80);            //switch on LED    
//  digitalWrite(LEDB1,80);            //switch on LED
//  digitalWrite(LEDB2,80);            //switch on LED
  //delay(10);
}

void ledOff() {
  //if value from bluetooth serial is 's' turn leds OFF
  digitalWrite(LEDF1,LOW);            //turn off LED
  digitalWrite(LEDF2,LOW);            //turn off LED    
//  digitalWrite(LEDB1,LOW);            //turn off LED
//  digitalWrite(LEDB2,LOW);            //turn off LED
  //delay(10);
}

void motorForward(int speed) {
   digitalWrite(STBY, HIGH); //disable standby
   digitalWrite(AIN1, HIGH);
   digitalWrite(AIN2, LOW);
   analogWrite(PWMA, speed);
}

void motorBack(int speed) {
   digitalWrite(STBY, HIGH); //disable standby
   digitalWrite(AIN1, LOW);
   digitalWrite(AIN2, HIGH);
   analogWrite(PWMA, speed);
}

void motorLeft(int speed) {
   //direction: 0 clockwise, 1 counter-clockwise
   digitalWrite(STBY, HIGH); //disable standby
   digitalWrite(BIN1, HIGH);
   digitalWrite(BIN2, LOW);
   analogWrite(PWMB, speed);
}

void motorRight(int speed) {
   //direction: 0 clockwise, 1 counter-clockwise
   digitalWrite(STBY, HIGH); //disable standby
   digitalWrite(BIN1, LOW);
   digitalWrite(BIN2, HIGH);
   analogWrite(PWMB, speed);
}

void stop(){
//enable standby  
  digitalWrite(STBY, LOW); 
}


