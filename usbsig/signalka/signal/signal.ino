


const int startPin = 3;
const int transPin = 5;
const int keyPin = A2;
const int doorPin = 2;
const int imdoorPin = A4;
const int opendoor = A5;
char cmd;

void startbutton() {
  // digitalWrite(doorPin, LOW);
  pinMode(doorPin, OUTPUT);
  //delay(500);
  analogWrite(keyPin, 0);
  delay(500);
  digitalWrite(transPin, LOW);
  delay(1000);
  digitalWrite(startPin, LOW);
  delay(600);
  digitalWrite(startPin, HIGH);
  delay(500);
  //digitalWrite(doorPin, LOW);
 // delay(600);
  //digitalWrite(doorPin, HIGH);
  digitalWrite(transPin, HIGH);
  delay (1500);
  //digitalWrite(keyPin, HIGH);
  analogWrite(keyPin, 255);
  delay(500);
  //pinMode(doorPin, INPUT);
}
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  Serial.println("Command: ");
  pinMode(startPin, OUTPUT);
  pinMode(transPin, OUTPUT);
  digitalWrite(startPin, HIGH);
  digitalWrite(transPin, HIGH);
  analogWrite(opendoor, 255);
  analogWrite(keyPin, 255);
  digitalWrite(doorPin, HIGH);
  pinMode(doorPin, OUTPUT);
  //analogWrite(imdoorPin, OUTPUT);
}

void loop() {
  // put your main code here, to run repeatedly:

  if (!Serial.available()) return;
  cmd = Serial.read();
  if (cmd == 's') {
    Serial.print("COM ok\n");
    startbutton();
    return;
  } else if (cmd == 't') {

    Serial.print("Temp ");
    //Serial.print(tempf);
    return;
  } else if (cmd == 'o') {
    Serial.print("Open door");
    analogWrite(opendoor, 0);
    delay (200);
    analogWrite(opendoor, 255);
  }
}
