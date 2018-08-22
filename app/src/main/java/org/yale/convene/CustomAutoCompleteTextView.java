package org.yale.convene;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;


public class CustomAutoCompleteTextView extends android.support.v7.widget.AppCompatAutoCompleteTextView {
    private int mThreshold;

    /**
     *
     * @param context
     */
    public CustomAutoCompleteTextView(Context context) {
        super(context);
    }

    /**
     *
     * @param context
     * @param attrs
     */
    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     *
     * @param threshold
     */
    public void setThreshold(int threshold) {
            mThreshold = threshold;

    }

    public int getThreshold() {
        return mThreshold;
    }


    /**
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public CustomAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isPopupShowing()) {
            InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            if(inputManager.hideSoftInputFromWindow(findFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS)){
                return true;
            }
        }
        if(keyCode== EditorInfo.IME_ACTION_SEARCH){
            InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            if(inputManager.hideSoftInputFromWindow(findFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS)){
                return true;
            }
        }


        return super.onKeyPreIme(keyCode, event);
    }
}
