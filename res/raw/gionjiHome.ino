
int incomingByte = 0;   // for incoming serial data
int STARTING_SWITCH_PIN = 49;
int RED_PIN = 2;
int GREEN_PIN = 3;
int BLUE_PIN = 4;

int RELAYS_PINS[] = {49, 53, 51};

boolean relays[] = {false, false, false}; 

void setup() {

  for(int i=0; i<54; i++){  
    pinMode(i, INPUT);
  }
  
  pinMode(RED_PIN, OUTPUT);
  pinMode(GREEN_PIN, OUTPUT);
  pinMode(BLUE_PIN, OUTPUT);
  
  attachInterrupt(31, click_sw1, FALLING);
  attachInterrupt(33, click_sw2, FALLING);
  attachInterrupt(35, click_sw3, FALLING);
  
  pinMode(49, OUTPUT);
  pinMode(51, OUTPUT);
  pinMode(53, OUTPUT);
  
  Serial.begin(9600);
  
}

void loop() {
  // put your main code here, to run repeatedly:
  if (Serial.available() > 0) {
      incomingByte = Serial.read();

      // say what you got:
      Serial.print("I received: ");
      Serial.println(incomingByte, DEC);
      
      processPacket(incomingByte);
      
   }
   
   for(int i=0; i<3; i++){
     if(relays[i])
       digitalWrite(RELAYS_PINS[i], HIGH);
     else
       digitalWrite(RELAYS_PINS[i], LOW);
   }
   
   
   delay(20);
}


int processPacket(int data){
  
  if(((data >> 6) & 0x03) == 0x01){
    // switch
    for(int i=0; i<3; i++){
      getSwitchState(data, i);
    }
  }
  else if(((data >> 6) & 0x03) == 0x00){
    getRgb(data, 255);
  }
  
  
  return 0;
}


int getSwitchState(int data, int id){
  int state = (data >> id) & 0x0001;
  
  if(state == 0){
    relays[id] = false;
  }
  else{
    relays[id] = true;
  }
  
  return 0; 
}


int getRgb(int data, int common_anode){
  analogWrite(RED_PIN,   255 - (((data >> 4) & 0x03) * 85));
  analogWrite(GREEN_PIN, 255 - (((data >> 2) & 0x03) * 85));
  analogWrite(BLUE_PIN,  255 - ((data & 0x03) * 85));
}


void click_sw1(){  
    relays[0] = !relays[0];
}

void click_sw2(){
    relays[1] = !relays[1];
}

void click_sw3(){
    relays[2] = !relays[2];
}