package com.packagename.androidswitchbackportdemo;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.packagename.androidswitchbackportdemo.customviews.MySwitch;

public class MainActivity extends SherlockFragmentActivity implements OnClickListener, OnCheckedChangeListener {
	private Button mBtnToggle;
	private MySwitch mSwitchSyncVideos;
	private MySwitch mSwitchSyncVidsOverWifi;
	private static int  type = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);
        
        mBtnToggle = (Button)findViewById(R.id.btnToggle);
        mSwitchSyncVideos = (MySwitch)findViewById(R.id.switchSyncVideos);
        mSwitchSyncVidsOverWifi = (MySwitch)findViewById(R.id.switchSyncVidOverWifi);
        
        mSwitchSyncVideos.setOnCheckedChangeListener(this);
        mSwitchSyncVidsOverWifi.setOnCheckedChangeListener(this);
        mBtnToggle.setOnClickListener(this);
    }
    
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnToggle:
			
			if(type == 0) {
				type =1 ;
			}else {
				type = 0;
			}
			
			switch (type) {
			case 0:
				// trying to change the color of the background resources
				mSwitchSyncVideos.setLeftBackground(getResources().getDrawable(R.drawable.sleft_background_copy1_pink));
				mSwitchSyncVidsOverWifi.setLeftBackground(getResources().getDrawable(R.drawable.sleft_background_copy1_pink));
				break;
				
			case 1:
				// trying to change the color of the background resources
				mSwitchSyncVideos.setLeftBackground(getResources().getDrawable(R.drawable.sleft_background_copy1_blue));
				mSwitchSyncVidsOverWifi.setLeftBackground(getResources().getDrawable(R.drawable.sleft_background_copy1_blue));
				break;

			default:
				break;
			}
			
			break;

		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton button, boolean isChecked) {
		switch (button.getId()) {
		case R.id.switchSyncVideos:
			mSwitchSyncVideos.setChecked(isChecked);
			break;
			
		case R.id.switchSyncVidOverWifi:
			mSwitchSyncVidsOverWifi.setChecked(isChecked);
			break;

		default:
			break;
		}
	}
}
