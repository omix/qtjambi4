/****************************************************************************
**
** Copyright (C) 1992-2009 Nokia. All rights reserved.
** Copyright (C) 2013 Peter Droste. All rights reserved.
**
** This file is part of Qt Jambi.
**
** ** $BEGIN_LICENSE$
** Commercial Usage
** Licensees holding valid Qt Commercial licenses may use this file in
** accordance with the Qt Commercial License Agreement provided with the
** Software or, alternatively, in accordance with the terms contained in
** a written agreement between you and Nokia.
** 
** GNU Lesser General Public License Usage
** Alternatively, this file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
** 
** In addition, as a special exception, Nokia gives you certain
** additional rights. These rights are described in the Nokia Qt LGPL
** Exception version 1.0, included in the file LGPL_EXCEPTION.txt in this
** package.
** 
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
** 
** If you are unsure which license is appropriate for your use, please
** contact the sales department at qt-sales@nokia.com.
** $END_LICENSE$

**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.demos.qt3d.cubehouse;

import static javax.media.opengl.GL.GL_BACK;
import static javax.media.opengl.GL.GL_BLEND;
import static javax.media.opengl.GL.GL_CULL_FACE;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_FRONT;
import static javax.media.opengl.GL.GL_TEXTURE_2D;

import javax.media.opengl.GL;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLProfile;

import com.trolltech.qt.QtBlockedSlot;
import com.trolltech.qt.QtPropertyReader;
import com.trolltech.qt.QtPropertyWriter;
import com.trolltech.qt.core.QByteArray;
import com.trolltech.qt.core.QPropertyAnimation;
import com.trolltech.qt.core.QTime;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QImage;
import com.trolltech.qt.gui.QMatrix4x4;
import com.trolltech.qt.gui.QVector3D;
import com.trolltech.qt.gui.QVector4D;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.qt3d.QGL;
import com.trolltech.qt.qt3d.QGLBuilder;
import com.trolltech.qt.qt3d.QGLCamera;
import com.trolltech.qt.qt3d.QGLCube;
import com.trolltech.qt.qt3d.QGLLightModel;
import com.trolltech.qt.qt3d.QGLLightParameters;
import com.trolltech.qt.qt3d.QGLMaterial;
import com.trolltech.qt.qt3d.QGLMaterialCollection;
import com.trolltech.qt.qt3d.QGLPainter;
import com.trolltech.qt.qt3d.QGLSceneNode;
import com.trolltech.qt.qt3d.QGLTeapot;
import com.trolltech.qt.qt3d.QGLTexture2D;
import com.trolltech.qt.qt3d.QGLView;
import com.trolltech.qt.qt3d.QGeometryData;
import com.trolltech.qt.qt3d.QMatrix4x4Stack;

public class CubeView extends QGLView {

	public CubeView() {
		this(null);
	}

	public CubeView(QWidget parent) {
		super(parent);
	    setWindowTitle("Cube House");
	    setWindowIcon(new QIcon("classpath:/com/trolltech/demos/qt3d/cubehouse/qt3d.png"));
		setOption(QGLView.Option.CameraNavigation, false);

	    roomCamera = new QGLCamera(this);
	    roomCamera.setAdjustForAspectRatio(false);

	    QPropertyAnimation animation = new QPropertyAnimation(this, new QByteArray("cubeAngle"), this);
	    animation.setStartValue(0.0f);
	    animation.setEndValue(360.0f);
	    animation.setDuration(5000);
	    animation.setLoopCount(-1);
	    animation.start();

	    time.start();
	}
	
	public void setShowFrameRate(boolean value) { 
		this.showFrameRate = value; 
	}
	
	public void setStereo(boolean value) { 
		this.stereo = value; 
	}
	
	public void setProjectiveTextureEffect(boolean value) {
    	useProjectiveTextureEffect = value;
    }

	@QtPropertyReader(name="cubeAngle")
	public double cubeAngle() { 
		return cangle; 
	}
	
	@QtPropertyWriter(name="cubeAngle")
    public void setCubeAngle(double angle){
    	this.cangle = angle;
    	accelerometerTimeout();
        update();
    }


	@Override
	@QtBlockedSlot
	protected void paintGL(QGLPainter painter) {
		if(gl==null){
			return;
		}
		if (showFrameRate)
	        System.err.printf("time since last frame: %d ms", time.restart());

		gl.glDisable(GL_BLEND);

	    // Animate the projector position so the effect can be seen
	    if (useProjectiveTextureEffect)
	    {
	        projectorCamera.tiltPanRollCenter(-0.1f, -0.3f, 0.0f, QGLCamera.RotateOrder.PanTiltRoll);
	    }

	    painter.modelViewMatrix().push();
	    painter.projectionMatrix().push();

	    painter.setStandardEffect(QGL.StandardEffect.LitMaterial);
	    painter.setCamera(roomCamera);
	    painter.setLightModel(roomModel);
	    room.draw(painter);

	    painter.modelViewMatrix().pop();
	    painter.projectionMatrix().pop();

	    painter.modelViewMatrix().push();
	    // These are the model transformations
	    painter.modelViewMatrix().translate(-0.8f, -1.5f, -3.0f);
	    painter.setLightModel(normalModel);
	    if (useProjectiveTextureEffect)
	    {
	        modelMatrix.push();
	        // For an effect that looks like we have only one projector
	        // Over the whole screen, we duplicate transformations into the
	        // projector's model matrix.  For now, we don't apply the transform
	        // to center the effect on each object and see it more clearly.
	        // modelMatrix.translate(-0.8f, -1.5f, -3.0f);

	        updateProjectiveTextureEffect();

	        painter.setUserEffect(projectiveTextureEffect);
	        texture.bind();
	    }
	    else
	    {
	        painter.setStandardEffect(QGL.StandardEffect.LitMaterial);
	    }
	    teapot.draw(painter);

	    if (useProjectiveTextureEffect)
	        modelMatrix.pop();
	    painter.modelViewMatrix().pop();


	    // These are the model transformations
	    painter.modelViewMatrix().push();
	    painter.modelViewMatrix().translate(1.0f, -0.5f, 0.0f);
	    painter.modelViewMatrix().rotate(cangle, 1.0f, 1.0f, 1.0f);

	    texture.bind();
	    if (useProjectiveTextureEffect)
	    {
	        modelMatrix.push();
	        // For an effect that looks like we have only one projector
	        // Over the whole screen, we duplicate transformations into the
	        // projector's model matrix.  For now, we don't apply the transform
	        // to center the effect on each object and see it more clearly.
//	        modelMatrix.translate(1.0f, -0.5f, 0.0f);
	        modelMatrix.rotate(cangle, 1.0f, 1.0f, 1.0f);
	        updateProjectiveTextureEffect();
	        painter.setUserEffect(projectiveTextureEffect);
//	        painter.setStandardEffect(QGL::FlatDecalTexture2D);
	        cube.draw(painter);
	        modelMatrix.pop();
	    }
	    else
	    {
	    	gl.glEnable(GL_BLEND);
	        painter.setStandardEffect(QGL.StandardEffect.LitDecalTexture2D);
	        painter.setFaceColor(QGL.Face.AllFaces, new QColor(170, 202, 0, 120));
	        gl.glDisable(GL_DEPTH_TEST);
	        gl.glCullFace(GL_FRONT);
	        gl.glEnable(GL_CULL_FACE);
	        cube.draw(painter);
	        gl.glCullFace(GL_BACK);
	        cube.draw(painter);
	        gl.glDisable(GL_CULL_FACE);
	        gl.glEnable(GL_DEPTH_TEST);
	        gl.glBindTexture(GL_TEXTURE_2D, 0);
	    }

	    painter.modelViewMatrix().pop();
	}
	
	@Override
	@QtBlockedSlot
	protected void initializeGL(QGLPainter painter){
		if(gl==null){
			try {
				if(GLContext.getCurrent()==null){
					gl = GLDrawableFactory.getFactory(GLProfile.getDefault()).createExternalGLContext().getGL();
				}else{
					gl = GLContext.getCurrent().getGL();
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		QGLBuilder builder = new QGLBuilder();
	    builder.newSection(QGL.Smoothing.Faceted); 
	    builder.addCube(new QGLCube(1.0f));
	    cube = builder.currentNode();
	    builder.newSection(QGL.Smoothing.Faceted);
	    room = builder.currentNode();
	    builder.pushNode();
	    QGLSceneNode back = builder.newNode();
	    {
	        QGeometryData quad = new QGeometryData();
	        quad.appendVertex(new QVector3D(-3.0f, -3.0f, -15.0f));
	        quad.appendVertex(new QVector3D( 3.0f, -3.0f, -15.0f));
	        quad.appendVertex(new QVector3D( 3.0f,  3.0f, -15.0f));
	        quad.appendVertex(new QVector3D(-3.0f,  3.0f, -15.0f));
	        builder.addQuads(quad);
	    }
	    QGLSceneNode left = builder.newNode();
	    {
	        QGeometryData quad = new QGeometryData();
	        quad.appendVertex(new QVector3D(-3.0f, -3.0f, -15.0f));
	        quad.appendVertex(new QVector3D(-3.0f,  3.0f, -15.0f));
	        quad.appendVertex(new QVector3D(-3.0f,  3.0f, 0.0f));
	        quad.appendVertex(new QVector3D(-3.0f, -3.0f, 0.0f));
	        builder.addQuads(quad);
	    }
	    QGLSceneNode right = builder.newNode();
	    {
	    	QGeometryData quad = new QGeometryData();
	        quad.appendVertex(new QVector3D(3.0f,  3.0f, -15.0f));
	        quad.appendVertex(new QVector3D(3.0f, -3.0f, -15.0f));
	        quad.appendVertex(new QVector3D(3.0f, -3.0f, 0.0f));
	        quad.appendVertex(new QVector3D(3.0f,  3.0f, 0.0f));
	        builder.addQuads(quad);
	    }
	    QGLSceneNode top = builder.newNode();
	    {
	    	QGeometryData quad = new QGeometryData();
	        quad.appendVertex(new QVector3D(-3.0f,  3.0f, -15.0f));
	        quad.appendVertex(new QVector3D( 3.0f,  3.0f, -15.0f));
	        quad.appendVertex(new QVector3D( 3.0f,  3.0f, 0.0f));
	        quad.appendVertex(new QVector3D(-3.0f,  3.0f, 0.0f));
	        builder.addQuads(quad);
	    }
	    QGLSceneNode bottom = builder.newNode();
	    {
	    	QGeometryData quad = new QGeometryData();
	        quad.appendVertex(new QVector3D(-3.0f, -3.0f, -15.0f));
	        quad.appendVertex(new QVector3D(-3.0f, -3.0f, 0.0f));
	        quad.appendVertex(new QVector3D( 3.0f, -3.0f, 0.0f));
	        quad.appendVertex(new QVector3D( 3.0f, -3.0f, -15.0f));
	        builder.addQuads(quad);
	    }
	    builder.popNode();

	    int index;
	    QGLMaterialCollection palette = builder.sceneNode().palette();

	    QGLMaterial mat1 = new QGLMaterial();
	    mat1.setDiffuseColor(new QColor(128, 100, 0));
	    index = palette.addMaterial(mat1);
	    back.setMaterialIndex(index);

	    QGLMaterial mat2 = new QGLMaterial();
	    mat2.setDiffuseColor(new QColor(Qt.GlobalColor.cyan));
	    index = palette.addMaterial(mat2);
	    left.setMaterialIndex(index);
	    right.setMaterialIndex(index);

	    QGLMaterial mat3 = new QGLMaterial();
	    mat3.setDiffuseColor(new QColor(Qt.GlobalColor.yellow));
	    index = palette.addMaterial(mat3);
	    top.setMaterialIndex(index);
	    bottom.setMaterialIndex(index);

	    //qDumpScene(room);

	    builder.newSection();
	    builder.addBezierPatches(new QGLTeapot());
	    teapot = builder.currentNode();
	    QGLMaterial china = new QGLMaterial();
	    china.setAmbientColor(new QColor(192, 150, 128));
	    china.setSpecularColor(new QColor(60, 60, 60));
	    china.setShininess(128);
	    teapot.setMaterial(china);

	    scene = builder.finalizedSceneNode();
	    scene.setParent(this);

	    roomModel = new QGLLightModel(this);
	    roomModel.setAmbientSceneColor(new QColor(Qt.GlobalColor.white));
	    roomModel.setViewerPosition(QGLLightModel.ViewerPosition.LocalViewer);

	    normalModel = new QGLLightModel(this);

	    lightParameters = new QGLLightParameters(this);
	    lightParameters.setPosition(new QVector3D(0.0f, 0.0f, 3.0f));
	    painter.setMainLight(lightParameters);

	    QImage textureImage = new QImage("classpath:/com/trolltech/demos/qt3d/cubehouse/qtlogo.png");
	    texture.setImage(textureImage);

	    if (stereo) {
	        camera().setEyeSeparation(0.4f);
	        roomCamera.setEyeSeparation(0.1f);
	    }

	    if (useProjectiveTextureEffect)
	    {
	        // initialize the projector camera
	        projectorCamera = new QGLCamera(this);
	        projectiveTextureEffect = new ProjectiveTextureEffect();
	        projectorCamera.viewChanged.connect(this, "updateProjectorViewMatrix()");
	        projectorCamera.projectionChanged.connect(this, "updateProjectorProjectionMatrix()");
	    }
	}
	
	private void accelerometerTimeout(){
		QVector3D g = gravity();
	    camera().setMotionAdjustment(g);
	    roomCamera.setMotionAdjustment(g);
	}
	
	private void updateProjectorViewMatrix(){
	    projectiveTextureEffect.setProjectorViewMatrix(projectorCamera.modelViewMatrix());
	    updateProjectiveTextureEffect();
	}
	
	private void updateProjectorProjectionMatrix(){
		double projectorAspectRatio = 1.0;
	    projectiveTextureEffect.setProjectorProjectionMatrix(projectorCamera.projectionMatrix(projectorAspectRatio));
	    updateProjectiveTextureEffect();
	}
	
	private void updateProjectiveTextureEffect(){
		projectiveTextureEffect.setProjectorDirection(new QVector4D(projectorCamera.center().subtract(projectorCamera.eye())));
	    projectiveTextureEffect.setModelMatrix(modelMatrix.top());
	}
	
    private QGLTexture2D texture = new QGLTexture2D();
    private QGLSceneNode scene;
    private QGLSceneNode cube;
    private QGLSceneNode teapot;
    private QGLSceneNode room;
    private QGLCamera roomCamera;
    private QGLCamera projectorCamera;
    private double sensitivity = 0.1f;
    private QGLLightModel roomModel;
    private QGLLightModel normalModel;
    private QGLLightParameters lightParameters;
    private boolean showFrameRate;
    private boolean stereo;
    private boolean useProjectiveTextureEffect;
    private QTime time = new QTime();
    private double cangle;
    private double prevX, prevY, prevZ;
    private boolean havePrev;
    private ProjectiveTextureEffect projectiveTextureEffect;
    private QMatrix4x4 biasMatrix;
    private QMatrix4x4Stack modelMatrix;
    private QMatrix4x4 objectLinearTexgenMatrix;
    private GL gl;
    
    private QVector3D gravity(){
    	return new QVector3D(0, 0, -1);
    }
}
