package com.travelsmartlondon;


import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.travelsmartlondon.context.TSLApplicationContext;
import com.travelsmartlondon.database.DBSQLiteHelper;

public class MainActivity extends Activity {

	private ListView _mainListView;
	
	public static final String SHOW_MAP = "Show Map";
	public static final String TUBE_LINES = "Tube Lines";
	public static final String LOGIN_WITH_GOOGLE = "Log in with Google";
	public static final String LOGOUT = "Log out";
	public static final String LOGIN = "Log in";
	public static final String ABOUT_US = "About Travel Smart London";
	public static final String ACCOUNT_TYPE_GOOGLE = "com.google";
	public static final String AUTHORISATION_SCOPE_VIEW_YOUR_TASKS = "View your tasks";
	public static final String AUTHORISATION_SCOPE_MANAGE_YOUR_TASKS = "Manage your tasks";
	public static final String WELCOME_MESSAGE = "Welcome ";
	public static final String GOODBYE_MESSAGE = "Goodbye ";
	public static final String ERROR_MESSAGE = "Error occurred: ";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		this._mainListView = (ListView) findViewById(R.id.main_activity_listView);
	    String[] items = new String[] {SHOW_MAP, TUBE_LINES, LOGIN_WITH_GOOGLE, ABOUT_US};
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
	    this._mainListView.setAdapter(adapter);
	    this._mainListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if( ((TextView)view).getText().toString() == SHOW_MAP){
					Intent intent = new Intent(MainActivity.this,MapActivity.class);
					startActivity(intent);
				} else if(((TextView)view).getText().toString() == TUBE_LINES){
					Intent intent = new Intent(MainActivity.this,TubeLineActivity.class);
					startActivity(intent);
				} else if(((TextView)view).getText().toString() == LOGIN_WITH_GOOGLE) {
					showProgress(true);
					loginWithGoogleAccount(view);
				} else if(((TextView)view).getText().toString() == LOGOUT) {
					messageSuccessful(TSLApplicationContext.getInstance().currentEmailAddress(), LOGOUT);
					TSLApplicationContext.getInstance().injectEmailAddressOnceUserLoggedIn(null);
					((TextView)view).setText(LOGIN_WITH_GOOGLE);
				} else if(((TextView)view).getText().toString() == ABOUT_US) {
					//TODO: Implement the screen
				}
			}
	    });
	    
	    DBSQLiteHelper sqliteHelper = new DBSQLiteHelper(getApplicationContext());
	    try{
	    	sqliteHelper.createDataBase();
	    }
	    catch(IOException e)
	    {
	    	System.err.println("Database could not be created");
	    }
	    
	}
	
	private void loginWithGoogleAccount(View view) {
		AccountManager am = AccountManager.get(this);
		Bundle options = new Bundle();
		Account[] accounts = am.getAccountsByType(ACCOUNT_TYPE_GOOGLE);
		
		am.getAuthToken(
		    accounts[0],            				// Account retrieved using getAccountsByType()
		    AUTHORISATION_SCOPE_MANAGE_YOUR_TASKS,  // Authorization scope
		    options,                       			// Authenticator-specific options
		    this,                           		// Your activity
		    new OnTokenAcquired(view),      		// Callback called when a token is successfully acquired
		    new Handler(new OnError()));    		// Callback called if an error occurs
	}
	
	private class OnError implements Callback {

		@Override
		public boolean handleMessage(Message msg) {
			messageSuccessful(" PERMISSION NOT GRANTED", "NOT");
			return true;
		}
		
	}
	
	private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
		private View _view;
	    public OnTokenAcquired(View view_) {
			this._view = view_;
		}

		@Override
	    public void run(AccountManagerFuture<Bundle> result_) {
	        Bundle bundle = null;
			try {
				bundle = result_.getResult();
			} catch (OperationCanceledException e) {
				e.printStackTrace();
			} catch (AuthenticatorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    
	        // The token is a named value in the bundle. The name of the value
	        // is stored in the constant AccountManager.KEY_AUTHTOKEN.
			if(bundle != null) {
	        String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);//not used
	        String emailAddress = bundle.getString(AccountManager.KEY_ACCOUNT_NAME);
	        TSLApplicationContext.getInstance().injectEmailAddressOnceUserLoggedIn(emailAddress);
	        messageSuccessful(emailAddress, LOGIN);
	        ((TextView)this._view).setText(LOGOUT);
			} else { 
				messageSuccessful(" PERMISSION NOT GRANTED", "NOT");
			}
	        showProgress(false);
	    }
	}
	
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		
	}
	
	private void messageSuccessful(String emailAddress_, String loginLogout_) {
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		String title = (loginLogout_.equals(LOGIN)) ? WELCOME_MESSAGE : 
					((loginLogout_.equals(LOGOUT))? GOODBYE_MESSAGE : ERROR_MESSAGE);
		builder.setMessage(loginLogout_ + " successful!")
	       .setTitle(title + emailAddress_);
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		    	  dialog.dismiss();
		    } });
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			this._mainListView.setVisibility(View.VISIBLE);
			this._mainListView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							_mainListView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			this._mainListView.setVisibility(View.VISIBLE);
			this._mainListView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							_mainListView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			this._mainListView.setVisibility(show ? View.VISIBLE : View.GONE);
			this._mainListView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	 */
}
