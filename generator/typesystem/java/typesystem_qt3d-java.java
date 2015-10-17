/****************************************************************************
**
** Copyright (C) 2012 Peter Droste. All rights reserved.
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

package generator;

import com.trolltech.qt.*;
import com.trolltech.qt.qt3d.*;

class QGLAttributeValue___java extends QGLAttributeValue {
	public static QGLAttributeValue fromColorArray(java.util.List<com.trolltech.qt.qt3d.QColor4ub> array){
		return new QGLAttributeValue(array, (com.trolltech.qt.qt3d.QColor4ub)null);
	}
	
	public static QGLAttributeValue fromVector2DArray(java.util.List<com.trolltech.qt.gui.QVector2D> array){
		return new QGLAttributeValue(array, (com.trolltech.qt.gui.QVector2D)null);
	}
	
	public static QGLAttributeValue fromVector3DArray(java.util.List<com.trolltech.qt.gui.QVector3D> array){
		return new QGLAttributeValue(array, (com.trolltech.qt.gui.QVector3D)null);
	}
	
	public static QGLAttributeValue fromVector4DArray(java.util.List<com.trolltech.qt.gui.QVector4D> array){
		return new QGLAttributeValue(array, (com.trolltech.qt.gui.QVector4D)null);
	}
	
	public static QGLAttributeValue fromFloatArray(java.util.List<Float> array){
		return new QGLAttributeValue(array, (Float)null);
	}

	private QGLAttributeValue(java.util.List<com.trolltech.qt.qt3d.QColor4ub> array, QColor4ub dummy){
        super((QPrivateConstructor)null);
        __qt_QGLAttributeValue_ColorList(array);
    }

    native void __qt_QGLAttributeValue_ColorList(java.util.List<com.trolltech.qt.qt3d.QColor4ub> array);

    private QGLAttributeValue(java.util.List<com.trolltech.qt.gui.QVector2D> array, com.trolltech.qt.gui.QVector2D dummy){
        super((QPrivateConstructor)null);
        __qt_QGLAttributeValue_Vector2DList(array);
    }

    native void __qt_QGLAttributeValue_Vector2DList(java.util.List<com.trolltech.qt.gui.QVector2D> array);

    private QGLAttributeValue(java.util.List<com.trolltech.qt.gui.QVector3D> array, com.trolltech.qt.gui.QVector3D dummy){
        super((QPrivateConstructor)null);
        __qt_QGLAttributeValue_Vector3DList(array);
    }

    native void __qt_QGLAttributeValue_Vector3DList(java.util.List<com.trolltech.qt.gui.QVector3D> array);

    private QGLAttributeValue(java.util.List<com.trolltech.qt.gui.QVector4D> array, com.trolltech.qt.gui.QVector4D dummy){
        super((QPrivateConstructor)null);
        __qt_QGLAttributeValue_Vector4DList(array);
    }

    native void __qt_QGLAttributeValue_Vector4DList(java.util.List<com.trolltech.qt.gui.QVector4D> array);

    private QGLAttributeValue(java.util.List<java.lang.Float> array, Float dummy){
        super((QPrivateConstructor)null);
        __qt_QGLAttributeValue_FloatList(array);
    }

    native void __qt_QGLAttributeValue_FloatList(java.util.List<java.lang.Float> array);
}// class


class QGLAttributeValue___cplusplus {
// QGLAttributeValue::QGLAttributeValue(const QArray<QColor4ub > & array)
extern "C" Q_DECL_EXPORT void JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_qt3d_QGLAttributeValue__1_1qt_1QGLAttributeValue_1ColorList__Ljava_util_List_2)
(JNIEnv *__jni_env,
 jobject __jni_object,
 jobject array0)
{
    QTJAMBI_DEBUG_TRACE("(native) entering: QGLAttributeValue::QGLAttributeValue(const QArray<QColor4ub > & array)");
    Q_UNUSED(__jni_env)
    Q_UNUSED(__jni_object)
    QArray<QColor4ub > __qt_array0;
    if (array0 != 0) {
        jobjectArray __qt__array = qtjambi_collection_toArray(__jni_env, array0);
        jsize __qt__size = __jni_env->GetArrayLength(__qt__array);
        for (int i=0; i<__qt__size; ++i) {
            jobject __java_element = __jni_env->GetObjectArrayElement(__qt__array, i);
            QColor4ub  __qt_element = (QColor4ub ) *(QColor4ub *)qtjambi_to_object(__jni_env, __java_element);
            QTJAMBI_EXCEPTION_CHECK(__jni_env);
            __qt_array0 << __qt_element;
        }
    }
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    QGLAttributeValue *__qt_this = new QGLAttributeValue((const QArray<QColor4ub >& )__qt_array0);
    QtJambiLink *__qt_java_link = qtjambi_construct_object(__jni_env, __jni_object, __qt_this, "QGLAttributeValue");
    if (!__qt_java_link) {
        qWarning("object construction failed for type: QGLAttributeValue");
        return;
    }
    __qt_java_link->setJavaOwnership(__jni_env, __jni_object);

}

// QGLAttributeValue::QGLAttributeValue(const QArray<QVector2D > & array)
extern "C" Q_DECL_EXPORT void JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_qt3d_QGLAttributeValue__1_1qt_1QGLAttributeValue_1Vector2DList__Ljava_util_List_2)
(JNIEnv *__jni_env,
 jobject __jni_object,
 jobject array0)
{
    QTJAMBI_DEBUG_TRACE("(native) entering: QGLAttributeValue::QGLAttributeValue(const QArray<QVector2D > & array)");
    Q_UNUSED(__jni_env)
    Q_UNUSED(__jni_object)
    QArray<QVector2D > __qt_array0;
    if (array0 != 0) {
        jobjectArray __qt__array = qtjambi_collection_toArray(__jni_env, array0);
        jsize __qt__size = __jni_env->GetArrayLength(__qt__array);
        for (int i=0; i<__qt__size; ++i) {
            jobject __java_element = __jni_env->GetObjectArrayElement(__qt__array, i);
            QVector2D  __qt_element = (QVector2D ) *(QVector2D *)qtjambi_to_object(__jni_env, __java_element);
            QTJAMBI_EXCEPTION_CHECK(__jni_env);
            __qt_array0 << __qt_element;
        }
    }
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    QGLAttributeValue *__qt_this = new QGLAttributeValue((const QArray<QVector2D >& )__qt_array0);
    QtJambiLink *__qt_java_link = qtjambi_construct_object(__jni_env, __jni_object, __qt_this, "QGLAttributeValue");
    if (!__qt_java_link) {
        qWarning("object construction failed for type: QGLAttributeValue");
        return;
    }
    __qt_java_link->setJavaOwnership(__jni_env, __jni_object);

}

// QGLAttributeValue::QGLAttributeValue(const QArray<QVector3D > & array)
extern "C" Q_DECL_EXPORT void JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_qt3d_QGLAttributeValue__1_1qt_1QGLAttributeValue_1Vector3DList__Ljava_util_List_2)
(JNIEnv *__jni_env,
 jobject __jni_object,
 jobject array0)
{
    QTJAMBI_DEBUG_TRACE("(native) entering: QGLAttributeValue::QGLAttributeValue(const QArray<QVector3D > & array)");
    Q_UNUSED(__jni_env)
    Q_UNUSED(__jni_object)
    QArray<QVector3D > __qt_array0;
    if (array0 != 0) {
        jobjectArray __qt__array = qtjambi_collection_toArray(__jni_env, array0);
        jsize __qt__size = __jni_env->GetArrayLength(__qt__array);
        for (int i=0; i<__qt__size; ++i) {
            jobject __java_element = __jni_env->GetObjectArrayElement(__qt__array, i);
            QVector3D  __qt_element = (QVector3D ) *(QVector3D *)qtjambi_to_object(__jni_env, __java_element);
            QTJAMBI_EXCEPTION_CHECK(__jni_env);
            __qt_array0 << __qt_element;
        }
    }
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    QGLAttributeValue *__qt_this = new QGLAttributeValue((const QArray<QVector3D >& )__qt_array0);
    QtJambiLink *__qt_java_link = qtjambi_construct_object(__jni_env, __jni_object, __qt_this, "QGLAttributeValue");
    if (!__qt_java_link) {
        qWarning("object construction failed for type: QGLAttributeValue");
        return;
    }
    __qt_java_link->setJavaOwnership(__jni_env, __jni_object);

}

// QGLAttributeValue::QGLAttributeValue(const QArray<QVector4D > & array)
extern "C" Q_DECL_EXPORT void JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_qt3d_QGLAttributeValue__1_1qt_1QGLAttributeValue_1Vector4DList__Ljava_util_List_2)
(JNIEnv *__jni_env,
 jobject __jni_object,
 jobject array0)
{
    QTJAMBI_DEBUG_TRACE("(native) entering: QGLAttributeValue::QGLAttributeValue(const QArray<QVector4D > & array)");
    Q_UNUSED(__jni_env)
    Q_UNUSED(__jni_object)
    QArray<QVector4D > __qt_array0;
    if (array0 != 0) {
        jobjectArray __qt__array = qtjambi_collection_toArray(__jni_env, array0);
        jsize __qt__size = __jni_env->GetArrayLength(__qt__array);
        for (int i=0; i<__qt__size; ++i) {
            jobject __java_element = __jni_env->GetObjectArrayElement(__qt__array, i);
            QVector4D  __qt_element = (QVector4D ) *(QVector4D *)qtjambi_to_object(__jni_env, __java_element);
            QTJAMBI_EXCEPTION_CHECK(__jni_env);
            __qt_array0 << __qt_element;
        }
    }
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    QGLAttributeValue *__qt_this = new QGLAttributeValue((const QArray<QVector4D >& )__qt_array0);
    QtJambiLink *__qt_java_link = qtjambi_construct_object(__jni_env, __jni_object, __qt_this, "QGLAttributeValue");
    if (!__qt_java_link) {
        qWarning("object construction failed for type: QGLAttributeValue");
        return;
    }
    __qt_java_link->setJavaOwnership(__jni_env, __jni_object);

}

// QGLAttributeValue::QGLAttributeValue(const QArray<float > & array)
extern "C" Q_DECL_EXPORT void JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_qt3d_QGLAttributeValue__1_1qt_1QGLAttributeValue_1FloatList__Ljava_util_List_2)
(JNIEnv *__jni_env,
 jobject __jni_object,
 jobject array0)
{
    QTJAMBI_DEBUG_TRACE("(native) entering: QGLAttributeValue::QGLAttributeValue(const QArray<float > & array)");
    Q_UNUSED(__jni_env)
    Q_UNUSED(__jni_object)
    QArray<float > __qt_array0;
    if (array0 != 0) {
        jobjectArray __qt__array = qtjambi_collection_toArray(__jni_env, array0);
        jsize __qt__size = __jni_env->GetArrayLength(__qt__array);
        for (int i=0; i<__qt__size; ++i) {
            jobject __java_element = __jni_env->GetObjectArrayElement(__qt__array, i);
            float  __qt_element = (float ) qtjambi_to_float(__jni_env, __java_element);
            QTJAMBI_EXCEPTION_CHECK(__jni_env);
            __qt_array0 << __qt_element;
        }
    }
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    QGLAttributeValue *__qt_this = new QGLAttributeValue((const QArray<float >& )__qt_array0);
    QtJambiLink *__qt_java_link = qtjambi_construct_object(__jni_env, __jni_object, __qt_this, "QGLAttributeValue");
    if (!__qt_java_link) {
        qWarning("object construction failed for type: QGLAttributeValue");
        return;
    }
    __qt_java_link->setJavaOwnership(__jni_env, __jni_object);

}
}// class












class QCustomDataArray___java extends QCustomDataArray {
	public static QCustomDataArray fromColorArray(java.util.List<com.trolltech.qt.qt3d.QColor4ub> array){
		return new QCustomDataArray(array, (com.trolltech.qt.qt3d.QColor4ub)null);
	}
	
	public static QCustomDataArray fromVector2DArray(java.util.List<com.trolltech.qt.gui.QVector2D> array){
		return new QCustomDataArray(array, (com.trolltech.qt.gui.QVector2D)null);
	}
	
	public static QCustomDataArray fromVector3DArray(java.util.List<com.trolltech.qt.gui.QVector3D> array){
		return new QCustomDataArray(array, (com.trolltech.qt.gui.QVector3D)null);
	}
	
	public static QCustomDataArray fromVector4DArray(java.util.List<com.trolltech.qt.gui.QVector4D> array){
		return new QCustomDataArray(array, (com.trolltech.qt.gui.QVector4D)null);
	}
	
	public static QCustomDataArray fromFloatArray(java.util.List<Float> array){
		return new QCustomDataArray(array, (Float)null);
	}
	
    private QCustomDataArray(java.util.List<com.trolltech.qt.qt3d.QColor4ub> other, com.trolltech.qt.qt3d.QColor4ub dummy){
        super((QPrivateConstructor)null);
        __qt_QCustomDataArray_ColorList(other);
    }

    native void __qt_QCustomDataArray_ColorList(java.util.List<com.trolltech.qt.qt3d.QColor4ub> other);

    private QCustomDataArray(java.util.List<com.trolltech.qt.gui.QVector2D> other, com.trolltech.qt.gui.QVector2D dummy){
        super((QPrivateConstructor)null);
        __qt_QCustomDataArray_Vector2DList(other);
    }

    native void __qt_QCustomDataArray_Vector2DList(java.util.List<com.trolltech.qt.gui.QVector2D> other);

    private QCustomDataArray(java.util.List<com.trolltech.qt.gui.QVector3D> other, com.trolltech.qt.gui.QVector3D dummy){
        super((QPrivateConstructor)null);
        __qt_QCustomDataArray_Vector3DList(other);
    }

    native void __qt_QCustomDataArray_Vector3DList(java.util.List<com.trolltech.qt.gui.QVector3D> other);

    private QCustomDataArray(java.util.List<com.trolltech.qt.gui.QVector4D> other, com.trolltech.qt.gui.QVector4D dummy){
        super((QPrivateConstructor)null);
        __qt_QCustomDataArray_Vector4DList(other);
    }

    native void __qt_QCustomDataArray_Vector4DList(java.util.List<com.trolltech.qt.gui.QVector4D> other);

    private QCustomDataArray(java.util.List<java.lang.Float> other, java.lang.Float dummy){
        super((QPrivateConstructor)null);
        __qt_QCustomDataArray_FloatList(other);
    }

    native void __qt_QCustomDataArray_FloatList(java.util.List<java.lang.Float> other);
}// class


class QCustomDataArray___cplusplus {
// QCustomDataArray::QCustomDataArray(const QArray<QColor4ub > & other)
extern "C" Q_DECL_EXPORT void JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_qt3d_QCustomDataArray__1_1qt_1QCustomDataArray_1ColorList__Ljava_util_List_2)
(JNIEnv *__jni_env,
 jobject __jni_object,
 jobject other0)
{
    QTJAMBI_DEBUG_TRACE("(native) entering: QCustomDataArray::QCustomDataArray(const QArray<QColor4ub > & other)");
    Q_UNUSED(__jni_env)
    Q_UNUSED(__jni_object)
    QArray<QColor4ub > __qt_other0;
    if (other0 != 0) {
        jobjectArray __qt__array = qtjambi_collection_toArray(__jni_env, other0);
        jsize __qt__size = __jni_env->GetArrayLength(__qt__array);
        for (int i=0; i<__qt__size; ++i) {
            jobject __java_element = __jni_env->GetObjectArrayElement(__qt__array, i);
            QColor4ub  __qt_element = (QColor4ub ) *(QColor4ub *)qtjambi_to_object(__jni_env, __java_element);
            QTJAMBI_EXCEPTION_CHECK(__jni_env);
            __qt_other0 << __qt_element;
        }
    }
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    QCustomDataArray *__qt_this = new QCustomDataArray((const QArray<QColor4ub >& )__qt_other0);
    QtJambiLink *__qt_java_link = qtjambi_construct_object(__jni_env, __jni_object, __qt_this, "QCustomDataArray");
    if (!__qt_java_link) {
        qWarning("object construction failed for type: QCustomDataArray");
        return;
    }
    __qt_java_link->setJavaOwnership(__jni_env, __jni_object);

}

// QCustomDataArray::QCustomDataArray(const QArray<QVector2D > & other)
extern "C" Q_DECL_EXPORT void JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_qt3d_QCustomDataArray__1_1qt_1QCustomDataArray_1Vector2DList__Ljava_util_List_2)
(JNIEnv *__jni_env,
 jobject __jni_object,
 jobject other0)
{
    QTJAMBI_DEBUG_TRACE("(native) entering: QCustomDataArray::QCustomDataArray(const QArray<QVector2D > & other)");
    Q_UNUSED(__jni_env)
    Q_UNUSED(__jni_object)
    QArray<QVector2D > __qt_other0;
    if (other0 != 0) {
        jobjectArray __qt__array = qtjambi_collection_toArray(__jni_env, other0);
        jsize __qt__size = __jni_env->GetArrayLength(__qt__array);
        for (int i=0; i<__qt__size; ++i) {
            jobject __java_element = __jni_env->GetObjectArrayElement(__qt__array, i);
            QVector2D  __qt_element = (QVector2D ) *(QVector2D *)qtjambi_to_object(__jni_env, __java_element);
            QTJAMBI_EXCEPTION_CHECK(__jni_env);
            __qt_other0 << __qt_element;
        }
    }
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    QCustomDataArray *__qt_this = new QCustomDataArray((const QArray<QVector2D >& )__qt_other0);
    QtJambiLink *__qt_java_link = qtjambi_construct_object(__jni_env, __jni_object, __qt_this, "QCustomDataArray");
    if (!__qt_java_link) {
        qWarning("object construction failed for type: QCustomDataArray");
        return;
    }
    __qt_java_link->setJavaOwnership(__jni_env, __jni_object);

}

// QCustomDataArray::QCustomDataArray(const QArray<QVector3D > & other)
extern "C" Q_DECL_EXPORT void JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_qt3d_QCustomDataArray__1_1qt_1QCustomDataArray_1Vector3DList__Ljava_util_List_2)
(JNIEnv *__jni_env,
 jobject __jni_object,
 jobject other0)
{
    QTJAMBI_DEBUG_TRACE("(native) entering: QCustomDataArray::QCustomDataArray(const QArray<QVector3D > & other)");
    Q_UNUSED(__jni_env)
    Q_UNUSED(__jni_object)
    QArray<QVector3D > __qt_other0;
    if (other0 != 0) {
        jobjectArray __qt__array = qtjambi_collection_toArray(__jni_env, other0);
        jsize __qt__size = __jni_env->GetArrayLength(__qt__array);
        for (int i=0; i<__qt__size; ++i) {
            jobject __java_element = __jni_env->GetObjectArrayElement(__qt__array, i);
            QVector3D  __qt_element = (QVector3D ) *(QVector3D *)qtjambi_to_object(__jni_env, __java_element);
            QTJAMBI_EXCEPTION_CHECK(__jni_env);
            __qt_other0 << __qt_element;
        }
    }
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    QCustomDataArray *__qt_this = new QCustomDataArray((const QArray<QVector3D >& )__qt_other0);
    QtJambiLink *__qt_java_link = qtjambi_construct_object(__jni_env, __jni_object, __qt_this, "QCustomDataArray");
    if (!__qt_java_link) {
        qWarning("object construction failed for type: QCustomDataArray");
        return;
    }
    __qt_java_link->setJavaOwnership(__jni_env, __jni_object);

}

// QCustomDataArray::QCustomDataArray(const QArray<QVector4D > & other)
extern "C" Q_DECL_EXPORT void JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_qt3d_QCustomDataArray__1_1qt_1QCustomDataArray_1Vector4DList__Ljava_util_List_2)
(JNIEnv *__jni_env,
 jobject __jni_object,
 jobject other0)
{
    QTJAMBI_DEBUG_TRACE("(native) entering: QCustomDataArray::QCustomDataArray(const QArray<QVector4D > & other)");
    Q_UNUSED(__jni_env)
    Q_UNUSED(__jni_object)
    QArray<QVector4D > __qt_other0;
    if (other0 != 0) {
        jobjectArray __qt__array = qtjambi_collection_toArray(__jni_env, other0);
        jsize __qt__size = __jni_env->GetArrayLength(__qt__array);
        for (int i=0; i<__qt__size; ++i) {
            jobject __java_element = __jni_env->GetObjectArrayElement(__qt__array, i);
            QVector4D  __qt_element = (QVector4D ) *(QVector4D *)qtjambi_to_object(__jni_env, __java_element);
            QTJAMBI_EXCEPTION_CHECK(__jni_env);
            __qt_other0 << __qt_element;
        }
    }
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    QCustomDataArray *__qt_this = new QCustomDataArray((const QArray<QVector4D >& )__qt_other0);
    QtJambiLink *__qt_java_link = qtjambi_construct_object(__jni_env, __jni_object, __qt_this, "QCustomDataArray");
    if (!__qt_java_link) {
        qWarning("object construction failed for type: QCustomDataArray");
        return;
    }
    __qt_java_link->setJavaOwnership(__jni_env, __jni_object);

}

// QCustomDataArray::QCustomDataArray(const QArray<float > & other)
extern "C" Q_DECL_EXPORT void JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_qt3d_QCustomDataArray__1_1qt_1QCustomDataArray_1FloatList__Ljava_util_List_2)
(JNIEnv *__jni_env,
 jobject __jni_object,
 jobject other0)
{
    QTJAMBI_DEBUG_TRACE("(native) entering: QCustomDataArray::QCustomDataArray(const QArray<float > & other)");
    Q_UNUSED(__jni_env)
    Q_UNUSED(__jni_object)
    QArray<float > __qt_other0;
    if (other0 != 0) {
        jobjectArray __qt__array = qtjambi_collection_toArray(__jni_env, other0);
        jsize __qt__size = __jni_env->GetArrayLength(__qt__array);
        for (int i=0; i<__qt__size; ++i) {
            jobject __java_element = __jni_env->GetObjectArrayElement(__qt__array, i);
            float  __qt_element = (float ) qtjambi_to_float(__jni_env, __java_element);
            QTJAMBI_EXCEPTION_CHECK(__jni_env);
            __qt_other0 << __qt_element;
        }
    }
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    QCustomDataArray *__qt_this = new QCustomDataArray((const QArray<float >& )__qt_other0);
    QtJambiLink *__qt_java_link = qtjambi_construct_object(__jni_env, __jni_object, __qt_this, "QCustomDataArray");
    if (!__qt_java_link) {
        qWarning("object construction failed for type: QCustomDataArray");
        return;
    }
    __qt_java_link->setJavaOwnership(__jni_env, __jni_object);

}
}// class




class QGLIndexBuffer___java{
	
	@QtBlockedSlot
    public final java.util.List<Integer> indexes()    {
        if (nativeId() == 0)
            throw new QNoNativeResourcesException("Function call on incomplete object of type: " +getClass().getName());
        return __qt_indexes(nativeId());
    }
	
    @QtBlockedSlot
    native java.util.List<Integer> __qt_indexes(long __this__nativeId);
}// class

class QGLIndexBuffer___cplusplus{

// QGLIndexBuffer::indexesUInt() const
extern "C" Q_DECL_EXPORT jobject JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_qt3d_QGLIndexBuffer__1_1qt_1indexes__J)
(JNIEnv *__jni_env,
 jobject,
 jlong __this_nativeId)
{
    QTJAMBI_DEBUG_TRACE("(native) entering: QGLIndexBuffer::indexesUInt() const");
    Q_UNUSED(__jni_env)
    Q_UNUSED(__this_nativeId)
    QGLIndexBuffer *__qt_this = (QGLIndexBuffer *) qtjambi_from_jlong(__this_nativeId);
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    Q_ASSERT(__qt_this);
    QArray<uint>  __qt_return_value = __qt_this->indexesUInt();

    jobject __java_return_value = qtjambi_arraylist_new(__jni_env, __qt_return_value.size());
    for (int i=0; i<__qt_return_value.size(); i++) {
        jobject __java_tmp = qtjambi_from_int(__jni_env, (int)__qt_return_value[i]);
        QTJAMBI_EXCEPTION_CHECK(__jni_env);
        qtjambi_collection_add(__jni_env, __java_return_value, __java_tmp);
    }
	QTJAMBI_EXCEPTION_CHECK(__jni_env);
    
    QTJAMBI_DEBUG_TRACE("(native) -> leaving: QGLIndexBuffer::indexesUInt() const");
    return __java_return_value;
}
}// class

class QGeometryData___java{

	@QtBlockedSlot
    public final java.util.List<QColor4ub> colors()    {
        if (nativeId() == 0)
            throw new QNoNativeResourcesException("Function call on incomplete object of type: " +getClass().getName());
        return __qt_colors(nativeId());
    }
    @QtBlockedSlot
    native java.util.List<QColor4ub> __qt_colors(long __this__nativeId);

    @QtBlockedSlot
    public final java.util.List<Integer> indices()    {
        if (nativeId() == 0)
            throw new QNoNativeResourcesException("Function call on incomplete object of type: " +getClass().getName());
        return __qt_indices(nativeId());
    }
    @QtBlockedSlot
    native java.util.List<Integer> __qt_indices(long __this__nativeId);
}// class
	
	
class QGeometryData___cplusplus{

// QGeometryData::colors() const
extern "C" Q_DECL_EXPORT jobject JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_qt3d_QGeometryData__1_1qt_1colors__J)
(JNIEnv *__jni_env,
 jobject,
 jlong __this_nativeId)
{
    QTJAMBI_DEBUG_TRACE("(native) entering: QGeometryData::colors() const");
    Q_UNUSED(__jni_env)
    Q_UNUSED(__this_nativeId)
    QtJambiShell_QGeometryData *__qt_this = (QtJambiShell_QGeometryData *) qtjambi_from_jlong(__this_nativeId);
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    Q_ASSERT(__qt_this);
    QArray<QColor4ub>  __qt_return_value = __qt_this->colors();

    
    // TEMPLATE - qt3d.qarray.to.arraylist - START
    jobject __java_return_value = qtjambi_arraylist_new(__jni_env, __qt_return_value.size());
    for (int i=0; i<__qt_return_value.size(); i++) {
    jobject __java_tmp = qtjambi_from_object(__jni_env, new QColor4ub(__qt_return_value[i]), "QColor4ub", "com/trolltech/qt/qt3d/", true);
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    qtjambi_collection_add(__jni_env, __java_return_value, __java_tmp);
    }
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    // TEMPLATE - qt3d.qarray.to.arraylist - END
    
    QTJAMBI_DEBUG_TRACE("(native) -> leaving: QGeometryData::colors() const");
    return __java_return_value;
}

// QGeometryData::indices() const
extern "C" Q_DECL_EXPORT jobject JNICALL QTJAMBI_FUNCTION_PREFIX(Java_com_trolltech_qt_qt3d_QGeometryData__1_1qt_1indices__J)
(JNIEnv *__jni_env,
 jobject,
 jlong __this_nativeId)
{
    QTJAMBI_DEBUG_TRACE("(native) entering: QGeometryData::indices() const");
    Q_UNUSED(__jni_env)
    Q_UNUSED(__this_nativeId)
    QtJambiShell_QGeometryData *__qt_this = (QtJambiShell_QGeometryData *) qtjambi_from_jlong(__this_nativeId);
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    Q_ASSERT(__qt_this);
    QGL::IndexArray  __qt_return_value = __qt_this->indices();

    
    // TEMPLATE - qt3d.qarray.to.integer.arraylist - START
    jobject __java_return_value = qtjambi_arraylist_new(__jni_env, __qt_return_value.size());
    for (int i=0; i<__qt_return_value.size(); i++) {
    jobject __java_tmp = qtjambi_from_int(__jni_env, (int)__qt_return_value[i]);
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    qtjambi_collection_add(__jni_env, __java_return_value, __java_tmp);
    }
    QTJAMBI_EXCEPTION_CHECK(__jni_env);
    // TEMPLATE - qt3d.qarray.to.integer.arraylist - END
    
    QTJAMBI_DEBUG_TRACE("(native) -> leaving: QGeometryData::indices() const");
    return __java_return_value;
}
}// class
