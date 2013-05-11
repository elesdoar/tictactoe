package com.canvasoft.androidtictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.canvasoft.androidtictactoe.game.TicTacToeGame;

public class AndroidTicTacToeActivity extends Activity {
	static final int DIALOG_DIFFICULTY_ID = 0;
	static final int DIALOG_QUIT_ID = 1;

	// Represents the internal state of the game
	private TicTacToeGame mGame;
	private boolean mGameOver = false;

	// Buttons making up the board
	private Button mBoardButtons[];

	// Various text displayed
	private TextView mInfoTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_android_tic_tac_toe);

		mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
		mBoardButtons[0] = (Button) findViewById(R.id.one);
		mBoardButtons[1] = (Button) findViewById(R.id.two);
		mBoardButtons[2] = (Button) findViewById(R.id.three);
		mBoardButtons[3] = (Button) findViewById(R.id.four);
		mBoardButtons[4] = (Button) findViewById(R.id.five);
		mBoardButtons[5] = (Button) findViewById(R.id.six);
		mBoardButtons[6] = (Button) findViewById(R.id.seven);
		mBoardButtons[7] = (Button) findViewById(R.id.eight);
		mBoardButtons[8] = (Button) findViewById(R.id.nine);

		mInfoTextView = (TextView) findViewById(R.id.information);

		mGame = new TicTacToeGame();
		startNewGame();
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
	
								mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.values()[item]);
	
								// Display the selected difficulty level
								Toast.makeText(getApplicationContext(),
										levels[item], Toast.LENGTH_SHORT).show();
	
							}
						});
				builder.create().show();
				return true;
			case R.id.about_menu:
				Context context = getApplicationContext();
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
				View aboutView;
				aboutView = inflater.inflate(R.layout.about_tic_tac_toe, null);
				builder.setView(aboutView);
				builder.setPositiveButton("OK", null);
				builder.create().show();
				return true;
			case R.id.quit:
				builder.setMessage(R.string.quit_question)
				.setCancelable(false)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int id) {
						AndroidTicTacToeActivity.this.finish();
					}
				}).setNegativeButton(R.string.no, null);
				builder.create().show();
				return true;
		}
		return false;
	}

	// Set up the game board.
	private void startNewGame() {
		mGame.clearBoard();
		mGameOver = false;

		// Reset all buttons
		for (int i = 0; i < mBoardButtons.length; i++) {
			mBoardButtons[i].setText("");
			mBoardButtons[i].setEnabled(true);
			mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
		}

		// Human goes first
		mInfoTextView.setText(R.string.first_human);
	}

	// Handles clicks on the game board buttons
	private class ButtonClickListener implements OnClickListener {
		int location;

		public ButtonClickListener(int location) {
			this.location = location;
		}

		public void onClick(View view) {
			if (mGameOver)
				return;
			if (mBoardButtons[location].isEnabled()) {
				setMove(TicTacToeGame.HUMAN_PLAYER, location);

				// If no winner yet, let the computer make a move
				int winner = mGame.checkForWinner();
				if (winner == 0) {
					mInfoTextView.setText(R.string.turn_computer);
					int move = mGame.getComputerMove();
					setMove(TicTacToeGame.COMPUTER_PLAYER, move);
					winner = mGame.checkForWinner();

				}

				if (winner == 0)
					mInfoTextView.setText(R.string.turn_human);
				else {
					if (winner == 1)
						mInfoTextView.setText(R.string.result_tie);
					else if (winner == 2)
						mInfoTextView.setText(R.string.result_human_wins);
					else
						mInfoTextView.setText(R.string.result_computer_wins);
					mGameOver = true;
				}

			}
		}

		private void setMove(char player, int location) {
			mGame.setMove(player, location);
			mBoardButtons[location].setEnabled(false);
			mBoardButtons[location].setText(String.valueOf(player));
			if (player == TicTacToeGame.HUMAN_PLAYER)
				mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
			else
				mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));

		}
	}
}
