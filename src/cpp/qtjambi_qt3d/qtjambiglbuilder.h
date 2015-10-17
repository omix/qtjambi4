#ifndef QTJAMBIGLBUILDER_H
#define QTJAMBIGLBUILDER_H

#include <Qt3D/qglbuilder.h>
#include <Qt3D/qglbezierpatches.h>
#include <Qt3D/qglcube.h>
#include <Qt3D/qgldome.h>
#include <Qt3D/qglsphere.h>
#include <Qt3D/qglcylinder.h>
#include <Qt3D/qglnamespace.h>

// NOTE:
// this class includes the non-member
// QGLBuilder & operator<< ( QGLBuilder & list, const QGLBezierPatches & patches )
// as member of QGLBuilder
class QtJambiGLBuilder : public QGLBuilder
{
public:
	explicit QtJambiGLBuilder(QGLMaterialCollection *materials = 0);
    virtual ~QtJambiGLBuilder();
	
	void addPatches(const QGLBezierPatches &patches);
	void addCube(const QGLCube &cube);
	void addCylinder(const QGLCylinder &cylinder);
	void addDome(const QGLDome &dome);
	void addSphere(const QGLSphere &sphere);
	
	void newSection(QGL::Smoothing sm = QGL::Smooth);

    // scene management
    QGLSceneNode *sceneNode();
    QGLSceneNode *currentNode();
    QGLSceneNode *newNode();
    QGLSceneNode *pushNode();
    QGLSceneNode *popNode();
    QGLMaterialCollection *palette();
    QGLSceneNode *finalizedSceneNode();

    // geometry building by primitive
    void addTriangles(const QGeometryData &triangle);
    void addQuads(const QGeometryData &quad);
    void addTriangleFan(const QGeometryData &fan);
    void addTriangleStrip(const QGeometryData &strip);
    void addTriangulatedFace(const QGeometryData &face);
    void addQuadStrip(const QGeometryData &strip);
    void addQuadsInterleaved(const QGeometryData &top,
                        const QGeometryData &bottom);
    void addPane(qreal size = 1.0f);
    void addPane(QSizeF size);
};

#endif // QTJAMBIGLBUILDER_H
