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

import com.trolltech.qt.core.QFile;
import com.trolltech.qt.core.QIODevice;
import com.trolltech.qt.gui.QMatrix4x4;
import com.trolltech.qt.gui.QVector4D;
import com.trolltech.qt.qt3d.QGLPainter;
import com.trolltech.qt.qt3d.QGLShaderProgramEffect;

/*!
The ProjectiveTextureEffect mimics the effect of shining a projector onto
a scene from a specific direction.  Vertex coordinates in object space are
transformed into eye-space coordinates relative to the light direction,
using the objectLinearTexgenMatrix.
*/
public class ProjectiveTextureEffect extends QGLShaderProgramEffect {

	private static QMatrix4x4 biasMatrix = new QMatrix4x4(0.5, 0.0, 0.0, 0.5,
            0.0, 0.5, 0.0, 0.5,
            0.0, 0.0, 0.5, 0.5,
            0.0, 0.0, 0.0, 1.0);
	
	public ProjectiveTextureEffect() {
		setupShaders();
	}

	public void setActive(QGLPainter painter, boolean flag){
		super.setActive(painter, flag);
	}
	
	public void update(QGLPainter painter, QGLPainter.Updates updates){
		super.update(painter, updates);
		if (matrixDirty)
	    {
	        recalulateObjectLinearTexgenMatrix();
	        matrixDirty = false;
	    }

	    program().setUniformValue("objectLinearTexgenMatrix", objectLinearTexgenMatrix);
	    program().setUniformValue("projectorDirection", projectorDirection);
	}
	
	public void setProjectorDirection(QVector4D direction){
	    this.projectorDirection = direction;
	    matrixDirty = true;
	}

	public void setCameraModelViewMatrix(QMatrix4x4 newCameraModelViewMatrix){
		cameraModelViewMatrix = newCameraModelViewMatrix;
	    QMatrix4x4 result = newCameraModelViewMatrix.inverted();
	    if (result==null){
	        System.err.println("camera Model view matrix not invertible in ProjectiveDepthTestEffect::setCameraModelViewMatrix()");
	    }else{
	    	inverseCameraModelViewMatrix = result;
	    }
	    matrixDirty = true;
	}
	
	public void setProjectorProjectionMatrix(QMatrix4x4 newMatrix){
		projectorProjectionMatrix = newMatrix;
	    matrixDirty = true;
	}
	
	public void setProjectorViewMatrix(QMatrix4x4 newMatrix){
		projectorViewMatrix = newMatrix;
	    matrixDirty = true;
	}
	
	public void setModelMatrix(QMatrix4x4 newMatrix){
		modelMatrix = newMatrix;
	    matrixDirty = true;
	}

	protected void setupShaders(){
		String vertexShaderFileName = "classpath:/com/trolltech/demos/qt3d/cubehouse/shaders/objectlineartexgen.vert";
	    QFile vertexShaderFile = new QFile(vertexShaderFileName);
	    if (vertexShaderFile.open(new QIODevice.OpenMode(QIODevice.OpenModeFlag.ReadOnly, QIODevice.OpenModeFlag.Text)))
	    {
	        setVertexShader(vertexShaderFile.readAll());
	    } else {
	        System.err.println("Could not open file "+vertexShaderFileName+", failed to load vertex shader");
	    }

	    String fragmentShaderFileName = "classpath:/com/trolltech/demos/qt3d/cubehouse/shaders/objectlineartexgen.frag";
	    QFile fragmentShaderFile = new QFile(fragmentShaderFileName);
	    if (fragmentShaderFile.open(new QIODevice.OpenMode(QIODevice.OpenModeFlag.ReadOnly, QIODevice.OpenModeFlag.Text)))
	    {
	        setFragmentShader(fragmentShaderFile.readAll());
	    } else {
	    	System.err.println("Could not open file "+fragmentShaderFileName+", failed to load fragment shader");
	    }
	}
	
	private void recalulateObjectLinearTexgenMatrix(){
		objectLinearTexgenMatrix = biasMatrix.multiply(projectorProjectionMatrix).multiply(projectorViewMatrix).multiply(modelMatrix);
	}
	
	private boolean matrixDirty = true;
	private QMatrix4x4 modelMatrix;
	private QMatrix4x4 objectLinearTexgenMatrix;
	private QMatrix4x4 cameraModelViewMatrix;
	private QMatrix4x4 inverseCameraModelViewMatrix;
	private QMatrix4x4 projectorProjectionMatrix;
	private QMatrix4x4 projectorViewMatrix;
	private QVector4D projectorDirection;
}
