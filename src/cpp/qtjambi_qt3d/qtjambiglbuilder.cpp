#include <Qt3D/qglbuilder.h>
#include <Qt3D/qglbezierpatches.h>
#include "qtjambiglbuilder.h"

QtJambiGLBuilder::QtJambiGLBuilder(QGLMaterialCollection *materials)
 : QGLBuilder(materials){}
 
QtJambiGLBuilder::~QtJambiGLBuilder(){}

void QtJambiGLBuilder::addPatches(const QGLBezierPatches &patches){
	*this << patches;
}

QGLSceneNode * QtJambiGLBuilder::sceneNode(){
	return QGLBuilder::sceneNode();
}

QGLSceneNode * QtJambiGLBuilder::currentNode(){
	return QGLBuilder::currentNode();
}

QGLSceneNode * QtJambiGLBuilder::newNode(){
	return QGLBuilder::newNode();
}

QGLSceneNode * QtJambiGLBuilder::pushNode(){
	return QGLBuilder::pushNode();
}

QGLSceneNode * QtJambiGLBuilder::popNode(){
	return QGLBuilder::popNode();
}

QGLMaterialCollection * QtJambiGLBuilder::palette(){
	return QGLBuilder::palette();
}

QGLSceneNode * QtJambiGLBuilder::finalizedSceneNode(){
	return QGLBuilder::finalizedSceneNode();
}


void QtJambiGLBuilder::addTriangles(const QGeometryData &triangle){
	QGLBuilder::addTriangles(triangle);
}

void QtJambiGLBuilder::addQuads(const QGeometryData &quad){
	QGLBuilder::addQuads(quad);
}

void QtJambiGLBuilder::addTriangleFan(const QGeometryData &fan){
	QGLBuilder::addTriangleFan(fan);
}

void QtJambiGLBuilder::addTriangleStrip(const QGeometryData &strip){
	QGLBuilder::addTriangleStrip(strip);
}

void QtJambiGLBuilder::addTriangulatedFace(const QGeometryData &face){
	QGLBuilder::addTriangulatedFace(face);
}

void QtJambiGLBuilder::addQuadStrip(const QGeometryData &strip){
	QGLBuilder::addQuadStrip(strip);
}

void QtJambiGLBuilder::addQuadsInterleaved(const QGeometryData &top,
					const QGeometryData &bottom){
	QGLBuilder::addQuadsInterleaved(top, bottom);
}

void QtJambiGLBuilder::addPane(qreal size){
	QGLBuilder::addPane(size);
}

void QtJambiGLBuilder::addPane(QSizeF size){
	QGLBuilder::addPane(size);
}

void QtJambiGLBuilder::addCube(const QGLCube &cube){
	*this << cube;
}

void QtJambiGLBuilder::addCylinder(const QGLCylinder &cylinder){
	*this << cylinder;
}

void QtJambiGLBuilder::addDome(const QGLDome &dome){
	*this << dome;
}

void QtJambiGLBuilder::addSphere(const QGLSphere &sphere){
	*this << sphere;
}

void QtJambiGLBuilder::newSection(QGL::Smoothing sm){
	QGLBuilder::newSection(sm);
}
