package com.azilen.insuranceapp.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azilen.insuranceapp.R;
import com.azilen.insuranceapp.entities.network.response.DoneActivity;
import com.azilen.insuranceapp.utils.Prefs;

/**
 * Adapter for MyVideos Screen
 * @author dhara.shah
 *
 */
public class MyVideosAdapter extends ArrayAdapter<DoneActivity>{
	private Context mContext;
	private List<DoneActivity> mDoneActivities;
	private LayoutInflater mInflater;
	private int RESOURCE;
	
	public MyVideosAdapter(Context context, int resource,
			List<DoneActivity> objects, String sortBy) {
		super(context, resource, objects);
		mContext = context;
		RESOURCE = resource;
		mDoneActivities = objects;
		
		if(sortBy.equalsIgnoreCase(mContext.getString(R.string.inspector))) {
			Collections.sort(mDoneActivities, new Comparator<DoneActivity>() {
			    public int compare(DoneActivity e1, DoneActivity e2) {
			        if(e1.getUserName() != null && e2.getUserName() != null) {
			        	return e1.getUserName().compareTo(e2.getUserName());
			        }else {
			        	return 0;
			        }
			    }
			});
		}else if(sortBy.equalsIgnoreCase(mContext.getString(R.string.inspection))) {

			Collections.sort(mDoneActivities, new Comparator<DoneActivity>() {
			    public int compare(DoneActivity e1, DoneActivity e2) {
			    	int e1BoolVal = 0, e2BoolVal = 0;
			    	
			    	if(e1.isInspectionType()) {
			    		e1BoolVal = 0;
			    	}else {
			    		e1BoolVal= 1;
			    	}
			    	
			    	if(e2.isInspectionType()) {
			    		e2BoolVal = 0;
			    	}else {
			    		e2BoolVal = 1;
			    	}
			    	
			        return e1BoolVal - e2BoolVal;
			    }
			});
		}else if(sortBy.equalsIgnoreCase(mContext.getString(R.string.city))) {
			Collections.sort(mDoneActivities, new Comparator<DoneActivity>() {
			    public int compare(DoneActivity e1, DoneActivity e2) {
			        if(e1.getCity() != null && e2.getCity() != null) {
			        	return e1.getCity().compareTo(e2.getCity());
			        }else {
			        	return 0;
			        }
			    }
			});
		}else if(sortBy.equalsIgnoreCase(mContext.getString(R.string.upload))) {
			Collections.sort(mDoneActivities, new Comparator<DoneActivity>() {
			    public int compare(DoneActivity e1, DoneActivity e2) {
			    	int e1BoolVal = 0, e2BoolVal = 0;
			    	
			    	if(Boolean.parseBoolean(e1.getIsUploaded())) {
			    		e1BoolVal = 0;
			    	}else {
			    		e1BoolVal= 1;
			    	}
			    	
			    	if(Boolean.parseBoolean(e2.getIsUploaded())) {
			    		e2BoolVal = 0;
			    	}else {
			    		e2BoolVal = 1;
			    	}
			    	
			        return e1BoolVal - e2BoolVal;
			    }
			});
		}else if(sortBy.equalsIgnoreCase(mContext.getString(R.string.approved))) {
			Collections.sort(mDoneActivities, new Comparator<DoneActivity>() {
			    public int compare(DoneActivity e1, DoneActivity e2) {
			    	return Integer.parseInt(e2.getIsApproved()) - 
			        		Integer.parseInt(e1.getIsApproved());
			    }
			});
		}else if(sortBy.equalsIgnoreCase(mContext.getString(R.string.rejected))) {
			Collections.sort(mDoneActivities, new Comparator<DoneActivity>() {
			    public int compare(DoneActivity e1, DoneActivity e2) {
			    	return Integer.parseInt(e1.getIsApproved()) - 
			        			Integer.parseInt(e2.getIsApproved());
			    }
			});
		}else if(sortBy.equalsIgnoreCase(mContext.getString(R.string.date_time))) {
			Collections.sort(mDoneActivities, new Comparator<DoneActivity>() {
				public int compare(DoneActivity e1, DoneActivity e2) {
					String doneActivity1date = e1.getDateTime();
					String doneActivity2date = e2.getDateTime();
					
					try {

						Date dateForDoneActivity1 = 
								new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(doneActivity1date);
						Date dateForDoneActivity2 = 
								new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(doneActivity2date);
						
				        return String.valueOf(dateForDoneActivity1.getTime()).
				        			compareTo(String.valueOf(dateForDoneActivity2.getTime()));
					}catch(NullPointerException e) {
						e.printStackTrace();
					}catch(IllegalArgumentException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					return 0;
			    }
			});
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		View view = convertView;
		
		if(view == null) {
			mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(RESOURCE, parent,false);
			vh = new ViewHolder();
			
			vh.txtEventName = (TextView)view.findViewById(R.id.txtEventName);
			vh.txtDuration = (TextView)view.findViewById(R.id.txtDuration);
			vh.txtDate = (TextView)view.findViewById(R.id.txtDate);
			vh.txtUserName = (TextView)view.findViewById(R.id.txtUserName);
			vh.imgUserAvatar = (ImageView)view.findViewById(R.id.userAvatar);
			vh.imgVideoImage = (ImageView)view.findViewById(R.id.imgVideoImage);
			vh.imgArrow = (ImageView)view.findViewById(R.id.imgArrow);
			vh.relUploadStatus = (RelativeLayout)view.findViewById(R.id.relUploadStatus);
			vh.txtSize = (TextView)view.findViewById(R.id.txtSize);
			vh.txtIsUploaded = (TextView)view.findViewById(R.id.txtUploadStatus);
			vh.imgIsApproved = (ImageView)view.findViewById(R.id.imgVideoStatus);
			
			view.setTag(vh);
		}else {
			vh = (ViewHolder)view.getTag();
		}
		
		DoneActivity doneActivity = mDoneActivities.get(position);
		//User user = event.getUser();
		
		if(doneActivity.getEventTitle() != null && 
				doneActivity.getEventTitle().trim().length() > 0) {
			vh.txtEventName.setText(doneActivity.getEventTitle());
		}else {
			vh.txtEventName.setText(mContext.getString(R.string.event_title));
		}
		
		vh.txtDuration.setText(doneActivity.getVideoDuration());
		vh.txtSize.setText(mContext.getString(R.string.size) + doneActivity.getVideoSize());
		vh.txtUserName.setText(doneActivity.getUserName());
		vh.txtDate.setText(doneActivity.getDateTime());
		
		if(Boolean.parseBoolean(doneActivity.getIsUploaded())) {
			vh.txtIsUploaded.setText(mContext.getString(R.string.video_uploaded));
		}else {
			vh.txtIsUploaded.setText(mContext.getString(R.string.video_not_uploaded));
		}
		
		if(doneActivity.getIsApproved() != null) {
			if(doneActivity.getIsApproved().equals("1")) {
				vh.imgIsApproved.setBackgroundResource(R.drawable.tick);
			}else {
				vh.imgIsApproved.setBackgroundResource(R.drawable.rejected);
			}
		}else {
			vh.imgIsApproved.setBackgroundResource(R.drawable.rejected);
		}
		
		switch (Prefs.getKey_int(mContext, Prefs.USER_TYPE)) {
		// pink
		case 0:
			vh.txtEventName.setTextColor(mContext.getResources().getColor(R.color.pink));
			vh.imgArrow.setBackgroundResource(R.drawable.arrow_selector_pink);
			break;
		
		// blue
		case 1:
			vh.txtEventName.setTextColor(mContext.getResources().getColor(R.color.blue));
			vh.imgArrow.setBackgroundResource(R.drawable.arrow_selector_blue);
			vh.relUploadStatus.setVisibility(View.GONE);
			break;

		default:
			break;
		}
		
		return view;
	}

	static class ViewHolder {
		private TextView txtEventName;
		private TextView txtSize;
		private TextView txtDate;
		private TextView txtDuration;
		private TextView txtUserName;
		private ImageView imgVideoImage;
		private ImageView imgArrow;
		private ImageView imgIsApproved;
		private ImageView imgUserAvatar;
		private TextView txtIsUploaded;
		private RelativeLayout relUploadStatus;
	}
}
