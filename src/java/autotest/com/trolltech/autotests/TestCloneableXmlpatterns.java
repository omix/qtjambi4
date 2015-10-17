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

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.trolltech.qt.core.QBuffer;
import com.trolltech.qt.core.QIODevice;
import com.trolltech.qt.xmlpatterns.QAbstractXmlReceiver;
import com.trolltech.qt.xmlpatterns.QSourceLocation;
import com.trolltech.qt.xmlpatterns.QXmlFormatter;
import com.trolltech.qt.xmlpatterns.QXmlItem;
import com.trolltech.qt.xmlpatterns.QXmlName;
import com.trolltech.qt.xmlpatterns.QXmlNamePool;
import com.trolltech.qt.xmlpatterns.QXmlNodeModelIndex;
import com.trolltech.qt.xmlpatterns.QXmlQuery;
import com.trolltech.qt.xmlpatterns.QXmlSchema;
import com.trolltech.qt.xmlpatterns.QXmlSerializer;
import com.trolltech.unittests.support.CategoryXmlpatterns;
import com.trolltech.unittests.support.FilterXmlpatterns;

// Declarative support is an optional part of API:
// 1) The javac has to compile this package (this is the usual way the
//    test is deselected by having javac just not compile it)
// 2) The ANT testrunner looks over the source code folder for tests the
//    problem is that this class won't load in environment where Declarative
//    package does not exist.  FIXME
@Category(CategoryXmlpatterns.class)
public class TestCloneableXmlpatterns extends QApplicationTest {

    @BeforeClass
    public static void testInitialize() throws Exception {
        assumeTrue(FilterXmlpatterns.detectStatic());
        QApplicationTest.testInitialize(null);
    }
    
	@Test
	public void run_clone_QSourceLocation() {
		QSourceLocation org = new QSourceLocation();
		QSourceLocation clone = org.clone();
		org.dispose();
		QSourceLocation clone2 = clone.clone();
		assertEquals(clone, clone2);
	}

	@Test
	public void run_clone_QXmlName() {
		QXmlName org = new QXmlName();
		QXmlName clone = org.clone();
		org.dispose();
		QXmlName clone2 = clone.clone();
		assertEquals(clone, clone2);
	}

	@Test
	public void run_clone_QXmlNodeModelIndex() {
		QXmlNodeModelIndex org = new QXmlNodeModelIndex();
		QXmlNodeModelIndex clone = org.clone();
		org.dispose();
		QXmlNodeModelIndex clone2 = clone.clone();
		assertEquals(clone, clone2);
	}

    public static void main(String args[]) {
        org.junit.runner.JUnitCore.main(TestCloneableXmlpatterns.class.getName());
    }
}
