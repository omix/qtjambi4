TARGET = com_trolltech_qt_qt3dquick

include(../qtjambi/qtjambi_include.pri)
include ($$QTJAMBI_CPP/com_trolltech_qt_qt3dquick/com_trolltech_qt_qt3dquick.pri)

win32:CONFIG += precompile_header
CONFIG += qt3dquick
QT += core gui declarative opengl script
#INCLUDEPATH += $(QTDIR)/include/Qt3D $(QTDIR)/include/Qt3DQuick
#DEPENDPATH += $(QTDIR)/lib/Qt3D.lib $(QTDIR)/lib/Qt3DQuick.lib
#LIBS += -lQt3D -lQt3DQuick

