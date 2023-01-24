//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package Physics;

import processing.core.PVector;
import java.util.ArrayList;

public class Particle {
    protected Vector3D position = new Vector3D();
    protected Vector3D velocity = new Vector3D();
    protected Vector3D force = new Vector3D();
    protected float mass;
    protected float age;
    protected boolean dead;
    public String theText;
    public int col;
    public int str;
    public float strW;
    protected Vector3D OLDposition = new Vector3D();
    protected boolean fixed;
    protected boolean connected;
    public int transp;
    public int sizeCircle;
    public int area;
    public float h;
    public float w;
    public int numCirclePoints;
    public PVector[] point;
    public float scaleSize = 1f;
    public int realCircle;

    public Particle(float m) {
        this.mass = m;
        this.fixed = false;
        this.age = 0.0F;
        this.dead = false;
        this.theText = "";
        this.col = 0;
        this.connected = false;
        this.transp = 255;
        this.sizeCircle = 30;
        this.str = 0;
        this.strW = 0.0F;
        realCircle = (int)(sizeCircle*scaleSize);
        this.area = (int) (Math.PI *  Math.pow (( realCircle)/2,2));
        this.numCirclePoints = 8;
        this.point = new PVector[this.numCirclePoints];

        for(int i = 0; i<this.numCirclePoints;i++) {
            point[i] = new PVector(0,0,0);
        }

    }

    public final boolean containsPoint(float x, float y) {
        this.h = (float)this.realCircle;
        this.w = (float)this.realCircle;
        float dx = this.position.x - x;
        float dy = this.position.y - y;
        return Math.abs(dx) < this.w / 2.0F && Math.abs(dy) < this.h / 2.0F;
    }

    public final void updateArea()
    {
        realCircle = (int)(sizeCircle*scaleSize);
        area = (int) (Math.PI *  Math.pow (realCircle/2,2));
    }

    public  void circlePoints(){

        for(int i = 0; i<this.numCirclePoints;i++) {
            float xx = this.position.x + realCircle/2 * (float)(Math.sin(i * 1.0f /this.numCirclePoints * 2 * Math.PI));
            float yy = this.position.y + realCircle/2 * (float)(Math.cos(i * 1.0f /this.numCirclePoints * 2 * Math.PI));
            this.point[i].x = xx;
            this.point[i].y = yy;
        }
    }



    public final float distanceTo(Particle p) {
        return this.position().distanceTo(p.position());
    }

    public final void makeFixed() {
        this.fixed = true;
        this.velocity.clear();
    }

    public final boolean isFixed() {
        return this.fixed;
    }

    public final boolean isFree() {
        return !this.fixed;
    }

    public final void makeFree() {
        this.fixed = false;
    }

    public final Vector3D position() {
        return this.position;
    }

    public final Vector3D OLDposition() {
        return this.OLDposition;
    }

    public final Vector3D velocity() {
        return this.velocity;
    }

    public final float mass() {
        return this.mass;
    }

    public final void setMass(float m) {
        this.mass = m;
    }

    public final Vector3D force() {
        return this.force;
    }

    public final float age() {
        return this.age;
    }

    public final void reset() {
        this.age = 0.0F;
        this.dead = false;
        this.position.clear();
        this.OLDposition.clear();
        this.velocity.clear();
        this.force.clear();
        this.mass = 1.0F;
    }
}
