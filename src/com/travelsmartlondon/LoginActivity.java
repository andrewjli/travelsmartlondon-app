package com.travelsmartlondon;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static final String[] DUMMY_CREDENTIALS = new String[] {
			"foo@example.com:hello", "bar@example.com:world" };

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask _mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String _mEmail;
	private String _mPassword;

	// UI references.
	private EditText _mEmailView;
	private EditText _mPasswordView;
	private View _mLoginFormView;
	private View _mLoginStatusView;
	private TextView _mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState_) {
		super.onCreate(savedInstanceState_);

		setContentView(R.layout.login);

		// Set up the login form.
		this._mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		this._mEmailView = (EditText) findViewById(R.id.email);
		this._mEmailView.setText(_mEmail);

		this._mPasswordView = (EditText) findViewById(R.id.password);
		this._mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		this._mLoginFormView = findViewById(R.id.login_form);
		this._mLoginStatusView = findViewById(R.id.login_status);
		this._mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		
		findViewById(R.id.cancel_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						cancelLoginAction();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu_) {
		super.onCreateOptionsMenu(menu_);
		getMenuInflater().inflate(R.menu.login, menu_);
		return true;
	}
	
	/**
	 * Cancels the login action and causes the screen to revert back to main screen.
	 */
	public void cancelLoginAction() {
		Intent intent = new Intent(LoginActivity.this,MainActivity.class);
		startActivity(intent);
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (this._mAuthTask != null) {
			return;
		}

		// Reset errors.
		this._mEmailView.setError(null);
		this._mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		this._mEmail = _mEmailView.getText().toString();
		this._mPassword = _mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(this._mPassword)) {
			this._mPasswordView.setError(getString(R.string.error_field_required));
			focusView = this._mPasswordView;
			cancel = true;
		} else if (this._mPassword.length() < 4) {
			this._mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = this._mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(_mEmail)) {
			this._mEmailView.setError(getString(R.string.error_field_required));
			focusView = this._mEmailView;
			cancel = true;
		} else if (!this._mEmail.contains("@")) {
			this._mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = this._mEmailView;
			cancel = true;
		}

		if (cancel) {
			/*
			There was an error; don't attempt login and focus the first
			form field with an error.
			*/
			focusView.requestFocus();
		} else {
			/*
			Show a progress spinner, and kick off a background task to
			perform the user login attempt.
			 */
			this._mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			this._mAuthTask = new UserLoginTask();
			this._mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show_) {
		/*
		On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		for very easy animations. If available, use these APIs to fade-in
		the progress spinner.
		*/
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
			this._mLoginStatusView.setVisibility(View.VISIBLE);
			this._mLoginStatusView.animate().setDuration(shortAnimTime)
											.alpha(show_ ? 1 : 0)
											.setListener(new AnimatorListenerAdapter() {
												@Override
												public void onAnimationEnd(Animator animation) {
													_mLoginStatusView.setVisibility(show_ ? View.VISIBLE : View.GONE);
												}
											});
			this._mLoginFormView.setVisibility(View.VISIBLE);
			this._mLoginFormView.animate().setDuration(shortAnimTime)
											.alpha(show_ ? 0 : 1)
											.setListener(new AnimatorListenerAdapter() {
												@Override
												public void onAnimationEnd(Animator animation) {
													_mLoginFormView.setVisibility(show_ ? View.GONE	: View.VISIBLE);
												}
											});
		} else {
			/*
			The ViewPropertyAnimator APIs are not available, so simply show
			and hide the relevant UI components.
			*/
			this._mLoginStatusView.setVisibility(show_ ? View.VISIBLE : View.GONE);
			this._mLoginFormView.setVisibility(show_ ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params_) {
			// TODO: attempt authentication against a network service.

			try {
				// Simulate network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return false;
			}

			for (String credential : DUMMY_CREDENTIALS) {
				String[] pieces = credential.split(":");
				if (pieces[0].equals(_mEmail)) {
					// Account exists, return true if the password matches.
					return pieces[1].equals(_mPassword);
				}
			}

			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success_) {
			_mAuthTask = null;
			showProgress(false);

			if (success_) {
				finish();
			} else {
				_mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				_mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			_mAuthTask = null;
			showProgress(false);
		}
	}
}
