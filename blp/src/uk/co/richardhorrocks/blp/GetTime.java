package uk.co.richardhorrocks.blp;

import android.view.View;
import android.widget.TextView;
import uk.co.richardhorrocks.blp.R;

public class GetTime extends MainActivity {
    public static String string1 = "foo";

    public static String getState(View view) {
      return ((TextView)view.findViewById(R.id.level)).getText().toString();
      
    }
}
