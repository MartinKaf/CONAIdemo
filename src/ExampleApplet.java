
import processing.core.*;
import Physics.Particle;
import Physics.ParticleSystem;
import Physics.Spring;
import processing.core.PApplet;
import Elements.DistancePointSegment;
import processing.core.PImage;
import processing.event.MouseEvent;
import g4p_controls.*;
import processing.core.PStyle;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PMatrix3D;
import processing.data.XML; //<>//
import java.io.*;

import java.awt.*;
import java.util.ArrayList;
import peasy.*;

public class ExampleApplet extends PApplet {

    public static void main(String args[]) {
        PApplet.main("ExampleApplet");
    }

    @Override
    public void settings() {
        // TODO: Customize screen size and so on here
        size (1000,1000,P2D);
        //fullScreen();
    }
    // globals
    ParticleSystem physics;
    int keyValue = 0;
    int in = -1;
    int out = -1;
    int strokeLink = color(255,80);
    int textCol = color(30);
    int scale = 10;
    PApplet p = null;
    PImage boundaryImg;
    public PApplet papp;
    int nodeInt = -1;
    //+++++++++++++++++++ import graph
    //boolean loadGraph = true;
    XML xml;
    ArrayList<String> rooms = new ArrayList<String>();
    ArrayList<Integer> roomColor = new ArrayList<Integer>();
    ArrayList <String> nodesNames = new ArrayList<String>();
    ArrayList <String> adjStrList = new ArrayList<String>();// { "1-2", "3-0", "2-4" };//*
    ArrayList<Integer> roomAreas = new ArrayList<Integer>();
    ArrayList<String> roomSize = new ArrayList<String>();
    ArrayList <int[]> adjRoomList = new ArrayList<int[]>();// { "1-2", "3-0", "2-4" };//*
    //+++++++++++++++++++ site
    Poly site;
    PVector b1 = new PVector(0,0);
    PVector b2 = new PVector(0,0);
    PVector b3 = new PVector(0,0);
    PVector b4 = new PVector(0,0);
    //+++++++++++++++++++ grid
    int gstep = 25;
    //+++++++++++++++++++ AI
    PGraphics pg;
    float pgSize = 256f;
    float scalePG = 0;
    float scaleRooms = 1;
    PImage AIimage;
    //+++++++++++++++++++ cells
    ArrayList<Cell> cells;

    //int sizeOffice = 30;
    // int sizeWC = 10;
    // int sizeHall = 50;
    // int sizeConferenceRoom = 60;
    // int sizeStorage = 30;
    String[] roomNames = {"Storage","Aquaponic","Microgreens","Vertical_Farm","Kitchen","WC","Hall","Stairs","Corridor"};
    int[] sizeRooms = {24,24,24,12,12,12,12,12,12};
    int[] coloursRooms = {color(255,0,0),color(0,0,255),color(0,255,0),
            color(255,255,0),color(255,88,227),color(200,200,200),color(0,255,220),color(255,0,255),color(150,150,150) };
    float bounceFact = 2;
    int numEdges = 20;
    //+++++++++++++GUI
    GLabel label1;
    GDropList dropList1;
    GButton button1;
    GButton button2;
    GLabel label2;
    GButton button3;
    GCheckbox checkbox1;
    GToggleGroup togGroup1;
    GOption option1;
    GOption option2;
    GOption option3;
    GOption option4;
    GOption option5;
    GOption option6;
    GOption option7;
    GOption option8;
    GOption option9;
    GOption option10;
    GOption option11;
    GLabel label3;
    GLabel label4;
    GWindow Help;
    GSlider slider1;
    GLabel label5;
    GSlider slider2;
    GLabel label6;
    GTextField textfield1;
    GCheckbox checkbox2;
    //canvases
    GViewPeasyCam view1;

    @Override
    public void setup() {

        papp = this;
        papp.registerMethod("mouseEvent", this);
        smooth();
        //++++++++++++++++++++++ create GUI
        createGUI();
        //customGUI();
        //++++++++++++++++++++++ physics setup
        this.physics = new ParticleSystem(0.0F, 0.1F);
        physics.setIntegrator(ParticleSystem.RUNGE_KUTTA);
        ellipseMode(CENTER);
        // create site
        // Site.createBoundary();
        site = new Poly(papp);
        site.createSite();
        cells = new ArrayList<Cell>();
        keyValue = 1;
        //+++++++++++++++++ AI
        pg = createGraphics((int)pgSize,(int)pgSize,P2D);
        scalePG = pgSize/(float)width;
        textMode(SCREEN);
        //++++++++++++++++ canvases
        view1 = new GViewPeasyCam(this, site.offset+800, site.offset, 500, 500, 200);
        PeasyCam pcam = view1.getPeasyCam();

        //uploadGraph();
    }

    boolean tick = true;


    @Override
    public void draw() {

        if (tick) this.physics.tick();

        background(55);

        site.display(nodeInt);

        if(keyValue==19) image(AIimage,0,0,width,height);

        drawGraph();

        if (keyValue == 14 || keyValue == 11  || keyValue == 15) gridLines();
        drawCells();

        //+++++++++++++++++++++++ draw site dimensions

        fill(255,255,0,80);
        strokeWeight(0.5f);
        b1.set((float)site.p.getBounds2D().getMinX(),(float)site.p.getBounds2D().getMinY()-20);
        b2.set((float)site.p.getBounds2D().getMaxX(),(float)site.p.getBounds2D().getMinY()-20);
        b3.set((float)site.p.getBounds2D().getMaxX()+20,(float)site.p.getBounds2D().getMinY());
        b4.set((float)site.p.getBounds2D().getMaxX()+20,(float)site.p.getBounds2D().getMaxY());

        ellipse(b1.x,b1.y,5,5);
        ellipse(b2.x,b2.y,5,5);
        ellipse(b3.x,b3.y,5,5);
        ellipse(b4.x,b4.y,5,5);
        stroke(255,255,0,80);
        strokeWeight(0.5f);
        line(b1.x,b1.y,b2.x,b2.y);
        line(b3.x,b3.y,b4.x,b4.y);
        text(String.valueOf((int)(site.p.getBounds2D().getWidth())/scale),(b2.x+b1.x)/2,b1.y);
        text(String.valueOf((int)(site.p.getBounds2D().getHeight())/scale),b3.x+15,(b3.y+b4.y)/2);
        text("site area " + (int)(site.siteArea/(scale*scale)) + " m2",width - 80,20);


        boundaries ();


        for (int i = 0; i<physics.numberOfParticles();i++)
        {
            Particle first = physics.getParticle(i);
            for (int j = 0; j<physics.numberOfParticles();j++)
            {
                if (i != j) {
                    Particle other = physics.getParticle(j);
                    checkCollision(first, other);
                }
            }
        }
        // draw canvas
        updateView1();

    }


    void drawGraph() {

        {

            //+++++++++++++++++++++++++++ draw nodes + text nodes
            Particle part;
            for (int i = 0; i < physics.numberOfParticles(); i++) {
                part = physics.getParticle(i);
                part.position().setZ(0);
                part.circlePoints();

                {
                    strokeWeight(part.strW);
                    //fill((part.col),part.transp);
                    fill((part.col),80);
                    stroke((part.str));
                }

                pushMatrix();
                translate(part.position().x(), part.position().y());
                //+++ellipse property start
                // fill(0,0,255); // node not selected in 2d
                //strokeWeight(1f);
                //stroke(255,255,0);
                if (part.isFree()==false){
                    //fill(255,80);
                    stroke(0,255,240,80);
                    strokeWeight(2);
                    //ellipse(0, 0, (part.realCircle +5), (part.realCircle +5));
                }
                fill(255,80);
                ellipse(0, 0, part.realCircle, part.realCircle);
                fill((part.col),80);
                ellipse(0, 0, part.realCircle, part.realCircle);
                //+++ellipse property end
                if (part == physics.getSelectedNode()) {
                    fill(255,0,0);
                    strokeWeight(0.1f);
                    stroke(0);
                    ellipse(0, 0, part.realCircle/2, part.realCircle/2);
                }
                //  /*  if (drawText)
                {
                    //textFont(font, 14);
                    textAlign(CENTER, CENTER);
                    fill(textCol);
                    text(part.theText, 0, 0);
                    text(part.area/(scale*scale), 0, 10);
                    //text((int)part.position().x() + "," + (int)part.position().y() + "," + (int)part.position().z(), 0, -30);
                }// */

          /*  if (part.isFixed()) {
                stroke (0,210,250);
                strokeWeight(3);
                noFill();
                ellipse(0, 0, part.sizeCircle+15, part.sizeCircle+15);
            }*/
                popMatrix();

            }
//+++++++++++++++++++++++++++++++++++++++++++++++++


            fill(0, 80.0F);
            noStroke();
            //stroke(fg);
            strokeWeight(5.0F);

            noFill();
            int check = 0;

            // +++++++++++++ draw Links
            Particle sel = physics.getSelectedNode();
            for (int i = 0; i < physics.numberOfSprings(); i++) {
                Spring link = physics.getSpring(i);
                Particle one = link.getOneEnd();
                Particle two = link.getTheOtherEnd();
                stroke((link.col),link.transp);
                if (sel == one || sel == two) {
                    //  if (!freeze)
                    //      strokeWeight(10);
                    // else
                    strokeWeight(5);
                }
                else
                {
                    float weight = (float) (1 + (link.currentLength()- link.restLength()) * 0.5);
                    if (weight  < 1) weight = 1;
                    //if (weight  > 10) weight = 10;
                    //if (!freeze && weigh)
                    //	strokeWeight(link.strW);
                    //  strokeWeight(1);
                    //else
                    strokeWeight(1);
                }

                line(link.getOneEnd().position().x(), link.getOneEnd()
                        .position().y(), link.getTheOtherEnd().position()
                        .x(), link.getTheOtherEnd().position().y());

                if (link.isFixed()) {
                    stroke (0,210,250,20);
                    strokeWeight(25);
                    line(link.getOneEnd().position().x(), link.getOneEnd()
                            .position().y(), link.getTheOtherEnd().position()
                            .x(), link.getTheOtherEnd().position().y());
                }

                //+++++++++++++ text Links
           /* if (drawText) {
                textFont(font, 14);
                textAlign(CENTER, CENTER);
                fill(textCol);
                //text(part.theText, 0, -45);
                float x = lerp(link.getOneEnd().position().x(),link.getTheOtherEnd().position().x(), 0.5f);
                float y = lerp(link.getOneEnd().position().y(),link.getTheOtherEnd().position().y(), 0.5f);
                text(link.theText, x, y-20);
                text(link.currentLength(), x, y);
            } */


            }
            if (physics.getSelectedLink() != null) {
                Spring picked = physics.getSelectedLink();
                //if (freeze)strokeWeight(5);
                //else
                strokeWeight(20);
                stroke(255,0,0,80);
                line(picked.getOneEnd().position().x(), picked.getOneEnd()
                        .position().y(), picked.getTheOtherEnd().position()
                        .x(), picked.getTheOtherEnd().position().y());
            }
            // ++++++++++++++++

            //stroke(bg);
            strokeWeight(1.0F);

       /* if (record) {
            endRaw();
            if (check == 1) {
                drawText = true;
                check = 0;
            }
            record = false;
        } */




            noTint();
            noFill();
            stroke(255,0,0,80);
            //if (freeze)strokeWeight(5);
            //else
            strokeWeight(15.0F);

            if (in >= 0 && keyValue==4) {

                line(physics.getParticle(in).position().x(), physics
                        .getParticle(in).position().y(), mouseX, mouseY);
            }
            // +++++++++ create text on selected node
            //if (createText && physics.getSelectedNode() != null)
            //    drawNodeText();
            //if (createTextLink && physics.getSelectedLink() != null)
            //    drawNodeText();
            //++++++++++ draw help menu

        /* if (helpMenu){
            noStroke();
            fill(198,143,0,100);
            rect(40,440,320,495);
            textFont(font, 14);
            fill(0);
            textAlign(LEFT);
            text(displayTxt,50,455);
        } */
            // +++++++++ draw mode text
      /*   if (selectNode)
            mode = "SELECT NODE";
        else if (drawNode)
            mode = "CREATE NODE";
        else if (deleteNode)
            mode = "DELETE NODE";
        else if (selectLink)
            mode = "SELECT LINK";
        else if (createLink)
            mode = "CREATE LINK";
        else if (deleteLink)
            mode = "DELETE LINK";
        else if (createText)
            mode = "CREATE LABEL - NODE";
        else if (createTextLink)
            mode = "CREATE LABEL - LINK";
        else if (freezeNode)
            mode = "FREEZE NODE";
        else if (pickNode)
            mode = "MOVE NODE";
        else
            mode = ""; */
      /*
        textFont(font, 14);
        //textAlign(CENTER, CENTER);
        textAlign(LEFT);
        fill(textCol);
        text(mode, 50, 70);
*/
/*
        if (freeze)
            mode2 = "FREEZE";
//				else if (drawNode)
//					mode = "CREATE NODE";
        else
            mode2 = "MOVE";

        textFont(font, 14);
        //textAlign(CENTER, CENTER);
        textAlign(LEFT);
        fill(textCol);
        text(mode2, 50, 50);


        if (drawMode == false)
        {
            textFont(font, 14);
            textAlign(CENTER, CENTER);
            fill(textCol);
            text("2D", 20, 20);
        }
*/

/*
        if (changeColor && selectNode == true)
        {

            textFont(font, 14);
            textAlign(LEFT);
            fill(textCol);
            if (strokeFill == false)
                text("STROKE", 50, 105);
            else text("FILL", 50, 105);
        } */

            // +++++++++

            //if (changeColor) slider.draw();
            //cam.endHUD();
        }

    }

    void drawCells(){
        for (int i=0;i<cells.size();i++) {
            //cells.get(i).draw();

            stroke(255);
            strokeWeight(1f);
            //fill(255);
            beginShape ();
            for (int j = 0; j < cells.get(i).point.size(); j++) {
                vertex(cells.get(i).point.get(j).x, cells.get(i).point.get(j).y);
                //point(cells.get(i).point.get(j).x, cells.get(i).point.get(j).y);
            }
            endShape(CLOSE);


        }
        adjustCorners();
//smoothEdges();
    }

    public void mmmselectNode(float xSent, float ySent) {

        for  (int i=0; i<site.vertices.size();i++){
            float distance = dist(site.vertices.get(i).x,site.vertices.get(i).y, xSent,ySent);
            // if (distance<=10)
        }
    }

    public void selectNode(float xSent, float ySent) {

        // if (changeColor == false)
        physics.setSelectedNode(null);
        //else
        physics.setSelectedLink(null);
        for (int i = 0; i < physics.numberOfParticles(); i++) {
            Particle n = physics.getParticle(i);
            //float[] position = cam.getPosition();
            if (n.containsPoint(mouseX, mouseY)) {
                physics.setSelectedNode(n);
            }
            // if (n.containsPoint(mouseX,mouseY) == false)
            // physics.setSelectedNode(null);
        }
    }


    public void createNode(float xSent, float ySent) {
        // int tnt = color(100.0F + random(155.0F), 100.0F + random(155.0F),
        //         100.0F + random(155.0F), 90);
        physics.makeParticle(1f, xSent, ySent, 0);

        Particle pp = physics.getParticle(physics.numberOfParticles()-1);
        pp.sizeCircle = sizeRooms[dropList1.getSelectedIndex()];
        pp.col = coloursRooms[dropList1.getSelectedIndex()];
        pp.scaleSize = scaleRooms;
        pp.theText = dropList1.getSelectedText();
        pp.updateArea();


        if (physics.numberOfParticles()>0) {

            for (int i = 0; i < physics.numberOfParticles(); i++) {
                Particle localParticle = physics.getParticle(i);
                if (pp != localParticle)
                {
                    physics.makeAttraction(pp, localParticle,
                            20.0f, 0.1f);
                }
            }

        }



        // physics.getParticle(physics.numberOfParticles()-1).theText = Integer.toString(physics.numberOfParticles()-1);
        int ct = 0;
        if (ct > 0)
            ct = physics.numberOfParticles() - 1;
        else
            ct = 0;
        Particle p = physics.getParticle(ct);
        p.OLDposition().setZ(random(-1,1));
        //p.position().setZ(0);
        // spaceCount++;
    }

    public void mouseEvent(MouseEvent event) {
        //PApplet.println("test");
        int id = event.getAction();
        // ++++++++++++++++++++++++ draw Node
        if (keyValue == 1) {

            switch (id) {

                case MouseEvent.RELEASE:
                    //PApplet.println("test");
                    if(mouseX > 150 )        createNode(mouseX, mouseY);
                    break;
            }
        }

        // ++++++++++++++++++++++++ select Node
        if (keyValue == 2) {
            //println(dropList1.isOver(mouseX, mouseY));
            switch (id) {
                case MouseEvent.PRESS:
                    selectNode(mouseX, mouseY);
                    break;

                case MouseEvent.RELEASE:
                    //if (changeColor == false) physics.setSelectedNode(null);
                    //PApplet.println(physics.getSelectedNode().area);
                    //PApplet.println(physics.getSelectedNode().sizeCircle);
                    if ( physics.getSelectedNode() != null)  {
                        physics.getSelectedNode().sizeCircle = sizeRooms[dropList1.getSelectedIndex()];
                        physics.getSelectedNode().col = coloursRooms[dropList1.getSelectedIndex()];
                        physics.getSelectedNode().theText = dropList1.getSelectedText();
                        physics.getSelectedNode().updateArea();
                    }
                    break;
            }
        }

        // ++++++++++++++++++++++++ scale Node
        if (keyValue == 20) {
            //println(dropList1.isOver(mouseX, mouseY));
            switch (id) {
                case MouseEvent.PRESS:
                    selectNode(mouseX, mouseY);
                    break;

                case MouseEvent.RELEASE:
                    //if (changeColor == false) physics.setSelectedNode(null);
                    //PApplet.println(physics.getSelectedNode().area);
                    //PApplet.println(physics.getSelectedNode().sizeCircle);
                    if ( physics.getSelectedNode() != null)  {
                        if (key=='+') physics.getSelectedNode().sizeCircle = physics.getSelectedNode().sizeCircle + 3;
                        if (key=='-') physics.getSelectedNode().sizeCircle = physics.getSelectedNode().sizeCircle - 3;
                        //physics.getSelectedNode().col = coloursRooms[dropList1.getSelectedIndex()];
                        //physics.getSelectedNode().theText = dropList1.getSelectedText();
                        physics.getSelectedNode().updateArea();
                    }
                    break;
            }
        }

        //+++++++++++++++++++++++++++ pick Node
        if (keyValue == 3) {
            switch (id) {
                case MouseEvent.PRESS:
                    selectNode(mouseX, mouseY);
                    if ( physics.getSelectedNode() != null && keyPressed && key == '+') {
                        physics.getSelectedNode().sizeCircle = physics.getSelectedNode().sizeCircle + 5;
                        physics.getSelectedNode().updateArea();
                    }
                    if ( physics.getSelectedNode() != null && keyPressed && key == '-') {
                        physics.getSelectedNode().sizeCircle = physics.getSelectedNode().sizeCircle - 5;
                        physics.getSelectedNode().updateArea();
                    }
                    break;

                case MouseEvent.DRAG:
                    if ( physics.getSelectedNode() != null)
                        physics.getSelectedNode().position().set(mouseX, mouseY, 0);
                    break;

                case MouseEvent.RELEASE:
                    //if (changeColor == false)
                    physics.setSelectedNode(null);
                    break;
            }
        }
        // ++++++++++++++++++++++++++ create Link
        if (keyValue == 4 ) {

            switch (id) {
                case MouseEvent.PRESS:
                    for (int i = 0; i < physics.numberOfParticles(); i++) {
                        Particle n = physics.getParticle(i);
                        if (n.containsPoint(mouseX, mouseY)) {
                            physics.setSelectedNode(n);
                            in = (i);
                            // println(i + " " + in);
                        }
                    }

                    break;

                case MouseEvent.DRAG:

                    for (int i = 0; i < physics.numberOfParticles(); i++) {
                        Particle n = physics.getParticle(i);
                        if (n.containsPoint(mouseX, mouseY)) {
                            physics.setSelectedNode(n);
                        }
                    }
                    break;

                case MouseEvent.RELEASE:
                    for (int i = 0; i < physics.numberOfParticles(); i++) {
                        Particle n = physics.getParticle(i);
                        if (n.containsPoint(mouseX, mouseY)) {
                            physics.setSelectedNode(n);
                            out = (i);
                        }
                    }
                    // println (in + " " + out);
                    if (in >= 0 && out >= 0 && in != out) {
                        Particle inn = physics.getParticle(in);
                        Particle outt = physics.getParticle(out);
                        int restLength = inn.realCircle + outt.realCircle;
                        physics.makeSpring(inn, outt, 0.001f, 0.001f, restLength/2);
                        Spring ss = physics.getSpring(physics.numberOfSprings()-1);
                        ss.theText = String.valueOf(physics.numberOfSprings()-1);
                        ss.col = strokeLink;
                        in = -1;
                        out = -1;

                    }
                    in = -1;
                    out = -1;
                    physics.setSelectedNode(null);

                    break;

            }

        }


        // +++++++++++++++++++++++++++++++ delete Link

        if (keyValue == 5) {
            // physics.setSelectedLink(null);
            switch (id) {

                case MouseEvent.MOVE:

                    float dataMin = MAX_FLOAT;
                    int indexLine = 0;

                    for (int i = 0; i < physics.numberOfSprings(); i++) {
                        float dista = (float) DistancePointSegment
                                .distanceToSegment(mouseX, mouseY, physics
                                                .getSpring(i).getOneEnd().position()
                                                .x(), physics.getSpring(i).getOneEnd()
                                                .position().y(), physics.getSpring(i)
                                                .getTheOtherEnd().position().x(),
                                        physics.getSpring(i).getTheOtherEnd()
                                                .position().y());
                        if (dista < dataMin) {
                            dataMin = dista;
                            indexLine = i;
                            if (dataMin < 40)
                                physics.setSelectedLink(physics
                                        .getSpring(indexLine));
                            else
                                physics.setSelectedLink(null);
                        }
                    }

                    break;

                case MouseEvent.PRESS:
                    Spring sp = physics.getSelectedLink();
                    if (sp != null) {
//				    Particle sp1 = sp.getOneEnd();
//				    Particle sp2 = sp.getTheOtherEnd();
//					Particle a;
//					Particle b;
//					for (int i = 0; i < physics.numberOfAttractions(); i++) {
//					Attraction atr =	physics.getAttraction(i);
//					a = atr.getOneEnd();
//					b = atr.getTheOtherEnd();
//					if ((sp1 == a ) || (sp2 == b) || (sp2 == a ) || (sp1 == b)) physics.removeAttraction(atr);
//					}


                        physics.removeSpring(sp);
                    }
                    physics.setSelectedLink(null);
                    break;

            }
        }

        // +++++++++++++++++++++++++++++++++ delete Node
        if (keyValue == 6) {
            switch (id) {

                case MouseEvent.MOVE:

                    selectNode(mouseX, mouseY);

                    break;

                case MouseEvent.PRESS:
                    Particle n = physics.getSelectedNode();
                    if (n != null) {
                        for (int j = 0; j < physics.numberOfSprings(); j++) {
                            for (int i = 0; i < physics.numberOfSprings(); i++) {
                                Spring sp = physics.getSpring(i);
                                Particle p1 = sp.getOneEnd();
                                Particle p2 = sp.getTheOtherEnd();
                                if (n == p1 || n == p2)
                                    physics.removeSpring(i);
                            }
                        }
                        n.reset();
                        physics.removeParticle(n);
                    }
                    break;
            }
        }

        // +++++++++++++++++++++++++++++++ create Text for node
/*        if (createText == true && drawMode == false ) {
            switch (mouseEvent.getID()) {

                case MouseEvent.MOUSE_PRESSED:
                    physics.setSelectedNode(null);
                    physics.setSelectedLink(null);
                    selectNode(mouseX, mouseY);
                    break;

                case MouseEvent.MOUSE_RELEASED:
                    break;
            }
        }*/


        // ++++++++++++++++++++++++++++++++++ select Link
        if ((keyValue == 7)) {
            // physics.setSelectedLink(null);
            switch (id) {
                case MouseEvent.PRESS:
                    //if (changeColor == false)
                {
                    physics.setSelectedLink(null);
                    physics.setSelectedNode(null);
                }
                //else physics.setSelectedNode(null);

                float dataMin = MAX_FLOAT;
                int indexLine = 0;

                for (int i = 0; i < physics.numberOfSprings(); i++) {
                    float dista = (float) DistancePointSegment
                            .distanceToSegment(mouseX, mouseY, physics
                                            .getSpring(i).getOneEnd().position()
                                            .x(), physics.getSpring(i).getOneEnd()
                                            .position().y(), physics.getSpring(i)
                                            .getTheOtherEnd().position().x(),
                                    physics.getSpring(i).getTheOtherEnd()
                                            .position().y());
                    if (dista < dataMin) {
                        dataMin = dista;
                        indexLine = i;
                        if (dataMin < 40) {

                            physics.setSelectedLink(physics
                                    .getSpring(indexLine));
                        } else {
                            //if (changeColor == false)
                            {
                                physics.setSelectedLink(null);
                                physics.setSelectedNode(null);
                            }
                        }
                    }
                }

                break;

                case MouseEvent.RELEASE:
                    // if (createTextLink == false)
                {
                    //     if (changeColor == false)
                    physics.setSelectedLink(null);
                }
                break;
            }
        }

        // ++++++++++++++++++++++++ freeze Node
        if (keyValue == 9) {
            switch (id) {
                case MouseEvent.PRESS:
                    selectNode(mouseX, mouseY);

                    if (physics.getSelectedNode() != null){
                        //physics.getSelectedNode();
                        if (physics.getSelectedNode().isFree())
                        {
                            physics.getSelectedNode().makeFixed();
                        }
                        else physics.getSelectedNode().makeFree();
                    }


                    break;
                //case MouseEvent.RELEASE:
                //if (changeColor == false) physics.setSelectedNode(null);
                //break;

            }
        }

        // ++++++++++++++++++++++++ add site boundary
        if (keyValue == 11) {
            switch (id) {
                case MouseEvent.PRESS:
                    if   (mouseX>120)
                        site.addVertex(mouseX, mouseY);


                    break;

            }
        }

        //++++++++++++++++++++++++++++ change  node size
        if (keyValue == 12) {
            switch (id) {
                case MouseEvent.PRESS:
                    selectNode(mouseX, mouseY);

                    if (physics.getSelectedNode() != null){
                        //physics.getSelectedNode();
                        //if (physics.getSelectedNode().isFree())
                        {
                            //    physics.getSelectedNode().makeFixed();
                        }
                        //else physics.getSelectedNode().makeFree();
                    }

                    break;

            }
        }
//++++++++++++++++++++++++++++ create cells
        if (keyValue == 13) {
            switch (id) {
                case MouseEvent.RELEASE:
                    addCells();
                    break;

            }
        }

        //++++++++++++++++++++++++++++ pick site node
        if (keyValue == 14) {
            // int nodeInt = -1;
            switch (id) {
                //nodeInt = -1;
                case MouseEvent.MOVE:
                case MouseEvent.PRESS:
                    for  (int i=0; i<site.vertices.size();i++){
                        float distance = dist(site.vertices.get(i).x,site.vertices.get(i).y, mouseX, mouseY);
                        if (distance<=10) nodeInt = i;
                    }
                    break;
                //println(nodeInt);

                case MouseEvent.DRAG:
                    println(nodeInt);
                    if ( nodeInt != -1 && mouseX>120) {
                        site.vertices.get(nodeInt).set(gridSnap(mouseX, gstep), gridSnap(mouseY, gstep));
                        //site.sortVertices();
                        site.updatePolygon();
                        site.updateCentroid();
                    }
                    break;

                case MouseEvent.RELEASE:
                    nodeInt = -1;
                    break;
            }
        }


        // +++++++++++++++++++++++++++++++++ delete site node
        if (keyValue == 15) {
            switch (id) {

                case MouseEvent.MOVE:
                    for  (int i=0; i<site.vertices.size();i++){
                        float distance = dist(site.vertices.get(i).x,site.vertices.get(i).y, mouseX, mouseY);
                        if (distance<=10) nodeInt = i;
                    }
                    break;

                case MouseEvent.PRESS:
                    if (nodeInt != -1) {
                        site.vertices.remove(nodeInt);
                        site.sortVertices();
                        site.updatePolygon();
                        site.updateCentroid();
                    }
                    break;
            }
        }


    }

    public void keyReleased() {

        if (key == '1') { keyValue = 1; option1.setSelected(true);}   //drawNode
        if (key == '2')  {keyValue = 2; option2.setSelected(true);}//selectNode
        if (key == '3')  {keyValue = 3; option3.setSelected(true);}//pickNode
        if (key == '4')  {keyValue = 4; option4.setSelected(true);}//createLink
        if (key == '5')  {keyValue = 5; option5.setSelected(true);}//delete link
        if (key == '6')  {keyValue = 6; option6.setSelected(true);}//delete node
        //if (key == 'c')  keyValue = 8; // run cells
        if (key == 'f')  {keyValue = 9; option10.setSelected(true);}// freeze node

        if (key == 'v')  {keyValue = 11; option7.setSelected(true);} // add site vertex
        if (key == 'p')  keyValue = 12; // switch physics on/off
        if (key == 'c')  keyValue = 13; // create cells
        if (key == 'b')  {keyValue = 14;option8.setSelected(true);} // select/move site vertex
        if (key == 'n')  {keyValue = 15;option9.setSelected(true);} // delete site vertex
        if (key == 's')  keyValue = 16; // save image AI
        if (key == 'l')  keyValue = 17; // links on/off
        if (key == 'a')  keyValue = 18; // load AI image
        if (key == 'q')  keyValue = 19; // show AI image
        if (key == 'm')  keyValue = 20; // scale individual node
        //boolean drawNode = true;
        //boolean selectNode = false;
        //boolean pickNode = false;
        //boolean createLink = false;
        //boolean deleteLink = false;
        //boolean deleteNode = false;
        //boolean selectLink = false;
        //boolean isSelectNode = false;
        if (keyValue == 12) {
            if (tick==true) {
                checkbox1.setSelected(false);
                tick = false;
            }
            else {
                checkbox1.setSelected(true);
                tick = true;
            }
        }
        if (keyValue == 16) { //export image for ai
            exportCanvas();
            pg.save( "D:/GoogleDrive/Conai_AI/trained/layout/" + "input" + ".png");
        }
        //pg.save( "C:/Users/VR/Google Drive/Pix2Pix/Augmented/Combined/output/" + "foo" + ".png");;

        if (keyValue == 17) {
            // if(physics.numberOfSprings()>0)
            {
                for (int i = 0; i < physics.numberOfSprings(); i++) {
                    Spring ss = physics.getSpring(i);
                    if (ss.isOn()) {
                        if(i==0)  checkbox2.setSelected(false);
                        ss.turnOff();
                    }
                    else {
                        if(i==0)  checkbox2.setSelected(true);
                        ss.turnOn();
                    }
                }
            }

        }

        if (keyValue == 18) { // upload ai image
            AIimage = loadImage("D:/GoogleDrive/CONAI_AI/trained/output/" + "foo" + ".png");
        }


    }


    public void boundaries () {

        for (int i = 0; i < physics.numberOfParticles(); i++) {
            Particle p = physics.getParticle(i);

            if (p.position().x() < 20) p.position().setX(20);
            if (p.position().x() > width-20) p.position().setX(width-20);

            if (p.position().y() < 20) p.position().setY(20);
            if (p.position().y() > height-20) p.position().setY(height-20);

            //boolean hit = site.polyCircle(p.position().x(),p.position().y(),p.sizeCircle);
            //println(hit);

            //boolean hit2 = site.p.contains((int)p.position().x(),(int)p.position().y());
            boolean hit2 = true;
            //while(hit2==false)
            {
                for(int m=0; m<10;m++){
                    float xx = p.position().x() + p.realCircle/2 * sin(m*1.0f/10*2*PI);
                    float yy = p.position().y() + p.realCircle/2 * cos(m*1.0f/10*2*PI);
                    //hit2 = site.p.contains((int)p.point[m].x,(int)p.point[m].y);
                    boolean hit22 = site.p.contains((int)xx,(int)yy);
                    if (hit22==false) hit2 = false;
                }
            }
            //if (hit2==true)println("iiiiiiii");

            if (hit2==false){ // bounce from site edges and tries to keep the circles inside site
                PVector a = new PVector(p.position().x(),p.position().y());
                PVector b = new PVector(site.centroid.x,site.centroid.y);
                float d = a.dist(b);
                PVector acceleration = PVector.sub(b,a);
                acceleration.setMag(1f);

                p.velocity().setX(acceleration.x);//.add(acceleration.x,acceleration.y,0);//.setX(p.velocity().x()-site.centroid.x);
                p.velocity().setY(acceleration.y);

                //fill(0,200,0);
                //ellipse(p.position().x(), p.position().y(), p.sizeCircle, p.sizeCircle);
            }

        }




    }

    void addCells(){
        cells = new ArrayList<Cell>();
        for (int i=0; i<physics.numberOfParticles();i++) {
            cells.add(new Cell(this));
            cells.get(i).addCell(physics.getParticle(i).position().x(),physics.getParticle(i).position().y(),10, (int) random(10),numEdges, physics.getParticle(i).area);
            // if (i == physics.numberOfParticles()  -2 ) println(cells.size());
        }
        println(cells.size());
    }

    void checkCollision(Particle first, Particle other) {

        // Get distances between the balls components
        PVector firstP = new PVector(first.position().x(),first.position().y(),first.position().z());
        PVector otherP = new PVector(other.position().x(),other.position().y(),other.position().z());
        PVector distanceVect = PVector.sub(otherP, firstP);

        // Calculate magnitude of the vector separating the balls
        float distanceVectMag = distanceVect.mag();

        // Minimum distance before they are touching
        float minDistance = first.realCircle/2 + other.realCircle/2;

        if (distanceVectMag < minDistance) {
            float distanceCorrection = (minDistance-distanceVectMag)/2.0f;
            PVector d = distanceVect.copy();
            PVector correctionVector = d.normalize().mult(distanceCorrection);
            if (other.isFixed()==false) other.position().add(correctionVector.x,correctionVector.y,correctionVector.z);
            if (first.isFixed()==false) first.position().subtract(correctionVector.x,correctionVector.y,correctionVector.z);

            // get angle of distanceVect
            float theta  = distanceVect.heading();
            // precalculate trig values
            float sine = sin(theta);
            float cosine = cos(theta);

      /* bTemp will hold rotated ball positions. You
       just need to worry about bTemp[1] position*/
            PVector[] bTemp = {
                    new PVector(), new PVector()
            };

      /* this ball's position is relative to the other
       so you can use the vector between them (bVect) as the
       reference point in the rotation expressions.
       bTemp[0].position.x and bTemp[0].position.y will initialize
       automatically to 0.0, which is what you want
       since b[1] will rotate around b[0] */
            bTemp[1].x  = cosine * distanceVect.x + sine * distanceVect.y;
            bTemp[1].y  = cosine * distanceVect.y - sine * distanceVect.x;

            // rotate Temporary velocities
            PVector[] vTemp = {
                    new PVector(), new PVector()
            };

            vTemp[0].x  = cosine * first.velocity().x()   + sine * first.velocity().y();
            vTemp[0].y  = cosine * first.velocity().y() - sine * first.velocity().x();
            vTemp[1].x  = cosine * other.velocity().x() + sine * other.velocity().y();
            vTemp[1].y  = cosine * other.velocity().y() - sine * other.velocity().x();

      /* Now that velocities are rotated, you can use 1D
       conservation of momentum equations to calculate
       the final velocity along the x-axis. */
            PVector[] vFinal = {
                    new PVector(), new PVector()
            };

            float m1 = first.realCircle/2 * 0.01f;
            float m2 = other.realCircle/2 * 0.01f;

            // final rotated velocity for b[0]
            vFinal[0].x = ((m1 - m2) * vTemp[0].x + 2 * m2 * vTemp[1].x) / (m1 + m2);
            vFinal[0].y = vTemp[0].y;

            // final rotated velocity for b[0]
            vFinal[1].x = ((m2 - m1) * vTemp[1].x + 2 * m1 * vTemp[0].x) / (m1 + m2);
            vFinal[1].y = vTemp[1].y;

            // hack to avoid clumping
            bTemp[0].x += vFinal[0].x;
            bTemp[1].x += vFinal[1].x;

      /* Rotate ball positions and velocities back
       Reverse signs in trig expressions to rotate
       in the opposite direction */
            // rotate balls
            PVector[] bFinal = {
                    new PVector(), new PVector()
            };

            bFinal[0].x = cosine * bTemp[0].x - sine * bTemp[0].y;
            bFinal[0].y = cosine * bTemp[0].y + sine * bTemp[0].x;
            bFinal[1].x = cosine * bTemp[1].x - sine * bTemp[1].y;
            bFinal[1].y = cosine * bTemp[1].y + sine * bTemp[1].x;

            // update balls to screen position
            // other.position().x() = first.position().x() + bFinal[1].x;
            // other.position().y() = first.position().y() + bFinal[1].y;

            if (first.isFixed()==false)  first.position().add(bFinal[0].x,bFinal[0].y,bFinal[0].z);

            // update velocities
            first.velocity().set(cosine * vFinal[0].x - sine * vFinal[0].y,cosine * vFinal[0].y + sine * vFinal[0].x,0);
            //velocity.y = cosine * vFinal[0].y + sine * vFinal[0].x;
            other.velocity().set(cosine * vFinal[1].x - sine * vFinal[1].y,
                    cosine * vFinal[1].y + sine * vFinal[1].x,0);
            //other.velocity.x = cosine * vFinal[1].x - sine * vFinal[1].y;
            //other.velocity.y = cosine * vFinal[1].y + sine * vFinal[1].x;
        }
    }

    void adjustCorners () { //adjust cell edges interaction

        float vx, vy;

        for (int j=0; j<cells.size(); j++){
            for (int i=0; i<cells.get(j).point.size(); i++) {

                PVector k = cells.get(j).point.get(i);
                PVector k1 = cells.get(j).point.get((cells.get(j).point.size()+i+1) % cells.get(j).point.size());;//first   // tady mozna problem s %
                PVector k0 = cells.get(j).point.get((cells.get(j).point.size()+i-1) % cells.get(j).point.size());;//second

                vx = ((k.x - k1.x) + (k0.x - k.x));//vector projecting clockwise
                vy = ((k.y - k1.y) + (k0.y - k.y));

                float d = dist (0,0,vx,vy) + 0.001f;
                cells.get(j).velocity.get(i).x += vy/d;
                cells.get(j).velocity.get(i).y -= vx/d; // normalize and rotate 90 degr. == projects out

                cells.get(j).velocity.get(i).x += (random(1) * 0.1f - 0.05f);// movement
                cells.get(j).velocity.get(i).y += (random(1) * 0.1f - 0.05f);
                cells.get(j).normalovat (i);//add random factor and normalize

                for ( int r=0; r<cells.size(); r++ )
                    if (!(r==j))
                        for (int s=0; s<cells.get(r).point.size(); s++) {
                            float factor = 1;
                            PVector p = cells.get(r).point.get(s);
                            PVector p2 = cells.get(r).point.get((s+1) % cells.get(r).point.size());

                            boolean intersect = false;
                            float a = (p2.x - p.x);
                            if (a==0) a = 0.001f;
                            a = (p2.y - p.y)/a;
                            float b = p.y - a*p.x;

                            if (cells.get(j).velocity.get(i).x==0) cells.get(j).velocity.get(i).x=0.001f;
                            float c = cells.get(j).velocity.get(i).y/cells.get(j).velocity.get(i).x;
                            d = k.y - c*k.x;

                            PVector intersection = new PVector();
                            intersection.x = (d-b) / (a-c);
                            intersection.y = intersection.x*c + d;

                            if ((abs(intersection.x-p2.x)<abs(p.x-p2.x)) &&
                                    (abs(intersection.x-p.x)<abs(p.x-p2.x))     )
                                intersect = true;

                            float pvy =  (p2.x - p.x); //perpendicular vector
                            float pvx = -(p2.y - p.y);
                            d= dist (0,0,pvx,pvy);
                            pvx/=d;
                            pvy/=d;

                            float d2 =  dist(intersection.x,intersection.y,k.x,k.y);//*/
                            ///*
                            if ((intersect) && (d2<bounceFact))
                            {
                                stroke(0);

                                ellipse(intersection.x,intersection.y,1,1);

                                float ax=(cells.get(j).velocity.get(i).x + a*cells.get(j).velocity.get(i).y)/(1+a*a);

                                float tvx = -(-2*ax   + cells.get(j).velocity.get(i).x); //bounce direction
                                float tvy = -(-2*a*ax + cells.get(j).velocity.get(i).y);
                                d = dist (0,0,tvx,tvy);
                                tvx/=d;
                                tvy/=d;
                                tvx=bounceFact*tvx;
                                tvy=bounceFact*tvy;

                                stroke(0,255,0);
                                line ( intersection.x, intersection.y, intersection.x+tvx*1, intersection.y+tvy*1 );

                                cells.get(j).velocity.get(i).x+=pvx*(bounceFact-d2)*factor;
                                cells.get(j).velocity.get(i).y+=pvy*(bounceFact-d2)*factor;

                                //if (r>0) //push edges out
                                {
                                    cells.get(r).calcArea();
                                    if (cells.get(r).realArea<=cells.get(r).targetArea) {
                                        float ff = 0.5f;
                                        cells.get(r).point.get(s).x -= pvx * (bounceFact - d2) * factor * ff;
                                        cells.get(r).point.get(s).y -= pvy * (bounceFact - d2) * factor * ff;
                                        cells.get(r).point.get((s + 1) % cells.get(r).point.size()).x -= pvx * (bounceFact - d2) * factor * ff;
                                        cells.get(r).point.get((s + 1) % cells.get(r).point.size()).y -= pvy * (bounceFact - d2) * factor * ff;
                                    }
                                }
                            }
                        }
                if ( (cells.get(j).point.get(i).x > 10)) cells.get(j).move(i);
                cells.get(j).normalovat(i);

            }
        }


    }


    void smoothEdges()
    {

        for (int i=0;i<cells.size();i++)
        {
            for (int j=0;j<cells.get(i).point.size();j++) {
                cells.get(i).point2.get(j).x = cells.get(i).point.get(j).x;
                cells.get(i).point2.get(j).y = cells.get(i).point.get(j).y;
            }
        }

        for (int j=0;j<cells.size();j++)
            for (int i=0;i<cells.get(j).point.size();i++)
            {
                PVector k = cells.get(j).point2.get(i);
                PVector k1 = cells.get(j).point2.get((cells.get(j).point.size()+i+1) % cells.get(j).point.size());
                PVector k0 = cells.get(j).point2.get((cells.get(j).point.size()+i-1) % cells.get(j).point.size());

                cells.get(j).point.get(i).x = (k.x*8+k1.x+k0.x)/10.0f;
                cells.get(j).point.get(i).y = (k.y*8+k1.y+k0.y)/10.0f;
            }
    }

    float gridSnap(float input, float step){
        input += (step*0.49f); // change snap-down to snap-closest
        float snapped = input - (input % step);
        return snapped;
    }

    void gridLines(){
        strokeWeight(0.5f);
        stroke(255,0,255,80);
        for(int x=0; x<=width; x+=gstep) line(x,0,x,width);
        for(int y=0; y<=height; y+=gstep) line(0,y,height,y);
    }

    void exportCanvas() {
        pg.beginDraw();
        pg.background(255);
        pg.fill(0);
        pg.noStroke();
        pg.beginShape();
        for (int i = 0; i<site.vertices.size();i++) {
            pg.vertex(scalePG*site.vertices.get(i).x, scalePG*site.vertices.get(i).y);
        }
        pg.endShape();
        pg.endDraw();
    }

    void uploadGraph()
    {
        rooms = new ArrayList<String>();
        roomColor = new ArrayList<Integer>();
        nodesNames = new ArrayList<String>();
        adjStrList = new ArrayList<String>();// { "1-2", "3-0", "2-4" };//*
        roomAreas = new ArrayList<Integer>();
        roomSize = new ArrayList<String>();
        adjRoomList = new ArrayList<int[]>();// { "1-2", "3-0", "2-4" };//*
        int entranceNodeId = -1;
        int entranceRoom = -1;
        // if (loadGraph == true)
        {
            //xml = loadXML("test_office.xml");
            xml = loadXML("Bulldog.xml");
            XML[] children = xml.getChildren("li");
            String name = "node";

            for (int i = 0; i < children.length; i++) {
                String type = children[i].getString("class");

                if (type.length() == 4) {
                    int id = children[i].getInt("data-node-id");
                    XML firstChild = (children[i].getChild("span"));
                    String temp = firstChild.getContent();

                    if (temp.equals("Exterior") == false)
                    {
                        rooms.add(Integer.toString(id));
                        nodesNames.add(firstChild.getContent());
                    }
                    else entranceNodeId = id;

                }
                if (type.length() == 12) {
                    int from = children[i].getInt("data-from");
                    int to = children[i].getInt("data-to");
                    if (from == entranceNodeId) entranceRoom = to;
                    else if (to == entranceNodeId) entranceRoom = from;
                    if (from !=entranceNodeId && to !=entranceNodeId) {
                        for (int j = 0; j< rooms.size(); j++)
                        {
                            if (Integer.parseInt(rooms.get(j)) == from) from = j+1;
                            if (Integer.parseInt(rooms.get(j)) == to) to = j+1;
                            if(entranceRoom == Integer.parseInt(rooms.get(j))) {
                                entranceRoom = j+1;
                            }
                        }
                        String spojeni = new String (from +"-" + to);
                        adjStrList.add(spojeni);
                        adjRoomList.add(new int[]{from,to});
                    }
                }
            }

            for (int j = 0; j< rooms.size(); j++)
            {
                rooms.set(j,Integer.toString(j));
                roomAreas.add(4);
            }
            //+++++++++++ end load graph
            //println(entranceRoom);
            println(adjRoomList.get(0));
            println(adjStrList);
            //println(roomAreas);
            println(nodesNames);
        }

        physics.clear();
        for (int i = 0; i<nodesNames.size(); i++) {

            physics.makeParticle(1f, random(width/2-20,width/2+20 ),  random(height/2-20,height/2+20 ), 0);

            //++++++++++++++++++++ create nodes

            Particle pp = physics.getParticle(physics.numberOfParticles()-1);
            int ind = -1;
            for (int m = 0; m<roomNames.length; m++) {
                if (roomNames[m].equals(nodesNames.get(i))) ind = m;
            }
            pp.sizeCircle = sizeRooms[ind];
            pp.col = coloursRooms[ind];
            pp.scaleSize = scaleRooms;
            pp.theText = roomNames[ind];
            pp.updateArea();

            if (physics.numberOfParticles()>0) {

                for (int j = 0; j < physics.numberOfParticles(); j++) {
                    Particle localParticle = physics.getParticle(j);
                    if (pp != localParticle)
                    {
                        physics.makeAttraction(pp, localParticle,
                                20.0f, 0.1f);
                    }
                }

            }

        }

        //++++++++++++++++++++++++ create links

        for (int i = 0; i < adjRoomList.size(); i++) {
            Particle inn = physics.getParticle(adjRoomList.get(i)[0]-1);
            Particle outt = physics.getParticle(adjRoomList.get(i)[1]-1);
            int restLength = inn.realCircle + outt.realCircle;
            physics.makeSpring(inn, outt, 0.001f, 0.001f, restLength/2);
            Spring ss = physics.getSpring(physics.numberOfSprings()-1);
            ss.theText = String.valueOf(physics.numberOfSprings()-1);
            ss.col = strokeLink;
        }

        for (int i = 0; i < physics.numberOfParticles(); i++) {
            Particle n = physics.getParticle(i);
            if (n.containsPoint(mouseX, mouseY)) {
                physics.setSelectedNode(n);
                out = (i);
            }
        }


    }

    // Create all the GUI controls.
// autogenerated do not edit
    public void createGUI(){
        G4P.messagesEnabled(false);
        G4P.setGlobalColorScheme(GCScheme.BLUE_SCHEME);
        G4P.setMouseOverEnabled(false);
        surface.setTitle("VFI space planner");
        label1 = new GLabel(this, 9, 10, 90, 50);
        label1.setIcon("vfi_sm.png", 1, GAlign.WEST, GAlign.CENTER, GAlign.MIDDLE);
        label1.setTextAlign(GAlign.CENTER, GAlign.MIDDLE);
        label1.setOpaque(true);
        dropList1 = new GDropList(this, 10, 80, 110, 220, 10, 10);
        dropList1.setItems(loadStrings("list_300783"), 0);
        dropList1.addEventHandler(this, "dropList1_click1");
        button1 = new GButton(this, 10, 680, 80, 30);
        button1.setText("load graph");
        button1.addEventHandler(this, "button1_click1");
        button2 = new GButton(this, 10, 760, 80, 30);
        button2.setText("export graph");
        button2.addEventHandler(this, "button2_click1");
        label2 = new GLabel(this, 110, 10, 120, 20);
        label2.setTextAlign(GAlign.CENTER, GAlign.MIDDLE);
        label2.setText("SPACE PLANNER");
        label2.setLocalColorScheme(GCScheme.SCHEME_8);
        label2.setOpaque(false);
        button3 = new GButton(this, 10, 720, 80, 30);
        button3.setText("predict layout");
        button3.addEventHandler(this, "button3_click1");
        checkbox1 = new GCheckbox(this, 10, 440, 110, 20);
        checkbox1.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
        checkbox1.setTextAlign(GAlign.RIGHT, GAlign.MIDDLE);
        checkbox1.setText("physics (p)");
        checkbox1.setLocalColorScheme(GCScheme.SCHEME_8);
        checkbox1.setOpaque(false);
        checkbox1.addEventHandler(this, "checkbox1_clicked1");
        checkbox1.setSelected(true);
        togGroup1 = new GToggleGroup();
        option1 = new GOption(this, 10, 180, 110, 20);
        option1.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
        option1.setTextAlign(GAlign.RIGHT, GAlign.MIDDLE);
        option1.setText("create node (1)");
        option1.setLocalColorScheme(GCScheme.SCHEME_8);
        option1.setOpaque(false);
        option1.addEventHandler(this, "option1_clicked1");
        option2 = new GOption(this, 10, 200, 110, 20);
        option2.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
        option2.setTextAlign(GAlign.RIGHT, GAlign.MIDDLE);
        option2.setText("select node  (2)");
        option2.setLocalColorScheme(GCScheme.SCHEME_8);
        option2.setOpaque(false);
        option2.addEventHandler(this, "option2_clicked1");
        option3 = new GOption(this, 10, 220, 110, 20);
        option3.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
        option3.setTextAlign(GAlign.RIGHT, GAlign.MIDDLE);
        option3.setText("move node  (3)");
        option3.setLocalColorScheme(GCScheme.SCHEME_8);
        option3.setOpaque(false);
        option3.addEventHandler(this, "option3_clicked1");
        option4 = new GOption(this, 10, 240, 110, 20);
        option4.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
        option4.setTextAlign(GAlign.RIGHT, GAlign.MIDDLE);
        option4.setText("create link   (4)");
        option4.setLocalColorScheme(GCScheme.SCHEME_8);
        option4.setOpaque(false);
        option4.addEventHandler(this, "option4_clicked1");
        option5 = new GOption(this, 10, 260, 110, 20);
        option5.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
        option5.setTextAlign(GAlign.RIGHT, GAlign.MIDDLE);
        option5.setText("delete link   (5)");
        option5.setLocalColorScheme(GCScheme.SCHEME_8);
        option5.setOpaque(false);
        option5.addEventHandler(this, "option5_clicked1");
        option6 = new GOption(this, 10, 280, 110, 20);
        option6.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
        option6.setTextAlign(GAlign.RIGHT, GAlign.MIDDLE);
        option6.setText("delete node (6)");
        option6.setLocalColorScheme(GCScheme.SCHEME_8);
        option6.setOpaque(false);
        option6.addEventHandler(this, "option6_clicked1");
        option7 = new GOption(this, 10, 360, 110, 20);
        option7.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
        option7.setTextAlign(GAlign.RIGHT, GAlign.MIDDLE);
        option7.setText("add node (v)");
        option7.setLocalColorScheme(GCScheme.SCHEME_8);
        option7.setOpaque(false);
        option7.addEventHandler(this, "option7_clicked1");
        option8 = new GOption(this, 10, 380, 110, 20);
        option8.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
        option8.setTextAlign(GAlign.RIGHT, GAlign.MIDDLE);
        option8.setText("  move node (b)");
        option8.setLocalColorScheme(GCScheme.SCHEME_8);
        option8.setOpaque(false);
        option8.addEventHandler(this, "option8_clicked1");
        option9 = new GOption(this, 10, 400, 110, 20);
        option9.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
        option9.setTextAlign(GAlign.RIGHT, GAlign.MIDDLE);
        option9.setText("delete node (n)");
        option9.setLocalColorScheme(GCScheme.SCHEME_8);
        option9.setOpaque(false);
        option9.addEventHandler(this, "option9_clicked1");
        option10 = new GOption(this, 10, 300, 110, 20);
        option10.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
        option10.setTextAlign(GAlign.RIGHT, GAlign.MIDDLE);
        option10.setText("freeze node (f)");
        option10.setLocalColorScheme(GCScheme.SCHEME_8);
        option10.setOpaque(false);
        option10.addEventHandler(this, "option10_clicked1");
        option11 = new GOption(this, 10, 320, 110, 20);
        option11.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
        option11.setTextAlign(GAlign.RIGHT, GAlign.MIDDLE);
        option11.setText("scale nod(m(+-))");
        option11.setLocalColorScheme(GCScheme.SCHEME_8);
        option11.setOpaque(false);
        option11.addEventHandler(this, "option11_clicked1");
        togGroup1.addControl(option1);
        option1.setSelected(true);
        togGroup1.addControl(option2);
        togGroup1.addControl(option3);
        togGroup1.addControl(option4);
        togGroup1.addControl(option5);
        togGroup1.addControl(option6);
        togGroup1.addControl(option7);
        togGroup1.addControl(option8);
        togGroup1.addControl(option9);
        togGroup1.addControl(option10);
        label3 = new GLabel(this, 12, 156, 80, 20);
        label3.setText("graph");
        label3.setLocalColorScheme(GCScheme.SCHEME_8);
        label3.setOpaque(false);
        label4 = new GLabel(this, 10, 340, 80, 20);
        label4.setText("site");
        label4.setLocalColorScheme(GCScheme.SCHEME_8);
        label4.setOpaque(false);
        slider1 = new GSlider(this, 10, 580, 110, 40, 10.0f);
        slider1.setShowValue(true);
        slider1.setShowLimits(true);
        slider1.setLimits(10, 1, 20);
        slider1.setNumberFormat(G4P.INTEGER, 0);
        slider1.setLocalColorScheme(GCScheme.GOLD_SCHEME);
        slider1.setOpaque(false);
        slider1.addEventHandler(this, "slider1_change1");
        label5 = new GLabel(this, 10, 560, 80, 20);
        label5.setText("scale units");
        label5.setLocalColorScheme(GCScheme.SCHEME_8);
        label5.setOpaque(false);
        slider2 = new GSlider(this, 10, 520, 110, 40, 10.0f);
        slider2.setShowValue(true);
        slider2.setShowLimits(true);
        slider2.setLimits(1.0f, 1.0f, 10.0f);
        slider2.setNumberFormat(G4P.DECIMAL, 2);
        slider2.setLocalColorScheme(GCScheme.GOLD_SCHEME);
        slider2.setOpaque(false);
        slider2.addEventHandler(this, "slider2_change1");
        label6 = new GLabel(this, 10, 500, 80, 20);
        label6.setText("scale rooms");
        label6.setLocalColorScheme(GCScheme.SCHEME_8);
        label6.setOpaque(false);
        textfield1 = new GTextField(this, 10, 650, 110, 30, G4P.SCROLLBARS_NONE);
        textfield1.setOpaque(true);
        textfield1.addEventHandler(this, "textfield1_change1");
        checkbox2 = new GCheckbox(this, 10, 460, 110, 20);
        checkbox2.setIconAlign(GAlign.LEFT, GAlign.MIDDLE);
        checkbox2.setTextAlign(GAlign.RIGHT, GAlign.MIDDLE);
        checkbox2.setText("links on/off (l)");
        checkbox2.setLocalColorScheme(GCScheme.SCHEME_8);
        checkbox2.setOpaque(false);
        checkbox2.addEventHandler(this, "checkbox2_clicked1");
        checkbox2.setSelected(true);
        Help = GWindow.getWindow(this, "Window title", 0, 0, 240, 120, JAVA2D);
        Help.noLoop();
        Help.setActionOnClose(G4P.KEEP_OPEN);
        Help.addDrawHandler(this, "win_draw1");
        Help.loop();
    }
        /*
        if (key == '1')  keyValue = 1; //drawNode
        if (key == '2')  keyValue = 2; //selectNode
        if (key == '3')  keyValue = 3; //pickNode
        if (key == '4')  keyValue = 4; //createLink
        if (key == '5')  keyValue = 5; //delete link
        if (key == '6')  keyValue = 6; //delete node
        if (key == 'c')  keyValue = 8; // run cells
        if (key == 'f')  keyValue = 9; // freeze node

        if (key == 'v')  keyValue = 11; // add site vertex
        if (key == 'p')  keyValue = 12; // switch physics on/off
        if (key == 'c')  keyValue = 13; // create cells
        if (key == 'b')  keyValue = 14; // select/move site vertex
        if (key == 'n')  keyValue = 15; // delete site vertex
        if (key == 's')  keyValue = 16; // save image AI+
         */


    public void dropList1_click1(GDropList source, GEvent event) { //_CODE_:dropList1:300783:
    } //_CODE_:dropList1:300783:

    public void checkbox1_clicked1(GCheckbox source, GEvent event) { //_CODE_:checkbox1:497579:
        keyValue = 12;
        if ( checkbox1.isSelected()==true) tick = true;
        if ( checkbox1.isSelected()==false) tick = false;
    } //_CODE_:checkbox1:497579:

    public void checkbox2_clicked1(GCheckbox source, GEvent event) { //_CODE_:checkbox2:369901:
        keyValue = 17;
        for (int i = 0; i < physics.numberOfSprings(); i++) {
            Spring ss = physics.getSpring(i);
            if ( checkbox2.isSelected()==true) {
                ss.turnOn();
            }
            if ( checkbox2.isSelected()==false) {
                ss.turnOff();
            }
        }
    } //_CODE_:checkbox2:369901:

    public void option1_clicked1(GOption source, GEvent event) { //_CODE_:option1:954046:
        keyValue = 1;
    } //_CODE_:option1:954046:

    public void option2_clicked1(GOption source, GEvent event) { //_CODE_:option2:981365:
        keyValue = 2;
    } //_CODE_:option2:981365:

    public void option3_clicked1(GOption source, GEvent event) { //_CODE_:option3:540456:
        keyValue = 3;
    } //_CODE_:option3:540456:

    public void option4_clicked1(GOption source, GEvent event) { //_CODE_:option4:288917:
        keyValue = 4;
    } //_CODE_:option4:288917:

    public void option5_clicked1(GOption source, GEvent event) { //_CODE_:option5:247758:
        keyValue = 5;
    } //_CODE_:option5:247758:

    public void option6_clicked1(GOption source, GEvent event) { //_CODE_:option5:247758:
        keyValue = 6;
    } //_CODE_:option5:247758:

    public void option10_clicked1(GOption source, GEvent event) { //_CODE_:option5:247758:
        keyValue = 9;
    } //_CODE_:option5:247758:

    public void option7_clicked1(GOption source, GEvent event) { //_CODE_:option5:247758:
        keyValue = 11;
    } //_CODE_:option5:247758:

    public void option8_clicked1(GOption source, GEvent event) { //_CODE_:option5:247758:
        keyValue = 14;
    } //_CODE_:option5:247758:

    public void option9_clicked1(GOption source, GEvent event) { //_CODE_:option5:247758:
        keyValue = 15;
    } //_CODE_:option5:247758:

    public void option11_clicked1(GOption source, GEvent event) { //_CODE_:option5:247758:
        keyValue = 20;
    } //_CODE_:option5:247758:

    public void slider1_change1(GSlider source, GEvent event) { //_CODE_:slider1:905491:
        scale = slider1.getValueI();
    } //_CODE_:slider1:905491:

    public void slider2_change1(GSlider source, GEvent event) { //_CODE_:slider2:259972:
        scaleRooms = slider2.getValueF();
        for (int i = 0; i < physics.numberOfParticles(); i++) {
            Particle n = physics.getParticle(i);
            n.scaleSize = scaleRooms;
            n.updateArea();
        }
    } //_CODE_:slider2:259972:

    public void button1_click1(GButton source, GEvent event) { //_CODE_:button1:592021:
        uploadGraph();
    } //_CODE_:button1:592021:

    void updateView1() {
        // ############################################################
        // Get the graphics context and camera
        PGraphics pg = view1.getGraphics();
        PeasyCam pcam = view1.getPeasyCam();
        //pcam.lookAt(site.centroid.x,site.centroid.y,site.centroid.z);
        //println(pcam.getLookAt());
        // ############################################################
        // Initialise the canvas
        pg.beginDraw();
        pg.resetMatrix();
        // ############################################################
        // World view lighting here (optional)
        pg.ambientLight(100, 100, 100);
        pg.directionalLight(220, 220, 0, 0.8f, 1, -1.2f);

        // ############################################################
        // set model view - using camera state
        pcam.feed();

        // ############################################################
        // Model view lighting here (optional)

        // ############################################################
        // Code to draw canvas
        pg.background(0, 0, 0);

        //pg.fill(255, 200, 128);
        //pg.stroke(255, 0, 0);
        //pg.strokeWeight(4);
        //pg.box(80);

        // ############################################################
        //+++++++++++++++++++++++++++++++++++++++++++++++
        // draw connectivy graph 3d
        {
            pg.scale(0.1f);
            //+++++++++++++++++++++++++++ draw nodes + text nodes
            Particle part;
            for (int i = 0; i < physics.numberOfParticles(); i++) {
                part = physics.getParticle(i);
                part.position().setZ(0);
                part.circlePoints();

                {
                    pg.strokeWeight(part.strW);
                    //fill((part.col),part.transp);
                    pg.fill((part.col),80);
                    pg.stroke((part.str));
                }

                pg.pushMatrix();
                //pg.translate(site.centroid.x,site.centroid.y);
                //pg.stroke(255);
                //pg.line(part.position().x(), part.position().y(),part.position().z(),0,0,0);
                //pg.scale(0.1f);
                pg.translate(part.position().x(), part.position().y(),part.position().z());
                //println(part.position().x() + " " + part.position().y() + part.position().y());
                //+++ellipse property start
                // fill(0,0,255); // node not selected in 2d
                //strokeWeight(1f);
                //stroke(255,255,0);
                if (part.isFree()==false){
                    //fill(255,80);
                    pg.stroke(0,255,240,80);
                    pg.strokeWeight(2);
                    //ellipse(0, 0, (part.realCircle +5), (part.realCircle +5));
                }
                pg.fill(255,80);
                //pg.box(80);
                pg.ellipse(0, 0, part.realCircle, part.realCircle);
                pg.fill((part.col),80);
                //pg.box(80);
                pg.ellipse(0, 0, part.realCircle, part.realCircle);
                //+++ellipse property end
                if (part == physics.getSelectedNode()) {
                    pg.fill(255,0,0);
                    pg.strokeWeight(0.1f);
                    pg.stroke(0);
                    pg.ellipse(0, 0, part.realCircle/2, part.realCircle/2);
                }
                //  /*  if (drawText)
                {
                    //textFont(font, 14);
                    pg.textAlign(CENTER, CENTER);
                    pg.fill(textCol);
                    pg.text(part.theText, 0, 0);
                    pg.text(part.area/(scale*scale), 0, 10);
                    //text((int)part.position().x() + "," + (int)part.position().y() + "," + (int)part.position().z(), 0, -30);
                }// */

                pg.popMatrix();

            }
//+++++++++++++++++++++++++++++++++++++++++++++++++


            pg.fill(0, 80.0F);
            pg.noStroke();
            //stroke(fg);
            pg.strokeWeight(5.0F);

            pg.noFill();
            int check = 0;

            // +++++++++++++ draw Links
            Particle sel = physics.getSelectedNode();
            for (int i = 0; i < physics.numberOfSprings(); i++) {
                Spring link = physics.getSpring(i);
                Particle one = link.getOneEnd();
                Particle two = link.getTheOtherEnd();
                pg.stroke((link.col),link.transp);
                if (sel == one || sel == two) {
                    //  if (!freeze)
                    //      strokeWeight(10);
                    // else
                    pg.strokeWeight(5);
                }
                else
                {
                    float weight = (float) (1 + (link.currentLength()- link.restLength()) * 0.5);
                    if (weight  < 1) weight = 1;
                    pg.strokeWeight(1);
                }

                pg.line(link.getOneEnd().position().x(), link.getOneEnd()
                        .position().y(), link.getTheOtherEnd().position()
                        .x(), link.getTheOtherEnd().position().y());

                if (link.isFixed()) {
                    pg.stroke (0,210,250,20);
                    pg.strokeWeight(25);
                    pg.line(link.getOneEnd().position().x(), link.getOneEnd()
                            .position().y(), link.getTheOtherEnd().position()
                            .x(), link.getTheOtherEnd().position().y());
                }


            }
            if (physics.getSelectedLink() != null) {
                Spring picked = physics.getSelectedLink();
                //if (freeze)strokeWeight(5);
                //else
                pg.strokeWeight(20);
                pg.stroke(255,0,0,80);
                pg.line(picked.getOneEnd().position().x(), picked.getOneEnd()
                        .position().y(), picked.getTheOtherEnd().position()
                        .x(), picked.getTheOtherEnd().position().y());
            }
            // ++++++++++++++++

            //stroke(bg);
            pg.strokeWeight(1.0F);

            pg.noTint();
            pg.noFill();
            pg.stroke(255,0,0,80);
            //if (freeze)strokeWeight(5);
            //else
            pg.strokeWeight(15.0F);

            if (in >= 0 && keyValue==4) {

                pg.line(physics.getParticle(in).position().x(), physics
                        .getParticle(in).position().y(), mouseX, mouseY);
            }

        }






        //++++++++++++++++++++++++++++++++++++++++++++++++
        // Demonstrates use of the PeaseyCam HUD feature
        pcam.beginHUD();
        pg.rectMode(CORNER);
        pg.noStroke();
        //pg.fill(0);
        //pg.rect(0, 0, view1.width(), 30);
        pg.fill(255, 255, 0);
        pg.textSize(18);
        pg.textAlign(CENTER, CENTER);
        //pg.text("Using Worldview lighting", 0, 0, view1.width(), 30);
        pcam.endHUD();

        // ############################################################
        // We are done!!!
        pg.endDraw();
    }
}
