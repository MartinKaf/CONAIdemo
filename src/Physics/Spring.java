//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package Physics;

public class Spring implements Force {
    float springConstant;
    float damping;
    float restLength;
    float oldSpringConstant;
    Particle a;
    Particle b;
    boolean on;
    public int col;
    public int transp;
    protected float fixedspringConstant;
    protected boolean fixed;
    public String theText;
    public float strW;

    public Spring(Particle A, Particle B, float ks, float d, float r) {
        this.springConstant = ks;
        this.damping = d;
        this.restLength = r;
        this.a = A;
        this.b = B;
        this.col = 0;
        this.transp = 255;
        this.on = true;
        this.fixedspringConstant = 1.0F;
        this.theText = "";
        this.strW = 0.1F;
        this.oldSpringConstant = ks;
    }

    public final void makeFixed() {
        this.fixed = true;
        this.oldSpringConstant = this.springConstant;
        this.springConstant = this.fixedspringConstant;
    }

    public final boolean isFixed() {
        return this.fixed;
    }

    public final boolean isFree() {
        return !this.fixed;
    }

    public final void makeFree() {
        this.fixed = false;
        this.springConstant = this.oldSpringConstant;
    }

    public final void turnOff() {
        this.on = false;
    }

    public final void turnOn() {
        this.on = true;
    }

    public final boolean isOn() {
        return this.on;
    }

    public final boolean isOff() {
        return !this.on;
    }

    public final Particle getOneEnd() {
        return this.a;
    }

    public final Particle getTheOtherEnd() {
        return this.b;
    }

    public final float currentLength() {
        return this.a.position().distanceTo(this.b.position());
    }

    public final float restLength() {
        return this.restLength;
    }

    public final float strength() {
        return this.springConstant;
    }

    public final void setStrength(float ks) {
        this.springConstant = ks;
    }

    public final float damping() {
        return this.damping;
    }

    public final void setDamping(float d) {
        this.damping = d;
    }

    public final void setRestLength(float l) {
        this.restLength = l;
    }

    public final void apply() {
        if (this.on && (this.a.isFree() || this.b.isFree())) {
            float a2bX = this.a.position().x - this.b.position().x;
            float a2bY = this.a.position().y - this.b.position().y;
            float a2bZ = this.a.position().z - this.b.position().z;
            float a2bDistance = (float)Math.sqrt((double)(a2bX * a2bX + a2bY * a2bY + a2bZ * a2bZ));
            if (a2bDistance == 0.0F) {
                a2bX = 0.0F;
                a2bY = 0.0F;
                a2bZ = 0.0F;
            } else {
                a2bX /= a2bDistance;
                a2bY /= a2bDistance;
                a2bZ /= a2bDistance;
            }

            float springForce = -(a2bDistance - this.restLength) * this.springConstant;
            float Va2bX = this.a.velocity().x - this.b.velocity().x;
            float Va2bY = this.a.velocity().y - this.b.velocity().y;
            float Va2bZ = this.a.velocity().z - this.b.velocity().z;
            float dampingForce = -this.damping * (a2bX * Va2bX + a2bY * Va2bY + a2bZ * Va2bZ);
            float r = springForce + dampingForce;
            a2bX *= r;
            a2bY *= r;
            a2bZ *= r;
            if (this.a.isFree()) {
                this.a.force().add(a2bX, a2bY, a2bZ);
            }

            if (this.b.isFree()) {
                this.b.force().add(-a2bX, -a2bY, -a2bZ);
            }
        }

    }

    protected void setA(Particle p) {
        this.a = p;
    }

    protected void setB(Particle p) {
        this.b = p;
    }
}
