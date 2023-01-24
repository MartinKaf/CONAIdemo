

import processing.core.PApplet;
import processing.core.PVector;
import processing.core.*;

import java.awt.*;
import java.util.ArrayList;

public class Poly{
    // A list of vertices
    ArrayList<PVector> vertices;
    // The center
    PVector centroid;
    private PApplet sketch;
    int offset = 250;
    java.awt.Polygon p = new java.awt.Polygon();
    int siteArea = 0;

    public Poly(PApplet sketch) {
        this.sketch = sketch;
        // Empty at first
        vertices = new ArrayList<PVector>();
        centroid = new PVector();;
        sketch.stroke(100);
    }

    // We can clear the whole thing if necessary
    public void clearP() {
        vertices.clear();
    }

    public void createSite()
    {
        addVertex(offset,offset);
        addVertex(offset,500 + offset);
        addVertex(offset+500,500+offset);
        addVertex(500+offset,offset);
    }

    // Add a new vertex
    public void addVertex(float inX, float inY) {
        vertices.add(new PVector(inX,inY));
        // Whenever we have a new vertex
        // We have to recalculate the center
        // and re-sort the vertices
        updateCentroid();
        // Come out the sorting if you want to see it drawn incorrectly
        sortVertices();
        updatePolygon();
    }

    public void updatePolygon() {
        p = new java.awt.Polygon();
        for(int i = 0; i< vertices.size(); i++) p.addPoint((int)vertices.get(i).x,(int)vertices.get(i).y);
        calcArea();
    }

    // The center is the average location of all vertices
    void updateCentroid() {
        centroid = new PVector();
        //  p = new java.awt.Polygon();
        // for(int i = 0; i< vertices.size(); i++) p.addPoint((int)vertices.get(i).x,(int)vertices.get(i).y);

        for (PVector v : vertices) {
            centroid.add(v);
        }
        centroid.div(vertices.size());
    }


    // Sorting the ArrayList
    void sortVertices() {

        // This is something like a selection sort
        // Here, instead of sorting within the ArrayList
        // We'll just build a new one sorted
        ArrayList<PVector> newVertices = new ArrayList<PVector>();

        // As long as it's not empty
        while (!vertices.isEmpty ()) {
            // Let's find the one with the highest angle
            float biggestAngle = 0;
            PVector biggestVertex = null;
            // Look through all of them
            for (PVector v : vertices) {
                // Make a vector that points from center
                PVector dir = PVector.sub(v, centroid);
                // What is it's heading
                // The heading function will give us values between -PI and PI
                // easier to sort if we have from 0 to TWO_PI
                float a = dir.heading2D() + sketch.PI;
                // Did we find it
                if (a > biggestAngle) {
                    biggestAngle = a;
                    biggestVertex = v;
                }
            }

            // Put the one we found in the new arraylist
            newVertices.add(biggestVertex);
            // Delete it so that the next biggest one
            // will be found the next time
            vertices.remove(biggestVertex);
        }
        // We've got a new ArrayList
        vertices = newVertices;
        calcArea();
        // p = new java.awt.Polygon();
        //  for(int i = 0; i< vertices.size(); i++) p.addPoint((int)vertices.get(i).x,(int)vertices.get(i).y);
    }


    public void calcArea(){
        if(vertices.size()>2) {
            float tempArea = 0;
            float ff1 = 0;
            float ff2 = 0;
            for (int i = 0; i < vertices.size() - 1; i++) {
                float f1 = vertices.get(i).x * vertices.get(i + 1).y; //x1y2
                float f2 = vertices.get(i+1).x * vertices.get(i).y; //x1y2
                ff1 += f1;
                ff2 += f2;
            }
            ff1+=(vertices.get(vertices.size()-1).x * vertices.get(0).y);
            ff2+=(vertices.get(0).x * vertices.get(vertices.size()-1).y);
            tempArea = sketch.abs((ff1-ff2) / 2);
            siteArea = (int)tempArea;
            //sketch.println(tempArea);
        }
    }

    // Draw everything!
    public void display(int index) {

        // First draw the polygon
        sketch.stroke(0);
        sketch.strokeWeight(0.1f);
        sketch.fill(0);
        sketch.beginShape();
        for (int i = 0; i<vertices.size();i++) {
            sketch.vertex(vertices.get(i).x, vertices.get(i).y);
        }
        sketch.endShape(sketch.CLOSE);

        // Then we'll draw some addition information
        // at each vertex to show the sorting
        for (int i = 0; i < vertices.size(); i++) {

            // This is overkill, but we want the numbers to
            // appear outside the polygon so we extend a vector
            // from the center
            PVector v = vertices.get(i);
            PVector dir = PVector.sub(v, centroid);
            dir.normalize();
            dir.mult(12);

            // Number the vertices
            sketch.fill(255,0,0,80);
            //sketch.stroke(255);
            sketch.noStroke();
            sketch.ellipse(v.x, v.y, 4, 4);
            if (i == index) {
                sketch.fill(255,0,255,80);
                sketch.ellipse(v.x, v.y,20,20);
            }
            sketch.textAlign(sketch.CENTER);
            sketch.fill(0);
            sketch.text(i, v.x+dir.x, v.y+dir.y+6);
        }


        // Once we have two vertices draw the center
        if (vertices.size() > 1  ) {
            sketch.fill(255,50);
            sketch.ellipse(centroid.x, centroid.y, 8, 8);
            // sketch.text("centroid", centroid.x, centroid.y+16);
        }
    }

    //+++++++++++++++++++ collision
    // POLYGON/CIRCLE
    //boolean polyCircle(PVector[] vertices, float cx, float cy, float r)
    boolean polyCircle(float cx, float cy, float r)
    {

        // go through each of the vertices, plus
        // the next vertex in the list
        int next = 0;
        for (int current=0; current<vertices.size(); current++) {

            // get next vertex in list
            // if we've hit the end, wrap around to 0
            next = current+1;
            if (next == vertices.size()) next = 0;

            // get the PVectors at our current position
            // this makes our if statement a little cleaner
            PVector vc = vertices.get(current);    // c for "current"
            PVector vn = vertices.get(next);       // n for "next"

            // check for collision between the circle and
            // a line formed between the two vertices
            boolean collision = lineCircle(vc.x,vc.y, vn.x,vn.y, cx,cy,r);
            if (collision) return true;
        }

        // the above algorithm only checks if the circle
        // is touching the edges of the polygon – in most
        // cases this is enough, but you can un-comment the
        // following code to also test if the center of the
        // circle is inside the polygon

        boolean centerInside = polygonPoint(cx,cy);
        if (centerInside) return true;

        // otherwise, after all that, return false
        return false;
    }

    // LINE/CIRCLE
    boolean lineCircle(float x1, float y1, float x2, float y2, float cx, float cy, float r) {

        // is either end INSIDE the circle?
        // if so, return true immediately
        boolean inside1 = pointCircle(x1,y1, cx,cy,r);
        boolean inside2 = pointCircle(x2,y2, cx,cy,r);
        if (inside1 || inside2) return true;

        // get length of the line
        float distX = x1 - x2;
        float distY = y1 - y2;
        float len = sketch.sqrt( (distX*distX) + (distY*distY) );

        // get dot product of the line and circle
        float dot = ( ((cx-x1)*(x2-x1)) + ((cy-y1)*(y2-y1)) ) / sketch.pow(len,2);

        // find the closest point on the line
        float closestX = x1 + (dot * (x2-x1));
        float closestY = y1 + (dot * (y2-y1));

        // is this point actually on the line segment?
        // if so keep going, but if not, return false
        boolean onSegment = linePoint(x1,y1,x2,y2, closestX,closestY);
        if (!onSegment) return false;

        // optionally, draw a circle at the closest point
        // on the line
        sketch.fill(255,0,0);
        sketch.noStroke();
        //sketch.ellipse(closestX, closestY, 20, 20);

        // get distance to closest point
        distX = closestX - cx;
        distY = closestY - cy;
        float distance = sketch.sqrt( (distX*distX) + (distY*distY) );

        // is the circle on the line?
        if (distance <= r) {
            return true;
        }
        return false;
    }

    // LINE/POINT
    boolean linePoint(float x1, float y1, float x2, float y2, float px, float py) {

        // get distance from the point to the two ends of the line
        float d1 = sketch.dist(px,py, x1,y1);
        float d2 = sketch.dist(px,py, x2,y2);

        // get the length of the line
        float lineLen = sketch.dist(x1,y1, x2,y2);

        // since floats are so minutely accurate, add
        // a little buffer zone that will give collision
        float buffer = 0.1f;    // higher # = less accurate

        // if the two distances are equal to the line's
        // length, the point is on the line!
        // note we use the buffer here to give a range, rather
        // than one #
        if (d1+d2 >= lineLen-buffer && d1+d2 <= lineLen+buffer) {
            return true;
        }
        return false;
    }


    // POINT/CIRCLE
    boolean pointCircle(float px, float py, float cx, float cy, float r) {

        // get distance between the point and circle's center
        // using the Pythagorean Theorem
        float distX = px - cx;
        float distY = py - cy;
        float distance = sketch.sqrt( (distX*distX) + (distY*distY) );

        // if the distance is less than the circle's
        // radius the point is inside!
        if (distance <= r) {
            return true;
        }
        return false;
    }

    // POLYGON/POINT
// only needed if you're going to check if the circle
// is INSIDE the polygon
    boolean polygonPoint(float px, float py) {
        boolean collision = false;

        // go through each of the vertices, plus the next
        // vertex in the list
        int next = 0;
        for (int current=0; current<vertices.size(); current++) {

            // get next vertex in list
            // if we've hit the end, wrap around to 0
            next = current+1;
            if (next == vertices.size()) next = 0;

            // get the PVectors at our current position
            // this makes our if statement a little cleaner
            PVector vc = vertices.get(current);    // c for "current"
            PVector vn = vertices.get(next);       // n for "next"

            // compare position, flip 'collision' variable
            // back and forth
            if (((vc.y > py && vn.y < py) || (vc.y < py && vn.y > py)) &&
                    (px < (vn.x-vc.x)*(py-vc.y) / (vn.y-vc.y)+vc.x)) {
                collision = !collision;
            }
        }
        return collision;
    }


}

