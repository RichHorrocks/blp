package uk.co.richardhorrocks.blp;

import java.util.List;
import uk.co.richardhorrocks.blp.R;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ResultsArrayAdapter extends ArrayAdapter<Times> {
    private final Context context;
    private final List<Times> values;

    public ResultsArrayAdapter(Context context, List<Times> values) {
        super(context, R.layout.resultslayout, values);
        this.context = context;
        this.values = values;
    }

    /*
     * Set the name of the user we're graphing for into the intent using this variable.
     * Must be accesible from the new activity, hence 'final'.
     */
    public final static String keyNAME = "uk.co.richardhorrocks.blp.NAME";
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {      
        /*
         * Inflate!
         */
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.resultslayout, parent, false);
                  
        /*
         * Assign some general views.
         */        
        final TextView dateView = (TextView)rowView.findViewById(R.id.date);
        final TextView nameView = (TextView)rowView.findViewById(R.id.name);
        final TextView levelView = (TextView)rowView.findViewById(R.id.level);
        final TextView timeView = (TextView)rowView.findViewById(R.id.time);
        final TextView vo2View = (TextView)rowView.findViewById(R.id.vo2);
     
        /*
         * Set the values into the view.
         */
        dateView.setText(values.get(position).getDate());
        nameView.setText(values.get(position).getName());
        levelView.setText(values.get(position).getLevel());
        timeView.setText(values.get(position).getTime());
        vo2View.setText(String.valueOf(values.get(position).getVo2()));
        
        /*
         * When selected, we open a new activity which shows performance graphs for that user.
         */
        nameView.setTag(position);
        nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DisplayGraphsActivity.class);
                intent.putExtra(keyNAME, values.get((Integer)v.getTag()).getName());            
                v.getContext().startActivity(intent);                                    
            }
        });    
                
        return rowView;
    }        
}