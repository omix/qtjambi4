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

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QKeyEvent;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.qt3d.QGLPainter;
import com.trolltech.qt.qt3d.QGLView;

public class TeaServiceView extends QGLView{
	public TeaServiceView(){
		this(null);
	}
	
	public TeaServiceView(QWidget parent){
		super(parent);
		teaService = new TeaService(this);
	    setOption(QGLView.Option.ObjectPicking, true);
	    teaService.changed.connect(this, "updateGL()");
	}
	
	public void standardLighting(){
		teaService.changeMaterials(false);
	    updateGL();
	}
	
	public void perPixelLighting(){
	    teaService.changeMaterials(true);
	    updateGL();
	}

	protected void initializeGL(QGLPainter painter){
		teaService.service.initialize(this, painter);
	}
	
	protected void paintGL(QGLPainter painter){
		teaService.service.draw(painter);
	}
	
	protected void keyPressEvent(QKeyEvent e){
	    if (e.key() == Qt.Key.Key_Tab.value()) {
	        // The Tab key turns the ShowPicking option on and off,
	        // which helps show what the pick buffer looks like.
	        setOption(QGLView.Option.ShowPicking, !options().isSet(QGLView.Option.ShowPicking));
	        updateGL();
	    }
	    super.keyPressEvent(e);
	}

	private TeaService teaService;
}