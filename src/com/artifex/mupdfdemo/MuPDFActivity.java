package com.artifex.mupdfdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.tpv.app.pdf.Common.Utils;
import com.tpv.app.pdf.Common.Utils_ErrorFileList;
import com.tpv.app.pdf.Common.Utils_PlayListDataInfo;
import com.tpv.app.pdf.MainActivity;
import com.tpv.app.pdf.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;

class ThreadPerTaskExecutor implements Executor {
	public void execute(Runnable r) {
		new Thread(r).start();
	}
}

public class MuPDFActivity extends Activity implements FilePicker.FilePickerSupport
{
    private static final String TAG = MuPDFActivity.class.getName();

    private Context mCtx;

	/* The core rendering instance */
	enum TopBarMode {Main, Search, Annot, Delete, More, Accept};
	enum AcceptMode {Highlight, Underline, StrikeOut, Ink, CopyText};

	private final int    OUTLINE_REQUEST=0;
	private final int    PRINT_REQUEST=1;
	private final int    FILEPICK_REQUEST=2;
	private MuPDFCore    core;
	private String       mFileName;
	private MuPDFReaderView mDocView;
    private View         mTextView;
    private TextView     mPageNumberTextView;
	private View         mButtonsView;
	private boolean      mButtonsVisible;
	private EditText     mPasswordView;
	private TextView     mFilenameView;
	private SeekBar      mPageSlider;
	private int          mPageSliderRes;
	private TextView     mPageNumberView;
	private TextView     mInfoView;
	private ImageButton  mSearchButton;
	private ImageButton  mReflowButton;
	private ImageButton  mOutlineButton;
	private ImageButton	mMoreButton;
	private TextView     mAnnotTypeText;
	private ImageButton mAnnotButton;
	private ViewAnimator mTopBarSwitcher;
	private ImageButton  mLinkButton;
	private TopBarMode   mTopBarMode = TopBarMode.Main;
	private AcceptMode   mAcceptMode;
	private ImageButton  mSearchBack;
	private ImageButton  mSearchFwd;
	private EditText     mSearchText;
	private SearchTask   mSearchTask;
	private AlertDialog.Builder mAlertBuilder;
	private boolean    mLinkHighlight = false;
	private final Handler mHandler = new Handler();
	private boolean mAlertsActive= false;
	private boolean mReflow = false;
	private AsyncTask<Void,Void,MuPDFAlert> mAlertTask;
	private AlertDialog mAlertDialog;
	private FilePicker mFilePicker;

    /** andy add */
    Bundle mSavedInstanceState;
    private List<Utils_PlayListDataInfo> mPdfPlayListDataInfo;
    private int mFileCount;
    private int mFileListIndex;
    private boolean mIsFromPdfPlayer;
    private float mZoom = 0.0f;
    private int mResetmJumpPageNoTimer = 0;

    /** Wake lock screen */
    protected PowerManager.WakeLock mWakeLock;
    /** ~andy add */

	public void createAlertWaiter() {
		mAlertsActive = true;
		// All mupdf library calls are performed on asynchronous tasks to avoid stalling
		// the UI. Some calls can lead to javascript-invoked requests to display an
		// alert dialog and collect a reply from the user. The task has to be blocked
		// until the user's reply is received. This method creates an asynchronous task,
		// the purpose of which is to wait of these requests and produce the dialog
		// in response, while leaving the core blocked. When the dialog receives the
		// user's response, it is sent to the core via replyToAlert, unblocking it.
		// Another alert-waiting task is then created to pick up the next alert.
		if (mAlertTask != null) {
			mAlertTask.cancel(true);
			mAlertTask = null;
		}
		if (mAlertDialog != null) {
			mAlertDialog.cancel();
			mAlertDialog = null;
		}
		mAlertTask = new AsyncTask<Void,Void,MuPDFAlert>() {

			@Override
			protected MuPDFAlert doInBackground(Void... arg0) {
				if (!mAlertsActive)
					return null;

				return core.waitForAlert();
			}

			@Override
			protected void onPostExecute(final MuPDFAlert result) {
				// core.waitForAlert may return null when shutting down
				if (result == null)
					return;
				final MuPDFAlert.ButtonPressed pressed[] = new MuPDFAlert.ButtonPressed[3];
				for(int i = 0; i < 3; i++)
					pressed[i] = MuPDFAlert.ButtonPressed.None;
				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						mAlertDialog = null;
						if (mAlertsActive) {
							int index = 0;
							switch (which) {
							case AlertDialog.BUTTON1: index=0; break;
							case AlertDialog.BUTTON2: index=1; break;
							case AlertDialog.BUTTON3: index=2; break;
							}
							result.buttonPressed = pressed[index];
							// Send the user's response to the core, so that it can
							// continue processing.
							core.replyToAlert(result);
							// Create another alert-waiter to pick up the next alert.
							createAlertWaiter();
						}
					}
				};
				mAlertDialog = mAlertBuilder.create();
				mAlertDialog.setTitle(result.title);
				mAlertDialog.setMessage(result.message);
				switch (result.iconType)
				{
				case Error:
					break;
				case Warning:
					break;
				case Question:
					break;
				case Status:
					break;
				}
				switch (result.buttonGroupType)
				{
				case OkCancel:
					mAlertDialog.setButton(AlertDialog.BUTTON2, getString(R.string.cancel), listener);
					pressed[1] = MuPDFAlert.ButtonPressed.Cancel;
				case Ok:
					mAlertDialog.setButton(AlertDialog.BUTTON1, getString(R.string.okay), listener);
					pressed[0] = MuPDFAlert.ButtonPressed.Ok;
					break;
				case YesNoCancel:
					mAlertDialog.setButton(AlertDialog.BUTTON3, getString(R.string.cancel), listener);
					pressed[2] = MuPDFAlert.ButtonPressed.Cancel;
				case YesNo:
					mAlertDialog.setButton(AlertDialog.BUTTON1, getString(R.string.yes), listener);
					pressed[0] = MuPDFAlert.ButtonPressed.Yes;
					mAlertDialog.setButton(AlertDialog.BUTTON2, getString(R.string.no), listener);
					pressed[1] = MuPDFAlert.ButtonPressed.No;
					break;
				}
				mAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						mAlertDialog = null;
						if (mAlertsActive) {
							result.buttonPressed = MuPDFAlert.ButtonPressed.None;
							core.replyToAlert(result);
							createAlertWaiter();
						}
					}
				});

				mAlertDialog.show();
			}
		};

		mAlertTask.executeOnExecutor(new ThreadPerTaskExecutor());
	}

	public void destroyAlertWaiter() {
		mAlertsActive = false;
		if (mAlertDialog != null) {
			mAlertDialog.cancel();
			mAlertDialog = null;
		}
		if (mAlertTask != null) {
			mAlertTask.cancel(true);
			mAlertTask = null;
		}
	}

	private MuPDFCore openFile(String path)
	{
		int lastSlashPos = path.lastIndexOf('/');
		mFileName = new String(lastSlashPos == -1
					? path
					: path.substring(lastSlashPos+1));
		System.out.println("Trying to open "+path);
		try
		{
			core = new MuPDFCore(this, path);
			// New file: drop the old outline data
			OutlineActivityData.set(null);
		}
		catch (Exception e)
		{
			System.out.println(e);
			return null;
		}
		return core;
	}

	private MuPDFCore openBuffer(byte buffer[], String magic)
	{
		System.out.println("Trying to open byte buffer");
		try
		{
			core = new MuPDFCore(this, buffer, magic);
			// New file: drop the old outline data
			OutlineActivityData.set(null);
		}
		catch (Exception e)
		{
			System.out.println(e);
			return null;
		}
		return core;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        Utils.DEBUG_LOG(TAG, "[onCreate]");

        mCtx = getApplicationContext();
        mIsIgnoreKey = false;

        Intent intent = getIntent();
        Utils.DEBUG_LOG(TAG, "[onCreate] intent : " + intent);
        if(intent != null && intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW)) {
            mIsFromPdfPlayer = false;
            String data = intent.getDataString();
            Utils.DEBUG_LOG(TAG, "[onCreate] dat : " + data);

            Utils_PlayListDataInfo PlayListLoadMenuInfo =
                    new Utils_PlayListDataInfo(data, data, "true", "false", "false");
            mPdfPlayListDataInfo = new ArrayList<Utils_PlayListDataInfo>();
            mPdfPlayListDataInfo.add(PlayListLoadMenuInfo);
        }
        else {
            mIsFromPdfPlayer = true;
            /** andy add get playlist data from Temp */
            mPdfPlayListDataInfo = Utils.getPlayListDataInfoTemp();
        }

        if(mPdfPlayListDataInfo != null && mPdfPlayListDataInfo.size() > 0) {
            Utils.DEBUG_LOG(TAG, "mPdfPlayListDataInfo.size() : " + mPdfPlayListDataInfo.size());
            for(Utils_PlayListDataInfo items : mPdfPlayListDataInfo) {
                Utils.DEBUG_LOG(TAG, "items: " + items.getFilePath());
            }
            mFileCount = mPdfPlayListDataInfo.size();
            mFileListIndex = Utils.findResumeFileIndex(mPdfPlayListDataInfo);

            /** andy add for init. */
            registerReceiver();

            // keep savedInstanceState
            mSavedInstanceState = savedInstanceState;

            //show pdf
            showPdf(savedInstanceState);

            //start slideshow timer.
            startPdfSlideshowTimer();
            /** ~andy add for init. */
        }
        else {
            Utils.Toast(this, getString(R.string.nodatainplaylist));
            finish();
        }
	}

    @Override
    protected void onResume() {

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        super.onResume();
    }

	public void requestPassword(final Bundle savedInstanceState) {
		mPasswordView = new EditText(this);
		mPasswordView.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
		mPasswordView.setTransformationMethod(new PasswordTransformationMethod());

		AlertDialog alert = mAlertBuilder.create();
		alert.setTitle(R.string.enter_password);
		alert.setView(mPasswordView);
		alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.okay),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (core.authenticatePassword(mPasswordView.getText().toString())) {
					createUI(savedInstanceState);
				} else {
					requestPassword(savedInstanceState);
				}
			}
		});
		alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		alert.show();
	}

	public void createUI(Bundle savedInstanceState) {
		if (core == null)
			return;

		// Now create the UI.
		// First create the document view
		mDocView = new MuPDFReaderView(this) {
			@Override
			protected void onMoveToChild(int i) {
				if (core == null)
					return;
				mPageNumberView.setText(String.format("%d / %d", i + 1,
						core.countPages()));
				mPageSlider.setMax((core.countPages() - 1) * mPageSliderRes);
				mPageSlider.setProgress(i * mPageSliderRes);
				super.onMoveToChild(i);
			}

			@Override
			protected void onTapMainDocArea() {
                /** andy remove for hide action bar */
				//if (!mButtonsVisible) {
				//	showButtons();
				//} else {
				//	if (mTopBarMode == TopBarMode.Main)
				//		hideButtons();
				//}
                /** ~andy remove for hide action bar */
			}

			@Override
			protected void onDocMotion() {
				hideButtons();
			}

			@Override
			protected void onHit(Hit item) {
				switch (mTopBarMode) {
				case Annot:
					if (item == Hit.Annotation) {
						showButtons();
						mTopBarMode = TopBarMode.Delete;
						mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
					}
					break;
				case Delete:
					mTopBarMode = TopBarMode.Annot;
					mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
				// fall through
				default:
					// Not in annotation editing mode, but the pageview will
					// still select and highlight hit annotations, so
					// deselect just in case.
					MuPDFView pageView = (MuPDFView) mDocView.getDisplayedView();
					if (pageView != null)
						pageView.deselectAnnotation();
					break;
				}
			}
		};
		mDocView.setAdapter(new MuPDFPageAdapter(this, this, core));

		mSearchTask = new SearchTask(this, core) {
			@Override
			protected void onTextFound(SearchTaskResult result) {
				SearchTaskResult.set(result);
				// Ask the ReaderView to move to the resulting page
				mDocView.setDisplayedViewIndex(result.pageNumber);
				// Make the ReaderView act on the change to SearchTaskResult
				// via overridden onChildSetup method.
				mDocView.resetupChildren();
			}
		};

		// Make the buttons overlay, and store all its
		// controls in variables
		makeButtonsView();

		// Set up the page slider
		int smax = Math.max(core.countPages()-1,1);
		mPageSliderRes = ((10 + smax - 1)/smax) * 2;

		// Set the file-name text
		mFilenameView.setText(mFileName);

		// Activate the seekbar
		mPageSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
				mDocView.setDisplayedViewIndex((seekBar.getProgress()+mPageSliderRes/2)/mPageSliderRes);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				updatePageNumView((progress+mPageSliderRes/2)/mPageSliderRes);
			}
		});

		// Activate the search-preparing button
		mSearchButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				searchModeOn();
			}
		});

		// Activate the reflow button
		mReflowButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                toggleReflow();
            }
        });

		if (core.fileFormat().startsWith("PDF") && core.isUnencryptedPDF() && !core.wasOpenedFromBuffer())
		{
			mAnnotButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					mTopBarMode = TopBarMode.Annot;
					mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
				}
			});
		}
		else
		{
			mAnnotButton.setVisibility(View.GONE);
		}

		// Search invoking buttons are disabled while there is no text specified
		mSearchBack.setEnabled(false);
		mSearchFwd.setEnabled(false);
		mSearchBack.setColorFilter(Color.argb(255, 128, 128, 128));
		mSearchFwd.setColorFilter(Color.argb(255, 128, 128, 128));

		// React to interaction with the text widget
		mSearchText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                boolean haveText = s.toString().length() > 0;
                setButtonEnabled(mSearchBack, haveText);
                setButtonEnabled(mSearchFwd, haveText);

                // Remove any previous search results
                if (SearchTaskResult.get() != null && !mSearchText.getText().toString().equals(SearchTaskResult.get().txt)) {
                    SearchTaskResult.set(null);
                    mDocView.resetupChildren();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
        });

		//React to Done button on keyboard
		mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE)
					search(1);
				return false;
			}
		});

		mSearchText.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER)
					search(1);
				return false;
			}
		});

		// Activate search invoking buttons
		mSearchBack.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				search(-1);
			}
		});
		mSearchFwd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                search(1);
            }
        });

		mLinkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setLinkHighlight(!mLinkHighlight);
            }
        });

		if (core.hasOutline()) {
			mOutlineButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					OutlineItem outline[] = core.getOutline();
					if (outline != null) {
						OutlineActivityData.get().items = outline;
						Intent intent = new Intent(MuPDFActivity.this, OutlineActivity.class);
						startActivityForResult(intent, OUTLINE_REQUEST);
					}
				}
			});
		} else {
			mOutlineButton.setVisibility(View.GONE);
		}

        /** andy remove for don't resume last page. */
		// Reenstate last state if it was recorded
		//SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
		//mDocView.setDisplayedViewIndex(prefs.getInt("page"+mFileName, 0));
        /** ~andy remove for don't resume last page. */

        /** andy remove for hide action bar  */
		//if (savedInstanceState == null || !savedInstanceState.getBoolean("ButtonsHidden", false)) {
        //    showButtons();
        //}

		//if(savedInstanceState != null && savedInstanceState.getBoolean("SearchMode", false)) {
        //    searchModeOn();
        //}

		//if(savedInstanceState != null && savedInstanceState.getBoolean("ReflowMode", false)) {
        //    reflowModeSet(true);
        //}
        /** ~andy remove for hide action bar  */

        /** andy add page number text */
        makeTextView();
        /** ~andy add page number text */

		// Stick the document view and the buttons overlay into a parent view
		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(mDocView);
		layout.addView(mButtonsView);
        layout.addView(mTextView);
		setContentView(layout);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case OUTLINE_REQUEST:
			if (resultCode >= 0)
				mDocView.setDisplayedViewIndex(resultCode);
			break;
		case PRINT_REQUEST:
			if (resultCode == RESULT_CANCELED)
				showInfo(getString(R.string.print_failed));
			break;
		case FILEPICK_REQUEST:
			if (mFilePicker != null && resultCode == RESULT_OK)
				mFilePicker.onPick(data.getData());
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public Object onRetainNonConfigurationInstance()
	{
		MuPDFCore mycore = core;
		core = null;
		return mycore;
	}

	private void reflowModeSet(boolean reflow)
	{
		mReflow = reflow;
		mDocView.setAdapter(mReflow ? new MuPDFReflowAdapter(this, core) : new MuPDFPageAdapter(this, this, core));
		mReflowButton.setColorFilter(mReflow ? Color.argb(0xFF, 172, 114, 37) : Color.argb(0xFF, 255, 255, 255));
		setButtonEnabled(mAnnotButton, !reflow);
		setButtonEnabled(mSearchButton, !reflow);
		if (reflow) setLinkHighlight(false);
		setButtonEnabled(mLinkButton, !reflow);
		setButtonEnabled(mMoreButton, !reflow);
		mDocView.refresh(mReflow, 0.0f);
	}

	private void toggleReflow() {
		reflowModeSet(!mReflow);
		showInfo(mReflow ? getString(R.string.entering_reflow_mode) : getString(R.string.leaving_reflow_mode));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (mFileName != null && mDocView != null) {
			outState.putString("FileName", mFileName);

			// Store current page in the prefs against the file name,
			// so that we can pick it up each time the file is loaded
			// Other info is needed only for screen-orientation change,
			// so it can go in the bundle
			SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = prefs.edit();
			edit.putInt("page"+mFileName, mDocView.getDisplayedViewIndex());
			edit.commit();
		}

		if (!mButtonsVisible)
			outState.putBoolean("ButtonsHidden", true);

		if (mTopBarMode == TopBarMode.Search)
			outState.putBoolean("SearchMode", true);

		if (mReflow)
			outState.putBoolean("ReflowMode", true);
	}

	@Override
	protected void onPause() {
		super.onPause();
        Utils.DEBUG_LOG(TAG, "[onPause]");

        /** andy add for select display page */
        //mJumpPage = 0;
        //mResetmJumpPageNoTimer = 0;
        /** ~andy add for select display page */

        /** andy add for stop pdf slide timer */
        //stopPdfSlideshowTimer();
        /** ~andy add for stop pdf slide timer */

		if (mSearchTask != null)
			mSearchTask.stop();

		if (mFileName != null && mDocView != null) {
			SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = prefs.edit();
			edit.putInt("page"+mFileName, mDocView.getDisplayedViewIndex());
			edit.commit();
		}
	}

	public void onDestroy()
	{
        Utils.DEBUG_LOG(TAG, "[onDestory]");

        /** andy add for select display page */
        mJumpPage = 0;
        mResetmJumpPageNoTimer = 0;
        /** ~andy add for select display page */

        /** andy add for stop pdf slide timer */
        stopPdfSlideshowTimer();
        /** ~andy add for stop pdf slide timer */

        /** andy add for unregisterReceiver. */
        if (mMuPDFActivityBroadcastReceiver != null) {
            Utils.DEBUG_LOG(TAG, "[unregisterReceiver]");
            unregisterReceiver(mMuPDFActivityBroadcastReceiver);
        }
        /** ~andy add for unregisterReceiver. */

		if (mDocView != null) {
			mDocView.applyToChildren(new ReaderView.ViewMapper() {
				void applyToView(View view) {
					((MuPDFView)view).releaseBitmaps();
				}
			});
		}
		if (core != null)
			core.onDestroy();
		if (mAlertTask != null) {
			mAlertTask.cancel(true);
			mAlertTask = null;
		}
		core = null;
		super.onDestroy();
	}

	private void setButtonEnabled(ImageButton button, boolean enabled) {
		button.setEnabled(enabled);
		button.setColorFilter(enabled ? Color.argb(255, 255, 255, 255):Color.argb(255, 128, 128, 128));
	}

	private void setLinkHighlight(boolean highlight) {
		mLinkHighlight = highlight;
		// LINK_COLOR tint
		mLinkButton.setColorFilter(highlight ? Color.argb(0xFF, 172, 114, 37) : Color.argb(0xFF, 255, 255, 255));
		// Inform pages of the change.
		mDocView.setLinksEnabled(highlight);
	}

	private void showButtons() {
		if (core == null)
			return;
		if (!mButtonsVisible) {
			mButtonsVisible = true;
			// Update page number text and slider
			int index = mDocView.getDisplayedViewIndex();
			updatePageNumView(index);
			mPageSlider.setMax((core.countPages()-1)*mPageSliderRes);
			mPageSlider.setProgress(index*mPageSliderRes);
			if (mTopBarMode == TopBarMode.Search) {
				mSearchText.requestFocus();
				showKeyboard();
			}

			Animation anim = new TranslateAnimation(0, 0, -mTopBarSwitcher.getHeight(), 0);
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
					mTopBarSwitcher.setVisibility(View.VISIBLE);
				}
				public void onAnimationRepeat(Animation animation) {}
				public void onAnimationEnd(Animation animation) {}
			});
			mTopBarSwitcher.startAnimation(anim);

			anim = new TranslateAnimation(0, 0, mPageSlider.getHeight(), 0);
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
					mPageSlider.setVisibility(View.VISIBLE);
				}
				public void onAnimationRepeat(Animation animation) {}
				public void onAnimationEnd(Animation animation) {
					mPageNumberView.setVisibility(View.VISIBLE);
				}
			});
			mPageSlider.startAnimation(anim);
		}
	}

	private void hideButtons() {
		if (mButtonsVisible) {
			mButtonsVisible = false;
			hideKeyboard();

			Animation anim = new TranslateAnimation(0, 0, 0, -mTopBarSwitcher.getHeight());
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {}
				public void onAnimationRepeat(Animation animation) {}
				public void onAnimationEnd(Animation animation) {
					mTopBarSwitcher.setVisibility(View.INVISIBLE);
				}
			});
			mTopBarSwitcher.startAnimation(anim);

			anim = new TranslateAnimation(0, 0, 0, mPageSlider.getHeight());
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
					mPageNumberView.setVisibility(View.INVISIBLE);
				}
				public void onAnimationRepeat(Animation animation) {}
				public void onAnimationEnd(Animation animation) {
					mPageSlider.setVisibility(View.INVISIBLE);
				}
			});
			mPageSlider.startAnimation(anim);
		}
	}

	private void searchModeOn() {
		if (mTopBarMode != TopBarMode.Search) {
			mTopBarMode = TopBarMode.Search;
			//Focus on EditTextWidget
			mSearchText.requestFocus();
			showKeyboard();
			mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
		}
	}

	private void searchModeOff() {
		if (mTopBarMode == TopBarMode.Search) {
			mTopBarMode = TopBarMode.Main;
			hideKeyboard();
			mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
			SearchTaskResult.set(null);
			// Make the ReaderView act on the change to mSearchTaskResult
			// via overridden onChildSetup method.
			mDocView.resetupChildren();
		}
	}

	private void updatePageNumView(int index) {
		if (core == null)
			return;
		mPageNumberView.setText(String.format("%d / %d", index+1, core.countPages()));
	}

	private void printDoc() {
		if (!core.fileFormat().startsWith("PDF")) {
			showInfo(getString(R.string.format_currently_not_supported));
			return;
		}

		Intent myIntent = getIntent();
		Uri docUri = myIntent != null ? myIntent.getData() : null;

		if (docUri == null) {
			showInfo(getString(R.string.print_failed));
		}

		if (docUri.getScheme() == null)
			docUri = Uri.parse("file://"+docUri.toString());

		Intent printIntent = new Intent(this, PrintDialogActivity.class);
		printIntent.setDataAndType(docUri, "aplication/pdf");
		printIntent.putExtra("title", mFileName);
		startActivityForResult(printIntent, PRINT_REQUEST);
	}

	private void showInfo(String message) {
		mInfoView.setText(message);

		int currentApiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentApiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			SafeAnimatorInflater safe = new SafeAnimatorInflater((Activity)this, R.animator.info, (View)mInfoView);
		} else {
			mInfoView.setVisibility(View.VISIBLE);
			mHandler.postDelayed(new Runnable() {
				public void run() {
					mInfoView.setVisibility(View.INVISIBLE);
				}
			}, 500);
		}
	}

	private void makeButtonsView() {
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mButtonsView = inflater.inflate(R.layout.buttons,null);
		mFilenameView = (TextView)mButtonsView.findViewById(R.id.docNameText);
		mPageSlider = (SeekBar)mButtonsView.findViewById(R.id.pageSlider);
		mPageNumberView = (TextView)mButtonsView.findViewById(R.id.pageNumber);
		mInfoView = (TextView)mButtonsView.findViewById(R.id.info);
		mSearchButton = (ImageButton)mButtonsView.findViewById(R.id.searchButton);
		mReflowButton = (ImageButton)mButtonsView.findViewById(R.id.reflowButton);
		mOutlineButton = (ImageButton)mButtonsView.findViewById(R.id.outlineButton);
		mAnnotButton = (ImageButton)mButtonsView.findViewById(R.id.editAnnotButton);
		mAnnotTypeText = (TextView)mButtonsView.findViewById(R.id.annotType);
		mTopBarSwitcher = (ViewAnimator)mButtonsView.findViewById(R.id.switcher);
		mSearchBack = (ImageButton)mButtonsView.findViewById(R.id.searchBack);
		mSearchFwd = (ImageButton)mButtonsView.findViewById(R.id.searchForward);
		mSearchText = (EditText)mButtonsView.findViewById(R.id.searchText);
		mLinkButton = (ImageButton)mButtonsView.findViewById(R.id.linkButton);
		mMoreButton = (ImageButton)mButtonsView.findViewById(R.id.moreButton);
		mTopBarSwitcher.setVisibility(View.INVISIBLE);
		mPageNumberView.setVisibility(View.INVISIBLE);
		mInfoView.setVisibility(View.INVISIBLE);
		mPageSlider.setVisibility(View.INVISIBLE);
	}


    private void makeTextView() {
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mTextView = inflater.inflate(R.layout.text, null);
        mPageNumberTextView = (TextView) mTextView.findViewById(R.id.PageText);
    }

	public void OnMoreButtonClick(View v) {
		mTopBarMode = TopBarMode.More;
		mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
	}

	public void OnCancelMoreButtonClick(View v) {
		mTopBarMode = TopBarMode.Main;
		mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
	}

	public void OnPrintButtonClick(View v) {
		printDoc();
	}

	public void OnCopyTextButtonClick(View v) {
		mTopBarMode = TopBarMode.Accept;
		mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
		mAcceptMode = AcceptMode.CopyText;
		mDocView.setMode(MuPDFReaderView.Mode.Selecting);
		mAnnotTypeText.setText(getString(R.string.copy_text));
		showInfo(getString(R.string.select_text));
	}

	public void OnEditAnnotButtonClick(View v) {
		mTopBarMode = TopBarMode.Annot;
		mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
	}

	public void OnCancelAnnotButtonClick(View v) {
		mTopBarMode = TopBarMode.More;
		mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
	}

	public void OnHighlightButtonClick(View v) {
		mTopBarMode = TopBarMode.Accept;
		mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
		mAcceptMode = AcceptMode.Highlight;
		mDocView.setMode(MuPDFReaderView.Mode.Selecting);
		mAnnotTypeText.setText(R.string.highlight);
		showInfo(getString(R.string.select_text));
	}

	public void OnUnderlineButtonClick(View v) {
		mTopBarMode = TopBarMode.Accept;
		mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
		mAcceptMode = AcceptMode.Underline;
		mDocView.setMode(MuPDFReaderView.Mode.Selecting);
		mAnnotTypeText.setText(R.string.underline);
		showInfo(getString(R.string.select_text));
	}

	public void OnStrikeOutButtonClick(View v) {
		mTopBarMode = TopBarMode.Accept;
		mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
		mAcceptMode = AcceptMode.StrikeOut;
		mDocView.setMode(MuPDFReaderView.Mode.Selecting);
		mAnnotTypeText.setText(R.string.strike_out);
		showInfo(getString(R.string.select_text));
	}

	public void OnInkButtonClick(View v) {
		mTopBarMode = TopBarMode.Accept;
		mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
		mAcceptMode = AcceptMode.Ink;
		mDocView.setMode(MuPDFReaderView.Mode.Drawing);
		mAnnotTypeText.setText(R.string.ink);
		showInfo(getString(R.string.draw_annotation));
	}

	public void OnCancelAcceptButtonClick(View v) {
		MuPDFView pageView = (MuPDFView) mDocView.getDisplayedView();
		if (pageView != null) {
			pageView.deselectText();
			pageView.cancelDraw();
		}
		mDocView.setMode(MuPDFReaderView.Mode.Viewing);
		switch (mAcceptMode) {
		case CopyText:
			mTopBarMode = TopBarMode.More;
			break;
		default:
			mTopBarMode = TopBarMode.Annot;
			break;
		}
		mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
	}

	public void OnAcceptButtonClick(View v) {
		MuPDFView pageView = (MuPDFView) mDocView.getDisplayedView();
		boolean success = false;
		switch (mAcceptMode) {
		case CopyText:
			if (pageView != null)
				success = pageView.copySelection();
			mTopBarMode = TopBarMode.More;
			showInfo(success?getString(R.string.copied_to_clipboard):getString(R.string.no_text_selected));
			break;

		case Highlight:
			if (pageView != null)
				success = pageView.markupSelection(Annotation.Type.HIGHLIGHT);
			mTopBarMode = TopBarMode.Annot;
			if (!success)
				showInfo(getString(R.string.no_text_selected));
			break;

		case Underline:
			if (pageView != null)
				success = pageView.markupSelection(Annotation.Type.UNDERLINE);
			mTopBarMode = TopBarMode.Annot;
			if (!success)
				showInfo(getString(R.string.no_text_selected));
			break;

		case StrikeOut:
			if (pageView != null)
				success = pageView.markupSelection(Annotation.Type.STRIKEOUT);
			mTopBarMode = TopBarMode.Annot;
			if (!success)
				showInfo(getString(R.string.no_text_selected));
			break;

		case Ink:
			if (pageView != null)
				success = pageView.saveDraw();
			mTopBarMode = TopBarMode.Annot;
			if (!success)
				showInfo(getString(R.string.nothing_to_save));
			break;
		}
		mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
		mDocView.setMode(MuPDFReaderView.Mode.Viewing);
	}

	public void OnCancelSearchButtonClick(View v) {
		searchModeOff();
	}

	public void OnDeleteButtonClick(View v) {
		MuPDFView pageView = (MuPDFView) mDocView.getDisplayedView();
		if (pageView != null)
			pageView.deleteSelectedAnnotation();
		mTopBarMode = TopBarMode.Annot;
		mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
	}

	public void OnCancelDeleteButtonClick(View v) {
		MuPDFView pageView = (MuPDFView) mDocView.getDisplayedView();
		if (pageView != null)
			pageView.deselectAnnotation();
		mTopBarMode = TopBarMode.Annot;
		mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
	}

	private void showKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null)
			imm.showSoftInput(mSearchText, 0);
	}

	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null)
			imm.hideSoftInputFromWindow(mSearchText.getWindowToken(), 0);
	}

	private void search(int direction) {
		hideKeyboard();
		int displayPage = mDocView.getDisplayedViewIndex();
		SearchTaskResult r = SearchTaskResult.get();
		int searchPage = r != null ? r.pageNumber : -1;
		mSearchTask.go(mSearchText.getText().toString(), direction, displayPage, searchPage);
	}

	@Override
	public boolean onSearchRequested() {
		if (mButtonsVisible && mTopBarMode == TopBarMode.Search) {
			hideButtons();
		} else {
			showButtons();
			searchModeOn();
		}
		return super.onSearchRequested();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (mButtonsVisible && mTopBarMode != TopBarMode.Search) {
			hideButtons();
		} else {
			showButtons();
			searchModeOff();
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void onStart() {
        Utils.DEBUG_LOG(TAG, "[onStart]");

        /** andy add for wake lock screen */
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "tpvMediaplayerWakeLockTag");
        this.mWakeLock.acquire();
        /** ~andy add for wake lock screen */

		if (core != null)
		{
			core.startAlerts();
			createAlertWaiter();
		}

		super.onStart();
	}

	@Override
	protected void onStop() {
        Utils.DEBUG_LOG(TAG, "[onStop]");
		if (core != null)
		{
			destroyAlertWaiter();
			core.stopAlerts();
		}

        /** andy add for release wake lock screen */
        this.mWakeLock.release();
        /** ~andy add for release wake lock screen */

		super.onStop();
	}

	@Override
	public void onBackPressed() {
        /** andy remove */
		//if (core != null && core.hasChanges()) {
		//	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
		//		public void onClick(DialogInterface dialog, int which) {
		//			if (which == AlertDialog.BUTTON_POSITIVE) {
        //                core.save();
        //            }
		//			finish();
		//		}
		//	};
		//	AlertDialog alert = mAlertBuilder.create();
		//	alert.setTitle("MuPDF");
		//	alert.setMessage(getString(R.string.document_has_changes_save_them_));
		//	alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), listener);
		//	alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no), listener);
		//	alert.show();
		//} else {
		//	super.onBackPressed();
		//}
        /** ~andy remove */

        finish();
        super.onBackPressed();
	}

	@Override
	public void performPickFor(FilePicker picker) {
		mFilePicker = picker;
		Intent intent = new Intent(this, ChoosePDFActivity.class);
		intent.setAction(ChoosePDFActivity.PICK_KEY_FILE);
		startActivityForResult(intent, FILEPICK_REQUEST);
	}

//region andy add
    private void updateResumeFile() {
        /** update resume file. */
        if (mFileListIndex == 0) {
            Utils.PDFPlayerContentProvider_Set_PlaylistResumeFile(Utils.mPlayListSelection, mPdfPlayListDataInfo.get(mPdfPlayListDataInfo.size() - 1).getFileName(), "false");
        } else {
            Utils.PDFPlayerContentProvider_Set_PlaylistResumeFile(Utils.mPlayListSelection, mPdfPlayListDataInfo.get(mFileListIndex - 1).getFileName(), "false");
        }
        Utils.PDFPlayerContentProvider_Set_PlaylistResumeFile(Utils.mPlayListSelection, mPdfPlayListDataInfo.get(mFileListIndex).getFileName(), "true");
    }


    //region andy add for display pdf.
    /** andy add for display pdf. */
    private void showPdf(Bundle savedInstanceState) {

        /** get pdf file */
        String filePath = mPdfPlayListDataInfo.get(mFileListIndex).getFilePath();
        Uri uri = Uri.parse(filePath);

        if(mIsFromPdfPlayer) {
            updateResumeFile();

            /** write log file */
            Utils.writeLogFileData(Utils.logFileItems.LOG_LAST_PLAY_FILE_NAME, filePath);
            Utils.writeLogFile();
            /** ~write log file */
        }

        mAlertBuilder = new AlertDialog.Builder(this);

        if (core == null) {
            core = (MuPDFCore)getLastNonConfigurationInstance();

            if (savedInstanceState != null && savedInstanceState.containsKey("FileName")) {
                mFileName = savedInstanceState.getString("FileName");
            }
        }
        if (core == null) {
            byte buffer[] = null;
            {
                System.out.println("URI to open is: " + uri);

                /** andy remove, because pdf file only store on storage. */
                //if (uri.toString().startsWith("content://")) {
                //    String reason = null;
                //    try {
                //        InputStream is = getContentResolver().openInputStream(uri);
                //        int len = is.available();
                //        buffer = new byte[len];
                //        is.read(buffer, 0, len);
                //        is.close();
                //    }
                //    catch (java.lang.OutOfMemoryError e) {
                //        System.out.println("Out of memory during buffer reading");
                //        reason = e.toString();
                //    }
                //    catch (Exception e) {
                //        System.out.println("Exception reading from stream: " + e);
                //        // Handle view requests from the Transformer Prime's file manager
                //        // Hopefully other file managers will use this same scheme, if not
                //        // using explicit paths.
                //        // I'm hoping that this case below is no longer needed...but it's
                //        // hard to test as the file manager seems to have changed in 4.x.
                //        try {
                //            Cursor cursor = getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
                //            if (cursor.moveToFirst()) {
                //                String str = cursor.getString(0);
                //                if (str == null) {
                //                    reason = "Couldn't parse data in intent";
                //                }
                //                else {
                //                    uri = Uri.parse(str);
                //                }
                //            }
                //        }
                //        catch (Exception e2) {
                //            System.out.println("Exception in Transformer Prime file manager code: " + e2);
                //            reason = e2.toString();
                //        }
                //    }
                //    if (reason != null) {
                //        buffer = null;
                //        Resources res = getResources();
                //        AlertDialog alert = mAlertBuilder.create();
                //        setTitle(String.format(res.getString(R.string.cannot_open_document_Reason), reason));
                //        alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dismiss),
                //                new DialogInterface.OnClickListener() {
                //                    public void onClick(DialogInterface dialog, int which) {
                //                        finish();
                //                    }
                //                });
                //        alert.show();
                //        return;
                //    }
                //}
                //if (buffer != null) {
                //    core = openBuffer(buffer, intent.getType());
                //}
                //else
                /** ~andy remove, because pdf file only store on storage. */

                {
                    String path = Uri.decode(uri.getEncodedPath());
                    if (path == null) {
                        path = uri.toString();
                    }
                    core = openFile(path);
                }
                SearchTaskResult.set(null);
            }
            if (core != null && core.needsPassword()) {
                requestPassword(savedInstanceState);
                return;
            }
            if (core != null && core.countPages() == 0)
            {
                core = null;
            }
        }
        if (core == null)
        {
            /** andy remove dialog. */
            //AlertDialog alert = mAlertBuilder.create();
            //alert.setTitle(R.string.cannot_open_document);
            //alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dismiss),
            //        new DialogInterface.OnClickListener() {
            //            public void onClick(DialogInterface dialog, int which) {
            //                finish();
            //            }
            //        });
            //alert.setOnCancelListener(new OnCancelListener() {
            //    @Override
            //    public void onCancel(DialogInterface dialog) {
            //        finish();
            //    }
            //});
            //alert.show();
            /** ~andy remove dialog. */

            /** write log file */
            writeErrorFileToLogFile(filePath);
            /** ~write log file */

            /** check all files are error or not */
            boolean isAllFileError = isAllFileError();
            /** ~check all files are error or not */

            if (!isAllFileError) {
                nextPdfFile();
            }
            return;
        }

        createUI(savedInstanceState);
    }
    //endregion

    //region Timer
    /** Timer */
    private static Timer mTimer;
    private TimerTask mTimerTask;
    private static int mTimerCount;
    private static boolean mIsPause;

    private Utils.repeatMode mRepeatMode;
    private Utils.effectDuration mPdfEffectDuration;

    private final int PDF_NEXT = 1;
    private final int PDF_PREVIOUS = 2;
    private final int PDF_STOP = 3;
    private final int PDF_CLEAR_PAGE_NUM =4;

    private int mPageNo = 0;
    private int mJumpPage = 0;

    private void startPdfSlideshowTimer(){
        if(mIsFromPdfPlayer) {
            mRepeatMode = Utils.PDFPlayerContentProvider_Get_PlaylistRepeatModeSettings();
            mPdfEffectDuration = Utils.PDFPlayerContentProvider_Get_EffectDuration();
        }
        else {
            mRepeatMode = Utils.repeatMode.REPEAT_ONCE;
            mPdfEffectDuration = Utils.effectDuration.PERIOD_5;
        }

        Utils.DEBUG_LOG(TAG, "[startPhotoSlideshowTimer] mPhotoPeriodType.getValue() : " + mPdfEffectDuration.getValue());
		mIsPause = false;
        mTimerCount = 0;
        //宣告Timer
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask(){
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if(mTimerCount == 0){
                        //Utils.DEBUG_LOG(TAG, "[startPdfSlideshowTimer] mTimerCount == 0 ");
                    }
                    else if(mTimerCount >= mPdfEffectDuration.getValue()){
                        mTimerCount = 0;
                        Message msg = mPdfSlideShowHandler.obtainMessage();
                        msg.what = PDF_NEXT;
                        msg.sendToTarget();
                    }
                    if(!mIsPause) {
                        mTimerCount++;
                    }

                    if(!mIsFromPdfPlayer) {
                        mTimerCount = 0;
                    }

                    mResetmJumpPageNoTimer++;
                    if(mResetmJumpPageNoTimer >= 3) {
                        mJumpPage = 0;
                        Message msg = mPdfSlideShowHandler.obtainMessage();
                        msg.what = PDF_CLEAR_PAGE_NUM;
                        msg.sendToTarget();
                    }
                }
            };
        }

        //設定Timer(task為執行內容，0代表立刻開始,間格1秒執行一次)
        if(mTimer != null && mTimerTask != null ) {
            mTimer.schedule(mTimerTask, 0, 1000);
        }
    }
    private void stopPdfSlideshowTimer(){
        Utils.DEBUG_LOG(TAG, "stopPpfSlideshowTimer");
        if (mTimerTask != null) {
            Utils.DEBUG_LOG(TAG, "[Ppf] cancel mTimerTask ");
            mTimerTask.cancel();
            mTimerTask = null;
        }

        if (mTimer != null) {
            Utils.DEBUG_LOG(TAG, "[Ppf] cancel mTimer ");
            mTimer.cancel();
            mTimer = null;
        }
    }
    private Handler mPdfSlideShowHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == PDF_NEXT) {
                Utils.DEBUG_LOG(TAG, "[Ppf] PDF_NEXT");
                if(core != null) {
                    Utils.DEBUG_LOG(TAG, "[Ppf] core.countPages() : " + core.countPages());
                    mPageNo++;
                    Utils.DEBUG_LOG(TAG, "[Ppf] mPageNo : " + mPageNo);

                    if (core.countPages() == mPageNo) {
                        Utils.DEBUG_LOG(TAG, "[Ppf] go to next pdf ");
                        if (mRepeatMode != Utils.repeatMode.REPEAT_NONE) {
                            nextPdfFile();
                        }
                    } else {
                        Utils.DEBUG_LOG(TAG, "[Ppf] go to next page ");
                        mDocView.moveToNext();
                    }
                }
            }
            else if(msg.what == PDF_PREVIOUS) {
                Utils.DEBUG_LOG(TAG, "[Ppf] PDF_PREVIOUS");
                if(core != null) {
                    Utils.DEBUG_LOG(TAG, "[Ppf] core.countPages() : " + core.countPages());
                    mPageNo--;
                    Utils.DEBUG_LOG(TAG, "[Ppf] mPageNo : " + mPageNo);

                    if (mPageNo == -1) {
                        mPageNo = 0;
                        Utils.DEBUG_LOG(TAG, "[Ppf] go to Previous pdf ");
                        if (mRepeatMode != Utils.repeatMode.REPEAT_NONE) {
                            previousPdfFile();
                        }
                    } else {
                        Utils.DEBUG_LOG(TAG, "[Ppf] go to Previous page ");
                        mDocView.moveToPrevious();
                    }
                }
            }
            else if(msg.what == PDF_STOP) {
                Utils.DEBUG_LOG(TAG, "[Ppf] PDF_STOP");
                if(mPageNo != 0) {
                    mPageNo = 0;
                    mDocView.setDisplayedViewIndex(0);
                }
            }
            else if(msg.what == PDF_CLEAR_PAGE_NUM) {
                mPageNumberTextView.setText("");
            }
            super.handleMessage(msg);
        }
    };
    /** ~Timer */
    //endregion

    //region destroy memory
    private void destroyMemory() {
        Utils.DEBUG_LOG(TAG, "[destoryMemory]");
        if (mSearchTask != null) {
            mSearchTask.stop();
        }

        if (mDocView != null) {
            mDocView.applyToChildren(new ReaderView.ViewMapper() {
                void applyToView(View view) {
                    Utils.DEBUG_LOG(TAG, "[destoryMemory] applyToChildren : " + view);
                    ((MuPDFView)view).releaseBitmaps();
                }
            });
        }

        if (core != null) {
            destroyAlertWaiter();
            core.stopAlerts();
            core.onDestroy();
        }

        core = null;
    }
    //endregion

    //region change pdf file
    private void nextPdfFile() {
        Utils.DEBUG_LOG(TAG, "[nextPdfFile]");
        mPageNo = 0;

        //destroyMemory();

        boolean isStop = false;
        if(mIsFromPdfPlayer) {
            mRepeatMode = Utils.PDFPlayerContentProvider_Get_PlaylistRepeatModeSettings();
        }
        else {
            mRepeatMode = Utils.repeatMode.REPEAT_ONCE;
        }

        if(mRepeatMode == Utils.repeatMode.REPEAT_ONCE) {
            mFileListIndex++;
            if (mFileListIndex == mFileCount) {
                isStop = true;
            }
        }
        else if(mRepeatMode == Utils.repeatMode.REPEAT_ALL) {
            mFileListIndex++;
            if (mFileListIndex == mFileCount) {
                mFileListIndex = 0;
            }
        }

        if(!isStop) {
			destroyMemory();
            showPdf(mSavedInstanceState);
            if (core != null) {
                core.startAlerts();
                createAlertWaiter();
            }
        }
        else {			
			stopPdfSlideshowTimer();
			/*
			destroyMemory();
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            if(!mIsFromPdfPlayer) {
                i.putExtra(Utils.EXTRA_KEY_SOURCE_FROM, Utils.EXTRA_VALUE_SOURCE_MUPDF);
                i.putExtra(Utils.EXTRA_KEY_SOURCE_ACTION, Utils.EXTRA_VALUE_SOURCE_FINISH);
            }
            startActivity(i);
            finish();
			*/
        }
    }
    private void previousPdfFile() {
        Utils.DEBUG_LOG(TAG, "[previousPdfFile]");
        mPageNo = 0;

        destroyMemory();

        //boolean isStop = false;
        if(mIsFromPdfPlayer) {
            mRepeatMode = Utils.PDFPlayerContentProvider_Get_PlaylistRepeatModeSettings();
        }
        else {
            mRepeatMode = Utils.repeatMode.REPEAT_ONCE;
        }

        if(mRepeatMode == Utils.repeatMode.REPEAT_ONCE) {
            mFileListIndex--;
            if (mFileListIndex == -1) {
                mFileListIndex = (mFileCount-1);
                //isStop = true;
            }
        }
        else if(mRepeatMode == Utils.repeatMode.REPEAT_ALL) {
            mFileListIndex--;
            if (mFileListIndex == -1) {
                mFileListIndex = (mFileCount-1);
                //isStop = true;
            }
        }

        //if(!isStop) {
            showPdf(mSavedInstanceState);
            if (core != null) {
                core.startAlerts();
                createAlertWaiter();
            }
        //}
        //else {
        //    Intent i = new Intent(getApplicationContext(), MainActivity.class);
        //    startActivity(i);
        //    finish();
        //}
    }
    //endregion


    private BroadcastReceiver mMuPDFActivityBroadcastReceiver;
    private void registerReceiver() {
        if (mMuPDFActivityBroadcastReceiver == null) {
            Utils.DEBUG_LOG(TAG, "[registerReceiver]");
            mMuPDFActivityBroadcastReceiver = new MuPDFActivityReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Utils.INTENT_SCALARSERVICE_POWERSAVING_MODE);
            filter.addAction(Utils.INTENT_SCALARSERVICE_CHANGE_SOURCE);
            filter.addAction(Utils.INTENT_PDFPLAYER_MAIL_CALLBACK);
            filter.addAction(Intent.ACTION_MEDIA_EJECT);
            filter.addAction(Intent.ACTION_SHUTDOWN);
            filter.addDataScheme("file");
            registerReceiver(mMuPDFActivityBroadcastReceiver, filter);
        }
    }

    class MuPDFActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Utils.DEBUG_LOG(TAG, "[onReceive] MainActivityReceiver : " + intent);
            final String action = intent.getAction();
            if (action.equals(Utils.INTENT_SCALARSERVICE_CHANGE_SOURCE)) {
                Utils.DEBUG_LOG(TAG, "[onReceive] INTENT_SCALARSERVICE_CHANGE_SOURCE");
                Bundle extras = intent.getExtras();
                if(extras != null) {
                    String source = extras.getString("Source");
                    int listNo = extras.getInt("Index");
                    Utils.DEBUG_LOG(TAG, "[onReceive] source : " + source);
                    Utils.DEBUG_LOG(TAG, "[onReceive] listNo : " + listNo);
                    Utils.DEBUG_LOG(TAG, "[onReceive] Utils.mPlayListSelection : " + Utils.mPlayListSelection);
                    if(source.equals("PDFPlayer")) {
                        if ((listNo > 0) && (listNo <= Utils.playlistMenu.PLAYLIST_COUNT.ordinal())) {
                            if (Utils.mPlayListSelection != (listNo - 1)) {
                                if (Utils.getNormalFinishPdfplayer()) {
                                    Utils.setNormalFinishPdfplayer("false");
                                    Utils.resetResumeFile();
                                }
                                Utils.mPlayListSelection = (listNo - 1);
                                mPdfPlayListDataInfo = Utils.PDFPlayerContentProvider_Get_Playlist(Utils.mPlayListSelection);
                                stopPdfSlideshowTimer();

                                boolean isPlaylistValid = true;
                                if(mPdfPlayListDataInfo == null || mPdfPlayListDataInfo.size() == 0) {
                                    Utils.mDefaultFilePath = Utils.getInternalStoragePath() + Utils.FILE_FOLDER_DEFAULT;
                                    mPdfPlayListDataInfo = Utils.getStorageFiles(Utils.mDefaultFilePath, Utils.fileType.NONE);
                                    if(mPdfPlayListDataInfo == null || mPdfPlayListDataInfo.size() == 0) {
                                        isPlaylistValid = false;
                                    }
                                }

                                if(isPlaylistValid) {
                                    nextPdfFile();
                                    startPdfSlideshowTimer();
                                }
                                else {
                                    Utils.DEBUG_LOG(TAG, "playlist invalid and finish MuPDFActivity");
                                    destroyMemory();

                                    Intent i = new Intent(mCtx, MainActivity.class);
                                    i.putExtra(Utils.EXTRA_KEY_SOURCE_FROM, Utils.EXTRA_VALUE_SOURCE_MUPDF);
                                    Utils.NotifyScalarServiceFailOver(listNo);
                                    startActivity(i);
                                    finish();
                                }
                            }
                        }
                        else {
                            Utils.DEBUG_LOG(TAG, "finish MuPDFActivity");
                            destroyMemory();
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);

                            if(listNo != 0) {
                                //Utils.DEBUG_LOG(TAG, "Notify Scalar Service list not exist");
                                i.putExtra(Utils.EXTRA_KEY_SOURCE_FROM, Utils.EXTRA_VALUE_SOURCE_MUPDF);
                                Utils.NotifyScalarServiceFailOver(listNo);
                            }

                            startActivity(i);
                            finish();
                        }
                    }
                    else{
                        Utils.DEBUG_LOG(TAG, "change source and finish MuPDFActivity");
                        destroyMemory();
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        i.putExtra(Utils.EXTRA_KEY_SOURCE_FROM, Utils.EXTRA_VALUE_SOURCE_MUPDF);
                        i.putExtra(Utils.EXTRA_KEY_SOURCE_ACTION, Utils.EXTRA_VALUE_SOURCE_FINISH);
                        startActivity(i);
                        finish();
                    }
                }
            }
            else if(action.equals(Utils.INTENT_SCALARSERVICE_POWERSAVING_MODE) || action.equals(Intent.ACTION_SHUTDOWN)) {
                Utils.DEBUG_LOG(TAG, "[onReceive] INTENT_SCALARSERVICE_POWERSAVING_MODE");
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra(Utils.EXTRA_KEY_SOURCE_FROM, Utils.EXTRA_VALUE_SOURCE_MUPDF);
                i.putExtra(Utils.EXTRA_KEY_SOURCE_ACTION, Utils.EXTRA_VALUE_SOURCE_FINISH);
                startActivity(i);
                finish();
            }
            else if(action.equals(Utils.INTENT_PDFPLAYER_MAIL_CALLBACK)) {
                Utils.DEBUG_LOG(TAG, "[onReceive] INTENT_PDFPLAYER_MAIL_CALLBACK");
                int result = (Integer)intent.getIntExtra("result", 0);
                String UUID = intent.getStringExtra("UUID");
                Utils.DEBUG_LOG(TAG, "[onReceive] result : " + result);
                Utils.DEBUG_LOG(TAG, "[onReceive] UUID : " + UUID);
            }
            else if(action.equals(Intent.ACTION_MEDIA_EJECT)) {
                Utils.DEBUG_LOG(TAG, "[onReceive] ACTION_MEDIA_EJECT");
                String path = intent.getData().getPath().toString();
                Utils.DEBUG_LOG(TAG, "[onReceive] ACTION_MEDIA_EJECT path : " + path);
                Utils.DEBUG_LOG(TAG, "[onReceive] ACTION_MEDIA_EJECT mPdfPlayListDataInfo.get(0).getFilePath() : " + mPdfPlayListDataInfo.get(0).getFilePath());
                if(mPdfPlayListDataInfo.get(0).getFilePath().contains(path)) {
					destroyMemory();
					Intent i = new Intent(mCtx, MainActivity.class);
					i.putExtra(Utils.EXTRA_KEY_SOURCE_FROM, Utils.EXTRA_VALUE_SOURCE_MUPDF);
					Utils.NotifyScalarServiceFailOver(Utils.mPlayListSelection+1);
					startActivity(i);
					finish();
				}
            }
        }
    }


    //region write error file to logfile
    public static void writeErrorFileToLogFile(String filePath) {
        /** write log file */
        boolean isAdd = true;
        for(Utils_ErrorFileList item : Utils.mUtilsLogFileDataInfo.mUtilsErrorFileList) {
            if(item.getErrorFilePath().equals(filePath)) {
                isAdd = false;
                break;
            }
        }
        Utils.DEBUG_LOG(TAG, "[writeErrorFileToLogFile] isAdd : " + isAdd);
        if(isAdd)
        {
            Utils.writeLogFileData(Utils.logFileItems.LOG_ERROR_FILE_LIST, filePath);
            Utils.writeLogFile();
        }

        /** ~write log file */
    }

    private int mDefaultFileListSize;
    private Utils.playlistStyle mPlayListStyle;
    private void getDefaultFileList() {
        mPdfPlayListDataInfo.clear();
        mPdfPlayListDataInfo = Utils.getStorageFiles(Utils.mDefaultFilePath, Utils.fileType.NONE);
        if(mPdfPlayListDataInfo.size() > 0) {
            mPdfPlayListDataInfo.remove(0);
        }
        mDefaultFileListSize = mPdfPlayListDataInfo.size();

        /** check playlist style and store to contentprovider. */
        mPlayListStyle = Utils.checkPlaylistStyle(mPdfPlayListDataInfo);

        Utils.DEBUG_LOG(TAG, "[getDefaultFileList] mPlayListStyle : " + mPlayListStyle);
        Utils.DEBUG_LOG(TAG, "[getDefaultFileList] mDefaultFileListSize : " + mDefaultFileListSize);
    }

    private boolean isAllFileError() {
        boolean isAllFileError = false;

        if(!mIsFromPdfPlayer) {
            Utils.DEBUG_LOG(TAG, "startActivity to MainActivity");
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra(Utils.EXTRA_KEY_SOURCE_FROM, Utils.EXTRA_VALUE_SOURCE_MUPDF);
            i.putExtra(Utils.EXTRA_KEY_SOURCE_ACTION, Utils.EXTRA_VALUE_SOURCE_FINISH);
            startActivity(i);
            finish();
            return true;
        }

        /** if mUtilsLogFileDataInfo.mUtilsErrorFileList size == playlistitem size, notify framework no data can playback. */
        List<Utils_PlayListDataInfo> playlistdatainfo = Utils.PDFPlayerContentProvider_Get_Playlist(Utils.mPlayListSelection);
        if(Utils.mUtilsLogFileDataInfo.mUtilsErrorFileList.size() >= (playlistdatainfo.size() + mDefaultFileListSize)) {
            if(mDefaultFileListSize != 0) {
                Utils.NotifyScalarServiceFailOver(Utils.mPlayListSelection+1);
                isAllFileError = true;
            }
            else {
                getDefaultFileList();
                if (mPdfPlayListDataInfo == null || mDefaultFileListSize == 0) {
                    Utils.NotifyScalarServiceFailOver(Utils.mPlayListSelection+1);
                    isAllFileError = true;
                }
            }
        }
        /** ~if mUtilsLogFileDataInfo.mUtilsErrorFileList size == playlistitem size, notify framework no data can playback. */

        if(isAllFileError) {
            Utils.DEBUG_LOG(TAG, "startActivity to MainActivity");
            Utils.Toast(this, getString(R.string.not_available));
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            i.putExtra(Utils.EXTRA_KEY_SOURCE_FROM, Utils.EXTRA_VALUE_SOURCE_MUPDF);
            startActivity(i);
            finish();
        }

        return isAllFileError;
    }
    //endregion

    /** delay for pdf reflash issue.*/
    private final Handler mIgnoreKeyHandler = new Handler();
    private boolean mIsIgnoreKey = false;

    private Runnable IgnoreKeyTimer = new Runnable() {
        public void run() {
            mIsIgnoreKey = false;
        }
    };
    /** ~delay for pdf reflash issue.*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        Utils.DEBUG_LOG(TAG, "[onKeyDown] keyCode : " + keyCode);

        /** delay for pdf reflash issue.*/
        if(mIsIgnoreKey)
            return false;

        mIsIgnoreKey = true;
        mIgnoreKeyHandler.removeCallbacks(IgnoreKeyTimer);
        mIgnoreKeyHandler.postDelayed(IgnoreKeyTimer, 650);
        /** ~delay for pdf reflash issue.*/

        boolean resetmJumpPageNo = true;
        mResetmJumpPageNoTimer = 0;
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_ESCAPE: {
                if(!mIsFromPdfPlayer) {
                    Utils.DEBUG_LOG(TAG, "startActivity to MainActivity");
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.putExtra(Utils.EXTRA_KEY_SOURCE_FROM, Utils.EXTRA_VALUE_SOURCE_MUPDF);
                    i.putExtra(Utils.EXTRA_KEY_SOURCE_ACTION, Utils.EXTRA_VALUE_SOURCE_FINISH);
                    startActivity(i);
                }
                finish();
                return false;
            }
            case KeyEvent.KEYCODE_BUTTON_16:
            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_P: {
                Utils.DEBUG_LOG(TAG, "Play");
                mIsPause = false;
                break;
            }
            case KeyEvent.KEYCODE_BREAK:
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
            case KeyEvent.KEYCODE_A: {
                Utils.DEBUG_LOG(TAG, "Pause");
                mIsPause = true;
                break;
            }
            case KeyEvent.KEYCODE_MEDIA_STOP:
            case KeyEvent.KEYCODE_S: {
                Utils.DEBUG_LOG(TAG, "Stop");
                mIsPause = true;
                mTimerCount = 0;
                Message msg = mPdfSlideShowHandler.obtainMessage();
                msg.what = PDF_STOP;
                msg.sendToTarget();
                break;
            }
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
            case KeyEvent.KEYCODE_F: {
                Utils.DEBUG_LOG(TAG, "Fast Forward");
                if(core != null) {
                    if (!(core.countPages() == (mPageNo+1) && ((mFileListIndex+1) == mFileCount))) {
                        mTimerCount = 0;
                        Message msg = mPdfSlideShowHandler.obtainMessage();
                        msg.what = PDF_NEXT;
                        msg.sendToTarget();
                    }
                }
                break;
            }
            case KeyEvent.KEYCODE_MEDIA_REWIND:
            case KeyEvent.KEYCODE_R: {
                Utils.DEBUG_LOG(TAG, "Rewind");
                if (!(mPageNo == 0 && (mFileListIndex == 0))) {
                    mTimerCount = 0;
                    Message msg = mPdfSlideShowHandler.obtainMessage();
                    msg.what = PDF_PREVIOUS;
                    msg.sendToTarget();
                }
                break;
            }
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:{
                mTimerCount = 0;
                if(mJumpPage != 0) {
                    if(mJumpPage > core.countPages()) {
                        Utils.Toast(this, Integer.toString(mPageNo+1) + "/" + Integer.toString(core.countPages()));
                    }
                    else {
                        Utils.Toast(this, Integer.toString(mJumpPage) + "/" + Integer.toString(core.countPages()));
                        mPageNo = mJumpPage - 1;
                        mDocView.setDisplayedViewIndex(mPageNo);
                    }
                    mJumpPage = 0;
                }

                if(mZoom != 0.0f || mDocView.getScrollX() != 0 || mDocView.getScrollY() != 0) {
                    mZoom = 0.0f;
                    mDocView.refresh(mReflow, mZoom);
                    mDocView.scrollTo(0, 0);
                }
                break;
            }
            case KeyEvent.KEYCODE_PROG_BLUE:
            case KeyEvent.KEYCODE_I: {
                String strZoom = String.format("%.2f", (mZoom + 0.1f));
                mZoom = Float.parseFloat(strZoom);
                Utils.DEBUG_LOG(TAG, "[in] mZoom : " + mZoom);
                if(mZoom <= 1.0f) {
                    mDocView.refresh(mReflow, mZoom);
                }
                else {
                    mZoom = 1.0f;
                }
                break;
            }
            case KeyEvent.KEYCODE_PROG_YELLOW:
            case KeyEvent.KEYCODE_O: {
                String strZoom = String.format("%.2f", (mZoom - 0.1f));
                mZoom = Float.parseFloat(strZoom);
                Utils.DEBUG_LOG(TAG, "[out] mZoom : " + mZoom);
                if(mZoom >= 0.0f) {
                    mDocView.refresh(mReflow, mZoom);

                    int zoomCount = (int) (mZoom * 10) + 1;
                    if(zoomCount > 0) {
                        int scrollX = (0 - (mDocView.getScrollX()) / zoomCount);
                        int scrollY = (0 - (mDocView.getScrollY()) / zoomCount);
                        mDocView.scrollTo(mDocView.getScrollX() + scrollX, mDocView.getScrollY() + scrollY);
                    }
                }
                else {
                    mZoom = 0.0f;
                }
                break;
            }
            case KeyEvent.KEYCODE_DPAD_UP: {
                if(mZoom != 0.0f) {
                    mDocView.scrollTo(mDocView.getScrollX(), mDocView.getScrollY() - 10);
                }
                break;
            }
            case KeyEvent.KEYCODE_DPAD_DOWN: {
                if(mZoom != 0.0f) {
                    mDocView.scrollTo(mDocView.getScrollX(), mDocView.getScrollY() + 10);
                }
                break;
            }
            case KeyEvent.KEYCODE_DPAD_LEFT: {
                if(mZoom != 0.0f) {
                    mDocView.scrollTo(mDocView.getScrollX() + 10, mDocView.getScrollY());
                }
                else {
                    if (!(mPageNo == 0 && (mFileListIndex == 0))) {
                        mTimerCount = 0;
                        Message msg = mPdfSlideShowHandler.obtainMessage();
                        msg.what = PDF_PREVIOUS;
                        msg.sendToTarget();
                    }
                }
                break;
            }
            case KeyEvent.KEYCODE_DPAD_RIGHT: {
                if(mZoom != 0.0f) {
                    mDocView.scrollTo(mDocView.getScrollX() - 10, mDocView.getScrollY());
                }
                else {
                    if(core != null) {
                        if (!(core.countPages() == (mPageNo+1) && ((mFileListIndex+1) == mFileCount))) {
                            mTimerCount = 0;
                            Message msg = mPdfSlideShowHandler.obtainMessage();
                            msg.what = PDF_NEXT;
                            msg.sendToTarget();
                        }
                    }
                }
                break;
            }
            case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_1:
            case KeyEvent.KEYCODE_2:
            case KeyEvent.KEYCODE_3:
            case KeyEvent.KEYCODE_4:
            case KeyEvent.KEYCODE_5:
            case KeyEvent.KEYCODE_6:
            case KeyEvent.KEYCODE_7:
            case KeyEvent.KEYCODE_8:
            case KeyEvent.KEYCODE_9: {
                resetmJumpPageNo = false;
                int num = keyCode-KeyEvent.KEYCODE_0;
                if(mJumpPage != 0) {
                    if(mJumpPage*10 > 99999999) {
                        mJumpPage = ((mJumpPage * 10)%100000000) + num;
                    }
                    else {
                        mJumpPage = mJumpPage * 10 + num;
                    }
                }
                else {
                    mJumpPage = num;
                }
                mPageNumberTextView.setText(Integer.toString(mJumpPage));
                break;
            }
            default: {
                mJumpPage = 0;
                mPageNumberTextView.setText("");
                Utils.DEBUG_LOG(TAG, "[onKeyDown] return false");
                return false;
            }
        }

        if(resetmJumpPageNo) {
            mPageNumberTextView.setText("");
            mJumpPage = 0;
        }

        return true;
    }
//endregion
}
