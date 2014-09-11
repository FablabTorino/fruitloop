import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 
import ddf.minim.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class fruitloop_processing extends PApplet {




//step sequencer 0.1 (no bpm yet, just timer)
//started from 2d array demo on processing.org

//modified from http://www.openprocessing.org/sketch/30818


//USAGE:
// activate samples with mouse or keyboard 1234qwerasdfzxcv
// start/stop loop with right-click


Minim minim;


AudioSample[][] brek;
AudioSample[][] hiphop;



Cell[][] grid;

int cols = 4;
int rows = 4;
int starttime;
int delaytime = 1000;
int curtime;  
float count_up;
float count_down;
int steptimer;
boolean play = false;
int cellSize=200;

//byte[] bytes = new byte[2];

byte uno=0;
byte due=0;


Serial myPort;  // Create object from Serial class

public void setup() {

  brek= new AudioSample[4][4];
  //load audio samples

  minim = new Minim(this);

  for (int i=0; i<4; i++) {
    println("break-beat-0"+(i+1)+".mp3");
    brek[i][0]=minim.loadSample("break-beat-0"+(i+1)+".mp3", 512);
    brek[i][1]=minim.loadSample("break-perc-0"+(i+1)+".mp3", 512);
    brek[i][2]=minim.loadSample("break-piano-0"+(i+1)+".mp3", 512);
    brek[i][3]=minim.loadSample("break-strings-0"+(i+1)+".mp3", 512);
  }



  size(cellSize*cols, cellSize*rows);

  //  bytes[0]=0;
  // bytes[1]=1;

  grid = new Cell[cols][rows];
  for (int i = 0; i < cols; i++) {
    for (int j = 0; j < rows; j++) {
      grid[i][j] = new Cell(i*cellSize, j*cellSize, cellSize, cellSize);
    }
  }
  println(Serial.list());

  String portName = Serial.list()[2];
  myPort = new Serial(this, portName, 9600);
}

public void draw() {
  background(0);

  for (int i = 0; i < cols; i++) {
    for (int j = 0; j < rows; j++) {

      if ((i & 1) == 0) {
        // even rows white
        grid[i][j].display(255);
      } else {
        // odd rows gray
        grid[i][j].display(220);
      }
    }
  }

  if (play == true) {
    int j;
    if (millis() - starttime < delaytime) {      
      count_up = (millis() - starttime);
      count_down = delaytime - count_up;
      steptimer = floor(4 / (delaytime / count_up));       
      fill(0);
      //        textSize(12);
      //        text(steptimer, mouseX, mouseY);
      for (j = 0; j < rows; j++) {
        grid[steptimer][j].display(120);  
        if (grid[steptimer][j].active) {       
          grid[steptimer][j].trigger(steptimer, j);
        }
      }

      switch (steptimer) {
      case 0:
        uno = PApplet.parseByte(unbinary("00010001"));
        due = PApplet.parseByte(unbinary("00010001"));
        break; 
      case 1:
        uno = PApplet.parseByte(unbinary("00100010"));
        due = PApplet.parseByte(unbinary("00100010"));
        break; 
      case 2:
        uno = PApplet.parseByte(unbinary("01000100"));
        due = PApplet.parseByte(unbinary("01000100"));
        break; 
      case 3:
        uno = PApplet.parseByte(unbinary("10001000"));
        due = PApplet.parseByte(unbinary("10001000"));
        break;
      }
      print(binary(uno));
      println(binary(due));

      myPort.write(due);
      myPort.write(uno);
      myPort.write(',');
    } else {
      starttime = millis();
      j = 0;
    }


    for (int i = 0; i < cols; i++) {
      for (int k = 0; k < rows; k++) {

        if (grid[i][k].active) {
          // even rows white

            if ((i+4*k)<8) {
            uno|=(1<<i+4*k);
          } else {
            due|=(1<<(i+4*k)-8);
          }
        } else {
          // odd rows gray

          if ((i+4*k)<8) {
            uno&=~(1<<i+4*k); //chiedete a vanzati f.vanzati@arduino.cc
          } else {
            due&=~(1<<(i+4*k)-8);
          }
        }
      }
    }
  }

  //print(binary(uno));
  //print(binary(due));


  myPort.write(due);
  myPort.write(uno);
  myPort.write(',');

  println();
}

public void mousePressed() {
  if (mouseButton == LEFT) {

    int xX = PApplet.parseInt(mouseX)/cellSize;
    int yY = PApplet.parseInt(mouseY)/cellSize;   
    //    fill(255);
    //    textSize(12);
    //    text( xX + " " + yY, mouseX, mouseY);
    grid[xX][yY].pressed();
  }
  if (mouseButton == RIGHT) {
    starttime = millis(); 
    if (play == true) { 
      play = false;
    } else { 
      play = true;
    }
  }
}

// A Cell object
class Cell {
  float x, y;
  float w, h;
  boolean active = false;

  // Cell Constructor
  Cell(float tempX, float tempY, float tempW, float tempH) {
    x = tempX;
    y = tempY;
    w = tempW;
    h = tempH;
  }

  public void display( int step ) {
    stroke(0);

    if (active == true) {
      fill(0);
    } else {
      fill(step);
    }

    rect(x, y, w, h);
    fill(255, 0, 0);
    //textSize(5);
    //text("X: " + x, x, y-9);
    //text("Y: " + y, x, y);
  }

  public void pressed() {
    if (active == true) { 
      active = false;
    } else { 
      active = true;
    }
  }

  public void trigger(int x, int y) {
    int y2;
    y2 = y+1;
    //    textSize(21);
    //    if (active == true) {
    //      //cell is triggered, do stuff here (play wav, whatever)
    //      //i just display text showing the triggered cell[][] info for testing
    //      text(x + " " + y, 210, y2*20);
    //    }

    brek[x][y].trigger();
  }
}

public void keyPressed() {
  int keyIndex = -1;
  //  switch(key){
  //
  //    char letter = 'N';

  switch(key) {
  case '1':
    println("00");  // Does not execute
    grid[0][0].active=!grid[0][0].active;

    break;
  case '2': 
    println("10");  // Does not execute
    grid[1][0].active=!grid[1][0].active;

    break; 
  case '3': 
    println("20");  // Does not execute
    grid[2][0].active=!grid[2][0].active;

    break;
  case '4': 
    println("30");  // Does not execute
    grid[3][0].active=!grid[3][0].active;

    break;
  case 'q': 
    println("01");  // Does not execute
    grid[0][1].active=!grid[0][1].active;

    break;
  case 'w': 
    println("11");  // Does not execute  
    grid[1][1].active=!grid[1][1].active;

    break;
  case 'e': 
    println("21");  // Does not execute
    grid[2][1].active=!grid[2][1].active;

    break;
  case 'r': 
    println("31");  // Does not execute
    grid[3][1].active=!grid[3][1].active;

    break;
  case 'a': 
    println("02");  // Does not execute
    grid[0][2].active=!grid[0][2].active;

    break;
  case 's': 
    println("12");  // Does not execute
    grid[1][2].active=!grid[1][2].active;

    break;
  case 'd': 
    println("22");  // Does not execute
    grid[2][2].active=!grid[2][2].active;

    break;
  case 'f': 
    println("32");  // Does not execute
    grid[3][2].active=!grid[3][2].active;

    break;
  case 'z': 
    println("03");  // Does not execute
    grid[0][3].active=!grid[0][3].active;

    break;
  case 'x': 
    println("13");  // Does not execute
    grid[1][3].active=!grid[1][3].active;

    break;
  case 'c': 
    println("23");  // Does not execute
    grid[2][3].active=!grid[2][3].active;

    break;
  case 'v': 
    println("33");  // Does not execute
    grid[3][3].active=!grid[3][3].active;

    break;


  default:             // Default executes if the case labels
    println("");   // don't match the switch parameter
    break;
  }
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--full-screen", "--bgcolor=#666666", "--stop-color=#cccccc", "fruitloop_processing" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
