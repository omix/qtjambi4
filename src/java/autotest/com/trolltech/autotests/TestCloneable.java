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

package com.trolltech.autotests;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import com.trolltech.qt.core.QBasicTimer;
import com.trolltech.qt.core.QBitArray;
import com.trolltech.qt.core.QByteArray;
import com.trolltech.qt.core.QByteArrayMatcher;
import com.trolltech.qt.core.QDate;
import com.trolltech.qt.core.QDateTime;
import com.trolltech.qt.core.QDir;
import com.trolltech.qt.core.QEasingCurve;
import com.trolltech.qt.core.QElapsedTimer;
import com.trolltech.qt.core.QFileInfo;
import com.trolltech.qt.core.QFuture;
import com.trolltech.qt.core.QFutureVoid;
import com.trolltech.qt.core.QLocale;
import com.trolltech.qt.core.QMargins;
import com.trolltech.qt.core.QPersistentModelIndex;
import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QProcessEnvironment;
import com.trolltech.qt.core.QRect;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.QRegExp;
import com.trolltech.qt.core.QSize;
import com.trolltech.qt.core.QSizeF;
import com.trolltech.qt.core.QStringMatcher;
import com.trolltech.qt.core.QSystemLocale;
import com.trolltech.qt.core.QTextBoundaryFinder;
import com.trolltech.qt.core.QTime;
import com.trolltech.qt.core.QUrl;
import com.trolltech.qt.core.QUuid;
import com.trolltech.qt.core.QXmlStreamAttribute;
import com.trolltech.qt.core.QXmlStreamAttributes;
import com.trolltech.qt.core.QXmlStreamEntityDeclaration;
import com.trolltech.qt.core.QXmlStreamNamespaceDeclaration;
import com.trolltech.qt.core.QXmlStreamNotationDeclaration;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.core.Qt.AlignmentFlag;
import com.trolltech.qt.gui.QAbstractTextDocumentLayout;
import com.trolltech.qt.gui.QAbstractTextDocumentLayout;
import com.trolltech.qt.gui.QBitmap;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QColormap;
import com.trolltech.qt.gui.QConicalGradient;
import com.trolltech.qt.gui.QCursor;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QFontInfo;
import com.trolltech.qt.gui.QFontMetrics;
import com.trolltech.qt.gui.QFontMetricsF;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QImage;
import com.trolltech.qt.gui.QInputMethodEvent;
import com.trolltech.qt.gui.QInputMethodEvent;
import com.trolltech.qt.gui.QItemSelection;
import com.trolltech.qt.gui.QItemSelectionRange;
import com.trolltech.qt.gui.QKeySequence;
import com.trolltech.qt.core.QLine;
import com.trolltech.qt.core.QLineF;
import com.trolltech.qt.gui.QLinearGradient;
import com.trolltech.qt.gui.QListWidgetItem;
import com.trolltech.qt.gui.QMatrix;
import com.trolltech.qt.gui.QMatrix4x4;
import com.trolltech.qt.gui.QPainterPath;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPalette;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QPicture;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QPolygon;
import com.trolltech.qt.gui.QPolygonF;
import com.trolltech.qt.gui.QQuaternion;
import com.trolltech.qt.gui.QRadialGradient;
import com.trolltech.qt.gui.QRegion;
import com.trolltech.qt.gui.QSizePolicy;
import com.trolltech.qt.gui.QStandardItem;
import com.trolltech.qt.gui.QStandardItemModel;
import com.trolltech.qt.gui.QStaticText;
import com.trolltech.qt.gui.QStyleOption;
import com.trolltech.qt.gui.QTableWidgetItem;
import com.trolltech.qt.gui.QTableWidgetSelectionRange;
import com.trolltech.qt.gui.QTextBlock;
import com.trolltech.qt.gui.QTextBlockFormat;
import com.trolltech.qt.gui.QTextCharFormat;
import com.trolltech.qt.gui.QTextCursor;
import com.trolltech.qt.gui.QTextDocument;
import com.trolltech.qt.gui.QTextDocumentFragment;
import com.trolltech.qt.gui.QTextEdit;
import com.trolltech.qt.gui.QTextFormat;
import com.trolltech.qt.gui.QTextFragment;
import com.trolltech.qt.gui.QTextFrame;
import com.trolltech.qt.gui.QTextFrameFormat;
import com.trolltech.qt.gui.QTextFrame.iterator;
import com.trolltech.qt.gui.QTextImageFormat;
import com.trolltech.qt.gui.QTextInlineObject;
import com.trolltech.qt.gui.QTextLayout;
import com.trolltech.qt.gui.QTextLength;
import com.trolltech.qt.gui.QTextLine;
import com.trolltech.qt.gui.QTextListFormat;
import com.trolltech.qt.gui.QTextOption;
import com.trolltech.qt.gui.QTextTableCell;
import com.trolltech.qt.gui.QTextTableCellFormat;
import com.trolltech.qt.gui.QTextTableFormat;
import com.trolltech.qt.gui.QTileRules;
import com.trolltech.qt.gui.QTouchEvent;
import com.trolltech.qt.gui.QTransform;
import com.trolltech.qt.gui.QTreeWidget;
import com.trolltech.qt.gui.QTreeWidgetItem;
import com.trolltech.qt.gui.QTreeWidgetItemIterator;
import com.trolltech.qt.gui.QVector2D;
import com.trolltech.qt.gui.QVector3D;
import com.trolltech.qt.gui.QVector4D;
import com.trolltech.qt.network.QAuthenticator;
import com.trolltech.qt.network.QHostAddress;
import com.trolltech.qt.network.QHostInfo;
import com.trolltech.qt.network.QHttpRequestHeader;
import com.trolltech.qt.network.QHttpResponseHeader;
import com.trolltech.qt.network.QIPv6Address;
import com.trolltech.qt.network.QNetworkAddressEntry;
import com.trolltech.qt.network.QNetworkCacheMetaData;
import com.trolltech.qt.network.QNetworkConfiguration;
import com.trolltech.qt.network.QNetworkCookie;
import com.trolltech.qt.network.QNetworkInterface;
import com.trolltech.qt.network.QNetworkProxy;
import com.trolltech.qt.network.QNetworkProxyQuery;
import com.trolltech.qt.network.QNetworkRequest;
import com.trolltech.qt.network.QUrlInfo;
import com.trolltech.qt.xml.QDomAttr;
import com.trolltech.qt.xml.QDomCDATASection;
import com.trolltech.qt.xml.QDomCharacterData;
import com.trolltech.qt.xml.QDomComment;
import com.trolltech.qt.xml.QDomDocument;
import com.trolltech.qt.xml.QDomDocumentFragment;
import com.trolltech.qt.xml.QDomDocumentType;
import com.trolltech.qt.xml.QDomElement;
import com.trolltech.qt.xml.QDomEntity;
import com.trolltech.qt.xml.QDomEntityReference;
import com.trolltech.qt.xml.QDomImplementation;
import com.trolltech.qt.xml.QDomNamedNodeMap;
import com.trolltech.qt.xml.QDomNode;
import com.trolltech.qt.xml.QDomNodeList;
import com.trolltech.qt.xml.QDomNotation;
import com.trolltech.qt.xml.QDomProcessingInstruction;
import com.trolltech.qt.xml.QDomText;

public class TestCloneable extends QApplicationTest {

    @Test
    public void run_clone_QAuthenticator() {
        QAuthenticator org = new QAuthenticator();
        QAuthenticator clone = org.clone();
        org.dispose();
        QAuthenticator clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QBitArray() {
        QBitArray org = new QBitArray();
        QBitArray clone = org.clone();
        org.dispose();
        QBitArray clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QBrush() {
        QBrush org = new QBrush();
        QBrush clone = org.clone();
        org.dispose();
        QBrush clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QByteArray() {
        QByteArray org = new QByteArray();
        QByteArray clone = org.clone();
        org.dispose();
        QByteArray clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QByteArrayMatcher() {
        QByteArrayMatcher org = new QByteArrayMatcher(new QByteArray("1234"));
        QByteArrayMatcher clone = org.clone();
        org.dispose();
        QByteArrayMatcher clone2 = clone.clone();
        assertEquals(clone.pattern(), clone2.pattern());
    }

    @Test
    public void run_clone_QColor() {
        QColor org = new QColor();
        QColor clone = org.clone();
        org.dispose();
        QColor clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QColormap() {
        QColormap org = QColormap.instance(0);
        QColormap clone = org.clone();
        org.dispose();
        QColormap clone2 = clone.clone();
        assertEquals(clone.depth(), clone2.depth());
    }

    @Test
    public void run_clone_QCursor() {
        QCursor org = new QCursor();
        QCursor clone = org.clone();
        org.dispose();
        QCursor clone2 = clone.clone();
        assertEquals(clone.hotSpot(), clone2.hotSpot());
    }

    @Test
    public void run_clone_QDateTime() {
        QDateTime org = new QDateTime();
        QDateTime clone = org.clone();
        org.dispose();
        QDateTime clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDir() {
        QDir org = new QDir();
        QDir clone = org.clone();
        org.dispose();
        QDir clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomAttr() {
        QDomAttr org = new QDomAttr();
        QDomAttr clone = org.clone();
        org.dispose();
        QDomAttr clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomCDATASection() {
        QDomCDATASection org = new QDomCDATASection();
        QDomCDATASection clone = org.clone();
        org.dispose();
        QDomCDATASection clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomCharacterData() {
        QDomCharacterData org = new QDomCharacterData();
        QDomCharacterData clone = org.clone();
        org.dispose();
        QDomCharacterData clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomComment() {
        QDomComment org = new QDomComment();
        QDomComment clone = org.clone();
        org.dispose();
        QDomComment clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomDocument() {
        QDomDocument org = new QDomDocument();
        QDomDocument clone = org.clone();
        org.dispose();
        QDomDocument clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomDocumentFragment() {
        QDomDocumentFragment org = new QDomDocumentFragment();
        QDomDocumentFragment clone = org.clone();
        org.dispose();
        QDomDocumentFragment clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomDocumentType() {
        QDomDocumentType org = new QDomDocumentType();
        QDomDocumentType clone = org.clone();
        org.dispose();
        QDomDocumentType clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomElement() {
        QDomElement org = new QDomElement();
        QDomElement clone = org.clone();
        org.dispose();
        QDomElement clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomEntity() {
        QDomEntity org = new QDomEntity();
        QDomEntity clone = org.clone();
        org.dispose();
        QDomEntity clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomEntityReference() {
        QDomEntityReference org = new QDomEntityReference();
        QDomEntityReference clone = org.clone();
        org.dispose();
        QDomEntityReference clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomImplementation() {
        QDomImplementation org = new QDomImplementation();
        QDomImplementation clone = org.clone();
        org.dispose();
        QDomImplementation clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomNamedNodeMap() {
        QDomNamedNodeMap org = new QDomNamedNodeMap();
        QDomNamedNodeMap clone = org.clone();
        org.dispose();
        QDomNamedNodeMap clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomNode() {
        QDomNode org = new QDomNode();
        QDomNode clone = org.clone();
        org.dispose();
        QDomNode clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomNodeList() {
        QDomNodeList org = new QDomNodeList();
        QDomNodeList clone = org.clone();
        org.dispose();
        QDomNodeList clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomNotation() {
        QDomNotation org = new QDomNotation();
        QDomNotation clone = org.clone();
        org.dispose();
        QDomNotation clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomProcessingInstruction() {
        QDomProcessingInstruction org = new QDomProcessingInstruction();
        QDomProcessingInstruction clone = org.clone();
        org.dispose();
        QDomProcessingInstruction clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QDomText() {
        QDomText org = new QDomText();
        QDomText clone = org.clone();
        org.dispose();
        QDomText clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QFileInfo() {
        QFileInfo org = new QFileInfo();
        QFileInfo clone = org.clone();
        org.dispose();
        QFileInfo clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QFont() {
        QFont org = new QFont();
        QFont clone = org.clone();
        org.dispose();
        QFont clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QFontInfo() {
        QFontInfo org = new QFontInfo(new QFont("frodo"));
        QFontInfo clone = org.clone();
        org.dispose();
        QFontInfo clone2 = clone.clone();
        assertEquals(clone.family(), clone2.family());
    }

    @Test
    public void run_clone_QFontMetrics() {
        QFontMetrics org = new QFontMetrics(new QFont());
        QFontMetrics clone = org.clone();
        org.dispose();
        QFontMetrics clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QFontMetricsF() {
        QFontMetricsF org = new QFontMetricsF(new QFont());
        QFontMetricsF clone = org.clone();
        org.dispose();
        QFontMetricsF clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QHostAddress() {
        QHostAddress org = new QHostAddress();
        QHostAddress clone = org.clone();
        org.dispose();
        QHostAddress clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QHostInfo() {
        QHostInfo org = new QHostInfo();
        QHostInfo clone = org.clone();
        org.dispose();
        QHostInfo clone2 = clone.clone();
        assertEquals(clone.hostName(), clone2.hostName());
    }

    @Test
    public void run_clone_QHttpRequestHeader() {
        QHttpRequestHeader org = new QHttpRequestHeader();
        QHttpRequestHeader clone = org.clone();
        org.dispose();
        QHttpRequestHeader clone2 = clone.clone();
        assertEquals(clone.toString(), clone2.toString());
    }

    @Test
    public void run_clone_QHttpResponseHeader() {
        QHttpResponseHeader org = new QHttpResponseHeader();
        QHttpResponseHeader clone = org.clone();
        org.dispose();
        QHttpResponseHeader clone2 = clone.clone();
        assertEquals(clone.toString(), clone2.toString());
    }

    @Test
    public void run_clone_QIcon() {
        QIcon org = new QIcon("file");
        QIcon clone = org.clone();
        org.dispose();
        QIcon clone2 = clone.clone();
        assertEquals(clone.isNull(), clone2.isNull());
    }

    @Test
    public void run_clone_QImage() {
        QImage org = new QImage();
        QImage clone = org.clone();
        org.dispose();
        QImage clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QKeySequence() {
        QKeySequence org = new QKeySequence();
        QKeySequence clone = org.clone();
        org.dispose();
        QKeySequence clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QLocale() {
        QLocale org = new QLocale();
        QLocale clone = org.clone();
        org.dispose();
        QLocale clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QMatrix() {
        QMatrix org = new QMatrix();
        QMatrix clone = org.clone();
        org.dispose();
        QMatrix clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QNetworkAddressEntry() {
        QNetworkAddressEntry org = new QNetworkAddressEntry();
        QNetworkAddressEntry clone = org.clone();
        org.dispose();
        QNetworkAddressEntry clone2 = clone.clone();
        assertEquals(clone.broadcast(), clone2.broadcast());
    }

    @Test
    public void run_clone_QNetworkCookie() {
        QNetworkCookie org = new QNetworkCookie();
        QNetworkCookie clone = org.clone();
        org.dispose();
        QNetworkCookie clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QNetworkInterface() {
        QNetworkInterface org = new QNetworkInterface();
        QNetworkInterface clone = org.clone();
        org.dispose();
        QNetworkInterface clone2 = clone.clone();
        assertEquals(clone.toString(), clone2.toString());
    }

    @Test
    public void run_clone_QNetworkProxy() {
        QNetworkProxy org = new QNetworkProxy();
        QNetworkProxy clone = org.clone();
        org.dispose();
        QNetworkProxy clone2 = clone.clone();
        assertEquals(clone.hostName(), clone2.hostName());
    }

    @Test
    public void run_clone_QNetworkRequest() {
        QNetworkRequest org = new QNetworkRequest();
        QNetworkRequest clone = org.clone();
        org.dispose();
        QNetworkRequest clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QPainterPath() {
        QPainterPath org = new QPainterPath();
        QPainterPath clone = org.clone();
        org.dispose();
        QPainterPath clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QPalette() {
        QPalette org = new QPalette();
        QPalette clone = org.clone();
        org.dispose();
        QPalette clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QPen() {
        QPen org = new QPen();
        QPen clone = org.clone();
        org.dispose();
        QPen clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QPersistentModelIndex() {
        QPersistentModelIndex org = new QPersistentModelIndex();
        QPersistentModelIndex clone = org.clone();
        org.dispose();
        QPersistentModelIndex clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QPicture() {
        QPicture org = new QPicture();
        QPicture clone = org.clone();
        org.dispose();
        QPicture clone2 = clone.clone();
        assertEquals(clone.size(), clone2.size());
    }

    @Test
    public void run_clone_QPixmap() {
        QPixmap org = new QPixmap(4, 5);
        QPixmap clone = org.clone();
        org.dispose();
        QPixmap clone2 = clone.clone();
        assertEquals(clone.rect(), clone2.rect());
    }

    @Test
    public void run_clone_QRegExp() {
        QRegExp org = new QRegExp();
        QRegExp clone = org.clone();
        org.dispose();
        QRegExp clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QRegion() {
        QRegion org = new QRegion();
        QRegion clone = org.clone();
        org.dispose();
        QRegion clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QStringMatcher() {
        QStringMatcher org = new QStringMatcher("pattern");
        QStringMatcher clone = org.clone();
        org.dispose();
        QStringMatcher clone2 = clone.clone();
        assertEquals(clone.pattern(), clone2.pattern());
    }

    @Test
    public void run_clone_QStyleOption() {
        QStyleOption org = new QStyleOption();
        org.setRect(new QRect(5, 5, 1, 1));
        QStyleOption clone = org.clone();
        org.dispose();
        QStyleOption clone2 = clone.clone();
        assertEquals(clone.rect(), clone2.rect());
    }

    @Test
    public void run_clone_QTextBlock() {
        QTextBlock org = new QTextBlock();
        QTextBlock clone = org.clone();
        org.dispose();
        QTextBlock clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QTextCursor() {
        QTextCursor org = new QTextCursor();
        QTextCursor clone = org.clone();
        org.dispose();
        QTextCursor clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QTextDocumentFragment() {
        QTextDocumentFragment org = new QTextDocumentFragment();
        QTextDocumentFragment clone = org.clone();
        org.dispose();
        QTextDocumentFragment clone2 = clone.clone();
        assertEquals(clone.toPlainText(), clone2.toPlainText());
    }

    @Test
    public void run_clone_QTextFormat() {
        QTextFormat org = new QTextFormat();
        QTextFormat clone = org.clone();
        org.dispose();
        QTextFormat clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QTextFragment() {
        QTextFragment org = new QTextFragment();
        QTextFragment clone = org.clone();
        org.dispose();
        QTextFragment clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QTextFrame_iterator() {
        QTextFrame.iterator org = new QTextFrame.iterator();
        QTextFrame.iterator clone = org.clone();
        org.dispose();
        QTextFrame.iterator clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QTextOption() {
        QTextOption org = new QTextOption();
        org.setAlignment(AlignmentFlag.AlignHCenter);
        QTextOption clone = org.clone();
        org.dispose();
        QTextOption clone2 = clone.clone();
        assertEquals(clone.alignment(), clone2.alignment());
    }

    @Test
    public void run_clone_QTextTableCell() {
        QTextTableCell org = new QTextTableCell();
        QTextTableCell clone = org.clone();
        org.dispose();
        QTextTableCell clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QTransform() {
        QTransform org = new QTransform();
        QTransform clone = org.clone();
        org.dispose();
        QTransform clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QTreeWidgetItemIterator() {
        QTreeWidgetItemIterator org = new QTreeWidgetItemIterator(new QTreeWidget());
        QTreeWidgetItemIterator clone = org.clone();
        org.dispose();
        QTreeWidgetItemIterator clone2 = clone.clone();
        assertEquals(clone.current(), clone2.current());
    }

    @Test
    public void run_clone_QUrl() {
        QUrl org = new QUrl();
        QUrl clone = org.clone();
        org.dispose();
        QUrl clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QUrlInfo() {
        QUrlInfo org = new QUrlInfo();
        QUrlInfo clone = org.clone();
        org.dispose();
        QUrlInfo clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QXmlStreamAttribute() {
        QXmlStreamAttribute org = new QXmlStreamAttribute();
        QXmlStreamAttribute clone = org.clone();
        org.dispose();
        QXmlStreamAttribute clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

    @Test
    public void run_clone_QXmlStreamEntityDeclaration() {
        QXmlStreamEntityDeclaration org = new QXmlStreamEntityDeclaration();
        QXmlStreamEntityDeclaration clone = org.clone();
        org.dispose();
        QXmlStreamEntityDeclaration clone2 = clone.clone();
        assertEquals(clone, clone2);
    }

	@Test
	public void run_clone_QDate() {
		QDate org = new QDate();
		QDate clone = org.clone();
		org.dispose();
		QDate clone2 = clone.clone();
		assertEquals(clone.day(), clone2.day());
		assertEquals(clone.month(), clone2.month());
		assertEquals(clone.year(), clone2.year());
	}

	@Test
	public void run_clone_QEasingCurve() {
		QEasingCurve org = new QEasingCurve();
		QEasingCurve clone = org.clone();
		org.dispose();
		QEasingCurve clone2 = clone.clone();
		assertEquals(clone, clone2);
	}

	@Test
	public void run_clone_QElapsedTimer() {
		QElapsedTimer org = new QElapsedTimer();
		QElapsedTimer clone = org.clone();
		org.dispose();
		QElapsedTimer clone2 = clone.clone();
		assertEquals(clone, clone2);
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void run_clone_QFuture() {
		QFuture org = new QFuture();
		QFuture clone = org.clone();
		org.dispose();
		QFuture clone2 = clone.clone();
		assertEquals(clone, clone2);
	}

	@Test
	public void run_clone_QFutureVoid() {
		QFutureVoid org = new QFutureVoid();
		QFutureVoid clone = org.clone();
		org.dispose();
		QFutureVoid clone2 = clone.clone();
		assertEquals(clone, clone2);
	}

	@Test
	public void run_clone_QMargins() {
		QMargins org = new QMargins(1, 2, 3, 4);
		QMargins clone = org.clone();
		org.dispose();
		QMargins clone2 = clone.clone();
		assertEquals(clone.top(), clone2.top());
		assertEquals(clone.bottom(), clone2.bottom());
		assertEquals(clone.left(), clone2.left());
		assertEquals(clone.right(), clone2.right());
	}

	@Test
	public void run_clone_QPoint() {
		QPoint org = new QPoint(1, 2);
		QPoint clone = org.clone();
		org.dispose();
		QPoint clone2 = clone.clone();
		assertEquals(clone.x(), clone2.x());
		assertEquals(clone.y(), clone2.y());
	}

	@Test
	public void run_clone_QPointF() {
		QPointF org = new QPointF(3.3, 4.4);
		QPointF clone = org.clone();
		org.dispose();
		QPointF clone2 = clone.clone();
		assertEquals((Object)clone.x(), (Object)clone2.x());
		assertEquals((Object)clone.y(), (Object)clone2.y());
	}

	@Test
	public void run_clone_QProcessEnvironment() {
		QProcessEnvironment org = new QProcessEnvironment();
		org.insert("VARIABLE", "value");
		QProcessEnvironment clone = org.clone();
		org.dispose();
		QProcessEnvironment clone2 = clone.clone();
		assertEquals(clone.keys(), clone2.keys());
		for(String key : clone.keys()){
			assertEquals(clone.value(key), clone2.value(key));
		}
	}

	@Test
	public void run_clone_QRect() {
		QRect org = new QRect(10, 20, 30, 40);
		QRect clone = org.clone();
		org.dispose();
		QRect clone2 = clone.clone();
		assertEquals(clone.top(), clone2.top());
		assertEquals(clone.bottom(), clone2.bottom());
		assertEquals(clone.left(), clone2.left());
		assertEquals(clone.right(), clone2.right());
	}

	@Test
	public void run_clone_QRectF() {
		QRectF org = new QRectF(10, 20, 30, 40);
		QRectF clone = org.clone();
		org.dispose();
		QRectF clone2 = clone.clone();
		assertEquals((Object)clone.top(), clone2.top());
		assertEquals((Object)clone.bottom(), clone2.bottom());
		assertEquals((Object)clone.left(), clone2.left());
		assertEquals((Object)clone.right(), clone2.right());
	}

	@Test
	public void run_clone_QSize() {
		QSize org = new QSize(20, 60);
		QSize clone = org.clone();
		org.dispose();
		QSize clone2 = clone.clone();
		assertEquals(clone.width(), clone2.width());
		assertEquals(clone.height(), clone2.height());
	}

	@Test
	public void run_clone_QSizeF() {
		QSizeF org = new QSizeF(20, 60);
		QSizeF clone = org.clone();
		org.dispose();
		QSizeF clone2 = clone.clone();
		assertEquals((Object)clone.width(), clone2.width());
		assertEquals((Object)clone.height(), clone2.height());
	}

	@Test
	public void run_clone_QSystemLocale_CurrencyToStringArgument() {
		QSystemLocale.CurrencyToStringArgument org = new QSystemLocale.CurrencyToStringArgument("symbol", "value");
		QSystemLocale.CurrencyToStringArgument clone = org.clone();
		org.dispose();
		QSystemLocale.CurrencyToStringArgument clone2 = clone.clone();
		assertEquals(clone.symbol(), clone2.symbol());
		assertEquals(clone.value(), clone2.value());
	}

	@Test
	public void run_clone_QTextBoundaryFinder() {
		QTextBoundaryFinder org = new QTextBoundaryFinder(QTextBoundaryFinder.BoundaryType.Word, "A");
		QTextBoundaryFinder clone = org.clone();
		org.dispose();
		QTextBoundaryFinder clone2 = clone.clone();
		assertEquals(clone.string(), clone2.string());
		assertEquals(clone.type(), clone2.type());
	}

	@Test
	public void run_clone_QTime() {
		QTime org = new QTime();
		QTime clone = org.clone();
		org.dispose();
		QTime clone2 = clone.clone();
		assertEquals(clone.msec(), clone2.msec());
		assertEquals(clone.second(), clone2.second());
		assertEquals(clone.minute(), clone2.minute());
		assertEquals(clone.hour(), clone2.hour());
	}

	@Test
	public void run_clone_QUuid() {
		QUuid org = QUuid.createUuid();
		QUuid clone = org.clone();
		org.dispose();
		QUuid clone2 = clone.clone();
		assertEquals(clone.toString(), clone2.toString());
	}

	public void run_clone_QXmlStreamAttributes() {
		QXmlStreamAttributes org = new QXmlStreamAttributes();
		org.append(new QXmlStreamAttribute());
		QXmlStreamAttributes clone = org.clone();
		org.dispose();
		QXmlStreamAttributes clone2 = clone.clone();
		assertEquals(clone, clone2);
	}

	@Test
	public void run_clone_QXmlStreamNamespaceDeclaration() {
		QXmlStreamNamespaceDeclaration org = new QXmlStreamNamespaceDeclaration("prefix", "uri.org");
		QXmlStreamNamespaceDeclaration clone = org.clone();
		org.dispose();
		QXmlStreamNamespaceDeclaration clone2 = clone.clone();
		assertEquals(clone.namespaceUri(), clone2.namespaceUri());
		assertEquals(clone.prefix(), clone2.prefix());
	}

	@Test
	public void run_clone_QXmlStreamNotationDeclaration() {
		QXmlStreamNotationDeclaration org = new QXmlStreamNotationDeclaration();
		QXmlStreamNotationDeclaration clone = org.clone();
		org.dispose();
		QXmlStreamNotationDeclaration clone2 = clone.clone();
		assertEquals(clone, clone2);
	}
	
	@Test
	public void run_clone_QAbstractTextDocumentLayout_PaintContext() {
		QAbstractTextDocumentLayout.PaintContext org = new QAbstractTextDocumentLayout.PaintContext();
		org.setCursorPosition(5);
		QAbstractTextDocumentLayout.PaintContext clone = org.clone();
		org.dispose();
		QAbstractTextDocumentLayout.PaintContext clone2 = clone.clone();
		assertEquals(clone.cursorPosition(), clone2.cursorPosition());
	}

	@Test
	public void run_clone_QAbstractTextDocumentLayout_Selection() {
		QAbstractTextDocumentLayout.Selection org = new QAbstractTextDocumentLayout.Selection();
		org.setFormat(new QTextCharFormat());
		QAbstractTextDocumentLayout.Selection clone = org.clone();
		org.dispose();
		QAbstractTextDocumentLayout.Selection clone2 = clone.clone();
		assertEquals(clone.format(), clone2.format());
	}

	@Test
	public void run_clone_QBitmap() {
		QBitmap org = new QBitmap(100, 400);
		QBitmap clone = org.clone();
		org.dispose();
		QBitmap clone2 = clone.clone();
		assertEquals(clone.width(), clone2.width());
		assertEquals(clone.height(), clone2.height());
	}

	@Test
	public void run_clone_QConicalGradient() {
		QConicalGradient org = new QConicalGradient();
		org.setAngle(50);
		org.setCenter(3.9, 4.1);
		QConicalGradient clone = org.clone();
		org.dispose();
		QConicalGradient clone2 = clone.clone();
		assertEquals((Object)clone.angle(), clone2.angle());
		assertEquals(clone.center(), clone2.center());
	}

	@Test
	public void run_clone_QInputMethodEvent_Attribute() {
		QInputMethodEvent.Attribute org = new QInputMethodEvent.Attribute(QInputMethodEvent.AttributeType.Language, 0, 1, "1");
		QInputMethodEvent.Attribute clone = org.clone();
		org.dispose();
		QInputMethodEvent.Attribute clone2 = clone.clone();
		assertEquals(clone.type(), clone2.type());
		assertEquals(clone.start(), clone2.start());
		assertEquals(clone.length(), clone2.length());
		assertEquals(clone.value(), clone2.value());
	}

	@Test
	public void run_clone_QItemSelection() {
		QStandardItemModel model = new QStandardItemModel();
		model.insertColumns(0, 5);
		model.insertRows(0, 5);
		QItemSelection org = new QItemSelection(model.index(0, 1), model.index(3, 4));
		QItemSelection clone = org.clone();
		org.dispose();
		QItemSelection clone2 = clone.clone();
		assertEquals(clone.count(), clone2.count());
		for (int i = 0; i < clone.count(); i++) {
			assertEquals(clone.at(i), clone2.at(i));
			assertEquals(clone.at(i).bottom(), clone2.at(i).bottom());
			assertEquals(clone.at(i).top(), clone2.at(i).top());
			assertEquals(clone.at(i).left(), clone2.at(i).left());
			assertEquals(clone.at(i).right(), clone2.at(i).right());
		}
	}

	@Test
	public void run_clone_QItemSelectionRange() {
		QStandardItemModel model = new QStandardItemModel();
		model.insertColumns(0, 5);
		model.insertRows(0, 5);
		QItemSelectionRange org = new QItemSelectionRange(model.index(0, 1), model.index(3, 4));
		QItemSelectionRange clone = org.clone();
		org.dispose();
		QItemSelectionRange clone2 = clone.clone();
		assertEquals(clone.bottom(), clone2.bottom());
		assertEquals(clone.top(), clone2.top());
		assertEquals(clone.left(), clone2.left());
		assertEquals(clone.right(), clone2.right());
	}

	@Test
	public void run_clone_QLine() {
		QLine org = new QLine(1, 5, 6, 9);
		QLine clone = org.clone();
		org.dispose();
		QLine clone2 = clone.clone();
		assertEquals(clone.p1(), clone2.p1());
		assertEquals(clone.p2(), clone2.p2());
	}

	@Test
	public void run_clone_QLinearGradient() {
		QLinearGradient org = new QLinearGradient();
		org.setStart(3, 13);
		QLinearGradient clone = org.clone();
		org.dispose();
		QLinearGradient clone2 = clone.clone();
		assertEquals(clone.start(), clone2.start());
	}

	@Test
	public void run_clone_QLineF() {
		QLineF org = new QLineF(1,2,3,4);
		QLineF clone = org.clone();
		org.dispose();
		QLineF clone2 = clone.clone();
		assertEquals(clone.p1(), clone2.p1());
		assertEquals(clone.p2(), clone2.p2());
	}

	@Test
	public void run_clone_QListWidgetItem() {
		QListWidgetItem org = new QListWidgetItem("itemtext");
		QListWidgetItem clone = org.clone();
		org.dispose();
		QListWidgetItem clone2 = clone.clone();
		assertEquals(clone.text(), clone2.text());
	}

	@Test
	public void run_clone_QMatrix4x4() {
		QMatrix4x4 org = new QMatrix4x4(1,2,3,4,5,6,7,8,9,10,11,12,3,14,15,16);
		QMatrix4x4 clone = org.clone();
		org.dispose();
		QMatrix4x4 clone2 = clone.clone();
		assertEquals(clone, clone2);
	}

	@Test
	public void run_clone_QPainter_PixmapFragment() {
		QPainter.PixmapFragment org = new QPainter.PixmapFragment();
		org.setHeight(2);
		org.setWidth(4);
		QPainter.PixmapFragment clone = org.clone();
		org.dispose();
		QPainter.PixmapFragment clone2 = clone.clone();
		assertEquals((Object)clone.width(), clone2.width());
		assertEquals((Object)clone.height(), clone2.height());
	}

	@Test
	public void run_clone_QPolygon() {
		QPolygon org = new QPolygon();
		org.add(6, 5);
		QPolygon clone = org.clone();
		org.dispose();
		QPolygon clone2 = clone.clone();
		assertEquals(clone.at(0), clone2.at(0));
	}

	@Test
	public void run_clone_QPolygonF() {
		QPolygonF org = new QPolygonF();
		org.add(6, 5);
		QPolygonF clone = org.clone();
		org.dispose();
		QPolygonF clone2 = clone.clone();
		assertEquals(clone.at(0), clone2.at(0));
	}

	@Test
	public void run_clone_QQuaternion() {
		QQuaternion org = new QQuaternion(0.0,1.0,2.0,3.0);
		QQuaternion clone = org.clone();
		org.dispose();
		QQuaternion clone2 = clone.clone();
		assertEquals((Object)clone.x(), 1.0);
		assertEquals((Object)clone.y(), 2.0);
		assertEquals((Object)clone.z(), 3.0);
		assertEquals((Object)clone.scalar(), 0.0);
		assertEquals((Object)clone.x(), clone2.x());
		assertEquals((Object)clone.y(), clone2.y());
		assertEquals((Object)clone.z(), clone2.z());
		assertEquals((Object)clone.scalar(), clone2.scalar());
	}

	@Test
	public void run_clone_QRadialGradient() {
		QRadialGradient org = new QRadialGradient();
		org.setCenter(1, 2);
		QRadialGradient clone = org.clone();
		org.dispose();
		QRadialGradient clone2 = clone.clone();
		assertEquals(clone.center(), clone2.center());
		assertEquals((Object)clone.center().x(), 1.0);
		assertEquals((Object)clone.center().y(), 2.0);
	}

	@Test
	public void run_clone_QSizePolicy() {
		QSizePolicy org = new QSizePolicy(QSizePolicy.Policy.Expanding, QSizePolicy.Policy.Maximum);
		QSizePolicy clone = org.clone();
		org.dispose();
		QSizePolicy clone2 = clone.clone();
		assertEquals(clone.horizontalPolicy(), QSizePolicy.Policy.Expanding);
		assertEquals(clone.verticalPolicy(), QSizePolicy.Policy.Maximum);
		assertEquals(clone.horizontalPolicy(), clone2.horizontalPolicy());
		assertEquals(clone.verticalPolicy(), clone2.verticalPolicy());
	}

	@Test
	public void run_clone_QStandardItem() {
		QStandardItem org = new QStandardItem("text");
		QStandardItem clone = org.clone();
		org.dispose();
		QStandardItem clone2 = clone.clone();
		assertEquals(clone.text(), "text");
		assertEquals(clone.text(), clone2.text());
	}

	@Test
	public void run_clone_QStaticText() {
		QStaticText org = new QStaticText("statictext");
		QStaticText clone = org.clone();
		org.dispose();
		QStaticText clone2 = clone.clone();
		assertEquals(clone.text(), "statictext");
		assertEquals(clone, clone2);
	}

	@Test
	public void run_clone_QTableWidgetItem() {
		QTableWidgetItem org = new QTableWidgetItem("text");
		QTableWidgetItem clone = org.clone();
		org.dispose();
		QTableWidgetItem clone2 = clone.clone();
		assertEquals(clone.text(), "text");
		assertEquals(clone.text(), clone2.text());
	}

	@Test
	public void run_clone_QTableWidgetSelectionRange() {
		QTableWidgetSelectionRange org = new QTableWidgetSelectionRange(1, 6, 5, 9);
		QTableWidgetSelectionRange clone = org.clone();
		org.dispose();
		QTableWidgetSelectionRange clone2 = clone.clone();
		assertEquals(clone.topRow(), 1);
		assertEquals(clone.leftColumn(), 6);
		assertEquals(clone.bottomRow(), 5);
		assertEquals(clone.rightColumn(), 9);
		assertEquals(clone.topRow(), clone2.topRow());
		assertEquals(clone.leftColumn(), clone2.leftColumn());
		assertEquals(clone.bottomRow(), clone2.bottomRow());
		assertEquals(clone.rightColumn(), clone2.rightColumn());
	}

	@Test
	public void run_clone_QTextBlockFormat() {
		QTextBlockFormat org = new QTextBlockFormat();
		org.setTopMargin(1.45);
		QTextBlockFormat clone = org.clone();
		org.dispose();
		QTextBlockFormat clone2 = clone.clone();
		assertEquals((Object)clone.topMargin(), 1.45);
		assertEquals((Object)clone.topMargin(), clone2.topMargin());
	}

	@Test
	public void run_clone_QTextCharFormat() {
		QTextCharFormat org = new QTextCharFormat();
		org.setFontPointSize(4.5);
		QTextCharFormat clone = org.clone();
		org.dispose();
		QTextCharFormat clone2 = clone.clone();
		assertEquals((Object)clone.fontPointSize(), 4.5);
		assertEquals((Object)clone.fontPointSize(), clone2.fontPointSize());
	}

	@Test
	public void run_clone_QTextDocument() {
		QTextDocument org = new QTextDocument();
		org.setPlainText("plaintext");
		QTextDocument clone = org.clone();
		org.dispose();
		QTextDocument clone2 = clone.clone();
		assertEquals(clone.toPlainText(), "plaintext");
		assertEquals(clone.toPlainText(), clone2.toPlainText());
	}

	@Test
	public void run_clone_QTextEdit_ExtraSelection() {
		QTextCharFormat format = new QTextCharFormat();
		format.setFontPointSize(4.5);
		QTextEdit.ExtraSelection org = new QTextEdit.ExtraSelection();
		org.setFormat(format);
		QTextEdit.ExtraSelection clone = org.clone();
		org.dispose();
		QTextEdit.ExtraSelection clone2 = clone.clone();
		assertEquals((Object)clone.format().fontPointSize(), format.fontPointSize());
		assertEquals((Object)clone.format().fontPointSize(), clone2.format().fontPointSize());
	}

	@Test
	public void run_clone_QTextFrameFormat() {
		QTextFrameFormat org = new QTextFrameFormat();
		org.setBorder(4.5);
		QTextFrameFormat clone = org.clone();
		org.dispose();
		QTextFrameFormat clone2 = clone.clone();
		assertEquals((Object)clone.border(), 4.5);
		assertEquals((Object)clone.border(), clone2.border());
	}

	@Test
	public void run_clone_QTextImageFormat() {
		QTextImageFormat org = new QTextImageFormat();
		org.setHeight(4.5);
		QTextImageFormat clone = org.clone();
		org.dispose();
		QTextImageFormat clone2 = clone.clone();
		assertEquals((Object)clone.height(), 4.5);
		assertEquals((Object)clone.height(), clone2.height());
	}

	/**
	 * this test never could success because void initialized QTextInlineObject is completely instable.
	 * QTextInlineObject's default constructor does not belong to the Qt API and is thus declared private in QtJambi.
	 */
//	@Test
//	public void run_clone_QTextInlineObject() {
//		QTextInlineObject org = new QTextInlineObject();
//		org.setDescent(6.987);
//		QTextInlineObject clone = org.clone();
//		org.dispose();
//		QTextInlineObject clone2 = clone.clone();
//		assertEquals((Object)clone.descent(), 6.987);
//		assertEquals((Object)clone.descent(), clone2.descent());
//	}

	@Test
	public void run_clone_QTextLayout_FormatRange() {
		QTextLayout.FormatRange org = new QTextLayout.FormatRange();
		org.setLength(6);
		org.setStart(3);
		QTextLayout.FormatRange clone = org.clone();
		org.dispose();
		QTextLayout.FormatRange clone2 = clone.clone();
		assertEquals(clone.start(), 3);
		assertEquals(clone.length(), 6);
		assertEquals(clone.start(), clone2.start());
		assertEquals(clone.length(), clone2.length());
	}

	@Test
	public void run_clone_QTextLength() {
		QTextLength org = new QTextLength();
		QTextLength clone = org.clone();
		org.dispose();
		QTextLength clone2 = clone.clone();
		assertEquals(clone, clone2);
	}

//	@Test
	public void run_clone_QTextLine() {
		QTextLayout layout = new QTextLayout("\n\n");
		layout.beginLayout();
		QTextLine org = layout.createLine();
		org.setPosition(new QPointF(2,3));
		QTextLine clone = org.clone();
		org.dispose();
		QTextLine clone2 = clone.clone();
		assertEquals((Object)clone.position().x(), 2.);
		assertEquals((Object)clone.position().y(), 3.);
		assertEquals((Object)clone.position(), clone2.position());
		layout.endLayout();
	}

	@Test
	public void run_clone_QTextListFormat() {
		QTextListFormat org = new QTextListFormat();
		org.setIndent(5);
		QTextListFormat clone = org.clone();
		org.dispose();
		QTextListFormat clone2 = clone.clone();
		assertEquals(clone.indent(), 5);
		assertEquals(clone.indent(), clone2.indent());
	}

	@Test
	public void run_clone_QTextOption_Tab() {
		QTextOption.Tab org = new QTextOption.Tab(1.0, QTextOption.TabType.LeftTab ,'0');
		QTextOption.Tab clone = org.clone();
		org.dispose();
		QTextOption.Tab clone2 = clone.clone();
		assertEquals(clone.delimiter(), '0');
		assertEquals(clone.type(), QTextOption.TabType.LeftTab);
		assertEquals((Object)clone.position(), 1.0);
		assertEquals(clone.delimiter(), clone2.delimiter());
		assertEquals(clone.type(), clone2.type());
		assertEquals((Object)clone.position(), clone2.position());
	}

	@Test
	public void run_clone_QTextTableCellFormat() {
		QTextTableCellFormat org = new QTextTableCellFormat();
		org.setLeftPadding(8.9);
		QTextTableCellFormat clone = org.clone();
		org.dispose();
		QTextTableCellFormat clone2 = clone.clone();
		assertEquals((Object)clone.leftPadding(), 8.9);
		assertEquals((Object)clone.leftPadding(), clone2.leftPadding());
	}

	@Test
	public void run_clone_QTextTableFormat() {
		QTextTableFormat org = new QTextTableFormat();
		org.setBorder(6.6);
		QTextTableFormat clone = org.clone();
		org.dispose();
		QTextTableFormat clone2 = clone.clone();
		assertEquals((Object)clone.border(), 6.6);
		assertEquals((Object)clone.border(), clone2.border());
	}

	@Test
	public void run_clone_QTileRules() {
		QTileRules org = new QTileRules(Qt.TileRule.StretchTile, Qt.TileRule.RoundTile);
		QTileRules clone = org.clone();
		org.dispose();
		QTileRules clone2 = clone.clone();
		assertEquals(clone.horizontal(), Qt.TileRule.StretchTile);
		assertEquals(clone.vertical(), Qt.TileRule.RoundTile);
		assertEquals(clone.horizontal(), clone2.horizontal());
		assertEquals(clone.vertical(), clone2.vertical());
	}

	@Test
	public void run_clone_QTouchEvent_TouchPoint() {
		QTouchEvent.TouchPoint org = new QTouchEvent.TouchPoint();
		org.setPos(new QPointF(5,6));
		QTouchEvent.TouchPoint clone = org.clone();
		org.dispose();
		QTouchEvent.TouchPoint clone2 = clone.clone();
		assertEquals((Object)clone.pos().x(), 5.);
		assertEquals((Object)clone.pos().y(), 6.);
		assertEquals(clone.pos(), clone2.pos());
	}

	@Test
	public void run_clone_QTreeWidgetItem() {
		QTreeWidgetItem org = new QTreeWidgetItem(Collections.singletonList("text"));
		QTreeWidgetItem clone = org.clone();
		org.dispose();
		QTreeWidgetItem clone2 = clone.clone();
		assertEquals(clone.text(0), "text");
		assertEquals(clone.text(0), clone2.text(0));
	}

	@Test
	public void run_clone_QVector2D() {
		QVector2D org = new QVector2D(1,2);
		QVector2D clone = org.clone();
		org.dispose();
		QVector2D clone2 = clone.clone();
		assertEquals((Object)clone.x(), 1.);
		assertEquals((Object)clone.y(), 2.);
		assertEquals((Object)clone.x(), clone2.x());
		assertEquals((Object)clone.y(), clone2.y());
	}

	@Test
	public void run_clone_QVector3D() {
		QVector3D org = new QVector3D(1,2,3);
		QVector3D clone = org.clone();
		org.dispose();
		QVector3D clone2 = clone.clone();
		assertEquals((Object)clone.x(), 1.);
		assertEquals((Object)clone.y(), 2.);
		assertEquals((Object)clone.z(), 3.);
		assertEquals((Object)clone.x(), clone2.x());
		assertEquals((Object)clone.y(), clone2.y());
		assertEquals((Object)clone.z(), clone2.z());
	}

	@Test
	public void run_clone_QVector4D() {
		QVector4D org = new QVector4D(1,2,3,0);
		QVector4D clone = org.clone();
		org.dispose();
		QVector4D clone2 = clone.clone();
		assertEquals((Object)clone.x(), 1.);
		assertEquals((Object)clone.y(), 2.);
		assertEquals((Object)clone.z(), 3.);
		assertEquals((Object)clone.w(), 0.);
		assertEquals((Object)clone.x(), clone2.x());
		assertEquals((Object)clone.y(), clone2.y());
		assertEquals((Object)clone.z(), clone2.z());
		assertEquals((Object)clone.w(), clone2.w());
	}
	
	@Test
	public void run_clone_QIPv6Address() {
		for(QHostAddress address : QNetworkInterface.allAddresses()){
			QIPv6Address org = address.toIPv6Address();
			QIPv6Address clone = org.clone();
			org.dispose();
			QIPv6Address clone2 = clone.clone();
			assertArrayEquals(clone.c(), clone2.c());
		}
	}

	@Test
	public void run_clone_QNetworkCacheMetaData() {
		QNetworkCacheMetaData org = new QNetworkCacheMetaData();
		org.setLastModified(QDateTime.currentDateTimeUtc());
		QNetworkCacheMetaData clone = org.clone();
		org.dispose();
		QNetworkCacheMetaData clone2 = clone.clone();
		assertEquals(clone.lastModified(), clone2.lastModified());
	}

	@Test
	public void run_clone_QNetworkConfiguration() {
		QNetworkConfiguration org = new QNetworkConfiguration();
		QNetworkConfiguration clone = org.clone();
		org.dispose();
		QNetworkConfiguration clone2 = clone.clone();
		assertEquals(clone, clone2);
	}

	@Test
	public void run_clone_QNetworkProxyQuery() {
		QNetworkProxyQuery org = new QNetworkProxyQuery();
		org.setPeerHostName("test.org");
		QNetworkProxyQuery clone = org.clone();
		org.dispose();
		QNetworkProxyQuery clone2 = clone.clone();
		assertEquals(clone.peerHostName(), "test.org");
		assertEquals(clone.peerHostName(), clone2.peerHostName());
	}

    public static void main(String args[]) {
        org.junit.runner.JUnitCore.main(TestCloneable.class.getName());
    }
}
