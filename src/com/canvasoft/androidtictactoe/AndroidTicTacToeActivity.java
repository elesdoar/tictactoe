package com.canvasoft.androidtictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.Toast;

import com.canvasoft.androidtictactoe.game.TicTacToeGame;
import com.canvasoft.androidtictactoe.game.TicTacToeGame.DifficultyLevel;
import com.canvasoft.androidtictactoe.views.BoardView;

public class AndroidTicTacToeActivity extends Activity {
	static final String TAG = "game.AndroidTicTacToeActivity";
	static final int DIALOG_DIFFICULTY_ID = 0;
	static final int DIALOG_QUIT_ID = 1;

	// Represents the internal state of the game
	private TicTacToeGame mGame;
	private boolean mGameOver = false;

	private int mHumanWins = 0;
	private int mComputerWins = 0;
	private int mTies = 0;
	private char mGoFirst = TicTacToeGame.HUMAN_PLAYER;

	// Buttons making up the board
	private BoardView mBoardView;

	// Various text displayed
	private TextView mInfoTextView;
	private TextView mHumanScoreTextView;
	private TextView mComputerScoreTextView;
	private TextView mTieScoreTextView;
	private SharedPreferences mPrefs;

	MediaPlayer mHumanMediaPlayer;
	MediaPlayer mComputerMediaPlayer;

	// Listen for touches on the board

	private OnTouchListener mTouchListener = new OnTouchListener() {

		public boolean onTouch(View v, MotionEvent event) {
			// Determine which cell was touched
			int col = (int) event.getX() / mBoardView.getBoardCellWidth();
			int row = (int) event.getY() / mBoardView.getBoardCellHeight();
			int pos = row * 3 + col;

			if (!mGameOver && setMove(TicTacToeGame.HUMAN_PLAYER, pos)) {
				mGoFirst = mGoFirst == TicTacToeGame.HUMAN_PLAYER? TicTacToeGame.COMPUTER_PLAYER:TicTacToeGame.HUMAN_PLAYER;
				try {
					mHumanMediaPlayer.start(); // Play the sound effect
				} catch(Exception e) {
					
				}
				int winner = mGame.checkForWinner();
				if (winner == 0) {
					mInfoTextView.setText(R.string.turn_computer);
					turnComputer();
				} else
					endGame(winner);
			}

			// So we aren't notified of continued events when finger is moved
			return false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_android_tic_tac_toe);

		mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
		mHumanWins = mPrefs.getInt("mHumanWins", 0);
		mComputerWins = mPrefs.getInt("mComputerWins", 0);
		mTies = mPrefs.getInt("mTies", 0);
		
		mInfoTextView = (TextView) findViewById(R.id.information);
		mHumanScoreTextView = (TextView) findViewById(R.id.player_score);
		mComputerScoreTextView = (TextView) findViewById(R.id.computer_score);
		mTieScoreTextView = (TextView) findViewById(R.id.tie_score);

		mGame = new TicTacToeGame();
		
		int selected = mPrefs.getInt("mDifficultyLevel", 0);;
		switch (selected) {
			case 0:
				mGame.setDifficultyLevel(DifficultyLevel.Easy);
				break;
			case 1:
				mGame.setDifficultyLevel(DifficultyLevel.Harder);
				break;
			case 2:
				mGame.setDifficultyLevel(DifficultyLevel.Expert);
				break;
		}
		
		mBoardView = (BoardView) findViewById(R.id.board);
		// Listen for touches on the board
		mBoardView.setOnTouchListener(mTouchListener);
		mBoardView.setGame(mGame);

		if (savedInstanceState == null) {
			startNewGame();
		} else {
			// Restore the game's state
			mGame.setBoardState(savedInstanceState.getCharArray("board"));
			mGameOver = savedInstanceState.getBoolean("mGameOver");
			mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
			mHumanWins = savedInstanceState.getInt("mHumanWins");
			mComputerWins = savedInstanceState.getInt("mComputerWins");
			mTies = savedInstanceState.getInt("mTies");
			mGoFirst = savedInstanceState.getChar("mGoFirst");

			
			endGame(mGame.checkForWinner());
			if(!mGameOver) {
				mInfoTextView.setText(mGoFirst == TicTacToeGame.COMPUTER_PLAYER? R.string.turn_computer:R.string.turn_human);
				mBoardView.invalidate();
			}
				
		}

		displayScores();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putCharArray("board", mGame.getBoardState());
		outState.putBoolean("mGameOver", mGameOver);
		outState.putInt("mHumanWins", Integer.valueOf(mHumanWins));
		outState.putInt("mComputerWins", Integer.valueOf(mComputerWins));
		outState.putInt("mTies", Integer.valueOf(mTies));
		outState.putCharSequence("info", mInfoTextView.getText());
		outState.putChar("mGoFirst", mGoFirst);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mGame.setBoardState(savedInstanceState.getCharArray("board"));
		mGameOver = savedInstanceState.getBoolean("mGameOver");
		mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
		mHumanWins = savedInstanceState.getInt("mHumanWins");
		mComputerWins = savedInstanceState.getInt("mComputerWins");
		mTies = savedInstanceState.getInt("mTies");
		mGoFirst = savedInstanceState.getChar("mGoFirst");
	}

	@Override
	protected void onResume() {
		super.onResume();
		mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(),
				R.raw.human);
		mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(),
				R.raw.pc);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mHumanMediaPlayer.release();
		mComputerMediaPlayer.release();
	}

	@Override
	protected void onStop() {
		super.onStop();

		// Save the current scores
		SharedPreferences.Editor ed = mPrefs.edit();
		ed.putInt("mHumanWins", mHumanWins);
		ed.putInt("mComputerWins", mComputerWins);
		ed.putInt("mTies", mTies);
		
		int selected = 0;
		switch (mGame.getDifficultyLevel()) {
			case Easy:
				selected = 0;
				break;
			case Harder:
				selected = 1;
				break;
			case Expert:
				selected = 2;
				break;
		}
		ed.putInt("mDifficultyLevel", selected);
		ed.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.android_tic_tac_toe, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (item.getItemId()) {
		case R.id.action_new_game:
			startNewGame();
			return true;
		case R.id.ai_difficulty:
			builder.setTitle(R.string.difficulty_choose);

			final CharSequence[] levels = {
					getResources().getString(R.string.difficulty_easy),
					getResources().getString(R.string.difficulty_harder),
					getResources().getString(R.string.difficulty_expert) };

			int selected = 0;

			switch (mGame.getDifficultyLevel()) {
				case Easy:
					selected = 0;
					break;
				case Harder:
					selected = 1;
					break;
				case Expert:
					selected = 2;
					break;
			}

			builder.setSingleChoiceItems(levels, selected,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							dialog.dismiss();
							// Close dialog

							mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel
									.values()[item]);

							// Display the selected difficulty level
							Toast.makeText(getApplicationContext(),
									levels[item], Toast.LENGTH_SHORT).show();

						}
					});
			builder.create().show();
			return true;
		case R.id.about_menu:
			Context context = getApplicationContext();
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			View aboutView;
			aboutView = inflater.inflate(R.layout.about_tic_tac_toe, null);
			builder.setView(aboutView);
			builder.setPositiveButton("OK", null);
			builder.create().show();
			return true;
		case R.id.reset:
			mHumanWins = 0;
			mComputerWins = 0;
			mTies = 0;
			displayScores();
			return true;
		}
		return false;
	}

	// Set up the game board.
	private void startNewGame() {
		mGame.clearBoard();
		mBoardView.invalidate(); // Redraw the board
		mGameOver = false;
		// Human goes first
		mInfoTextView.setText(R.string.first_human);
	}

	private void displayScores() {
		mHumanScoreTextView.setText(Integer.toString(mHumanWins));
		mComputerScoreTextView.setText(Integer.toString(mComputerWins));
		mTieScoreTextView.setText(Integer.toString(mTies));
		
	}

	private boolean setMove(char player, int location) {
		if (mGame.setMove(player, location)) {
			mBoardView.invalidate(); // Redraw the board
			return true;
		}
		return false;
	}

	private void endGame(int winner) {
		switch (winner) {
		case 0:
			return;
		case 1:
			mInfoTextView.setText(R.string.result_tie);
			mTies++;
			mTieScoreTextView.setText(Integer.toString(mTies));
			break;
		case 2:
			mInfoTextView.setText(R.string.result_human_wins);
			mHumanWins++;
			mHumanScoreTextView.setText(Integer.toString(mHumanWins));
			break;
		default:
			mInfoTextView.setText(R.string.result_computer_wins);
			mComputerWins++;
			mComputerScoreTextView.setText(Integer.toString(mComputerWins));
			break;
		}
		mGameOver = true;
	}

	private void turnComputer() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				Log.v(TAG, "Android Jugando...");
				int move = mGame.getComputerMove();
				setMove(TicTacToeGame.COMPUTER_PLAYER, move);
				mGoFirst = mGoFirst == TicTacToeGame.HUMAN_PLAYER? TicTacToeGame.COMPUTER_PLAYER:TicTacToeGame.HUMAN_PLAYER;
				mBoardView.invalidate();
				try {
					mComputerMediaPlayer.start(); // Play the sound effect
				} catch (Exception e) {
				}
				int winner = mGame.checkForWinner();
				if (winner == 0) {
					mInfoTextView.setText(R.string.turn_human);
				}
				else
					endGame(winner);
			}
		}, 1000);
	}
}
