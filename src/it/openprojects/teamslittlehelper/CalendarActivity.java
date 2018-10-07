package it.openprojects.teamslittlehelper;

import java.io.File;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;


public class CalendarActivity extends Activity implements OnGestureListener {
	private static final int SWIPE_MIN_DISTANCE = 80;
	private static final int SWIPE_MAX_OFF_PATH = 300;
	private static final int SWIPE_THRESHOLD_VELOCITY = 500;
	private GestureDetector gestureScanner;
	
	private String day = "";
	public Calendar month;
	public CalendarAdapter adapter;
	public Handler handler;
	public ArrayList<String> items; // container to store some random calendar items
	
	public final static int SUNDAY = 0;
    public final static int MONDAY = 1;
    public final static int TUESDAY = 2;
    public final static int WEDNESDAY = 3;
    public final static int THURSDAY = 4;
    public final static int FRIDAY = 5;
    public final static int SATURDAY = 6;
    static final int INSERT_GAME_REQUEST = 0;
    static final int DELETE_GAME_REQUEST = 1;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
	    gestureScanner = new GestureDetector(this);
	    setContentView(R.layout.calendar);
	    TextView sectionTitle = (TextView) findViewById(R.id.sectionTitle);
	    sectionTitle.setText(this.getString(R.string.sectionCalendar) + " " + GameManagement.teamDir);
	    
	    month = Calendar.getInstance();
	    //onNewIntent(getIntent());
	    items = new ArrayList<String>();
	    adapter = new CalendarAdapter(this, month);
	    
	    GridView gridview = (GridView) findViewById(R.id.gridview);
	    gridview.setAdapter(adapter);
	    
	    // SET DAYS NAMES
	    String[] weekdays = new DateFormatSymbols().getShortWeekdays();
	    // Hack to get rid of 1-8 weekdays problem
	    ArrayList<String> days = new ArrayList<String>();
	    for (int i = 1; i < (weekdays.length); i++) {
	         days.add(weekdays[i]);
	    }
	    GridView gridviewdays = (GridView) findViewById(R.id.gridviewdays);
	    ArrayAdapter<String> adapterdays = new ArrayAdapter<String>(this, R.layout.calendar_day, days);
	    gridviewdays.setAdapter(adapterdays);
	    
	    handler = new Handler();
	    handler.post(calendarUpdater);
	    
	    TextView title  = (TextView) findViewById(R.id.title);
	    title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	    
	    TextView previous  = (TextView) findViewById(R.id.previous);
	    previous.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				prevClicked();
			}
		});
	    
	    TextView next  = (TextView) findViewById(R.id.next);
	    next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				nextClicked();
				
			}
		});
	    
	    
		gridview.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		    	
		    	TextView date = (TextView)v.findViewById(R.id.date);
		        if(date instanceof TextView && !date.getText().equals("")) {
		        	day = date.getText().toString();
		        	if(day.length()==1) {
		        		day = "0"+day;
		        	}

	            	Intent myIntent = new Intent(getApplicationContext(), GameActivity.class);
		        	myIntent.putExtra("dateFile", android.text.format.DateFormat.format("yyyy-MM", month)+"-"+day);
		        	if (items.contains(day)) { myIntent.putExtra("saved", 1); }
					startActivity(myIntent);
		        	
		        }
		        
		    }
		});
		
		gridview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
				TextView date = (TextView)v.findViewById(R.id.date);
		        if(date instanceof TextView && !date.getText().equals("")) {
		        	day = date.getText().toString();
		        	if(day.length()==1) {
		        		day = "0"+day;
		        	}
					if (items.contains(day)) {

			        	final String choosenDate = android.text.format.DateFormat.format("yyyy-MM", month)+"-"+day;
						AlertDialog.Builder adb=new AlertDialog.Builder(CalendarActivity.this);
						adb.setTitle(R.string.delete);
				        adb.setMessage(R.string.delete_confirm);
				        adb.setNegativeButton(android.R.string.no, null);
				        adb.setPositiveButton(android.R.string.yes, new AlertDialog.OnClickListener() {
				            public void onClick(DialogInterface dialog, int which) {
				            	// DELETE saved file
				            	File file = new File(GameManagement.DATA_DIRECTORY + GameManagement.teamDir + "/" + choosenDate + GameManagement.TLH_EXT);
				            	boolean deleted = file.delete();
				            	if (deleted) {
				            		Toast.makeText(getApplicationContext(),R.string.delete_ok, Toast.LENGTH_SHORT).show();
				            	} else {
				            		Toast.makeText(getApplicationContext(),R.string.delete_ko, Toast.LENGTH_SHORT).show();
				            	}
				            	refreshCalendar();
				            }});
				        adb.show();
					
					}
		        }
		        // STOP CATCHING PRESS
				return true;
			}
		});
		
	}
	
    private void nextClicked() {
    	if(month.get(Calendar.MONTH)== month.getActualMaximum(Calendar.MONTH)) {				
			month.set((month.get(Calendar.YEAR)+1),month.getActualMinimum(Calendar.MONTH),1);
		} else {
			month.set(Calendar.MONTH,month.get(Calendar.MONTH)+1);
		}
		refreshCalendar();	
    }
	
    private void prevClicked() {
    	if(month.get(Calendar.MONTH)== month.getActualMinimum(Calendar.MONTH)) {				
			month.set((month.get(Calendar.YEAR)-1),month.getActualMaximum(Calendar.MONTH),1);
		} else {
			month.set(Calendar.MONTH,month.get(Calendar.MONTH)-1);
		}
		refreshCalendar();	
    }
	
	public void refreshCalendar()
	{
		TextView title  = (TextView) findViewById(R.id.title);
		adapter.refreshDays();
		adapter.notifyDataSetChanged();				
		handler.post(calendarUpdater);				
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
	}
	
	public void onNewIntent(Intent intent) {
		String date = intent.getStringExtra("date");
		String[] dateArr = date.split("-"); // date format is yyyy-mm-dd
		month.set(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]), Integer.parseInt(dateArr[2]));
	}
	

	public Runnable calendarUpdater = new Runnable() {
		
		@Override
		public void run() {
			items.clear();

			ArrayList<String> savedGames = new ArrayList<String>(); 
			savedGames = GameManagement.getSavedGamesName();
  	
			Integer actual_month = 0;
			//Toast.makeText(getApplicationContext(),savedGames.toString(), Toast.LENGTH_SHORT).show();

	    	if (!savedGames.isEmpty()) {
	    		for ( int i = 0;i<savedGames.size();i++) {
	    			actual_month = ConvertToDate(savedGames.get(i),"yyyy-MM-dd").getMonth();
    				if (actual_month == month.get(Calendar.MONTH)) {
    					items.add(Integer.toString(ConvertToDate(savedGames.get(i),"yyyy-MM-dd").getDate()));
    				}    				
	    		}
	    	}

			adapter.setItems(items);
			adapter.notifyDataSetChanged();
		}
	};
	
	
	public static Date ConvertToDate(String dateString, String format){
	    
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date convertedDate;
		try {
			convertedDate = dateFormat.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		return convertedDate;
	}

	@Override
	   public boolean onTouchEvent(MotionEvent me)
	   {
	    return gestureScanner.onTouchEvent(me);
	   }
	
	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
		    return false;
		   if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
		     && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
		    nextClicked();
		   } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
		     && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
		    prevClicked();
		   }
		   return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		handler.post(calendarUpdater);
		super.onResume();
	}
}
