void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  
  
    Serial.println("canneeeeee");
  
  pinMode(9,  OUTPUT);
  pinMode(10, OUTPUT);
  pinMode(11, OUTPUT);
  pinMode(12, OUTPUT);
  
  pinMode(13, OUTPUT);
  
  digitalWrite(12, HIGH);
}

int alpha=0, r=0, g=0, b=0;
boolean led = false;

void loop() {
  // put your main code here, to run repeatedly: 
  if(Serial.available() > 4){
    led = !led;
    digitalWrite(13, led);
    
    
    alpha = (int)Serial.read();
    r = (int)Serial.read();
    g = (int)Serial.read();
    b = (int)Serial.read();  
       
    analogWrite(9,  255 - r);
    analogWrite(10, 255 - g);
    analogWrite(11, 255 - b);
  
  }

  delay(10);
  
}
