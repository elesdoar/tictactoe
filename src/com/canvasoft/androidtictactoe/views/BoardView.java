package com.canvasoft.androidtictactoe.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.canvasoft.androidtictactoe.R;
import com.canvasoft.androidtictactoe.game.TicTacToeGame;

public class BoardView extends View {
	// Width of the board grid lines
	public static final int GRID_WIDTH = 6;

	private Bitmap mHumanBitmap;
	private Bitmap mComputerBitmap;
	private Paint mPaint;

	private TicTacToeGame mGame;

	public BoardView(Context context) {
		super(context);
		initialize();
	}

	public BoardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	public BoardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public void initialize() {
		mHumanBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.x);
		mComputerBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.o);

		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// Determine the width and height of the View

		int boardWidth = getWidth();
		int boardHeight = getHeight();

		// Make thick, light gray lines

		mPaint.setColor(Color.LTGRAY);
		mPaint.setStrokeWidth(GRID_WIDTH);

		// Draw the two vertical board lines

		int cellWidth = boardWidth / 3;

		canvas.drawLine(cellWidth, 0, cellWidth, boardHeight, mPaint);
		canvas.drawLine(cellWidth * 2, 0, cellWidth * 2, boardHeight, mPaint);
		canvas.drawLine(0, cellWidth, boardWidth, cellWidth, mPaint);
		canvas.drawLine(0, cellWidth * 2, boardWidth, cellWidth * 2, mPaint);

		// Draw all the X and O images

		for (int i = 0; i < TicTacToeGame.BOARD_SIZE; i++) {
			int col = i % 3;
			int row = i / 3;

			// Define the boundaries of a destination rectangle for the image
			int left = col*cellWidth;
			int top = row*cellWidth;
			int right = left + cellWidth;
			int bottom = top + cellWidth;

			if (mGame != null
					&& mGame.getBoardOccupant(i) == TicTacToeGame.HUMAN_PLAYER) {
				canvas.drawBitmap(mHumanBitmap, null, new Rect(left, top,
						right, bottom), null);

			}

			else if (mGame != null
					&& mGame.getBoardOccupant(i) == TicTacToeGame.COMPUTER_PLAYER) {
				canvas.drawBitmap(mComputerBitmap, null, new Rect(left, top,
						right, bottom), null);
			}
		}

	}

	public void setGame(TicTacToeGame game) {
		mGame = game;
	}

	public int getBoardCellWidth() {
		return getWidth() / 3;
	}

	public int getBoardCellHeight() {
		return getHeight() / 3;
	}
}
