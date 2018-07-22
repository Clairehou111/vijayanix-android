package com.vijayanix.iot.ui.device;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vijayanix.iot.R;


public abstract class ActivityAbs extends Activity
{
    private FrameLayout mContentView;
    
    private LinearLayout mTitleView;
    
    private TextView mTitleTV;
    private ImageView mLeftIcon;
    private ImageView mRightIcon;
    private ViewGroup mTitleContentView;

    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        super.setContentView(R.layout.activity_abs);
        getActionBar().hide();
        
        mContentView = (FrameLayout)findViewById(R.id.content);
        
        mTitleView = (LinearLayout)findViewById(R.id.title_bar);
        mTitleTV = (TextView)findViewById(R.id.title_text);
        mLeftIcon = (ImageView)findViewById(R.id.left_icon);
        mRightIcon = (ImageView)findViewById(R.id.right_icon);
        mTitleContentView = (ViewGroup)findViewById(R.id.title_content);
        
        setTitleLeftIcon(R.drawable.icon_back);
    }
    
    @Override
    public void setContentView(int layoutResID)
    {
        mContentView.removeAllViews();
        getLayoutInflater().inflate(layoutResID, mContentView);
    }
    
    @Override
    public void setContentView(View view)
    {
        mContentView.removeAllViews();
        mContentView.addView(view);
    }
    
    @Override
    public void setContentView(View view, LayoutParams params)
    {
        mContentView.removeAllViews();
        mContentView.addView(view, params);
    }
    
    @Override
    public void setTitle(int titleId)
    {
        super.setTitle(titleId);
        
        mTitleTV.setText(titleId);
    }
    
    @Override
    public void setTitle(CharSequence title)
    {
        super.setTitle(title);
        
        mTitleTV.setText(title);
    }
    
    /**
     * Set the icon on title bar left side
     * 
     * @param iconId
     */
    public void setTitleLeftIcon(int iconId)
    {
        mLeftIcon.setImageResource(iconId);
        if (iconId == 0)
        {
            mLeftIcon.setClickable(false);
        }
        else
        {
            mLeftIcon.setOnClickListener(mIconClickListener);
        }
    }
    



    /**
     * Set the view on the left side of right title icon
     *
     * @param view
     * @param paddingLeft of the content view
     * @param paddingTop of the content view
     * @param paddingRight of the content view
     * @param paddingBottom of the content view
     */
    public void setTitleContentView(View view, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom)
    {
        mTitleContentView.removeAllViews();
        mTitleContentView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        if (view != null)
        {
            mTitleContentView.setVisibility(View.VISIBLE);
            mTitleContentView.addView(view);
        }
        else
        {
            mTitleContentView.setVisibility(View.GONE);
        }

        checkTitleRightIconAndTitleContentView();
    }

    private void checkTitleRightIconAndTitleContentView() {
        if (mRightIcon.getDrawable() == null) {
            if (mTitleContentView.getVisibility() == View.VISIBLE) {
                mRightIcon.setVisibility(View.GONE);
            } else {
                mRightIcon.setVisibility(View.VISIBLE);
            }
        } else {
            mRightIcon.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Show ESP title bar
     */
    protected void showTitleBar()
    {
        mTitleView.setVisibility(View.VISIBLE);
    }

    /**
     * Hide ESP title bar
     */
    protected void hideTitleBar()
    {
        mTitleView.setVisibility(View.GONE);
    }

    private View.OnClickListener mIconClickListener = new View.OnClickListener()
    {

        @Override
        public void onClick(View v)
        {
            if (v == mLeftIcon)
            {
                onTitleLeftIconClick(mLeftIcon);
            }
            else if (v == mRightIcon)
            {
                onTitleRightIconClick(mRightIcon);
            }
        }
    };

    /**
     * Get left icon on title
     *
     * @return
     */
    protected View getLeftTitleIcon()
    {
        return mLeftIcon;
    }

    /**
     * Get right icon on title
     *
     * @return
     */
    protected View getRightTitleIcon()
    {
        return mRightIcon;
    }

    /**
     * The title bar left icon click listener
     */
    protected void onTitleLeftIconClick(View leftIcon)
    {
        onBackPressed();
    }
    
    /**
     * The title bar right icon click listener
     */
    protected void onTitleRightIconClick(View rightIcon)
    {
    }
}
