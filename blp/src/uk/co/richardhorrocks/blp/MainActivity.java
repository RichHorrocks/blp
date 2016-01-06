package uk.co.richardhorrocks.blp;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ListActivity {
	
	/*
	 * Globals used when adding new users.
	 */
	static int userMax = 12;
    static int userStopped = 0;
    static int userCount = 0;
    EditText addUserText;
	String   addUserString;
	
	//ArrayList<String> listItems = new ArrayList<String>();
	ArrayList<Times> listItems = new ArrayList<Times>();
	MainArrayAdapter adapter;	
	
    /*
     * Globals used by the timer.
     */
    private Handler mHandler = new Handler(); 
    private long startTime; 
    private long elapsedTime; 
    private final int REFRESH_RATE = 100;
    private boolean updateLevel = false; // HACK
    	    
    /*
     * Globals used for the level.
     */
    private int minor_lev = 0;
    private int major_lev = 1;
    private int dist = 0;
    private String distance;
    private String minor_level;
    private String major_level;
    
    /*
     * Globals used for the bleep calculations.
     * These are the number of 20m shuttles required for the respective levels.
     */
    private int shuttleDistancePref = 20;        
    private int[] shuttleArray = { 7, 8, 8, 9, 9, 10, 10, 11, 11, 11,
                                   12, 12, 13, 13, 13, 14, 14, 15, 15, 16, 16 }; //21 levels
    private double[] shuttleDistance = { 8, 9, 9.5, 10, 10.5, 11, 11.5, 12, 12.5, 13, 13.5, 14, 
                                         14.5, 15, 15.5, 16, 16.5, 17, 17.5, 18, 18.5 };
    static String userTime;
    static String userLevel = "1.0";
    static String userDistance = "0000m";
    static double userVo2 = 0;
    
    /*
     * Static declarations used by other activities.
     */
    public static long runId = 1;
    public static KeyListener savedListener;
    public static boolean showListStop = false;
    public static boolean resetList = false;
    public static boolean stopList = false;
    public static CommentsDataSource datasource;   
    public static void incrementStopped() {
        userStopped++;
    }         

    /*
     * Handler for the addition of a new user.
     */
	public void addUserClick (View view) {
        /*
         * Hide the virtual keyboard.
         */	    
        InputMethodManager imm = 
          (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);        
	    
    	/*
    	 * In response to the captured click we want to add the user's name to the list of users. 
    	 */
    	EditText addUserText = (EditText)findViewById(R.id.newuserName);
    	String givenString = addUserText.getText().toString();
    	    	
    	/*
    	 * Remove any trailing whitespace.
    	 */
    	addUserString = givenString.trim(); 
    	
    	/*
    	 * Check whether we actually have some input, and whether the name is unique.
    	 */
    	if (!addUserString.matches("") && !listItems.contains(addUserString)) {
            /*
             * There's some input, and it's a non-duplicate.
             */
            if (userCount != userMax) {
            	/*
                 * If this is the first user we've added, then the 'Start' button will become available.
                 * Show the 'available' state button.
            	 */
            	((Button)findViewById(R.id.startButton)).setVisibility(View.VISIBLE);            	           	        	
               
            	/*
            	 * Add the user string to the list, and update the display adapter.
            	 */
            	adapter = new MainArrayAdapter(this, listItems);
            	setListAdapter(adapter);
            	//listItems.add(addUserString);
                listItems.add(new Times((long)0, "", addUserString, "", "", "", 0, false, false));
            	adapter.notifyDataSetChanged();
                
                
                /*
                 * Clear the entered name from the text box..parseColor("#E19090"));
                 */
                addUserText.setText("");
                userCount++;        

                /*
                 * If we've reached the upper limit of allowed users, remove the availability of the 
                 * 'Add user' button.
                 */
                if (userCount == userMax) {
                    ((Button)findViewById(R.id.adduserButton)).setClickable(false);
                    ((Button)findViewById(R.id.adduserButton)).setTextColor(Color.parseColor("#E19090"));
                    ((EditText)findViewById(R.id.newuserName)).setText(R.string.fulluserText);
                    ((EditText)findViewById(R.id.newuserName)).setTextColor(Color.parseColor("#E19090"));
                    savedListener = ((EditText)findViewById(R.id.newuserName)).getKeyListener();
                    ((EditText)findViewById(R.id.newuserName)).setKeyListener(null);                    
                }    
            } else {
            	/*
            	 * The maximum number of users has already been defined.
            	 */
            }
    	}    
    }

    /*
     * Called when the 'Remove' button is clicked in the ListView of users.
     */
	public static Button userButtonView;
	public static EditText userTextView;
	public static Button startButtonView;
		
    static public void removeUser() {       
        if (userCount == userMax) {
            userButtonView.setClickable(true);
            userButtonView.setTextColor(Color.WHITE);
            userTextView.setText("");
            userTextView.setTextColor(Color.BLACK);
            userTextView.setKeyListener(savedListener);
            userTextView.setHint(R.string.newuserText);
        } else if (userCount - 1 == 0) {
            startButtonView.setVisibility(View.GONE);           
        }
        userCount--;        
    }
        
    /*
     * Listener for 'enter' keypresses.
     */
    OnKeyListener returnListener = new EditText.OnKeyListener() {            
        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {            
            switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                addUserClick(addUserText);                                                                
                return true;
            default:
                return false;                
            }            
        }    
    };      
    
    /*
     * Calculate the VO2 value.
     * 
     * **Ramsbottom et al. (1988) "A progressive shuttle run test to 
     * estimate maximal oxygen uptake." British Journal of Sports Medicine 22: 141-5.** 
     * 
     * Particularly laborious due to the non-decimality of the number of shuttles per stage...
     * Return an int - can't be bothered with decimals.
     * Seems to match the published figures within ~3 VO2 "units".
     */
    int calculateVo2 (int major, int minor) {
        /*
         * Determine the number of shuttles in this major level, then what each equates to.
         */
        double shuttleWorth = 1 / shuttleArray[major - 1];
        
        /*
         * Plug this into our formula.
         * y = 3.48x + 14.4
         */
        return (int)(3.48 * (major + (shuttleWorth * minor)) + 14.4);
    }
    
    /*
     * Update the level output to the user.
     */
    public void updateLevel () {
        String padding;
        
        if (updateLevel) {
    	    /*
    	     * Increment the minor level.
    	     * Check, and if appropriate, increment the major level.
    	     */
    	    if (++minor_lev == shuttleArray[major_lev - 1]) {
            	/*
            	 * We've reached the end of this level. 
            	 * Increment the major level, and reset the minor level to 0.
            	 * Also calculate the new rate of VO2.
            	 */
    	    	++major_lev;
    	    	minor_lev = 0;    	    	    	    	    	    	 
    	    	
    	    	shortPool.play(shortId, 100, 100, 1, 0, 1f);
    	    } else {
                longPool.play(longId, 100, 100, 1, 0, 1f);
    	    }
    	        	    
    	    userVo2 = calculateVo2(major_lev, minor_lev);
            
    	    major_level = String.valueOf(major_lev);    		
 	        minor_level = String.valueOf(minor_lev);
    	    ((TextView)findViewById(R.id.level)).setText(major_level + "." + minor_level);
    	    userLevel = major_level + "." + minor_level;
    	    
    	    /*
    	     * Increment the shown distance by the length of a single shuttle.
    	     */ 
    	    dist += shuttleDistancePref;
    	    if (dist < 100) {
    	        padding = "00"; 
    	    } else if (dist >= 100 && dist < 1000) {
    	        padding = "0";
    	    } else {
    	        padding = "";
    	    }
    	    distance = String.valueOf(dist);
    	    ((TextView)findViewById(R.id.distance)).setText("Distance: " + padding + distance + " m");
    	    userDistance = padding + distance + "m";    	        	        	    
    	    
            /*
             * If we've reached the end of the test, inform the user and stop everything.
             */
            if (major_lev == 21) {                
                stopList = true;
                adapter.notifyDataSetChanged();                        
                stopEvent();                
                
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("You've beaten the bleep test! Well done!.");                             
                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        /*
                         * Do nothing, just remove the dialog box.
                         */
                    }
                });
                builder.show();            
            }    	    
        } else {
            updateLevel = true; // HACK There must be a better way to do this...
        }
    }    	

    /*
     * Create and start the timer associated with the stopwatch.
     */
    private Runnable levelTimer = new Runnable() {
    	public void run() {    
    	    double levelTime = shuttleDistancePref / (((shuttleDistance[major_lev - 1]) * 1000) / 3600);
    	                		
    		mHandler.postDelayed(levelTimer, (long)(levelTime * 1000));
  			updateLevel();	
       	};       	
    };  
    
    /*
     * Update the timer at the given refresh rate.
     */
    private void updateTimer (float time) {
        String deciseconds, seconds, minutes; 
    	long decs = (long)(time/100);
    	long secs = (long)(time/1000); 
    	long mins = (long)((time/1000)/60);

    	decs = decs % 10;
    	deciseconds = String.valueOf(decs);
    	
    	secs = secs % 60; 
    	seconds = String.valueOf(secs); 
    	if (secs == 0) { 
    		seconds = "00"; 
        } else if (secs < 10 && secs > 0) { 
    		seconds = "0" + seconds; 
        } 
    	
    	mins = mins % 60; 
    	minutes = String.valueOf(mins); 
    	if (mins == 0) { 
    		minutes = "00"; 
    	} else if (mins < 10 && mins > 0) { 
    		minutes = "0" + minutes; 
        } 
    	   	  	    	
    	userTime = minutes + ":" + seconds + "." + deciseconds;
    	((TextView)findViewById(R.id.timer)).setText("Time: " + userTime);    	    	
    }    
    
    private Runnable startTimer = new Runnable() { 
    	public void run() { 
    		elapsedTime = System.currentTimeMillis() - startTime; 
    		updateTimer(elapsedTime);
    		mHandler.postDelayed(this, REFRESH_RATE);
    	    
    		/*
    		 * If all individual users have stopped, stop the overall timer.
    		 */
    		if (userStopped == userCount) {
    			stopList = true;
    		    stopEvent();
    	    }
       	};
    };  
    
    private SoundPool shortPool;
    private SoundPool longPool;
    private int shortId;
    private int longId;    
    
    public void loadSounds() {
        /*
         * Load our sounds.
         */
        shortPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        shortId = shortPool.load(MainActivity.this, R.raw.beep7, 1);
        longPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        longId = longPool.load(MainActivity.this, R.raw.beep9, 1);
        
        /*
         * Ensure the volume buttons control the app volume, not the phone's ringer volume.
         */
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }
    
    
    public void shortBleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {            
            e.printStackTrace();
        }
        shortPool.play(shortId, 100, 100, 1, 0, 1f);
    }
        
    public void longBleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        longPool.play(longId, 100, 100, 1, 0, 1f);
    }
                
    /*
     * Tell the user we're about to start!
     */
    public void warnUser () {        
        for (int index = 0; index < 3; index++) {
            shortBleep();        
        }
        longBleep();
    }
        
    /*
     * Called when the 'Start' button is pressed.
     */
    public void startClick (View view) {
        /*
         * Hide the virtual keyboard.
         */
        InputMethodManager imm = 
          (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(userTextView.getWindowToken(), 0);        

        /*
         * Add a wakelock to prevent the screen turning off while the timer is running.
         */
        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        /*
         * Get the shuttle distance from the preferences.
         */
    	((Button)findViewById(R.id.startButton)).setVisibility(View.GONE);
    	((TextView)findViewById(R.id.timer)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.level)).setVisibility(View.VISIBLE);
    	((TextView)findViewById(R.id.level)).setText("1.0");
    	((Button)findViewById(R.id.adduserButton)).setVisibility(View.GONE);
    	((EditText)findViewById(R.id.newuserName)).setVisibility(View.GONE);

    	/*
    	 * Update the view for all listview objects. We want each member to show its individual
    	 * stop button.
    	 */
    	showListStop = true;
    	resetList = false;
    	runId++;
        adapter.notifyDataSetChanged();

        /*
         * Make the overall stop button visible.
         */
    	((Button)findViewById(R.id.stopButton)).setVisibility(View.VISIBLE);    	    	
    	
    	/*
    	 * Start the timer.
    	 */
    	warnUser();
      	startTime = System.currentTimeMillis(); 
    	mHandler.removeCallbacks(startTimer); 
    	mHandler.postDelayed(startTimer, 0);
    	mHandler.removeCallbacks(levelTimer); 
    	mHandler.postDelayed(levelTimer, 0);
    }
    
    /*
     * Reset the whole thing!
     */
    public void resetClick (View view) {
        userStopped = 0;
        elapsedTime = 0; 
        minor_lev = 0;
        major_lev = 1;
        dist = 0;
        userLevel = "1.0";
        userDistance = "0000m";
        userVo2 = 0;
        
        updateLevel = false;
        showListStop = false;
        resetList = true;
        stopList = false;
        adapter.notifyDataSetChanged();        
        
        ((Button)findViewById(R.id.startButton)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.timer)).setText(R.string.timer);
        ((TextView)findViewById(R.id.level)).setText(R.string.level);
        ((TextView)findViewById(R.id.distance)).setText(R.string.distance);
        ((Button)findViewById(R.id.resetButton)).setVisibility(View.GONE);        
        ((Button)findViewById(R.id.adduserButton)).setVisibility(View.VISIBLE);
        ((EditText)findViewById(R.id.newuserName)).setVisibility(View.VISIBLE);
    }

    /*
     * Handle the overall 'Stop' button being pressed.
     */
    public void stopClick (View view) {                
        stopEvent();
    	
    	stopList = true;
        showListStop = false;
        adapter.notifyDataSetChanged();                
    }
    
    /*
     * Called if the timer is completely stopped.
     */
    public void stopEvent () {    
        /*
         * Remove the screen lock!
         */
        getWindow().clearFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        /*
         * Stop the timers.
         */
        mHandler.removeCallbacks(startTimer);
        mHandler.removeCallbacks(levelTimer);        

        /*
         * Show the 'Reset' button, and remove the 'Stop' button.
         */
        ((Button)findViewById(R.id.stopButton)).setVisibility(View.GONE);        
        ((Button)findViewById(R.id.resetButton)).setVisibility(View.VISIBLE);        
    }        
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);                               

        /*
         * Initialise some variables.
         */        
        userStopped = 0;
        userCount = 0;
        showListStop = false;
        resetList = false;
        stopList = false;
        
        /*
         * Create the SQL database.
         */
        datasource = new CommentsDataSource(this);
        datasource.open();

        /*
         * Listen for 'return' keystrokes when adding users.
         */
        EditText addUserText = (EditText)findViewById(R.id.newuserName);        
        addUserText.setOnKeyListener(returnListener);        
        
        /*
         * Load our sound pools.
         */
        loadSounds();
        
        /*
         * Begin with the 'Start' button as unclickable.
         */        
        userButtonView = (Button)findViewById(R.id.adduserButton);
        userTextView = (EditText)findViewById(R.id.newuserName);
        startButtonView = (Button)findViewById(R.id.startButton);
        startButtonView.setVisibility(View.GONE);
    }
        
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        
        int itemId = item.getItemId();
		if (itemId == R.id.action_settings) {
			/*
			 * Do nothing for now.
			 */
		} else if (itemId == R.id.action_results) {
			intent = new Intent(this, DisplayResultsActivity.class);
			startActivity(intent);
		} else if (itemId == R.id.action_instructions) {
			intent = new Intent(this, DisplayInstructionsActivity.class);
			startActivity(intent);
		} else {
			intent = null;
			startActivity(intent);
		}        

        return true;
    } 
}