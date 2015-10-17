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

package com.trolltech.demos.qt3d.teaservice;

import com.trolltech.qt.core.QEvent;
import com.trolltech.qt.core.QObject;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QVector3D;
import com.trolltech.qt.qt3d.QGL;
import com.trolltech.qt.qt3d.QGLAbstractEffect;
import com.trolltech.qt.qt3d.QGLAbstractScene;
import com.trolltech.qt.qt3d.QGLMaterial;
import com.trolltech.qt.qt3d.QGLPainter;
import com.trolltech.qt.qt3d.QGLSceneNode;
import com.trolltech.qt.qt3d.QGLView;

public class MeshObject extends QObject
{
	public MeshObject(QGLSceneNode meshObject){
		this(meshObject, null);
	}
	
	public MeshObject(QGLAbstractScene scene){
		this(scene, null);
	}
	
	public MeshObject(QGLSceneNode meshObject, QObject parent)
	{
		super(parent);
	    m_meshObject = meshObject;
	    m_scale = 1.0f;
	    m_rotationAngle = 0.0f;
	    m_objectId = -1;
	    m_hovering = false;
	}
	
	public MeshObject(QGLAbstractScene scene, QObject parent){
		super(parent);
		if(scene!=null){
			scene.setParent(this);
			m_meshObject = scene.mainNode();
		}
	    m_scale = 1.0f;
	    m_rotationAngle = 0.0f;
	    m_objectId = -1;
	    m_hovering = false;
	}

	public QVector3D position() { return m_position; }
	public void setPosition(QVector3D value) { m_position = value; }

	public double scale() { return m_scale; }
	public void setScale(double value) { m_scale = value; }

	public double rotationAngle() { return m_rotationAngle; }
	public void setRotationAngle(double value) { m_rotationAngle = value; }

	public QVector3D rotationVector() { return m_rotationVector; }
	public void setRotationVector(QVector3D value) { m_rotationVector = value; }

	public QGLMaterial material() { return m_material; }
	public void setMaterial(QGLMaterial value)
        { m_material = value; m_hoverMaterial = value; }

	public QGLMaterial hoverMaterial() { return m_hoverMaterial; }
	public void setHoverMaterial(QGLMaterial value) { m_hoverMaterial = value; }

	public QGLAbstractEffect effect() { return m_effect; }
	public void setEffect(QGLAbstractEffect value) { m_effect = value; }

	public int objectId() { return m_objectId; }
	public void setObjectId(int id) { m_objectId = id; }

	public void initialize(QGLView view, QGLPainter painter){
    	if (m_objectId != -1)
            view.registerObject(m_objectId, this);
    }
    
	public void draw(QGLPainter painter){
    	{
    	    // Position the model at its designated position, scale, and orientation.
    	    painter.modelViewMatrix().push();
    	    painter.modelViewMatrix().translate(m_position);
    	    if (m_scale != 1.0f)
    	        painter.modelViewMatrix().scale(m_scale);
    	    if (m_rotationAngle != 0.0f)
    	        painter.modelViewMatrix().rotate(m_rotationAngle, m_rotationVector);

    	    // Apply the material and effect to the painter.
    	    QGLMaterial material;
    	    if (m_hovering)
    	        material = m_hoverMaterial;
    	    else
    	        material = m_material;
    	    painter.setColor(material.diffuseColor());
    	    painter.setFaceMaterial(QGL.Face.AllFaces, material);
    	    if (m_effect!=null)
    	        painter.setUserEffect(m_effect);
    	    else
    	        painter.setStandardEffect(QGL.StandardEffect.LitMaterial);

    	    // Mark the object for object picking purposes.
    	    int prevObjectId = painter.objectPickId();
    	    if (m_objectId != -1)
    	        painter.setObjectPickId(m_objectId);

    	    // Draw the geometry mesh.
    	    if (m_meshObject!=null)
    	        m_meshObject.draw(painter);
    	    else
    	        m_mesh.draw(painter);

    	    // Turn off the user effect, if present.
    	    if (m_effect!=null)
    	        painter.setStandardEffect(QGL.StandardEffect.LitMaterial);

    	    // Revert to the previous object identifier.
    	    painter.setObjectPickId(prevObjectId);

    	    // Restore the modelview matrix.
    	    painter.modelViewMatrix().pop();
    	}
    }

    public final Signal0 pressed = new Signal0();
    public final Signal0 released = new Signal0();
    public final Signal0 clicked = new Signal0();
    public final Signal0 doubleClicked = new Signal0();
    public final Signal0 hoverChanged = new Signal0();

    public boolean event(QEvent e){
        // Convert the raw event into a signal representing the user's action.
        if (e.type() == QEvent.Type.MouseButtonPress) {
            QMouseEvent me = (QMouseEvent)e;
            if (me.button() == Qt.MouseButton.LeftButton)
                pressed.emit();
        } else if (e.type() == QEvent.Type.MouseButtonRelease) {
            QMouseEvent me = (QMouseEvent)e;
            if (me.button() == Qt.MouseButton.LeftButton) {
                released.emit();
                if (me.x() >= 0)   // Positive: inside object, Negative: outside.
                    clicked.emit();
            }
        } else if (e.type() == QEvent.Type.MouseButtonDblClick) {
            doubleClicked.emit();
        } else if (e.type() == QEvent.Type.Enter) {
            m_hovering = true;
            hoverChanged.emit();
        } else if (e.type() == QEvent.Type.Leave) {
            m_hovering = false;
            hoverChanged.emit();
        }
        return super.event(e);
    }

    private QGLSceneNode m_mesh;
    private QGLSceneNode m_meshObject;
    @SuppressWarnings("unused")
	private QGLAbstractScene m_scene;
    private QVector3D m_position = new QVector3D();
    private double m_scale;
    private double m_rotationAngle;
    private QVector3D m_rotationVector = new QVector3D();
    private QGLMaterial m_material;
    private QGLMaterial m_hoverMaterial;
    private QGLAbstractEffect m_effect;
    private int m_objectId;
    private boolean m_hovering;
};
