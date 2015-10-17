/****************************************************************************
**
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

package com.trolltech.autotests;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

import java.util.Collections;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QVector2D;
import com.trolltech.qt.gui.QVector3D;
import com.trolltech.qt.qt3d.QBox3D;
import com.trolltech.qt.qt3d.QColor4ub;
import com.trolltech.qt.qt3d.QCustomDataArray;
import com.trolltech.qt.qt3d.QGL;
import com.trolltech.qt.qt3d.QGLAttributeDescription;
import com.trolltech.qt.qt3d.QGLAttributeSet;
import com.trolltech.qt.qt3d.QGLAttributeValue;
import com.trolltech.qt.qt3d.QGLBezierPatches;
import com.trolltech.qt.qt3d.QGLCube;
import com.trolltech.qt.qt3d.QGLCylinder;
import com.trolltech.qt.qt3d.QGLDome;
import com.trolltech.qt.qt3d.QGLIndexBuffer;
import com.trolltech.qt.qt3d.QGLRenderOrder;
import com.trolltech.qt.qt3d.QGLRenderOrderComparator;
import com.trolltech.qt.qt3d.QGLRenderState;
import com.trolltech.qt.qt3d.QGLSceneNode;
import com.trolltech.qt.qt3d.QGLSphere;
import com.trolltech.qt.qt3d.QGLTeapot;
import com.trolltech.qt.qt3d.QGLVertexBundle;
import com.trolltech.qt.qt3d.QGeometryData;
import com.trolltech.qt.qt3d.QGraphicsBillboardTransform;
import com.trolltech.qt.qt3d.QGraphicsRotation3D;
import com.trolltech.qt.qt3d.QGraphicsScale3D;
import com.trolltech.qt.qt3d.QGraphicsTransform3D;
import com.trolltech.qt.qt3d.QGraphicsTranslation3D;
import com.trolltech.qt.qt3d.QPlane3D;
import com.trolltech.qt.qt3d.QRay3D;
import com.trolltech.qt.qt3d.QSphere3D;
import com.trolltech.qt.qt3d.QTriangle3D;
import com.trolltech.unittests.support.CategoryQt3D;
import com.trolltech.unittests.support.FilterQt3D;

// Declarative support is an optional part of API:
// 1) The javac has to compile this package (this is the usual way the
//    test is deselected by having javac just not compile it)
// 2) The ANT testrunner looks over the source code folder for tests the
//    problem is that this class won't load in environment where Declarative
//    package does not exist.  FIXME
@Category(CategoryQt3D.class)
public class TestCloneableQt3D extends QApplicationTest {

    @BeforeClass
    public static void testInitialize() throws Exception {
        assumeTrue(FilterQt3D.detectStatic());
        QApplicationTest.testInitialize(null);
    }
    
	@Test
	public void run_clone_QBox3D() {
		QBox3D org = new QBox3D();
		QBox3D clone = org.clone();
		org.dispose();
		QBox3D clone2 = clone.clone();
		assertEquals(clone, clone2);
	}

	@Test
	public void run_clone_QColor4ub() {
		QColor4ub org = new QColor4ub();
		QColor4ub clone = org.clone();
		org.dispose();
		QColor4ub clone2 = clone.clone();
		assertEquals(clone, clone2);
	}

	@Test
	public void run_clone_QCustomDataArray() {
		QCustomDataArray org = new QCustomDataArray(QCustomDataArray.ElementType.Vector3D);
		org.append(new QVector3D(1,2,3));
		QCustomDataArray clone = org.clone();
		org.dispose();
		QCustomDataArray clone2 = clone.clone();
		assertEquals(clone.toVector3DArray(), clone2.toVector3DArray());
	}

	@Test
	public void run_clone_QGeometryData() {
		QGeometryData org = new QGeometryData();
		QGeometryData clone = org.clone();
		org.dispose();
		QGeometryData clone2 = clone.clone();
		assertEquals(clone, clone2);
	}

	@Test
	public void run_clone_QGLAttributeDescription() {
		QGLAttributeDescription org = new QGLAttributeDescription(QGL.VertexAttribute.UserVertex, 3, 2, 1);
		QGLAttributeDescription clone = org.clone();
		org.dispose();
		QGLAttributeDescription clone2 = clone.clone();
		assertEquals(clone.attribute(), QGL.VertexAttribute.UserVertex);
		assertEquals(clone.tupleSize(), 3);
		assertEquals(clone.type(), 2);
		assertEquals(clone.stride(), 1);
		assertEquals(clone.attribute(), clone2.attribute());
		assertEquals(clone.tupleSize(), clone2.tupleSize());
		assertEquals(clone.type(), clone2.type());
		assertEquals(clone.stride(), clone2.stride());
	}

	@Test
	public void run_clone_QGLAttributeSet() {
		QGLAttributeSet org = new QGLAttributeSet();
		QGLAttributeSet clone = org.clone();
		org.dispose();
		QGLAttributeSet clone2 = clone.clone();
		assertEquals(clone, clone2);
	}

	@Test
	public void run_clone_QGLAttributeValue() {
		QGLAttributeValue org = new QGLAttributeValue(3, 2, 1, 0);
		QGLAttributeValue clone = org.clone();
		org.dispose();
		QGLAttributeValue clone2 = clone.clone();
		assertEquals(clone.tupleSize(), 3);
		assertEquals(clone.type(), 2);
		assertEquals(clone.stride(), 1);
		assertEquals(clone.tupleSize(), clone2.tupleSize());
		assertEquals(clone.type(), clone2.type());
		assertEquals(clone.stride(), clone2.stride());
	}

	@Test
	public void run_clone_QGLBezierPatches() {
		QGLBezierPatches org = new QGLBezierPatches();
		QGLBezierPatches clone = org.clone();
		org.dispose();
		QGLBezierPatches clone2 = clone.clone();
		assertEquals(clone.positions(), clone2.positions());
		assertEquals(clone.textureCoords(), clone2.textureCoords());
	}

	@Test
	public void run_clone_QGLCube() {
		QGLCube org = new QGLCube(2.0);
		QGLCube clone = org.clone();
		org.dispose();
		QGLCube clone2 = clone.clone();
		assertEquals((Object)clone.size(), 2.0);
		assertEquals((Object)clone.size(), clone2.size());
	}

	@Test
	public void run_clone_QGLCylinder() {
		QGLCylinder org = new QGLCylinder();
		org.setHeight(2.0);
		QGLCylinder clone = org.clone();
		org.dispose();
		QGLCylinder clone2 = clone.clone();
		assertEquals((Object)clone.height(), 2.0);
		assertEquals((Object)clone.height(), clone2.height());
	}

	@Test
	public void run_clone_QGLDome() {
		QGLDome org = new QGLDome(2.0);
		QGLDome clone = org.clone();
		org.dispose();
		QGLDome clone2 = clone.clone();
		assertEquals((Object)clone.diameter(), 2.0);
		assertEquals((Object)clone.diameter(), clone2.diameter());
	}

	@Test
	public void run_clone_QGLIndexBuffer() {
		QGLIndexBuffer org = new QGLIndexBuffer();
		org.setIndexes(Collections.singletonList(9));
		QGLIndexBuffer clone = org.clone();
		org.dispose();
		QGLIndexBuffer clone2 = clone.clone();
		assertEquals(clone.indexes(), clone2.indexes());
	}

	@Test
	public void run_clone_QGLRenderState() {
		QGLRenderState org = new QGLRenderState();
		QGLRenderState clone = org.clone();
		org.dispose();
		QGLRenderState clone2 = clone.clone();
		assertEquals(clone, clone2);
	}

	@Test
	public void run_clone_QGLSceneNode() {
		QGLSceneNode org = new QGLSceneNode();
		org.setX(1.0);
		org.setY(2.0);
		org.setZ(3.0);
		QGLSceneNode clone = org.clone();
		org.dispose();
		QGLSceneNode clone2 = clone.clone();
		assertEquals((Object)clone.x(), 1.0);
		assertEquals((Object)clone.x(), clone2.x());
		assertEquals((Object)clone.y(), 2.0);
		assertEquals((Object)clone.y(), clone2.y());
		assertEquals((Object)clone.z(), 3.0);
		assertEquals((Object)clone.z(), clone2.z());
	}

	@Test
	public void run_clone_QGLSphere() {
		QGLSphere org = new QGLSphere(3.0, 6);
		QGLSphere clone = org.clone();
		org.dispose();
		QGLSphere clone2 = clone.clone();
		assertEquals((Object)clone.diameter(), 3.);
		assertEquals(clone.subdivisionDepth(), 6);
		assertEquals((Object)clone.diameter(), clone2.diameter());
		assertEquals(clone.subdivisionDepth(), clone2.subdivisionDepth());
	}

	@Test
	public void run_clone_QGLTeapot() {
		QGLTeapot org = new QGLTeapot();
		QGLTeapot clone = org.clone();
		org.dispose();
		QGLTeapot clone2 = clone.clone();
		assertEquals(clone.positions(), clone2.positions());
		assertEquals(clone.textureCoords(), clone2.textureCoords());
	}

	@Test
	public void run_clone_QGLVertexBundle() {
		QGLVertexBundle org = new QGLVertexBundle();
		org.addVector2DAttribute(QGL.VertexAttribute.Position, Collections.singletonList(new QVector2D(3, 6)));
		QGLVertexBundle clone = org.clone();
		org.dispose();
		QGLVertexBundle clone2 = clone.clone();
		assertEquals(clone.attributes(), clone2.attributes());
		assertEquals(clone.vertexCount(), 1);
		assertEquals(clone.vertexCount(), clone2.vertexCount());
		assertEquals(clone.attributeValue(QGL.VertexAttribute.Position).tupleSize(), 2);
		assertEquals(clone.attributeValue(QGL.VertexAttribute.Position).tupleSize(), clone2.attributeValue(QGL.VertexAttribute.Position).tupleSize());
	}

	@Test
	public void run_clone_QGraphicsBillboardTransform() {
		QGraphicsBillboardTransform org = new QGraphicsBillboardTransform();
		org.setPreserveUpVector(true);
		QGraphicsTransform3D clone = org.clone();
		org.dispose();
		QGraphicsTransform3D clone2 = clone.clone();
		assertTrue(clone instanceof QGraphicsBillboardTransform);
		assertTrue(clone2 instanceof QGraphicsBillboardTransform);
		assertEquals(true, ((QGraphicsBillboardTransform)clone).preserveUpVector());
		assertEquals(true, ((QGraphicsBillboardTransform)clone2).preserveUpVector());
	}

	@Test
	public void run_clone_QGraphicsRotation3D() {
		QGraphicsRotation3D org = new QGraphicsRotation3D();
		org.setAngle(9.8);
		QGraphicsTransform3D clone = org.clone();
		org.dispose();
		QGraphicsTransform3D clone2 = clone.clone();
		assertTrue(clone instanceof QGraphicsRotation3D);
		assertTrue(clone2 instanceof QGraphicsRotation3D);
		assertEquals((Object)9.8, ((QGraphicsRotation3D)clone).angle());
		assertEquals((Object)9.8, ((QGraphicsRotation3D)clone2).angle());
	}

	@Test
	public void run_clone_QGraphicsScale3D() {
		QGraphicsScale3D org = new QGraphicsScale3D();
		org.setScale(new QVector3D(1, 2, 3));
		QGraphicsTransform3D clone = org.clone();
		org.dispose();
		QGraphicsTransform3D clone2 = clone.clone();
		assertTrue(clone instanceof QGraphicsScale3D);
		assertTrue(clone2 instanceof QGraphicsScale3D);
		assertEquals(new QVector3D(1, 2, 3), ((QGraphicsScale3D)clone).scale());
		assertEquals(new QVector3D(1, 2, 3), ((QGraphicsScale3D)clone2).scale());
	}

	@Test
	public void run_clone_QGraphicsTranslation3D() {
		QGraphicsTranslation3D org = new QGraphicsTranslation3D();
		org.setTranslate(new QVector3D(1, 2, 3));
		QGraphicsTransform3D clone = org.clone();
		org.dispose();
		QGraphicsTransform3D clone2 = clone.clone();
		assertTrue(clone instanceof QGraphicsTranslation3D);
		assertTrue(clone2 instanceof QGraphicsTranslation3D);
		assertEquals(new QVector3D(1, 2, 3), ((QGraphicsTranslation3D)clone).translate());
		assertEquals(new QVector3D(1, 2, 3), ((QGraphicsTranslation3D)clone2).translate());
	}

	@Test
	public void run_clone_QPlane3D() {
		QPlane3D org = new QPlane3D();
		QPlane3D clone = org.clone();
		org.dispose();
		QPlane3D clone2 = clone.clone();
		assertEquals(clone, clone2);
	}

	@Test
	public void run_clone_QRay3D() {
		QRay3D org = new QRay3D();
		QRay3D clone = org.clone();
		org.dispose();
		QRay3D clone2 = clone.clone();
		assertEquals(clone, clone2);
	}

	@Test
	public void run_clone_QSphere3D() {
		QSphere3D org = new QSphere3D();
		QSphere3D clone = org.clone();
		org.dispose();
		QSphere3D clone2 = clone.clone();
		assertEquals(clone, clone2);
	}

	@Test
	public void run_clone_QTriangle3D() {
		QTriangle3D org = new QTriangle3D();
		QTriangle3D clone = org.clone();
		org.dispose();
		QTriangle3D clone2 = clone.clone();
		assertEquals(clone, clone2);
	}

    public static void main(String args[]) {
        org.junit.runner.JUnitCore.main(TestCloneableQt3D.class.getName());
    }
}
