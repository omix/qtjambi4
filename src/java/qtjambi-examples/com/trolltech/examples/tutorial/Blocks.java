/****************************************************************************
**
** Copyright (C) 1992-2009 Nokia. All rights reserved.
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

package com.trolltech.examples.tutorial;

import com.trolltech.qt.gui.*;
import com.trolltech.qt.core.*;

//! [0]
public class Blocks extends QWidget
{
//! [1]
    public Blocks()
    {
        QPushButton quit = new QPushButton(tr("Quit"));
        quit.setFont(new QFont("Times", 18, QFont.Weight.Bold.value()));

        QLCDNumber lcd = new QLCDNumber(2);
        lcd.setSegmentStyle(QLCDNumber.SegmentStyle.Filled);
//! [1]

//! [2]
        QSlider slider = new QSlider(Qt.Orientation.Horizontal);
//! [2] //! [3]
        slider.setRange(0, 99);
//! [3] //! [4]
        slider.setValue(0);
//! [4]

        quit.clicked.connect(QApplication.instance(), "quit()");
//! [5]
        slider.valueChanged.connect(lcd, "display(int)");
//! [5]

//! [6]
        QVBoxLayout layout = new QVBoxLayout();
//! [6] //! [7]
        layout.addWidget(quit);
        layout.addWidget(lcd);
        layout.addWidget(slider);
        setLayout(layout);
//! [7]
        setWindowTitle(tr("Building Blocks"));
    }

    public static void main(String args[])
    {
        QApplication.initialize(args);

        Blocks widget = new Blocks();
        widget.show();

        QApplication.execStatic();
        QApplication.shutdown();
    }
}
//! [0]
