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

import com.trolltech.qt.core.QObject;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QVector3D;
import com.trolltech.qt.qt3d.QGLAbstractScene;
import com.trolltech.qt.qt3d.QGLMaterial;

public class TeaService extends QObject
{
	public enum Obj{
	    ObjTeapot,
	    ObjTeacup1,
	    ObjTeacup2,
	    ObjTeaspoon1,
	    ObjTeaspoon2
	};
	
	public TeaService(QObject parent){
		super(parent);
	    china = new QGLMaterial(this);
	    china.setAmbientColor(new QColor(192, 150, 128));
	    china.setSpecularColor(new QColor(60, 60, 60));
	    china.setShininess(128);
	
	    chinaHighlight = new QGLMaterial(this);
	    chinaHighlight.setAmbientColor(new QColor(255, 192, 0));
	    chinaHighlight.setSpecularColor(new QColor(60, 60, 0));
	    chinaHighlight.setShininess(128);
	
	    metal = new QGLMaterial(this);
	    metal.setAmbientColor(new QColor(255, 255, 255));
	    metal.setDiffuseColor(new QColor(150, 150, 150));
	    metal.setSpecularColor(new QColor(255, 255, 255));
	    metal.setShininess(128);
	
	    metalHighlight = new QGLMaterial(this);
	    metalHighlight.setAmbientColor(new QColor(255, 255, 96));
	    metalHighlight.setDiffuseColor(new QColor(150, 150, 96));
	    metalHighlight.setSpecularColor(new QColor(255, 255, 255));
	    metalHighlight.setShininess(128);
	
	    service = new SceneObject(this);
	    teapot = new Teapot(service);
	    teacup1 = new Teacup(service);
	    teacup2 = new Teacup(service);
	    teacup1.setPosition(new QVector3D(-2.3f, -0.75f, 0.0f));
	    teacup2.setRotationAngle(180);
	    teacup2.setRotationVector(new QVector3D(0, 1, 0));
	    teacup2.setPosition(new QVector3D(2.3f, -0.75f, 0.0f));
	    teaspoon1 = new Teaspoon(service);
	    teaspoon2 = new Teaspoon(service);
	    teaspoon1.setRotationAngle(275);
	    teaspoon1.setRotationVector(new QVector3D(1, 0, 0));
	    teaspoon1.setPosition(new QVector3D(-1.7f, -0.58f, 0.0f));
	    teaspoon2.setRotationAngle(275);
	    teaspoon2.setRotationVector(new QVector3D(1, 0, 0));
	    teaspoon2.setPosition(new QVector3D(1.7f, -0.58f, 0.0f));
	
	    teapot.setObjectId(Obj.ObjTeapot.ordinal());
	    teacup1.setObjectId(Obj.ObjTeacup1.ordinal());
	    teacup2.setObjectId(Obj.ObjTeacup2.ordinal());
	    teaspoon1.setObjectId(Obj.ObjTeaspoon1.ordinal());
	    teaspoon2.setObjectId(Obj.ObjTeaspoon2.ordinal());
	
	    lighting = new PerPixelEffect();
	    changeMaterials(false);
	
	    teapot.hoverChanged.connect(this.changed);
	    teacup1.hoverChanged.connect(this.changed);
	    teacup2.hoverChanged.connect(this.changed);
	    teaspoon1.hoverChanged.connect(this.changed);
	    teaspoon2.hoverChanged.connect(this.changed);
	
	    teapot.clicked.connect(this, "teapotClicked()");
	    teacup1.clicked.connect(this, "teacup1Clicked()");
	    teacup2.clicked.connect(this, "teacup2Clicked()");
	    teaspoon1.clicked.connect(this, "teaspoon1Clicked()");
	    teaspoon2.clicked.connect(this, "teaspoon2Clicked()");
	}
	
    SceneObject service;

    private Teapot teapot;
    private Teacup teacup1;
    private Teacup teacup2;
    private Teaspoon teaspoon1;
    private Teaspoon teaspoon2;
    private PerPixelEffect lighting;

    public void changeMaterials(boolean perPixel){
	    teapot.setMaterial(china);
	    teapot.setHoverMaterial(chinaHighlight);
	    teacup1.setMaterial(china);
	    teacup1.setHoverMaterial(chinaHighlight);
	    teacup2.setMaterial(china);
	    teacup2.setHoverMaterial(chinaHighlight);
	    if (perPixel) {
	        teapot.setEffect(lighting);
	        teacup1.setEffect(lighting);
	        teacup2.setEffect(lighting);
	    } else
	    {
	        teapot.setEffect(null);
	        teacup1.setEffect(null);
	        teacup2.setEffect(null);
	    }
	
	    teaspoon1.setMaterial(metal);
	    teaspoon1.setHoverMaterial(metalHighlight);
	    teaspoon2.setMaterial(metal);
	    teaspoon2.setHoverMaterial(metalHighlight);
	    if (perPixel) {
	        teaspoon1.setEffect(lighting);
	        teaspoon2.setEffect(lighting);
	    } else
	    {
	        teaspoon1.setEffect(null);
	        teaspoon2.setEffect(null);
	    }
	}

    public final Signal0 changed = new Signal0();

	void teapotClicked(){
		System.out.println("TeaService.teapotClicked()");
	}
	
	void teacup1Clicked(){
    	System.out.println("TeaService.teacup1Clicked()");
    }
	
	void teacup2Clicked(){
    	System.out.println("TeaService.teacup2Clicked()");
    }
	
	void teaspoon1Clicked(){
    	System.out.println("TeaService.teaspoon1Clicked()");
    }
	
	void teaspoon2Clicked(){
    	System.out.println("TeaService.teaspoon2Clicked()");
    }

    private QGLMaterial china;
    private QGLMaterial chinaHighlight;
    private QGLMaterial metal;
    private QGLMaterial metalHighlight;
    
    public static QGLAbstractScene loadBezier(String fileName)
	{
	    QGLAbstractScene scene;
	    scene = QGLAbstractScene.loadScene(fileName, "bezier");
	    if (scene==null)
	        System.err.println(String.format("Could not load %1$s, probably plugin could not be found", fileName));
	    return scene;
	}
}