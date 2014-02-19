package com.azilen.waiterpad.layouts.customviews;

import android.content.Context;
import android.widget.ExpandableListView;

public class ExtraLevelExpandableListView extends ExpandableListView{
	public ExtraLevelExpandableListView(Context context) {
		super(context);
	}

	public void setID( int level2, int level3 ) {
    	this.level2 = level2;
    	this.level3 = level3;
    }
    
    public int getIDLevel2() {
    	return level2;
    }

    public int getIDLevel3() {
    	return level3;
    }

    public String getIDString() {
    	return level2+":"+level3;
    }
    
    public void setPositionInParent( int positionInParent ) {
    	this.positionInParent = positionInParent;
    }
    
    public int getPositionInParent() {
    	return positionInParent;
    }
    
	/* public void setRows( int rows ) {
		this.rows = rows;
		Log.d( LOG_TAG, "rows set("+getIDString()+"): "+rows );
		try {
			throw new Exception();
		} catch( Exception ex ) {
			Log.d( LOG_TAG, "setRows",ex);
		}
	}

	public int getRows() {
		return rows;
	}
	
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure( widthMeasureSpec, heightMeasureSpec );
    	setMeasuredDimension( getMeasuredWidth(), rows*ROW_HEIGHT );
        Log.d( LOG_TAG, "onMeasure "+this+" id: ("+level2+":"+level3+")"+
                        ": width: "+decodeMeasureSpec( widthMeasureSpec )+
                        "; height: "+decodeMeasureSpec( heightMeasureSpec )+
                        "; measuredHeight: "+getMeasuredHeight()+
                        "; measuredWidth: "+getMeasuredWidth() );
    } 

    protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
        super.onLayout( changed, left,top,right,bottom );
        Log.d( LOG_TAG,"onLayout "+this+
        		" id: ("+level2+":"+level3+"): changed: "+
        		changed+"; left: "+
        		left+"; top: "+
        		top+"; right: "+
        		right+"; bottom: "+
        		bottom );
    }
 
    private String decodeMeasureSpec( int measureSpec ) {
        int mode = View.MeasureSpec.getMode( measureSpec );
        String modeString = "<> ";
        switch( mode ) {
            case View.MeasureSpec.UNSPECIFIED:
                modeString = "UNSPECIFIED ";
                break;

            case View.MeasureSpec.EXACTLY:
                modeString = "EXACTLY ";
                break;

            case View.MeasureSpec.AT_MOST:
                modeString = "AT_MOST ";
                break;
        }
        return modeString+Integer.toString( View.MeasureSpec.getSize( measureSpec ) );
    } */
    
   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(960,
                     MeasureSpec.AT_MOST);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(600,
                     MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

	private static final int ROW_HEIGHT = 24;
    private static final String LOG_TAG = "DebugExpandableListView";
	private int rows;
	private int level2 = -1;
	private int level3 = -1;
	private int positionInParent;
}
