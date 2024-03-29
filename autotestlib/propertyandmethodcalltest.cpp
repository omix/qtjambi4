#include "propertyandmethodcalltest.h"

PropertyAndMethodCallTest::PropertyAndMethodCallTest(QObject *parent) :
    QObject(parent),
    m_receivedEnum(0),
    m_receivedColor(),
    m_receivedQtEnum(),
    m_receivedList(),
    m_receivedNumber(),
    m_receivedCustomQtValue(0),
    m_receivedDerivedQObject(0),
    m_receivedCustomJavaType()
{
}

bool PropertyAndMethodCallTest::connectSignals(QObject* sender){
    bool result = true;
    result &= QObject::connect(sender, SIGNAL(customEnumChanged(JEnumWrapper)), this, SLOT(receiveCustomEnum(JEnumWrapper)));
    result &= QObject::connect(sender, SIGNAL(customQtEnumChanged(JEnumWrapper)), this, SLOT(receiveCustomQtEnum(JEnumWrapper)));
    result &= QObject::connect(sender, SIGNAL(customQtFlagsChanged(JEnumWrapper)), this, SLOT(receiveQtFlags(JEnumWrapper)));
    result &= QObject::connect(sender, SIGNAL(customColorChanged(QColor)), this, SLOT(receiveColor(QColor)));
    result &= QObject::connect(sender, SIGNAL(customQtValueChanged(QGraphicsItem*)), this, SLOT(receiveCustomQtValue(QGraphicsItem*)));
    result &= QObject::connect(sender, SIGNAL(customJavaTypeChanged(JObjectWrapper)), this, SLOT(receiveCustomJavaType(JObjectWrapper)));
    result &= QObject::connect(sender, SIGNAL(derivedQObjectChanged(QObject*)), this, SLOT(receiveDerivedQObject(QObject*)));
    return true;
}

void PropertyAndMethodCallTest::receiveCustomEnum(JEnumWrapper value){
    m_receivedEnum = value;
}

void PropertyAndMethodCallTest::receiveColor(QColor value){
    m_receivedColor = value;
}

void PropertyAndMethodCallTest::receiveCustomQtEnum(JEnumWrapper value){
    m_receivedQtEnum = value;
}

void PropertyAndMethodCallTest::receiveQtFlags(JEnumWrapper value){
    m_receivedQtFlags = value;
}

void PropertyAndMethodCallTest::receiveList(JObjectWrapper value){
    m_receivedList = value;
}

void PropertyAndMethodCallTest::receiveNumber(JObjectWrapper value){
    m_receivedNumber = value;
}

void PropertyAndMethodCallTest::receiveCustomQtValue(QGraphicsItem* value){
    m_receivedCustomQtValue = value;
}

void PropertyAndMethodCallTest::receiveCustomJavaType(JObjectWrapper value){
    m_receivedCustomJavaType = value;
}

void PropertyAndMethodCallTest::receiveDerivedQObject(QObject* value){
    m_receivedDerivedQObject = value;
}

JEnumWrapper PropertyAndMethodCallTest::receivedCustomEnum(){
    return m_receivedEnum;
}

QColor PropertyAndMethodCallTest::receivedColor(){
    return m_receivedColor;
}

JEnumWrapper PropertyAndMethodCallTest::receivedCustomQtEnum(){
    return m_receivedQtEnum;
}

JObjectWrapper PropertyAndMethodCallTest::receivedList(){
    return m_receivedList;
}

JObjectWrapper PropertyAndMethodCallTest::receivedNumber(){
    return m_receivedNumber;
}

QGraphicsItem* PropertyAndMethodCallTest::receivedCustomQtValue(){
    return m_receivedCustomQtValue;
}

JObjectWrapper PropertyAndMethodCallTest::receivedCustomJavaType(){
    return m_receivedCustomJavaType;
}

QObject* PropertyAndMethodCallTest::receivedDerivedQObject(){
    return m_receivedDerivedQObject;
}

JEnumWrapper PropertyAndMethodCallTest::receivedQtFlags(){
    return m_receivedQtFlags;
}

bool PropertyAndMethodCallTest::testMethodCallNumber(QObject* qobj){
    GETMETHOD_TEST(JObjectWrapper, Number)
}

bool PropertyAndMethodCallTest::testMethodCallEnum(QObject* qobj){
    GETMETHOD_TEST(JEnumWrapper, Enum)
}

bool PropertyAndMethodCallTest::testMethodCallColor(QObject* qobj){
    GETMETHOD_TEST(QColor, Color)
}

bool PropertyAndMethodCallTest::testMethodCallQtEnum(QObject* qobj){
    GETMETHOD_TEST(Qt::AspectRatioMode, QtEnum)
}

bool PropertyAndMethodCallTest::testMethodCallDerivedQObject(QObject* qobj){
    GETMETHOD_TEST(QObject*, DerivedQObject)

}

bool PropertyAndMethodCallTest::testMethodCallCustomQtEnum(QObject* qobj){
    GETMETHOD_TEST(JEnumWrapper, CustomQtEnum)
}

bool PropertyAndMethodCallTest::testMethodCallQtFlags(QObject* qobj){
    GETMETHOD_TEST(JEnumWrapper, QtFlags)
}

bool PropertyAndMethodCallTest::testMethodCallCustomQtValue(QObject* qobj){
    GETMETHOD_TEST(QGraphicsItem*, CustomQtValue)
}

bool PropertyAndMethodCallTest::testMethodCallCustomJavaType(QObject* qobj){
    GETMETHOD_TEST(JObjectWrapper, CustomJavaType)
}

bool PropertyAndMethodCallTest::testMethodCallCustomQtEnum2(QObject* qobj){
    GETMETHOD_TEST(JEnumWrapper, CustomQtEnum2)
}

bool PropertyAndMethodCallTest::testFetchPropertyNumber(QObject* qobj){
    PROPERTY_TEST(JObjectWrapper, Number)
}

bool PropertyAndMethodCallTest::testFetchPropertyEnum(QObject* qobj){
    PROPERTY_TEST(JEnumWrapper, Enum)
}

bool PropertyAndMethodCallTest::testFetchPropertyColor(QObject* qobj){
    PROPERTY_TEST(QColor, Color)
}

bool PropertyAndMethodCallTest::testFetchPropertyQtEnum(QObject* qobj){
    PROPERTY_TEST2(Qt::AspectRatioMode, QtEnum)
}

bool PropertyAndMethodCallTest::testFetchPropertyDerivedQObject(QObject* qobj){
    PROPERTY_TEST3(QObject*, DerivedQObject)
}

bool PropertyAndMethodCallTest::testFetchPropertyCustomQtEnum(QObject* qobj){
    PROPERTY_TEST(JEnumWrapper, CustomQtEnum)
}

bool PropertyAndMethodCallTest::testFetchPropertyQtFlags(QObject* qobj){
    PROPERTY_TEST(JEnumWrapper, QtFlags)
}

bool PropertyAndMethodCallTest::testFetchPropertyCustomQtValue(QObject* qobj){
    PROPERTY_TEST3(QGraphicsItem*, CustomQtValue)
}

bool PropertyAndMethodCallTest::testFetchPropertyCustomJavaType(QObject* qobj){
    PROPERTY_TEST(JObjectWrapper, CustomJavaType)
}

bool PropertyAndMethodCallTest::testFetchPropertyCustomQtEnum2(QObject* qobj){
    PROPERTY_TEST(JEnumWrapper, CustomQtEnum2)
}
