TARGET = com_trolltech_qt_qt3d

include(../qtjambi/qtjambi_include.pri)
include ($$QTJAMBI_CPP/com_trolltech_qt_qt3d/com_trolltech_qt_qt3d.pri)

win32:CONFIG += precompile_header
PRECOMPILED_HEADER = precompiledheaders_qt3d.h

#CONFIG += qt3d_deploy_pkg
CONFIG += qt3d
QT += core gui opengl declarative xml
#INCLUDEPATH += $(QTDIR)/include/Qt3D
#DEPENDPATH += $(QTDIR)/lib/Qt3D.lib
#LIBS += -lQt3D

#DEFINES = QT_DLL QT_NO_DEBUG
#win32:DEFINES = QT_MAKEDLL
