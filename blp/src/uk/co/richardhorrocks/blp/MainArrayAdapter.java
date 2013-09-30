package uk.co.richardhorrocks.blp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import uk.co.richardhorrocks.blp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class MainArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
	private final ArrayList<String> values;

	public MainArrayAdapter(Context context, ArrayList<String> values) {
	    super(context, R.layout.removelayout, values);
	    this.context = context;
	    this.values = values;
	}
	  
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {	    
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
		userNameView.setText(values.get(position));	    
		final Button removeButtonView = (Button)rowView.findViewById(R.id.removeuserButton);
		final Button saveButtonView = (Button)rowView.findViewById(R.id.saveuserButton);
        final Button stopButtonView = (Button)rowView.findViewById(R.id.stopuserButton);
        final TextView userTimeView = (TextView)rowView.findViewById(R.id.userTime);
        final TextView userLevelView = (TextView)rowView.findViewById(R.id.userLevel);
        final TextView userDistanceView = (TextView)rowView.findViewById(R.id.userDistance);        
        final Button savedButtonView = (Button)rowView.findViewById(R.id.saveduserButton);
        
		if (MainActivity.showListStop) {
            removeButtonView.setVisibility(View.GONE);
            stopButtonView.setVisibility(View.VISIBLE);
		}
		
		if (MainActivity.resetList) {
            removeButtonView.setVisibility(View.VISIBLE);
            stopButtonView.setVisibility(View.GONE);
            saveButtonView.setVisibility(View.GONE);
            userTimeView.setVisibility(View.GONE);
            userLevelView.setVisibility(View.GONE);
            userDistanceView.setVisibility(View.GONE);
            savedButtonView.setVisibility(View.GONE);            
		}
			
		if (MainActivity.stopList) {
            removeButtonView.setVisibility(View.GONE);
            stopButtonView.setVisibility(View.GONE);
            saveButtonView.setVisibility(View.VISIBLE);
            userTimeView.setText(MainActivity.userTime);
            userTimeView.setVisibility(View.VISIBLE);
            userLevelView.setText(MainActivity.userLevel);
            userLevelView.setVisibility(View.VISIBLE);
            userDistanceView.setText(MainActivity.userDistance);
            userDistanceView.setVisibility(View.VISIBLE);                        		    
		}
		
		/*
		 * Assign the click response for the delete button.
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
		        /*
		         * Save the following values in the SQL database:
		         *  - User's name;
		         *  - Time on this run;
		         *  - Level on this run.
		         */
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
	                                                       MainActivity.userTime,
	                                                       userLevelView.getText().toString(),
	                                                       (int)MainActivity.userVo2);
	                    
	                    /*
	                     * Remove the 'Save' button once saving is complete.
	                     */
	                    saveButtonView.setVisibility(View.GONE);
	                    savedButtonView.setVisibility(View.VISIBLE);
	                    savedButtonView.setClickable(false);
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
                        userTimeView.setText(MainActivity.userTime);
                        userTimeView.setVisibility(View.VISIBLE);
                        userLevelView.setText(MainActivity.userLevel);
                        userLevelView.setVisibility(View.VISIBLE);
                        userDistanceView.setText(MainActivity.userDistance);
                        userDistanceView.setVisibility(View.VISIBLE);                        
                        
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