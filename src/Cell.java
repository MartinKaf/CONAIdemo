
import processing.core.PApplet;
import processing.core.PVector;
import processing.core.*;

import java.awt.*;
import java.util.ArrayList;



public class Cell {

    private PApplet sketch;
    int edges;
    ArrayList<PVector> point;
    ArrayList<PVector> point2;
    ArrayList<PVector> velocity;
    int e;
    float centerX, centerY;
    float speed = 0.7f;
    float targetArea = 0;
    float realArea=0;

    public Cell(PApplet sketch) {
        this.sketch = sketch;
        sketch.stroke(100);
    }

    public void addCell (float x, float y, float radius, int c, int sentEdges, float sentArea) {
        centerX = x;
        centerY = y;
        point = new  ArrayList<PVector>();
        point2 = new  ArrayList<PVector>();
        velocity = new  ArrayList<PVector>();
        targetArea = sentArea;
        //sketch.println(targetArea);

        for ( int i=0; i<sentEdges; i++) {

           // pnts2[numPoints] = new point();
            float xx = x + radius * sketch.sin(i*1.0f/sentEdges*2*sketch.PI);
            float yy = y + radius * sketch.cos(i*1.0f/sentEdges*2*sketch.PI);
            PVector vv = new PVector(xx,yy);
            point.add(vv);
            point2.add(new PVector(0,0));
            velocity.add(new PVector(0,0));
            //cells[numCells].edge[i] = numPoints;
           //numPoints++;
        }
        e=c;
    }

   public void move (int iSent) {
       if (realArea<=targetArea)
       {
           point.get(iSent).x += velocity.get(iSent).x * speed;
           point.get(iSent).y += velocity.get(iSent).y * speed;
       }
           calcArea();
           if (realArea>targetArea){
           //    point.get(iSent).x -= velocity.get(iSent).x * speed;
           //    point.get(iSent).y -= velocity.get(iSent).y * speed;
           }
   }

  public void normalovat(int indexVelocity) {

        float dis = sketch.sqrt(velocity.get(indexVelocity).x * velocity.get(indexVelocity).x + velocity.get(indexVelocity).y * velocity.get(indexVelocity).y) + 0.001f;
        velocity.get(indexVelocity).x /= dis;
        velocity.get(indexVelocity).y /= dis;
        }

        public void calcArea() {
            if(point.size()>2) {
                float tempArea = 0;
                float ff1 = 0;
                float ff2 = 0;
                for (int i = 0; i < point.size() - 1; i++) {
                    float f1 = point.get(i).x * point.get(i + 1).y; //x1y2
                    float f2 = point.get(i+1).x * point.get(i).y; //x1y2
                    ff1 += f1;
                    ff2 += f2;
                }
                ff1+=(point.get(point.size()-1).x * point.get(0).y);
                ff2+=(point.get(0).x * point.get(point.size()-1).y);
                tempArea = sketch.abs((ff1-ff2) / 2);
                realArea = tempArea;
        }
        }

        public void draw() {
        sketch.stroke(255);
        sketch.fill(255);
            sketch.beginShape ();
            for (int i = 0; i < point.size(); i++) {
                sketch.vertex(point.get(i).x, point.get(i).y);
                //sketch.endShape(sketch.CLOSE);
                //sketch.println(point.get(i).x);
            }
        }


}
