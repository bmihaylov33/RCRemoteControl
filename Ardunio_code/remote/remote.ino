  #include <NewPing.h>
#include <Vcc.h>

//motor A connected between A01 and A02
//motor B connected between B01 and B02

#define PWMA 3
#define AIN1 7
#define AIN2 8
#define PWMB 5
#define BIN1 2
#define BIN2 4
#define STBY 6

#define TRIGGER_PIN  10  // Arduino pin tied to trigger pin on the ultrasonic sensor.
#define ECHO_PIN     9  // Arduino pin tied to echo pin on the ultrasonic sensor.
#define MAX_DISTANCE 250 // Maximum distance we want to ping for (in centimeters). Maximum sensor distance is rated at 400-500cm.

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
  
  pinMode(13,OUTPUT); //led front 1
  pinMode(12,OUTPUT);  //led front 2
  
  pinMode(STBY, OUTPUT);

  pinMode(PWMA, OUTPUT);
  pinMode(AIN1, OUTPUT);
  pinMode(AIN2, OUTPUT);

  pinMode(PWMB, OUTPUT);
  pinMode(BIN1, OUTPUT);
  pinMode(BIN2, OUTPUT);
}

void loop() {

  startUp();

//  delay(50);                      // Wait 50ms between pings (about 20 pings/sec). 29ms should be the shortest delay between pings.
//  unsigned int uS = sonar.ping(); // Send ping, get ping time in microseconds (uS).
//  Serial.print("Ping: ");
//  Serial.print(sonar.convert_cm(uS)); // Convert ping time to distance and print result (0 = outside set distance range, no ping echo)
//  Serial.println("cm");

  sendDistanceValue();
  delay(100);
  //sendBatteryPercentage();

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
   motorForward();
   delay(1000);
  }
  if(string == "B") {
    motorBack();
    delay(1000);
  }
  if(string == "L") {
    motorLeft();
    delay(1000);
  }
  if(string == "R") {
    motorRight();
    delay(1000);
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
  applyBrakes();
  delay(500);
}

//void sendBatteryPercentage() {
//   //puts # before the values so our app knows what to do with the data
//  Serial.print('#');
//  float percentage = vcc.Read_Perc(VccMin, VccMax);//gets the percentage value of the battery
//  Serial.print(percentage);
//  Serial.print('~'); //used as an end of transmission character - used in app for string length
//  Serial.println();
//  delay(500);        //added a delay to eliminate missed transmissions
//}

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
  digitalWrite(13,HIGH);            //switch on LED
  digitalWrite(12,HIGH);            //switch on LED    
//  digitalWrite(LEDB1,HIGH);            //switch on LED
//  digitalWrite(LEDB2,HIGH);            //switch on LED
  //delay(10);
}

void shortLedOn() {
  //if value from bluetooth serial is 'o' turn leds ON
  digitalWrite(13,40);            //switch on LED
  digitalWrite(12,40);            //switch on LED    
//  digitalWrite(LEDB1,80);            //switch on LED
//  digitalWrite(LEDB2,80);            //switch on LED
  //delay(10);
}

void ledOff() {
  //if value from bluetooth serial is 's' turn leds OFF
  digitalWrite(13,LOW);            //turn off LED
  digitalWrite(12,LOW);            //turn off LED    
//  digitalWrite(LEDB1,LOW);            //turn off LED
//  digitalWrite(LEDB2,LOW);            //turn off LED
  //delay(10);
}

void motorForward() {
  digitalWrite (AIN1,HIGH);
  digitalWrite (AIN2,LOW);
  analogWrite(PWMA,234);
  digitalWrite (BIN1,HIGH);
  digitalWrite (BIN2,LOW);
  analogWrite(PWMB,255);    
}

void motorBack() {
  digitalWrite (AIN1,LOW);
  digitalWrite (AIN2,HIGH);
  analogWrite(PWMA,233);
  digitalWrite (BIN1,LOW);
  digitalWrite (BIN2,HIGH);
  analogWrite(PWMB,255);  
}

void motorLeft() {
  digitalWrite (AIN1,HIGH);
  digitalWrite (AIN2,LOW);
  analogWrite(PWMA,190);
  digitalWrite (BIN1,HIGH);
  digitalWrite (BIN2,LOW);
  analogWrite(PWMB,255); 
}

void motorRight() {
  digitalWrite (AIN1,HIGH);
  digitalWrite (AIN2,LOW);
  analogWrite(PWMA,255);
  digitalWrite (BIN1,HIGH);
  digitalWrite (BIN2,LOW);
  analogWrite(PWMB,190);  
}

void veerLeft() {
  digitalWrite (AIN1,HIGH);
  digitalWrite (AIN2,LOW);
  analogWrite(PWMA,190);
  digitalWrite (BIN1,HIGH);
  digitalWrite (BIN2,LOW);
  analogWrite(PWMB,255);  
}

void veerRight() {
  digitalWrite (AIN1,HIGH);
  digitalWrite (AIN2,LOW);
  analogWrite(PWMA,255);
  digitalWrite (BIN1,HIGH);
  digitalWrite (BIN2,LOW);
  analogWrite(PWMB,190);  
}

void applyBrakes() {
  digitalWrite (AIN1,HIGH);
  digitalWrite (AIN2,HIGH);
  analogWrite(PWMA,255);
  digitalWrite (BIN1,HIGH);
  digitalWrite (BIN2,HIGH);
  analogWrite(PWMB,255);  
}

void rotateLeft ()
{
  digitalWrite (AIN1,LOW);
  digitalWrite (AIN2,HIGH);
  analogWrite(PWMA,255);
  digitalWrite (BIN1,HIGH);
  digitalWrite (BIN2,LOW);
  analogWrite(PWMB,255);  
}

void startUp() {
  digitalWrite(STBY,HIGH);
}

void turnAround() {
  rotateLeft();
  delay(1370);
}

void shutDown() {
  digitalWrite(STBY,LOW);
}


