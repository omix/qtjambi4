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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.trolltech.qt.QtBlockedSlot;
import com.trolltech.qt.core.QEventLoop;
import com.trolltech.qt.core.QObject;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QVector3D;
import com.trolltech.qt.opengl.QGLPixelBuffer;
import com.trolltech.qt.qt3d.QGL;
import com.trolltech.qt.qt3d.QGLAbstractScene;
import com.trolltech.qt.qt3d.QGLBuilder;
import com.trolltech.qt.qt3d.QGLCamera;
import com.trolltech.qt.qt3d.QGLGraphicsViewportItem;
import com.trolltech.qt.qt3d.QGLLightParameters;
import com.trolltech.qt.qt3d.QGLMaterial;
import com.trolltech.qt.qt3d.QGLPainter;
import com.trolltech.qt.qt3d.QGLPickNode;
import com.trolltech.qt.qt3d.QGLPixelBufferSurface;
import com.trolltech.qt.qt3d.QGLSceneNode;
import com.trolltech.qt.qt3d.QGLSphere;
import com.trolltech.qt.qt3d.QGLTexture2D;
import com.trolltech.qt.qt3d.QGLTwoSidedMaterial;
import com.trolltech.qt.qt3d.QGLView;
import com.trolltech.qt.qt3d.QGraphicsRotation3D;
import com.trolltech.qt.qt3d.QGraphicsTransform3D;
import com.trolltech.qt.qt3d.QGraphicsTranslation3D;
import com.trolltech.qt.qt3dquick.QDeclarativeEffect;
import com.trolltech.qt.qt3dquick.QDeclarativeItem3D;
import com.trolltech.qt.qt3dquick.QDeclarativeMesh;


public class TestMemoryManagementQt3D {
	
	@Before
	public void setUp() {
		 if(QApplication.startingUp()){
			 QApplication.initialize(new String[]{});
		 }
	}

	@org.junit.Test
	public void testQGLBuilder_newNode(){
		QGLBuilder builder = new QGLBuilder();
		QGLSceneNode node = builder.newNode();
		long hashCode = System.identityHashCode(node);
		long currentID = builder.currentNode().nativeId();
		assertEquals(currentID, node.nativeId());
		node = null;
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertEquals(currentID, builder.currentNode().nativeId());
		assertEquals(hashCode, System.identityHashCode(builder.currentNode()));
	}
	
	@org.junit.Test
	public void testQGLBuilder_currentNode(){
		QGLBuilder builder = new QGLBuilder();
		QGLSceneNode node = builder.currentNode();
		long hashCode = System.identityHashCode(node);
		long currentID = builder.currentNode().nativeId();
		node = null;
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertEquals(currentID, builder.currentNode().nativeId());
		assertEquals(hashCode, System.identityHashCode(builder.currentNode()));
	}
	
	@org.junit.Test
	public void testQGLBuilder_deleteUnfinalized_NewNode(){
		QGLBuilder builder = new QGLBuilder();
		QGLSceneNode node = builder.newNode();
		long currentID = builder.currentNode().nativeId();
		assertEquals(currentID, node.nativeId());
		builder.dispose();
		builder = null;
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertEquals(0, node.nativeId());
	}
	
	@org.junit.Test
	public void testQGLBuilder_deleteUnfinalized_CurrentNode(){
		QGLBuilder builder = new QGLBuilder();
		QGLSceneNode node = builder.currentNode();
		long currentID = node.nativeId();
		assertEquals(currentID, node.nativeId());
		builder = null;
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertEquals(0, node.nativeId());
	}
	
	@org.junit.Test
	public void testQGLBuilder_deleteUnfinalized_SceneNode(){
		QGLBuilder builder = new QGLBuilder();
		QGLSceneNode node = builder.sceneNode();
		builder = null;
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertEquals(0, node.nativeId());
	}
	
	@org.junit.Test
	public void testQGLBuilder_deleteUnfinalized_PushNode(){
		QGLBuilder builder = new QGLBuilder();
		QGLSceneNode node = builder.pushNode();
		QGLSceneNode fnode = builder.finalizedSceneNode();
		assertTrue("QGLBuilder pushNode disposed", 0==node.nativeId());
		assertTrue("finalized SceneNode not disposed", 0!=fnode.nativeId());
		builder.dispose();
		builder = null;
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		assertTrue("finalized SceneNode not disposed", 0!=fnode.nativeId());
	}
	
	@org.junit.Test
	public void testQGLBuilder_deleteFinalized_finalizedSceneNode(){
		QGLBuilder builder = new QGLBuilder();
		QGLSceneNode node = builder.finalizedSceneNode();
		long currentID = node.nativeId();
		builder.dispose();
		builder = null;
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertEquals(currentID, node.nativeId());
	}
	
	@org.junit.Test
	public void testQGLBuilder_deleteFinalized_newNode(){
		QGLBuilder builder = new QGLBuilder();
		QGLSceneNode node = builder.newNode();
		QGLSceneNode fnode = builder.finalizedSceneNode();
		assertTrue("QGLBuilder newNode disposed", 0==node.nativeId());
		assertTrue("finalized SceneNode not disposed", 0!=fnode.nativeId());
		builder.dispose();
		builder = null;
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		assertTrue("finalized SceneNode not disposed", 0!=fnode.nativeId());
	}
	
	@org.junit.Test
	public void testQGLBuilder_gced_translation(){
		final boolean[] finalized = {false}, disposed = {false};
		QGLSceneNode node = new QGLSceneNode();
		{
			QGraphicsTranslation3D translation = new QGraphicsTranslation3D(){
				@Override
				protected void disposed() {
					disposed[0]  = true;
					super.disposed();
				}
	
				@Override
				protected void finalize() {
					finalized[0]  = true;
					super.finalize();
				}
			};
			translation.setTranslate(new QVector3D(0, 0, -200));
			node.addTransform(translation);
		}
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertFalse(finalized[0]);
		assertFalse(disposed[0]);
		assertEquals(1, node.transforms().size());
		node.setTransforms(new ArrayList<QGraphicsTransform3D>());
		assertEquals(0, node.transforms().size());
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertFalse(finalized[0]);
		assertFalse(disposed[0]);
	}
	
	@org.junit.Test
	public void testQGLBuilder_deleteFinalized_currentNode(){
		QGLBuilder builder = new QGLBuilder();
		QGLSceneNode node = builder.currentNode();
		builder.finalizedSceneNode();
		builder = null;
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertEquals(0, node.nativeId());
	}
	
	@org.junit.Test
	public void testQGLBuilder_deleteFinalized_sceneNode(){
		QGLBuilder builder = new QGLBuilder();
		QGLSceneNode node = builder.sceneNode();
		long identity = System.identityHashCode(node);
		long currentID = node.nativeId();
		QGLSceneNode fnode = builder.finalizedSceneNode();
		long fidentity = System.identityHashCode(fnode);
		long fcurrentID = fnode.nativeId();
		assertEquals(currentID, fcurrentID);
		assertEquals(identity, fidentity);
		builder.dispose();
		builder = null;
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertEquals(node, fnode);
		assertEquals(currentID, node.nativeId());
	}
	
	@org.junit.Test
	public void testQGLBuilder_deleteFinalized_pushNode(){
		QGLBuilder builder = new QGLBuilder();
		QGLSceneNode node = builder.pushNode();
		builder.finalizedSceneNode();
		builder = null;
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertEquals(0, node.nativeId());
	}
	
	@org.junit.Test
	public void testQGLSceneNode_setMaterial(){
		QGLSceneNode node = new QGLSceneNode();
		final boolean[] finalized = {false};
		long hashcode1;
		long nativeID1;
		{
			QGLMaterial material = new QGLMaterial(){
				@Override
				protected void finalize() {
					finalized[0] = true;
					super.finalize();
				}
			};
			hashcode1 = System.identityHashCode(material);
			nativeID1 = material.nativeId();
			node.setMaterial(material);
			// end of scope
		}
		long hashcode2 = System.identityHashCode(node.material());
		long nativeID2 = node.material().nativeId();
		assertEquals(hashcode1, hashcode2);
		assertEquals(nativeID1, nativeID2);
		assertEquals(node.palette(), node.material().parent());
		System.gc(); // if bad: material is deleted
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		hashcode2 = System.identityHashCode(node.material());
		nativeID2 = node.material().nativeId();
		assertEquals(hashcode1, hashcode2);
		assertEquals(nativeID1, nativeID2);
		assertFalse(finalized[0]);
	}
	
	@org.junit.Test
	public void testQDeclarativeEffect_setMaterial(){
		QDeclarativeEffect node = new QDeclarativeEffect();
		final boolean[] finalized = {false};
		long hashcode1;
		long nativeID1;
		{
			QGLMaterial material = new QGLMaterial(){
				@Override
				protected void finalize() {
					finalized[0] = true;
					super.finalize();
				}
			};
			hashcode1 = System.identityHashCode(material);
			nativeID1 = material.nativeId();
			node.setMaterial(material);
			// end of scope
		}
		long hashcode2 = System.identityHashCode(node.material());
		long nativeID2 = node.material().nativeId();
		assertEquals(hashcode1, hashcode2);
		assertEquals(hashcode1, hashcode2);
		assertEquals(nativeID1, nativeID2);
		assertFalse(node.material().parent()==null);
		System.gc(); // if bad: material is deleted
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		hashcode2 = System.identityHashCode(node.material());
		nativeID2 = node.material().nativeId();
		assertEquals(hashcode1, hashcode2);
		assertEquals(nativeID1, nativeID2);
		assertFalse(finalized[0]);
	}
	
	@org.junit.Test
	public void testQDeclarativeEffect_MaterialDisposed(){
		QDeclarativeEffect node = new QDeclarativeEffect();
		QGLMaterial material = new QGLMaterial();
		node.setMaterial(material);
		// end of scope
		assertFalse(node.material().parent()==null);
		node.dispose();
		node = null;
		System.gc(); // if bad: material is deleted
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertEquals(0, material.nativeId());
	}
	
	@Test
	public void testQGLSceneNode_setPickNode(){
		QGLSceneNode node = new QGLSceneNode();
		final boolean[] finalized = {false};
		long hashcode1;
		long nativeID1;
		{
			QGLPickNode testObject = new QGLPickNode(){
				@Override
				protected void finalize() {
					finalized[0] = true;
					super.finalize();
				}
			};
			hashcode1 = System.identityHashCode(testObject);
			nativeID1 = testObject.nativeId();
			node.setPickNode(testObject);
		}
		long hashcode2 = System.identityHashCode(node.pickNode());
		long nativeID2 = node.pickNode().nativeId();
		assertEquals(hashcode1, hashcode2);
		assertEquals(nativeID1, nativeID2);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertEquals(null, node.pickNode().parent());
		assertFalse(finalized[0]);
	}
	
	@Test
	public void testQGLSceneNode_PickNodeUndisposed(){
		QGLSceneNode node = new QGLSceneNode();
		QGLPickNode testObject = new QGLPickNode();
		node.setPickNode(testObject);
		node.dispose();
		node = null;
		System.runFinalization();
		System.gc();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertFalse(testObject.nativeId()==0);
	}
	
	@Test
	public void testQGLSceneNode_addNode(){
		QGLSceneNode node = new QGLSceneNode();
		final boolean[] finalized = {false};
		{
			QGLSceneNode testObject = new QGLSceneNode(){
				@Override
				protected void finalize() {
					finalized[0] = true;
					super.finalize();
				}
			};
			node.addNode(testObject);
		}
		assertEquals(1, node.children().size());
		assertEquals(node, node.children().get(0).parent());
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertEquals(1, node.children().size());
		assertEquals(node, node.children().get(0).parent());
		assertFalse(finalized[0]);
	}
	
	@Test
	public void testQGLSceneNode_removeNode(){
		QGLSceneNode node = new QGLSceneNode();
		final boolean[] finalized = {false};
		QGLSceneNode testObject = new QGLSceneNode(node){
			@Override
			protected void finalize() {
				finalized[0] = true;
				super.finalize();
			}
		};
		//after removing, node should be gced
		node.removeNode(testObject);
		assertEquals(0, node.children().size());
		assertEquals(null, testObject.parent());
		testObject = null;
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertTrue(finalized[0]);
	}
	
	@Test
	public void testQGLSceneNode_addNodes(){
		QGLSceneNode node = new QGLSceneNode();
		final boolean[] finalized = {false};
		{
			QGLSceneNode testObject = new QGLSceneNode(){
				@Override
				protected void finalize() {
					finalized[0] = true;
					super.finalize();
				}
			};
			node.addNodes(Collections.singletonList(testObject));
		}
		assertEquals(1, node.children().size());
		assertEquals(node, node.children().get(0).parent());
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertEquals(1, node.children().size());
		assertEquals(node, node.children().get(0).parent());
		assertFalse(finalized[0]);
	}
	
	@Test
	public void testQGLSceneNode_removeNodes(){
		QGLSceneNode node = new QGLSceneNode();
		final boolean[] finalized = {false};
		QGLSceneNode testObject = new QGLSceneNode(node){
			@Override
			protected void finalize() {
				finalized[0] = true;
				super.finalize();
			}
		};
		
		//after removing, node should be gced
		node.removeNodes(Collections.singletonList(testObject));
		assertEquals(0, node.children().size());
		assertEquals(null, testObject.parent());
		testObject = null;
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertTrue(finalized[0]);
	}
	
	@Test
	public void testQGLSceneNode_allExcept(){
		QGLSceneNode node = new QGLSceneNode();
		{
			QGLSceneNode testObject = node.allExcept("name", node);
		}
		assertEquals(1, node.children().size());
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertEquals(1, node.children().size());
	}
	
	@Test
	public void testQGLSceneNode_addTransform(){
		QGLSceneNode node = new QGLSceneNode();
		final boolean[] finalized = {false};
		{
			QGraphicsTransform3D testObject = new QGraphicsRotation3D(){
				@Override
				protected void finalize() {
					finalized[0] = true;
					super.finalize();
				}
			};
			node.addTransform(testObject);
		}
		assertEquals(1, node.transforms().size());
		assertEquals(null, node.transforms().get(0).parent());
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertFalse(finalized[0]);
	}
	
	@Test
	public void testQGLMaterial_setTexture(){
		QGLMaterial node = new QGLMaterial();
		final boolean[] finalized = {false};
		{
			QGLTexture2D testObject = new QGLTexture2D(){
				@Override
				protected void finalize() {
					finalized[0] = true;
					super.finalize();
				}
			};
			node.setTexture(testObject);
		}
		assertTrue(node.texture()!=null);
		assertEquals(null, node.texture().parent());
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertFalse(finalized[0]);
	}
	
	@Test
	public void testQDeclarativeItem3D_setMesh(){
		QDeclarativeItem3D node = new QDeclarativeItem3D();
		final boolean[] finalized = {false};
		{
			QDeclarativeMesh testObject = new QDeclarativeMesh(){
				@Override
				protected void finalize() {
					finalized[0] = true;
					super.finalize();
				}
			};
			node.setMesh(testObject);
		}
		assertTrue(node.mesh()!=null);
		assertEquals(null, node.mesh().parent());
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertFalse(finalized[0]);
	}
	
	@Test
	public void testQDeclarativeItem3D_setEffect(){
		QDeclarativeItem3D node = new QDeclarativeItem3D();
		final boolean[] finalized = {false};
		{
			QDeclarativeEffect testObject = new QDeclarativeEffect(){
				@Override
				protected void finalize() {
					finalized[0] = true;
					super.finalize();
				}
			};
			node.setEffect(testObject);
		}
		assertTrue(node.effect()!=null);
		assertEquals(null, node.effect().parent());
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertFalse(finalized[0]);
	}
	
	@Test
	public void testQDeclarativeItem3D_setLight(){
		QDeclarativeItem3D node = new QDeclarativeItem3D();
		final boolean[] finalized = {false};
		{
			QGLLightParameters testObject = new QGLLightParameters(){
				@Override
				protected void finalize() {
					finalized[0] = true;
					super.finalize();
				}
			};
			node.setLight(testObject);
		}
		assertTrue(node.light()!=null);
		assertEquals(null, node.light().parent());
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertFalse(finalized[0]);
	}
	
	@Test
	public void testQDeclarativeMesh_setScene(){
		QDeclarativeMesh node = new QDeclarativeMesh();
		final boolean[] finalized = {false};
		{
			QGLAbstractScene testObject = new QGLAbstractScene(){
				private QGLSceneNode mainNode = new QGLSceneNode();
				@Override
				protected void finalize() {
					finalized[0] = true;
					super.finalize();
				}

				@Override
				@QtBlockedSlot
				public QGLSceneNode mainNode() {
					return mainNode;
				}

				@Override
				@QtBlockedSlot
				public List<QObject> objects() {
					return Collections.singletonList((QObject)mainNode);
				}
			};
			node.setScene(testObject);
			assertEquals(null, testObject.parent());
		}
		assertTrue(node.getSceneObject()!=null);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertFalse(finalized[0]);
	}
	
	@Test
	public void testQGLTwoSidedMaterial_setBack(){
		QGLTwoSidedMaterial node = new QGLTwoSidedMaterial();
		final boolean[] finalized = {false};
		{
			QGLMaterial testObject = new QGLMaterial(){
				@Override
				protected void finalize() {
					finalized[0] = true;
					super.finalize();
				}
			};
			node.setBack(testObject);
		}
		assertTrue(node.back()!=null);
		assertEquals(null, node.back().parent());
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertFalse(finalized[0]);
	}
	
	@Test
	public void testQGLGraphicsViewportItem_setCamera(){
		QGLGraphicsViewportItem node = new QGLGraphicsViewportItem(){
			@Override
			@QtBlockedSlot
			protected void paintGL(QGLPainter painter) {
			}
		};
		final boolean[] finalized = {false};
		{
			QGLCamera testObject = new QGLCamera(){
				@Override
				protected void finalize() {
					finalized[0] = true;
					super.finalize();
				}
			};
			node.setCamera(testObject);
		}
		assertTrue(node.camera()!=null);
		assertEquals(null, node.camera().parent());
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertFalse(finalized[0]);
	}
	
	@Test
	public void testQGLTwoSidedMaterial_setFront(){
		QGLTwoSidedMaterial node = new QGLTwoSidedMaterial();
		final boolean[] finalized = {false};
		{
			QGLMaterial testObject = new QGLMaterial(){
				@Override
				protected void finalize() {
					finalized[0] = true;
					super.finalize();
				}
			};
			node.setFront(testObject);
		}
		assertTrue(node.front()!=null);
		assertEquals(null, node.front().parent());
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertFalse(finalized[0]);
	}
	
	@Test
	public void testQGLPixelBufferSurface_setPixelBuffer(){
		QGLPixelBufferSurface node = new QGLPixelBufferSurface();
		final boolean[] finalized = {false};
		{
			QGLPixelBuffer testObject = new QGLPixelBuffer(100, 100){
				@Override
				protected void finalize() {
					finalized[0] = true;
					super.finalize();
				}
			};
			node.setPixelBuffer(testObject);
		}
		assertTrue(node.pixelBuffer()!=null);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertFalse(finalized[0]);
	}
	
	@org.junit.Test
	public void testQGLSceneNode_disposed(){
		final boolean[] disposed = {false};
		new QGLSceneNode(){

			@Override
			protected void disposed() {
				disposed[0] = true;
				super.disposed();
			}
			
		};
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
		System.gc();
		System.runFinalization();
		assertTrue(disposed[0]);
	}
}