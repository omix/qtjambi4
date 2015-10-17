package generator;

import com.trolltech.qt.QNativePointer;
import com.trolltech.qt.QtBlockedSlot;
import com.trolltech.qt.Utilities;
import com.trolltech.qt.QtJambiObject.QPrivateConstructor;
import com.trolltech.qt.core.QCoreApplication;
import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.gui.*;

class QHelpContentItem___ extends QHelpContentItem {

	@QtBlockedSlot
	public final int childPosition(QHelpContentItem child)    {
		if (nativeId() == 0)
			throw new QNoNativeResourcesException("Function call on incomplete object of type: " +getClass().getName());
		return __qt_childPosition_nativepointer(nativeId(), child == null ? null : child.nativePointer());
	}

	@QtBlockedSlot
    public final QHelpContentItem child(int row)    {
		return fromNativePointer(__child(row));
    }

	@QtBlockedSlot
	public final QHelpContentItem parent()    {
		return fromNativePointer(__parent());
	}

}// class

class QHelpContentModel___ extends QHelpContentModel {
@QtBlockedSlot
    public final QHelpContentItem contentItemAt(com.trolltech.qt.core.QModelIndex index)    {
        return QHelpContentItem.fromNativePointer(__contentItemAt(index));
    }
}// class
