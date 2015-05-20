package se.marteinn.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;


/**
 * Triggers a event when scrolling reaches bottom.
 *
 * Created by martinsandstrom on 2010-05-12.
 * Updated by martinsandstrom on 2014-07-22.
 *
 * Usage:
 *
 *  scrollView.setOnBottomReachedListener(
 *      new InteractiveScrollView.OnBottomReachedListener() {
 *          @Override
 *          public void onBottomReached() {
 *              // do something
 *          }
 *      }
 *  );
 * 
 *
 * Include in layout:
 *  
 *  <se.marteinn.ui.InteractiveScrollView
 *      android:layout_width="match_parent"
 *      android:layout_height="match_parent" />
 *  
 */
public class InteractiveScrollView extends ScrollView {
    OnBottomReachedListener mListenerBottom;

    OnTopReachedListener mListenerTop;

    public InteractiveScrollView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);
    }

    public InteractiveScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InteractiveScrollView(Context context) {
        super(context);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        View view = (View) getChildAt(getChildCount()-1);
        int diffBottom = (view.getBottom()-(getHeight()+getScrollY()));

        if (diffBottom == 0 && mListenerBottom != null) {
            mListenerBottom.onBottomReached();
        }

        int diffTop = (view.getTop()-getScrollY());

        if (diffTop == 0 && mListenerTop != null) {
            mListenerTop.onTopReached();
            // ugly hack to prevent double onTopReached();
            try {
                Thread.sleep(10);
            } catch (Exception e) {
            }
        }

        super.onScrollChanged(l, t, oldl, oldt);
    }


    // Getters & Setters

    public OnBottomReachedListener getOnBottomReachedListener() {
        return mListenerBottom;
    }

    public OnTopReachedListener getOnTopReachedListener() {
        return mListenerTop;
    }

    public void setOnBottomReachedListener(
            OnBottomReachedListener onBottomReachedListener) {
        mListenerBottom = onBottomReachedListener;
    }
    
    public void setOnTopReachedListener(
            OnTopReachedListener onTopReachedListener) {
        mListenerTop = onTopReachedListener;
    }

    /**
     * Event listener.
     */
    public interface OnBottomReachedListener{
        public void onBottomReached();
    }
    

    public interface OnTopReachedListener{
        public void onTopReached();
    }
}
