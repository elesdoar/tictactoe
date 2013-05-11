package com.canvasoft.androidtictactoe;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.canvasoft.androidtictactoe.game.TicTacToeGame;

public class AndroidTicTacToeActivity extends Activity {
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
		startNewGame();
		return true;
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
			if(mGameOver)
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
