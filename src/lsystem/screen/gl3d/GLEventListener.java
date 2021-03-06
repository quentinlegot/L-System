package lsystem.screen.gl3d;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import lsystem.engine.Element;
import lsystem.engine.ElementProperties;
import lsystem.screen.Constants;

/**
 * use to draw the 3d scene
 * @see AbstractListener
 */
public class GLEventListener extends AbstractListener {

    private final float[] light_0_position = {1000f, 1000f, 1000f, 1f};


    public GLEventListener(GLCanvas swingGLCanvas) {
        super(swingGLCanvas);
    }


    /**
     * init and enable openGL functionalities
     * @param glAutoDrawable openGL drawable surface
     */
    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();

        gl.glClearColor(0f, 0f, 0f, 1.0f);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glEnable(GL2.GL_COLOR_MATERIAL);

        gl.glClearDepth(1.0f);
        gl.glShadeModel(GL2.GL_SMOOTH);

        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, Constants.light_0_ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, Constants.light_0_diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, Constants.light_0_specular, 0);

        gl.glDepthFunc(GL2.GL_LEQUAL);
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
        super.init(glAutoDrawable);
        firstGen = true;
    }

    /**
     * call at each frame (without pause if fps are lower than you screen refresh rate)
     * @param glAutoDrawable openGL drawable surface
     */
    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        for (int i = 0; i < canvas.camera.length; i++) {
            canvas.camera[i] = canvas.camera[i] % 360;
        }
        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(0, 0, 0);
        DrawHelper.placeCamera(gl, canvas);
        glu.gluLookAt(canvas.camera[0], canvas.camera[1], canvas.camera[2], canvas.camera[0], canvas.camera[1], canvas.camera[2] - 1, 0f, 1f, 0f);
        gl.glPushMatrix();
        gl.glRotatef(90f, -1f, 0f, 0f);
        gl.glColor3f(0f, 1f, 0f);
        drawLSystem(gl, canvas.getLSystem());
        firstGen = false;
        gl.glPopMatrix();

        DrawHelper.drawAxes(gl, glut);
        // DrawHelper.drawDebugInformation(gl, glut, canvas);
        gl.glFlush();
        fps++;
    }


    /**
     * draw the L-System, move the camera and then use recursive call to draw branches of {@code element}
     * @param gl use to move cursor
     * @param element working branch of the {@link Element LSystem}
     */
    @Override
    public void drawLSystem(GL2 gl, Element element) {
        gl.glPushMatrix();
        gl.glRotatef(element.rotation[0]  * 360, 1f, 0f, 0f);
        gl.glRotatef(element.rotation[1]  * 360, 0f, 1f, 0f);
        gl.glRotated(-Math.sin(element.rotation[0]) * 180 - Math.sin(element.rotation[1]) * 180, 0f, 0f, 1f);
        gl.glTranslated(-Math.sin(element.rotation[0]), -Math.sin(element.rotation[1]), -Math.sin(element.rotation[0] + element.rotation[1]));

        if(element.property == ElementProperties.DRAW) {
            if(firstGen) {
                canvas.camera[1] = (float) Math.min(50f, canvas.camera[1] + 0.10f-Math.sin(element.rotation[1]));
                canvas.camera[2] = Math.min(50f, canvas.camera[2] + 0.10f);
            }
            glut.glutSolidCylinder(0.25f, 1f, 10, 10);
            gl.glTranslatef(0f, 0f, 1f);
        }

        for(Element child : element.children) {
            drawLSystem(gl, child);
        }
        gl.glPopMatrix();
    }


    /**
     * call when window is resized or moved
     * @see AbstractListener#reshape(GLAutoDrawable, int, int, int, int)
     * @param glAutoDrawable openGL drawable surface
     * @param width window width
     * @param height window height
     */
    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
        super.reshape(glAutoDrawable, x, y, width, height);
        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_0_position, 0);
        gl.glColorMaterial(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE);
        gl.glMateriali(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 90);
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, Constants.material_specular, 0);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }
}
