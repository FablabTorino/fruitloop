//  https://www.sparkfun.com/datasheets/Components/General/MSGEQ7.pdf
//The seven band graphic equalizer IC is a CMOS chip that divides
//the audio spectrum into seven bands. 
//63Hz, 160Hz, 400Hz, 1kHz, 2.5kHz, 6.25kHz and 16kHz.


// we are testing this on an Arduino MEGA 2560

const int msg7RESET = 34;  //5; // yellow cable for fablabTO!
const int msg7Strobe = 36; ////4; // white cable for fablabTO!
const int msg7DCout = A7; // A0;  // green cable for FablabTO!

const int LEDpins[16] = {2,3,4,5,6,7,8,9,10,11,12,13,54,55,56,57};    // there are 5 LEDs and 7 freq bands.  So, repeat LEDs

//#define pushButton 2

void setup() {
  Serial.begin(115200);
   //initialize the digital pin as an output.
   //Pin 13 has an LED connected on most Arduino boards:
  for (int x=0; x<15; x++) {
      pinMode(LEDpins[x], OUTPUT);
  }
  
  pinMode(msg7RESET, OUTPUT);
  pinMode(msg7Strobe, OUTPUT);

  //pinMode(pushButton, INPUT);       // never actually used in this example.
  //digitalWrite(pushButton, HIGH);  // Enable internal pull-up
}

void loop() {
  digitalWrite(msg7RESET, HIGH);          // reset the MSGEQ7's counter
  delay(5);
  digitalWrite(msg7RESET, LOW);

  for (int x = 0; x < 7; x++) {
    digitalWrite(msg7Strobe, LOW);      // output each DC value for each freq band
    delayMicroseconds(35); // to allow the output to settle
    int spectrumRead = analogRead(msg7DCout);
    Serial.print(x);
    Serial.print(" ");
    Serial.print(spectrumRead);
    Serial.print(" ");

    digitalWrite(msg7Strobe, HIGH);
    //delay(10);
  }
  Serial.println();
  delay(100);
}
