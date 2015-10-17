package com.trolltech.autotests;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.trolltech.autotests.generated.PropertyAndMethodCallTest;
import com.trolltech.qt.QFlags;
import com.trolltech.qt.QNoSuchEnumValueException;
import com.trolltech.qt.QtBlockedSlot;
import com.trolltech.qt.QtEnumerator;
import com.trolltech.qt.QtPropertyNotify;
import com.trolltech.qt.QtPropertyReader;
import com.trolltech.qt.QtPropertyWriter;
import com.trolltech.qt.core.QObject;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QDrag;
import com.trolltech.qt.gui.QGraphicsItem;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QStringListModel;
import com.trolltech.qt.gui.QStyleOptionGraphicsItem;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.internal.MetaObjectTools;

public class TestPropertyAndMethodCall extends QObject{
	
	static{
		com.trolltech.qt.Utilities.loadQtLibrary("QtXml"); // usually available
        com.trolltech.qt.Utilities.loadQtLibrary("QtSql"); // load if exists
	}
	
	private PropertyAndMethodCallTest object = new PropertyAndMethodCallTest(this);
	private TestQObject javaObject;
	private boolean connected = false;
	
	@Before
	 public void setUp() {
		 if(QApplication.startingUp()){
			 QApplication.initialize(new String[]{});
		 }
		 javaObject = new TestQObject(this);
	 }
	
	@Test
	public void testMethodCallColor(){
		assertTrue(PropertyAndMethodCallTest.testMethodCallColor(javaObject));
	}
	
	@Test
	public void testMethodCallCustomJavaType(){
		assertTrue(PropertyAndMethodCallTest.testMethodCallCustomJavaType(javaObject));
	}
	
	@Test
	public void testMethodCallCustomQtEnum(){
		assertTrue(PropertyAndMethodCallTest.testMethodCallCustomQtEnum(javaObject));
	}
	
	@Test
	public void testMethodCallCustomQtEnum2(){
		assertTrue(PropertyAndMethodCallTest.testMethodCallCustomQtEnum2(javaObject));
	}
	
	@Test
	public void testMethodCallCustomQtValue(){
		assertTrue(PropertyAndMethodCallTest.testMethodCallCustomQtValue(javaObject));
	}
	
	@Test
	public void testMethodCallDerivedQObject(){
		assertTrue(PropertyAndMethodCallTest.testMethodCallDerivedQObject(javaObject));
	}
	
	@Test
	public void testMethodCallEnum(){
		assertTrue(PropertyAndMethodCallTest.testMethodCallEnum(javaObject));
	}
	
	@Test
	public void testMethodCallNumber(){
		assertTrue(PropertyAndMethodCallTest.testMethodCallNumber(javaObject));
	}
	
	@Test
	public void testMethodCallQtEnum(){
		assertTrue(PropertyAndMethodCallTest.testMethodCallQtEnum(javaObject));
	}
	
	@Test
	public void testMethodCallQtFlags(){
		assertTrue(PropertyAndMethodCallTest.testMethodCallQtFlags(javaObject));
	}
	
	@Test
	public void testFetchPropertyCustomJavaTypeCPP(){
		assertTrue(PropertyAndMethodCallTest.testFetchPropertyCustomJavaType(javaObject));
	}
	
	@Test
	public void testFetchPropertyColorCPP(){
		assertTrue(PropertyAndMethodCallTest.testFetchPropertyColor(javaObject));
	}
	
	@Test
	public void testFetchPropertyCustomQtEnumCPP(){
		assertTrue(PropertyAndMethodCallTest.testFetchPropertyCustomQtEnum(javaObject));
	}
	
	@Test
	public void testFetchPropertyCustomQtEnum2CPP(){
		assertTrue(PropertyAndMethodCallTest.testFetchPropertyCustomQtEnum2(javaObject));
	}
	
	@Test
	public void testFetchPropertyCustomQtValueCPP(){
		assertTrue(PropertyAndMethodCallTest.testFetchPropertyCustomQtValue(javaObject));
	}
	
	@Test
	public void testFetchPropertyDerivedQObjectCPP(){
		assertTrue(PropertyAndMethodCallTest.testFetchPropertyDerivedQObject(javaObject));
	}
	
	@Test
	public void testFetchPropertyCustomEnumCPP(){
		assertTrue(PropertyAndMethodCallTest.testFetchPropertyEnum(javaObject));
	}
	
	@Test
	public void testFetchPropertyNumberCPP(){
		assertTrue(PropertyAndMethodCallTest.testFetchPropertyNumber(javaObject));
	}
	
	@Test
	public void testFetchPropertyQtEnumCPP(){
		assertTrue(PropertyAndMethodCallTest.testFetchPropertyQtEnum(javaObject));
	}
	
	@Test
	public void testFetchPropertyQtFlagsCPP(){
		assertTrue(PropertyAndMethodCallTest.testFetchPropertyQtFlags(javaObject));
	}
	
	@Test
	public void testFetchPropertyCustomJavaTypeJAVA(){
		assertEquals(javaObject.getCustomJavaType(), javaObject.property("customJavaType"));
	}
	
	@Test
	public void testFetchPropertyColorJAVA(){
		assertEquals(javaObject.getColor(), javaObject.property("color"));
	}
	
	@Test
	public void testFetchPropertyCustomQtEnumJAVA(){
		assertEquals(javaObject.getCustomQtEnum().value, javaObject.property("customQtEnum"));
	}
	
	@Test
	public void testFetchPropertyCustomQtEnum2JAVA(){
		assertEquals(0, javaObject.property("customQtEnum2"));
	}
	
	@Test
	public void testFetchPropertyCustomQtValueJAVA(){
		assertEquals(javaObject.getCustomQtValue(), javaObject.property("customQtValue"));
	}
	
	@Test
	public void testFetchPropertyDerivedQObjectJAVA(){
		assertEquals(javaObject.getDerivedQObject(), javaObject.property("derivedQObject"));
	}
	
	@Test
	public void testFetchPropertyCustomEnumJAVA(){
		assertEquals(javaObject.getEnum().ordinal(), javaObject.property("customEnum"));
	}
	
	@Test
	public void testFetchPropertyNumberJAVA(){
		assertEquals(javaObject.getNumber(), javaObject.property("number"));
	}
	
	@Test
	public void testFetchPropertyQtEnumJAVA(){
		assertEquals(javaObject.getQtEnum().value(), javaObject.property("qtEnum"));
	}
	
	@Test
	public void testFetchPropertyQtFlagsJAVA(){
		assertEquals(javaObject.getQtFlags().value(), javaObject.property("qtFlags"));
	}
	
	@Test
	public void testConnectSignalsAndSlots(){
		connected = object.connectSignals(javaObject);
		assertTrue(connected);
	}
	
	@Test
	public void testSignalCustomEnumNULL(){
		object.connectSignals(javaObject);
		javaObject.customEnumChanged.emit(null);
		assertEquals(object.receivedCustomEnum(), 0);
	}
	
	@Test
	public void testSignalCustomEnum(){
		object.connectSignals(javaObject);
		javaObject.customEnumChanged.emit(TestQObject.CustomEnum.Entry3);
		assertEquals(object.receivedCustomEnum(), TestQObject.CustomEnum.Entry3.ordinal());
	}
	
	@Test
	public void testSignalCustomQtEnumNULL(){
		object.connectSignals(javaObject);
		javaObject.customQtEnumChanged.emit(null);
		assertEquals(object.receivedCustomQtEnum(), 0);
	}
	
	@Test
	public void testSignalCustomQtEnum(){
		object.connectSignals(javaObject);
		javaObject.customQtEnumChanged.emit(TestQObject.CustomQtEnum.Entry3);
		assertEquals(TestQObject.CustomQtEnum.Entry3.value(), object.receivedCustomQtEnum());
	}
	
	@Test
	public void testSignalQtFlags(){
		object.connectSignals(javaObject);
		TestQObject.CustomQtFlags flags = new TestQObject.CustomQtFlags(TestQObject.CustomQtEnum.Entry1, TestQObject.CustomQtEnum.Entry2);
		javaObject.customQtFlagsChanged.emit(flags);
		assertEquals(object.receivedQtFlags(), flags.value());
	}
	
	@Test
	public void testSignalCustomQtValue(){
		object.connectSignals(javaObject);
		TestQObject.CustomQtValue customQtValue = new TestQObject.CustomQtValue();
		javaObject.customQtValueChanged.emit(customQtValue);
		assertEquals(object.receivedCustomQtValue(), customQtValue);
	}
	
	@Test
	public void testSignalCustomJavaType(){
		object.connectSignals(javaObject);
		TestQObject.CustomJavaType customJavaType = new TestQObject.CustomJavaType();
		javaObject.customJavaTypeChanged.emit(customJavaType);
		assertEquals(object.receivedCustomJavaType(), customJavaType);
	}
	
	@Test
	public void testSignalDerivedQObject(){
		object.connectSignals(javaObject);
		TestQObject.DerivedQObject derivedObject = new TestQObject.DerivedQObject();
		javaObject.derivedQObjectChanged.emit(derivedObject);
		assertEquals(object.receivedDerivedQObject(), derivedObject);
	}
	
	public static class TestQObject extends QObject{

		public enum CustomEnum{Entry1, Entry2, Entry3};
		
		public enum CustomQtEnum implements QtEnumerator{
			Entry1(0x0010), 
			Entry2(0x0100),
			Entry3(0x1000);
		
			CustomQtEnum(int value){
				this.value = value;
			}
			
			private final int value;
			@Override
			public int value() {
				return value;
			}
			
			public static CustomQtEnum resolve(int i){
				switch(i){
				case 0 : return null;
				case 0x0010: return Entry1;
				case 0x0100: return Entry2;
				case 0x1000: return Entry3;
				}
				throw new QNoSuchEnumValueException(i);
			}
		};
		
		public static class CustomQtFlags extends QFlags<CustomQtEnum>{
			public CustomQtFlags(CustomQtEnum... args) {
				super(args);
			}
			private static final long serialVersionUID = -8392034916129435499L;
		}
		
		public static class DerivedQObject extends QObject{

			public DerivedQObject() {
				super((QObject)null);
				this.setObjectName("DerivedQObject#"+Long.toHexString(this.nativeId()));
			}
			
		}

		public static class CustomQtValue extends QGraphicsItem{
			public CustomQtValue(){
				super();
			}

			@Override
			@QtBlockedSlot
			public QRectF boundingRect() {
				return null;
			}

			@Override
			@QtBlockedSlot
			public void paint(QPainter painter, QStyleOptionGraphicsItem option, QWidget widget) {
			}
		}

		public static class CustomJavaType{
			
		}
		
		public TestQObject(QObject obj) {
			super(obj);
			MetaObjectTools.registerMetaType(DerivedQObject.class);
			MetaObjectTools.registerMetaType(CustomQtValue.class);
			MetaObjectTools.registerMetaType(CustomJavaType.class);
			MetaObjectTools.registerMetaType(CustomJavaType.class);
			MetaObjectTools.registerMetaType(CustomQtEnum.class);
			MetaObjectTools.registerMetaType(CustomEnum.class);
			MetaObjectTools.registerMetaType(CustomQtFlags.class);
			MetaObjectTools.registerMetaType(QStringListModel.class);
			MetaObjectTools.registerMetaType(QDrag.class);
			MetaObjectTools.registerMetaType(Qt.Orientations.class);
			MetaObjectTools.registerMetaType(List.class);
		}

		private QStringListModel myModel;
		private List<String> myList;
		private DerivedQObject derivedQObject = new DerivedQObject();
		private CustomQtValue customQtValue = new CustomQtValue();
		private CustomJavaType customJavaType = new CustomJavaType();
		private CustomQtEnum customQtEnum = CustomQtEnum.Entry1;
		private CustomQtFlags customQtFlags = new CustomQtFlags();
		private Qt.AspectRatioMode qtEnum = Qt.AspectRatioMode.KeepAspectRatio;
		private Qt.Orientations qtFlags = new Qt.Orientations(Qt.Orientation.Horizontal);
		{
			setObjectName("MyDataSet");
			myList = new ArrayList<String>();
			myList.add("test item 1");
			myList.add("test item 2");
			myList.add("test item 3");
			myList.add("test item 4");
			myModel = new QStringListModel(myList);
			myModel.setObjectName("model#"+Long.toHexString(myModel.nativeId()));
		}
		
		@QtPropertyReader(name="qobject")
		public QObject getObject(){
			QObject o = new QObject();
			o.setObjectName("custom property");
			return o;
		}
	     
	    @QtPropertyReader(name="model")
	    public QStringListModel getModel(){
	    	 return myModel;
	    }
	    
	    @QtPropertyWriter(name="model")
	    public void setModel(QStringListModel myModel){
	    	 this.myModel = myModel;
	    }
	    
	    public boolean testModel(QStringListModel myModel){
	    	return myModel==this.myModel;
	    }
	    
	    @QtPropertyReader(name="color")
	    public QColor getColor(){
	    	return new QColor("red");
	    }
	    
	    public boolean testColor(QColor c){
	    	return new QColor("red").equals(c);
	    }
	    
	    @QtPropertyReader(name="customEnum")
	    public CustomEnum getEnum(){
	    	return CustomEnum.Entry1;
	    }
	    
	    public boolean testEnum(CustomEnum e){
	    	return System.identityHashCode(e)==System.identityHashCode(CustomEnum.Entry1);
	    }
	    
	    @QtPropertyReader(name="number")
	    public Number getNumber(){
	    	return BigInteger.valueOf(12345);
	    }
	    
	    public boolean testNumber(Number e){
	    	return BigInteger.valueOf(12345).equals(e);
	    }
	    
	    @QtPropertyNotify(name="model")
	    public final Signal1<QStringListModel> modelChanged = new Signal1<QStringListModel>();
	    
	    @QtPropertyNotify(name="customEnum")
	    public final Signal1<CustomEnum> customEnumChanged = new Signal1<CustomEnum>();
	    
	    @QtPropertyNotify(name="customQtEnum")
	    public final Signal1<CustomQtEnum> customQtEnumChanged = new Signal1<CustomQtEnum>();
	    
	    @QtPropertyNotify(name="customQtFlags")
	    public final Signal1<CustomQtFlags> customQtFlagsChanged = new Signal1<CustomQtFlags>();
	    
	    @QtPropertyNotify(name="customQtValue")
	    public final Signal1<CustomQtValue> customQtValueChanged = new Signal1<CustomQtValue>();
	    
	    @QtPropertyNotify(name="customJavaType")
	    public final Signal1<CustomJavaType> customJavaTypeChanged = new Signal1<CustomJavaType>();
	    
	    @QtPropertyNotify(name="customColor")
	    public final Signal1<QColor> customColorChanged = new Signal1<QColor>();
	    
	    @QtPropertyNotify(name="customColor")
	    public final Signal1<DerivedQObject> derivedQObjectChanged = new Signal1<DerivedQObject>();
	    
	   @QtPropertyReader(name="list")
	   public List<String> getList(){
	   	 return myList;
	   }
	   
	   @QtPropertyWriter(name="list")
	   public void setList(List<String> myList){
	   	 this.myList = myList;
	   }
	   
	   public boolean testList(List<String> myList){
		   	 return this.myList.equals(myList);
	   }
		
	   	@QtPropertyReader(name="derivedQObject")
		public DerivedQObject getDerivedQObject() {
			return derivedQObject;
		}
		
		public boolean testDerivedQObject(DerivedQObject derivedQObject) {
			return this.derivedQObject == derivedQObject;
		}
		
		@QtPropertyReader(name="customQtValue")
		public CustomQtValue getCustomQtValue() {
			return customQtValue;
		}
		
		public boolean testCustomQtValue(CustomQtValue customQtValue) {
			return this.customQtValue.equals(customQtValue);
		}
		
		@QtPropertyReader(name="customJavaType")
		public CustomJavaType getCustomJavaType() {
			return customJavaType;
		}
		
		public boolean testCustomJavaType(CustomJavaType customJavaType) {
			return this.customJavaType == customJavaType;
		}
		
		@QtPropertyReader(name="customQtEnum")
		public CustomQtEnum getCustomQtEnum() {
			return customQtEnum;
		}
		
		@QtPropertyReader(name="customQtEnum2")
		public CustomQtEnum getCustomQtEnum2() {
			return null;
		}
		
		public boolean testCustomQtEnum(CustomQtEnum customQtEnum) {
			return this.customQtEnum == customQtEnum;
		}
		
		public boolean testCustomQtEnum2(CustomQtEnum customQtEnum) {
			return null != customQtEnum;
		}
		
		@QtPropertyReader(name="customQtFlags")
		public CustomQtFlags getCustomQtFlags() {
			return customQtFlags;
		}
		
		public boolean testCustomQtFlags(CustomQtFlags customQtFlags) {
			return this.customQtFlags == customQtFlags;
		}
		
		@QtPropertyReader(name="qtEnum")
		public Qt.AspectRatioMode getQtEnum() {
			return qtEnum;
		}
		
		public boolean testQtEnum(Qt.AspectRatioMode customQtEnum) {
			return this.qtEnum == customQtEnum;
		}
		
		@QtPropertyReader(name="qtFlags")
		public Qt.Orientations getQtFlags() {
			return qtFlags;
		}
		
		public boolean testQtFlags(Qt.Orientations customQtFlags) {
			return this.qtFlags.equals(customQtFlags);
		}
	}
}
