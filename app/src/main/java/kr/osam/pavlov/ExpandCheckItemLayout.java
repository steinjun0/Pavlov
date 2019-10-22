package kr.osam.pavlov;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class ExpandCheckItemLayout extends LinearLayout {

    public ExpandCheckItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public ExpandCheckItemLayout(Context context) {
        super(context);

        init(context);
    }
    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_expandcheck,this,true);
    }
}

