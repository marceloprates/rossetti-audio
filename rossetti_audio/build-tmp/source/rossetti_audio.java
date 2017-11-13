import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.spi.*; 
import ddf.minim.signals.*; 
import ddf.minim.*; 
import ddf.minim.analysis.*; 
import ddf.minim.ugens.*; 
import ddf.minim.effects.*; 
import controlP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class rossetti_audio extends PApplet {









Minim minim;
AudioPlayer player;
AudioInput input;
FFT fft;

ControlP5 cp5;
ColorPicker colorpicker;

int N = 20;
int M = 50;

boolean cycle_color = true;
float offset;
float radius;
float inner_radius;
float nsi;
float nsj;
float nst;

float t = 0;

public void setup()
{
  size(600,600);
  colorMode(HSB);
 
  minim = new Minim(this);
  input = minim.getLineIn();
  
  fft = new FFT(input.bufferSize(), input.sampleRate());

  initGUI();
}

public void draw()
{
  background(255);
  stroke(255);
  
  pushMatrix();
    translate(width/2,height/2);
    
    fft.forward(input.mix);
    
    noFill();
    if(cycle_color)
    {
      colorMode(HSB);
      stroke(color(255*0.5f*(1+cos(0.05f*t)),150,150));
      colorMode(RGB);
    }
    else
    {
      stroke(colorpicker.getColorValue());
    }
    
    for(int i = 0; i < N; i++)
    {
      float fi = fft.getBand((i)*fft.specSize()/N);

      float r = inner_radius*width + radius*(N-i)*width/N;
      
      beginShape();
      for(int j = 0; j <= M+2; j++)
      {      
        float d1 = 0.1f*width*max(-1*fi, 1.5f*(-0.5f + noise(nsi*PApplet.parseFloat(i)/N + nst*t, nsj*cos(0.45f*PI + 0.1f*PI*j/(M-1)))));
        float d2 = 0.02f*width*noise(nsi*(PApplet.parseFloat(i)/N), nsj*cos(0.45f*PI + 0.1f*PI*j/(M-1)) );
        
        float x,y;
        x = (r+d1+d2)*cos(PApplet.parseFloat(j)*TWO_PI/M + offset*TWO_PI*PApplet.parseFloat(i)/N);
        y = (r+d1+d2)*sin(PApplet.parseFloat(j)*TWO_PI/M + offset*TWO_PI*PApplet.parseFloat(i)/N);
        curveVertex(x,y);
      }
      endShape();
    }
  popMatrix();

  t += 0.1f;
}

public void keyPressed()
{
  if(cp5.isVisible())
    cp5.hide();
  else
    cp5.show();
}

public void initGUI()
{
  cp5 = new ControlP5(this);
  cp5.hide();

  colorpicker = new ColorPicker(cp5, "hacktastic");
  colorpicker
  .setPosition(width-256,0)
  .setColorValue(color(0));

  cp5.addButton("Cycle_Color")
  .setValue(0)
  .setPosition(0,0)
  .setSize(120,20)
  ;

  cp5.addSlider("Offset")
  .setValue(0.1f)
  .setRange(0,1)
  .setPosition(0,21)
  .setSize(120,20);

  cp5.addSlider("Radius")
  .setValue(0.2f)
  .setRange(0,1)
  .setPosition(0,42)
  .setSize(120,20);

  cp5.addSlider("Inner_Radius")
  .setValue(0.2f)
  .setRange(0,1)
  .setPosition(0,63)
  .setSize(120,20);

  cp5.addSlider("Radial_Noise_Scale")
  .setValue(1)
  .setRange(0,5)
  .setPosition(0,84)
  .setSize(120,20);

  cp5.addSlider("Angular_Noise_Scale")
  .setValue(100)
  .setRange(0,1000)
  .setPosition(0,105)
  .setSize(120,20);

  cp5.addSlider("Time_Scale")
  .setValue(0.1f)
  .setRange(0,1)
  .setPosition(0,126)
  .setSize(120,20);
}

public void Cycle_Color()
{
  cycle_color = !cycle_color;
}

public void Offset(float x)
{
  offset = x;
}

public void Radius(float x)
{
  radius = x;
}

public void Inner_Radius(float x)
{
  inner_radius = x;
}

public void Radial_Noise_Scale(float x)
{
  nsi = x;
}

public void Angular_Noise_Scale(float x)
{
  nsj = x;
}

public void Time_Scale(float x)
{
  nst = x;
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "rossetti_audio" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
