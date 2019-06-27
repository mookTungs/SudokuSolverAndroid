package com.mookt.sudokusolver;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import static android.graphics.Typeface.BOLD;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity extends AppCompatActivity {
    private AdView mAdView;

    public static int size = 9;
    public static int row = 0;
    public static int column = 0;
    public static int[][] sudoku;
    public static int startId = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this,"ca-app-pub-3571332738971082~3598527299");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        LinearLayout container = (LinearLayout) findViewById(R.id.verticalContainer);
        container.setFocusableInTouchMode(true);
        container.setFocusable(true);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,WRAP_CONTENT, 1);

        LinearLayout horizontal = new LinearLayout(this);
        horizontal.setPadding(8,8,8,8);
        horizontal.setBackgroundResource(R.drawable.gridborder);
        LinearLayout vertical;
        View emptyView;
        ViewGroup.LayoutParams viewVerParams = new ViewGroup.LayoutParams(8,MATCH_PARENT);
        ViewGroup.LayoutParams viewHorParams = new ViewGroup.LayoutParams(MATCH_PARENT, 8);

        int tempId = startId;
        for(int i = 0; i < 9; i++){
            vertical = new LinearLayout(this);
            vertical.setLayoutParams(params);
            vertical.setOrientation(LinearLayout.VERTICAL);
            for(int j = 0; j < 9; j++){
                EditText editText = new EditText(this);
                setEditText(editText);
                editText.setId(tempId);
                tempId++;
                vertical.addView(editText);
                if(j == 2 || j == 5){
                    emptyView = new View(this);
                    emptyView.setLayoutParams(viewHorParams);
                    emptyView.setBackgroundColor(Color.BLACK);
                    vertical.addView(emptyView);
                }
            }
            horizontal.addView(vertical);
            if(i == 2 || i == 5){
                emptyView = new View(this);
                emptyView.setLayoutParams(viewVerParams);
                emptyView.setBackgroundColor(Color.BLACK);
                horizontal.addView(emptyView);
            }
        }

        LinearLayout buttonLayout = new LinearLayout(this);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1);
        buttonParams.setMargins(16,16,16,16);

        Button solveButton = new Button(this);
        solveButton.setText("SOLVE");
        solveButton.setLayoutParams(buttonParams);
        solveButton.setOnClickListener(solveButtonClicked);

        Button clearButton = new Button(this);
        clearButton.setText("CLEAR");
        clearButton.setLayoutParams(buttonParams);
        clearButton.setOnClickListener(clearButtonClicked);

        buttonLayout.addView(solveButton);
        buttonLayout.addView(clearButton);


        container.addView(horizontal);
        container.addView(buttonLayout);
    }

    private View.OnClickListener solveButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sudoku = new int[size][size];
            int tempId = startId;
            EditText editText;
            for(int i = 0; i < size; i++){
                for(int j = 0; j < size; j++){
                    editText = (EditText)findViewById(tempId);
                    String s = editText.getText().toString();
                    if(s.equals("")){
                        s = "0";
                    }
                    sudoku[i][j] = Integer.parseInt(s);
                    editText.clearFocus();
                    tempId++;
                }
            }

            hideSoftKeyboard(MainActivity.this, view);

            if(!isSudokuValid(sudoku)){

                AlertDialog alertDialog= new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Sudoku Solver");
                alertDialog.setMessage("Invalid Sudoku");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"Ok",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which){
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            else {
                if (!solveSudoku(sudoku)) {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Sudoku Solver");
                    alertDialog.setMessage("No solution");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }else {
                    tempId = startId;
                    for(int i = 0; i < size; i++){
                        for(int j =0; j < size; j++){
                            editText = (EditText) findViewById(tempId);
                            editText.setText(Integer.toString(sudoku[i][j]));
                            tempId++;
                        }
                    }
                }
            }

        }
    };

    private View.OnClickListener clearButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view){
            int tempId = startId;
            EditText editText;
            for(int i = 0; i < size; i++){
                for(int j = 0; j < size; j++){
                    editText = (EditText) findViewById(tempId);
                    editText.setText("");
                    editText.clearFocus();
                    tempId++;
                }
            }
            sudoku = new int[size][size];
            hideSoftKeyboard(MainActivity.this, view);
        }
    };


    public void setEditText(EditText editText){
        editText.setWidth(0);
        editText.setBackgroundResource(R.drawable.textboxborder);
        editText.setGravity(Gravity.CENTER);
        editText.setTextSize(30);
        editText.setTypeface(null, BOLD);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
    }

    public static void hideSoftKeyboard (Activity activity, View view)
    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }


    public boolean isSudokuValid(int[][] grid){
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(grid[i][j] != 0){
                    if(checkHor(grid,i,j,grid[i][j]) > 1 || checkVer(grid,i,j,grid[i][j]) > 1
                            || checkSquare(grid,i,j,grid[i][j]) > 1){
                        return false;
                    }

                }
            }
        }
        return true;
    }

    public int checkHor(int[][] result,int hor, int ver, int num){
        int count = 0;
        for(int j = 0; j < size; j++){
            if(result[hor][j] == num){
                count++;
            }
        }
        return count;
    }

    public int checkVer(int[][] result,int hor, int ver, int num){
        int count = 0;
        for(int i = 0; i < size; i++){
            if(result[i][ver] == num){
                count++;
            }
        }
        return count;
    }

    public int checkSquare(int[][] result,int hor, int ver, int num){
		/*'''''''''''
			1|2|3
			4|5|6
			7|8|9
		'''''''''''''*/
        int count = 0;
        //square 1 (0,0)
        int x = 0;
        int y = 0;
        if(hor < 3){
            if(ver > 2 && ver < 6){
                //square 2 (0,3)
                y = 3;
            }
            if(ver > 5){
                //square 3 (0,6)
                y = 6;
            }
        }

        if(hor > 2 && hor < 6){
            //square 4 (3,0)
            x = 3;
            if(ver > 2 && ver < 6){
                //square 5 (3,3)
                y = 3;
            }
            if(ver > 5){
                //square 6 (3,6)
                y = 6;
            }
        }

        if(hor > 5){
            //square 7 (6,0)
            x = 6;
            if(ver > 2 && ver < 6){
                //square 8 (6,3){
                y = 3;
            }
            if(ver > 5){
                //square 9 (6,6)
                y = 6;
            }
        }

        for(int i = x; i <= (x+2); i++){
            for(int j = y; j <= (y+2); j++){
                if(result[i][j] == num){
                    count++;
                }
            }
        }

        return count;
    }

    public boolean unassigned(int[][] s){
        int count = 0;
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(s[i][j] == 0){
                    row = i;
                    column = j;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean solveSudoku(int[][] result){
        if(!unassigned(result)){
            return true;
        }
        int oldRow = row;
        int oldColumn = column;
        for(int num = 1; num <=size; num++){
            if(checkHor(result,oldRow,oldColumn,num) == 0 && checkVer(result,oldRow,oldColumn,num) == 0
                    && checkSquare(result,oldRow,oldColumn,num) == 0){
                result[oldRow][oldColumn] = num;
                if(solveSudoku(result)){
                    return true;
                }
                result[oldRow][oldColumn] = 0;
            }
        }
        return false;
    }
}
