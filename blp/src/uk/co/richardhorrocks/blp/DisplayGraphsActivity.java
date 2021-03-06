package uk.co.richardhorrocks.blp;

import java.util.List;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.widget.LinearLayout;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class DisplayGraphsActivity extends Activity {
	
    /**
     * Sets the Action Bar for new Android versions.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void actionBarSetup(String nameTitle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar ab = getActionBar();
            ab.setTitle("VO2 fitness graph for " + nameTitle);            
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_graphs);
             
        int index = 0;        
        
        /*
         * Get the name of the user we want to graph for from the intent that created us.
         */
        Intent intent = getIntent();
        String graphName = intent.getStringExtra(ResultsArrayAdapter.keyNAME);
                
        /*
         * Set the title of the action bar.
         */
        actionBarSetup(graphName);
        
        /*
         * Get all records for this user from the database.
         */
        final List<Times> values = MainActivity.datasource.getTimesForUser(graphName);

        /*
         * Create our graph data.
         */
        GraphViewData[] userData = new GraphViewData[values.size()];             
        GraphViewSeries exampleSeries = new GraphViewSeries(userData);
                
        /*
         * There aren't enough entries to create a graph. Inform the user of this, and don't display one.
         */
        if (values.size() == 1) {            
            AlertDialog.Builder builder = new AlertDialog.Builder(DisplayGraphsActivity.this);
            builder.setMessage("There isn't enough data for this user to draw a graph yet.");                             
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            builder.show();            
        } else {        
            /*
             * Get the data for the graph. 
             */
            for (index = 0; index < values.size(); index++) {
                userData[index] = new GraphViewData(index, values.get(index).getVo2());                
            }
            
            
            /*
             * Set the graph name.
             */                    
            GraphView graphView = new LineGraphView(
                  this,
                  "VO2 mL/(kg.min) vs. Abstract Time"
            );
                        
            
            /*
             * Set the label colours. This is *white* by default(!)
             */            
            
            /*
             * If we've got more than 20 values, set the number of X values to 10.
             */
            if (values.size() > 20){
            	graphView.getGraphViewStyle().setNumHorizontalLabels(10); 	
            } else {
            	graphView.getGraphViewStyle().setNumHorizontalLabels(values.size()); 
            }
            
            graphView.getGraphViewStyle().setNumVerticalLabels(11);            
            graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLACK);
            graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLACK);    
            
            Resources r = getResources();                        
            graphView.getGraphViewStyle().setTextSize(
            		TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, r.getDisplayMetrics()));  
            graphView.setManualYAxisBounds(100, 0);
            graphView.addSeries(exampleSeries);
                           
            LinearLayout layout = (LinearLayout) findViewById(R.id.subLayout);
            layout.addView(graphView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}