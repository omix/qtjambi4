package com.trolltech.demos.qt3d.teaservice;

import com.trolltech.examples.QtJambiExample;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMainWindow;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QVector3D;
import com.trolltech.qt.gui.QWidget;

@QtJambiExample(name = "Qt3D Tea Service", canInstantiate = "call-static-method:checkQt3DSupport")
public class Main extends QMainWindow{
	
	public Main() {
		this(null, false);
	}
	
	public Main(QWidget parent) {
		this(parent, false);
	}
	
	public Main(QWidget parent, boolean inclQuit) {
		super(parent);
		
	    this.setMinimumSize(850, 480);
	    this.setWindowTitle("Tea Service");
	    this.setWindowIcon(new QIcon("classpath:/com/trolltech/demos/qt3d/teaservice/qt3d.png"));
	
	    TeaServiceView view = new TeaServiceView();
	    this.setCentralWidget(view);
	    view.setFocus();
	
	    view.camera().setEye(new QVector3D(0, 3, 10));
	
	    QMenu menu = this.menuBar().addMenu("Effects");
	
	    QAction standardLighting = new QAction("Standard lighting", this);
	    menu.addAction(standardLighting);
	    standardLighting.triggered.connect(view, "standardLighting()");
	
	    QAction perPixelLighting = new QAction("Per-pixel lighting", this);
	    menu.addAction(perPixelLighting);
	    perPixelLighting.triggered.connect(view, "perPixelLighting()");
	
	    menu.addSeparator();
	
	    if(inclQuit){
	    	QAction exitAction = new QAction("E&xit", this);
	    	menu.addAction(exitAction);
	    	exitAction.triggered.connect(QApplication.instance(), "quit()");
	    }
	
	    view.updateGL();
	}

	public static boolean checkQt3DSupport(){
		try {
            Class.forName("javax.media.opengl.GL2");
            Class.forName("com.trolltech.qt.qt3d.QGLView");
            return true;
        } catch (Exception e) {
        }
        return false;
	}
	
	public static void main(String argv[])
	{
	    QApplication.initialize(argv);
	    Main mainw = new Main(null, true);
	    mainw.show();
	    QApplication.execStatic();
	    QApplication.shutdown();
	}
}
