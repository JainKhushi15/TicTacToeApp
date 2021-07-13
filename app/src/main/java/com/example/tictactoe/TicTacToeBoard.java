package com.example.tictactoe;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class TicTacToeBoard extends View {

    private final int boardColor;
    private final int XColor;
    private final int OColor;
    private final int winningLineColor;

    private boolean winningLine = false;

    private final Paint paint = new Paint();

    private final GameLogic game;

    private int cellSize = getWidth() / 3;

    public TicTacToeBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        game = new GameLogic();

        TypedArray Attributes = context.getTheme().obtainStyledAttributes(attrs,R.styleable.TicTacToeBoard,0,0);

        try {
            boardColor = Attributes.getInteger(R.styleable.TicTacToeBoard_boardColor,0);
            XColor = Attributes.getInteger(R.styleable.TicTacToeBoard_XColor,0);
            OColor = Attributes.getInteger(R.styleable.TicTacToeBoard_OColor,0);
            winningLineColor = Attributes.getInteger(R.styleable.TicTacToeBoard_winningLineColor,0);
        }finally {
            Attributes.recycle();
        }

    }

    @Override
    protected void onMeasure(int width,int height) {
        super.onMeasure(width,height);

        int dimensions = Math.min(getMeasuredWidth(), getMeasuredHeight());
        cellSize = dimensions / 3;

        setMeasuredDimension(dimensions,dimensions);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        drawGameBoard(canvas);
        drawMarkers(canvas);

        if(winningLine) {
            paint.setColor(winningLineColor);
            drawWinningLine(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        int action = event.getAction();

        if(action == MotionEvent.ACTION_DOWN) {
            int row = (int) Math.ceil((y/cellSize));
            int col = (int) Math.ceil((x/cellSize));

            if(!winningLine) {
                if(game.updateGameBoard(row, col)) {
                    invalidate();

                    if(game.winnerCheck()) {
                        winningLine = true;
                        invalidate();
                    }

                    //Updating the Player's Turn
                    if(game.getPlayer() % 2 == 0) {
                        game.setPlayer(game.getPlayer() - 1);
                    }

                    else {
                        game.setPlayer(game.getPlayer() + 1);
                    }
                }
            }

            //invalidate();

            return true;
        }
        else {
            return false;
        }
        //return false;
    }

    private void drawGameBoard(Canvas canvas) {
        paint.setColor(boardColor);
        paint.setStrokeWidth(12);

        for(int c=1; c<3; c++) {
            canvas.drawLine(cellSize*c, 0, cellSize*c, canvas.getWidth(), paint);
        }

        for(int r=1; r<3; r++) {
            canvas.drawLine(0, cellSize*r, canvas.getWidth(), cellSize*r, paint);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawMarkers(Canvas canvas) {
        for(int r=0; r<3; r++) {
            for(int c=0; c<3; c++) {
                if(game.getGameBoard()[r][c] != 0) {
                    if(game.getGameBoard()[r][c] == 1) {
                        drawX(canvas, r, c);
                    }
                    else {
                        drawO(canvas, r, c);
                    }
                }
            }
        }
    }

    private void drawX(Canvas canvas, int row, int col) {
        paint.setColor(XColor);

        canvas.drawLine((float)((col+1)*cellSize - cellSize*0.2),
                        (float)(row*cellSize + cellSize*0.2),
                        (float)(col*cellSize + cellSize*0.2),
                        (float)((row+1)*cellSize - cellSize*0.2) ,
                        paint );


        canvas.drawLine((float)(col*cellSize + cellSize*0.2),
                        (float)(row*cellSize + cellSize*0.2),
                        (float)((col+1)*cellSize - cellSize*0.2),
                        (float)((row+1)*cellSize - cellSize*0.2) ,
                        paint );
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawO(Canvas canvas, int row, int col) {
        paint.setColor(OColor);

        canvas.drawOval((float)(col*cellSize + cellSize*0.2),
                        (float)(row*cellSize + cellSize*0.2),
                        (float)((col*cellSize + cellSize) - cellSize*0.2),
                        (float)((row*cellSize + cellSize) - cellSize*0.2),
                        paint );
    }

    private void drawHorizontalLine(Canvas canvas, int row, int col) {
        //Division by 2 creates the line in middle.
        canvas.drawLine(col,
                        row*cellSize + (float)cellSize/2,
                        cellSize*3,
                        row*cellSize + (float)cellSize/2,
                        paint);
    }

    private void drawVerticalLine(Canvas canvas, int row, int col) {
        //Division by 2 creates the line in middle.
        canvas.drawLine(col*cellSize + (float)cellSize/2,
                        row,
                        (col * cellSize) +(float)cellSize / 2,
                        cellSize*3,
                        paint);
    }

    private void drawDiagnolLinePos(Canvas canvas) {
        canvas.drawLine(0,
                        cellSize*3,
                        cellSize*3,
                        0,
                        paint);
    }

    private void drawDiagnolLineNeg(Canvas canvas) {
        canvas.drawLine(0,
                        0,
                        cellSize*3,
                        cellSize*3,
                        paint);
    }

    private void drawWinningLine(Canvas canvas) {
        int row = game.getWinType()[0];
        int col = game.getWinType()[1];

        switch (game.getWinType()[2]) {
            case 1:
                drawHorizontalLine(canvas, row, col);
                break;

            case 2:
                drawVerticalLine(canvas, row, col);
                break;

            case 3:
                drawDiagnolLineNeg(canvas);
                break;

            case 4:
                drawDiagnolLinePos(canvas);
                break;
        }
    }

    public void setUpGame(Button playAgain, Button home, TextView playerDisplay, String[] names) {
        game.setPlayAgainBTN(playAgain);
        game.setHomeBTN(home);
        game.setPlayerTurn(playerDisplay);
        if(!names[0].equals("") && !names[1].equals("")) {
            game.setPlayerNames(names);
        }
    }

    public void resetGame() {
        game.resetGame();
        winningLine = false;
    }
}
