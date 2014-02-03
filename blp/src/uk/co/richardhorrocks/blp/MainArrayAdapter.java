package uk.co.richardhorrocks.blp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import uk.co.richardhorrocks.blp.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainArrayAdapter extends ArrayAdapter<Times> {
    private final Context context;
	private final ArrayList<Times> values;		

	public MainArrayAdapter(Context context, ArrayList<Times> values) {
	    super(context, R.layout.removelayout, values);
	    this.context = context;
	    this.values = values;
	}
	  	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {	    
Log.e("MyActivity", "1");	  
		/*
		 * Inflate!
		 */
		LayoutInflater inflater = (LayoutInflater) context
	      .getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
        View rowView = inflater.inflate(R.layout.removelayout, parent, false);
		
		/*
		 * Assign some general views.
		 */
		final TextView userNameView = (TextView) rowView.findViewById(R.id.label);
        final TextView userTimeView = (TextView)rowView.findViewById(R.id.userTime);
        final TextView userLevelView = (TextView)rowView.findViewById(R.id.userLevel);
        final TextView userDistanceView = (TextView)rowView.findViewById(R.id.userDistance);
        final TextView userVo2View = (TextView)rowView.findViewById(R.id.userVo2);
		userNameView.setText(values.get(position).getName());	    

		final Button removeButtonView = (Button)rowView.findViewById(R.id.removeuserButton);
		final Button saveButtonView = (Button)rowView.findViewById(R.id.saveuserButton);
        final Button stopButtonView = (Button)rowView.findViewById(R.id.stopuserButton);
        final Button savedButtonView = (Button)rowView.findViewById(R.id.saveduserButton);
        
        /*
         * We've started the run. Set all users' views to show 'stop' buttons.
         */
		if (MainActivity.showListStop) {		
            removeButtonView.setVisibility(View.GONE);
            stopButtonView.setVisibility(View.VISIBLE);
		}
		
		/*
		 * We've hit the 'reset' button. Reset all users' views to their starting
		 * position (each showing a 'remove' button).
		 */
		if (MainActivity.resetList) {
Log.e("MyActivity", "2");
			removeButtonView.setVisibility(View.VISIBLE);
            stopButtonView.setVisibility(View.GONE);
            saveButtonView.setVisibility(View.GONE);
            userTimeView.setVisibility(View.GONE);
            userLevelView.setVisibility(View.GONE);
            userDistanceView.setVisibility(View.GONE);
            savedButtonView.setVisibility(View.GONE);						
		}
			
		/*
		 * Check if this user has been stopped and saved already.
		 * (We could be scrolling and recycling... )
		 */
		if (!MainActivity.resetList && values.get(position).getStopped()) {
		    if (values.get(position).getSaved()) {
Log.e("MyActivity", "5");
                stopButtonView.setVisibility(View.GONE);                
                savedButtonView.setVisibility(View.VISIBLE);
			    savedButtonView.setClickable(false);
		    } else {
Log.e("MyActivity", "6");					
                stopButtonView.setVisibility(View.GONE);
                saveButtonView.setVisibility(View.VISIBLE);
		    }

            userTimeView.setText(values.get(position).getTime());
            userLevelView.setText(values.get(position).getLevel());
            userDistanceView.setText(values.get(position).getDistance());
            userVo2View.setText(String.valueOf(values.get(position).getVo2()));

		    removeButtonView.setVisibility(View.GONE);
            userTimeView.setVisibility(View.VISIBLE);                
            userLevelView.setVisibility(View.VISIBLE);                
            userDistanceView.setVisibility(View.VISIBLE);
		}    
		
		/*
		 * We've hit the 'all stop' button.
		 * For each users that isn't already stopped, show the 'save' button and the stats.
		 * It's possible certain users will already have been stopped individually. Don't touch them.
		 */
		if (MainActivity.stopList && !values.get(position).getStopped()) {
Log.e("MyActivity", "3");				
            removeButtonView.setVisibility(View.GONE);
            stopButtonView.setVisibility(View.GONE);
            saveButtonView.setVisibility(View.VISIBLE);
            userTimeView.setVisibility(View.VISIBLE);
            userLevelView.setVisibility(View.VISIBLE);
            userDistanceView.setVisibility(View.VISIBLE);
            userTimeView.setText(MainActivity.userTime);
            userLevelView.setText(MainActivity.userLevel);
            userDistanceView.setText(MainActivity.userDistance);
            userVo2View.setText(String.valueOf(MainActivity.userVo2));
		}
		
		/*
		 * Assign the click response for the 'remove' button.
		 */		
		removeButtonView.setTag(position);
		removeButtonView.setOnClickListener(
		        new Button.OnClickListener() {
		            @Override
					public void onClick(View v) {
		                final int index = (Integer)v.getTag();		                
                        values.remove(index);
                        notifyDataSetChanged();
                        MainActivity.removeUser();
	                }		            
	            }
	    );

        /*
         * Assign the click response for the save button.
         */				
		saveButtonView.setOnClickListener(
	            new Button.OnClickListener() {
	                @Override
	                public void onClick(View v) {
	                    /*
	                     * Get the date.
	                     */
	                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.UK);	                    
                        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));	                                      
	                    String date = sdf.format(new Date()).toString();
                        	                    
	                    /*
	                     * Save the items to the database.
	                     */
	                    MainActivity.datasource.createTime(date,
	                                                       userNameView.getText().toString(),
	                                                       userTimeView.getText().toString(),
	                                                       userLevelView.getText().toString(),
	                                                       (int)(Double.parseDouble(
	                                                    		   userVo2View.getText().toString())));	                    	                    

	                    /*
	                     * Remove the 'Save' button once saving is complete.
	                     */
	                    saveButtonView.setVisibility(View.GONE);
	                    savedButtonView.setVisibility(View.VISIBLE);
	                    savedButtonView.setClickable(false);

	                    /*
	                     * Set that we've now saved this item.
	                     */
                        values.set(position,
                                   new Times((long)0,
                     			   "",
                     			   values.get(position).getName(),
                     			   values.get(position).getTime(),
                     			   values.get(position).getLevel(),
                     			   values.get(position).getDistance(),
                     			   values.get(position).getVo2(),
                     			   true,
                     			   true));
	                }
	            }
	        );      
	 		
        /*
         * Assign the click response for the stop button.
         */		
        stopButtonView.setTag(position);
        stopButtonView.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {                        
                        /*
                         * Remove the buttons.
                         * Display the time and level that the stop button was pressed.
                         */                                                
                        removeButtonView.setVisibility(View.GONE);
                        stopButtonView.setVisibility(View.GONE);
                        saveButtonView.setVisibility(View.VISIBLE);
                        userTimeView.setVisibility(View.VISIBLE);
                        userLevelView.setVisibility(View.VISIBLE);
                        userDistanceView.setVisibility(View.VISIBLE);           

                        userTimeView.setText(MainActivity.userTime);
                        userLevelView.setText(MainActivity.userLevel);
                        userDistanceView.setText(MainActivity.userDistance);
                        userVo2View.setText(String.valueOf(MainActivity.userVo2));
                        
                        values.set(position,
                        		   new Times((long)0,
                        				     "",
                        				     userNameView.getText().toString(),
                    		                 MainActivity.userTime,
                    		                 MainActivity.userLevel,
                    		                 MainActivity.userDistance,
                    		                 (int)MainActivity.userVo2,
                    		                 true, // stopped
                    		                 false));
                        
                        /*
                         * Increment the stopped user count.
                         */
                        MainActivity.incrementStopped();                        
                    }
                }
        );		
        
	    return rowView;
	}
}