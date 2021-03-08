package lsystem.screen.gl2d;

import com.jogamp.opengl.glu.GLU;

import java.awt.*;
import java.awt.event.*;

public class JoglMouseListener2D implements MouseListener, MouseMotionListener, MouseWheelListener {

    private final GLU glu;
    private final SwingGLCanvas2D canvas;
    private int button = 0;
    private Point origine;

    public JoglMouseListener2D(SwingGLCanvas2D canvas) {
        this.canvas = canvas;
        this.glu = canvas.glu;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(button == 0) {
            button = e.getButton();
            origine = e.getPoint();
        } else {
            button = 0;
            origine = null;
        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        button = 0;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
        System.out.println("exited " + canvas.camera[0] + ", " + canvas.camera[1]);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(origine != null) {
            double xDiff = origine.getX() - e.getPoint().getX();
            double yDiff = origine.getY() - e.getPoint().getY();
            /* if(button == 2) {
                canvas.camera[0] += Math.cos(canvas.camera[3]) * xDiff * 0.01;
                canvas.camera[1] += Math.cos(canvas.camera[4]) * yDiff * 0.01;
                canvas.camera[2] += Math.sin(canvas.camera[3]) * xDiff * 0.01;
            } */
            if(button == 3) {
                canvas.camera[3] += xDiff * 0.1;
                canvas.camera[4] += yDiff * 0.1;
            }
            origine = e.getPoint();
        }
        for (int i = 0; i < canvas.camera.length; i++) {
            canvas.camera[i] = canvas.camera[i] % 360;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if( 45 > Math.abs(canvas.camera[3]) && 45>Math.abs(canvas.camera[4]))
            canvas.camera[0] += e.getWheelRotation() * Math.tan(Math.toRadians(-canvas.camera[3])) * 0.25;
        else
            canvas.camera[0] += e.getWheelRotation() * Math.tan(Math.toRadians(canvas.camera[3])) * 0.25;
        canvas.camera[1] += e.getWheelRotation() * Math.tan(Math.toRadians(canvas.camera[4])) * 0.25;
        if( 45 < Math.abs(canvas.camera[3]) || 45 < Math.abs(canvas.camera[4]))
            canvas.camera[2] += -e.getWheelRotation()*0.25;
        else
            canvas.camera[2] += e.getWheelRotation()*0.25;

    }
}