package com.trolltech.demos.qt3d.cubehouse;

import com.trolltech.examples.QtJambiExample;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMainWindow;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.qt3d.QGLView;

@QtJambiExample(name = "Qt3D Cube House", canInstantiate = "call-static-method:checkQt3DSupport")
public class Main extends QMainWindow{
	
	public Main() {
		this(null);
	}

	public Main(QWidget parent) {
		super(parent);
		CubeView view = new CubeView(this);
		this.setCentralWidget(view);
		this.resize(500, 300);
		setWindowTitle("Cube House");
	    setWindowIcon(new QIcon("classpath:/com/trolltech/demos/qt3d/cubehouse/qt3d.png"));
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
	
	public static void main(String[] args){
    	QApplication.initialize(args);
        CubeView view = new CubeView();
        if (QApplication.arguments().contains("-framerate"))
            view.setShowFrameRate(true);
        if (QApplication.arguments().contains("-projectivetexture"))
            view.setProjectiveTextureEffect(true);
        if (QApplication.arguments().contains("-stereo"))
            view.setStereo(true);
        else if (view.stereoType() != QGLView.StereoType.RedCyanAnaglyph)
            view.setStereo(true);  
        if (QApplication.arguments().contains("-maximize"))
            view.showMaximized();
        else if (QApplication.arguments().contains("-fullscreen"))
            view.showFullScreen();
        else
            view.show();
        view = null;
        QApplication.execStatic();
        QApplication.shutdown();
    }
}
