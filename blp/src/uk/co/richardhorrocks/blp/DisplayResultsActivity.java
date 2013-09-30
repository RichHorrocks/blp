package uk.co.richardhorrocks.blp;

import java.util.Collections;
import java.util.List;
import uk.co.richardhorrocks.blp.R;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.graphics.Color;

public class DisplayResultsActivity extends ListActivity {

    boolean sortAscending = false;
    boolean sortDescending = false;
    int sortId = 0;
    int sortType = 0;
    
    void clearHeaderColours() {
        ((TextView)findViewById(R.id.dateHeader)).setTextColor(Color.parseColor("#21cbbd"));
        ((TextView)findViewById(R.id.nameHeader)).setTextColor(Color.parseColor("#21cbbd"));
        ((TextView)findViewById(R.id.levelHeader)).setTextColor(Color.parseColor("#21cbbd"));
        ((TextView)findViewById(R.id.timeHeader)).setTextColor(Color.parseColor("#21cbbd"));
        ((TextView)findViewById(R.id.vo2Header)).setTextColor(Color.parseColor("#21cbbd"));
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_results);
  
        /*
         * We basically just want to output the contents of the SQL results database.
         * Order the contents of the ListView before setting the list in the adaptor.
         * Don't sort the adaptor.
         */      
        final List<Times> values = MainActivity.datasource.getAllTimes();                      
        final ResultsArrayAdapter adapter = new ResultsArrayAdapter(this, values);
        setListAdapter(adapter);                    
        
        if (values.size() == 0) {            
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("There aren't any results to display yet.");                             
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            builder.show();            
        }
        
        final TextView dateView = (TextView)findViewById(R.id.dateHeader);
        final TextView nameView = (TextView)findViewById(R.id.nameHeader);
        final TextView levelView = (TextView)findViewById(R.id.levelHeader);
        final TextView timeView = (TextView)findViewById(R.id.timeHeader);
        final TextView vo2View = (TextView)findViewById(R.id.vo2Header);                        

        /*
         * I'm anal. Subscript the '2' on VO2.
         *
        ((TextView)findViewById(R.id.vo2Header)).setText(Html.fromHtml("VO<sub>2</sub>"));
        ((TextView)findViewById(R.id.graphInstructions)).setText(Html.fromHtml("VO<sub>2</sub>"));*/
        
        /*
         * Listen for clicks on the column headers.
         */
        View.OnClickListener sortListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {               
                /*
                 * Check if we're currently sorted on this column.
                 * If not, sort ascending.
                 * If so, sort in the opposite direction.
                 */
                if (v.getId() == sortId) {
                    if (sortAscending) {
                        sortAscending = false;
                        ((TextView)v).setTextColor(Color.RED);
                    } else {
                        /*
                         * We're already sorting descending.
                         */
                        ((TextView)v).setTextColor(Color.BLACK);
                        sortAscending = true;
                    }                            
                } else {
                    clearHeaderColours();
                    sortId = v.getId();
                    ((TextView)v).setTextColor(Color.BLACK);
                    sortAscending = true;
                }
                             
                /*
                 * We're using the same sort for all columns.
                 * Distinguish which column it is.
                 * TODO There must be a better way to do this, right?
                 */
                if (v.getId() == dateView.getId()) {
                    sortType = 1;                    
                } else if (v.getId() == nameView.getId()) {
                    sortType = 2;
                } else if (v.getId() == levelView.getId()) {
                    sortType = 3;
                } else if (v.getId() == timeView.getId()) {
                    sortType = 4;
                } else if (v.getId() == vo2View.getId()) {
                    sortType = 5;
                }
                
                /*
                 * Sort the database.
                 */
                Collections.sort(values, new SortResults(sortAscending, sortType));
                setListAdapter(adapter);
                adapter.notifyDataSetChanged();                
            }            
        };
        
        /*
         * Set the click listeners.
         */
        dateView.setOnClickListener(sortListener);            
        nameView.setOnClickListener(sortListener);
        levelView.setOnClickListener(sortListener);
        timeView.setOnClickListener(sortListener);
        vo2View.setOnClickListener(sortListener);    
        
        /*
         * Create a listener for deleting the long-clicked-on row.
         */
        ListView lv = getListView();
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){ 
            @Override 
            public boolean onItemLongClick(AdapterView<?> av, View v, int position, long id) 
           {
                Toast.makeText(DisplayResultsActivity.this, 
                        "Deleted item for user " + values.get(position).getName(), 
                        Toast.LENGTH_SHORT).show();
                
                /*
                 * Remove it from the database.
                 */
                MainActivity.datasource.deleteTime(values.get(position));                              
                
                /*
                 * Remove from the list, then update the adapter.
                 */
                values.remove(position);
                adapter.notifyDataSetChanged();     

                return true;                
           } 
      }); 
    }
}
