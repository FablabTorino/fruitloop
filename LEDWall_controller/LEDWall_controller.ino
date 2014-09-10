int lED_pins[16] = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, A0, A1, A2, A3 };
const char separator = ',';
unsigned int ledWall = 0;
int c = 0;

void setup() {

  Serial.begin(9600);

}

void loop() {
  if (Serial.available() > 0)
  {
    byte b = Serial.read();

    if (b != separator)
    {
      if (c == 0) {
        ledWall = b << 8;
      }
      else
        ledWall = ledWall | b;
      c++;
    }
    else {
      Serial.println(ledWall, BIN);
      for (int i = 0; i < 16; i++)
        digitalWrite(LED_pins[i], bitRead(ledWall, i));
      c = 0;
      ledWall = 0;
    }
  }
}
