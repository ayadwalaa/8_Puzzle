package com.wala.a8puzzle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private int[][] secmd = {{1, 2, 3}, {8, 0, 4}, {7, 6, 5}};
    private int[][] first_goal = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
    private int[][] init = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}};
    private String sol = "";

    private State current_state;
    private ArrayList<State> states = new ArrayList<>();
    public ArrayList<State> open = new ArrayList<>();
    public ArrayList<State> close = new ArrayList<>();
    private ArrayList<TextView> textViews = new ArrayList<>();
    private ArrayList<Integer> goal_list = new ArrayList<>();
    private PriorityQueue<State> priorityQueue;

    private GridLayout gridLayout;
    private TextView tile_0, tile_1, tile_2,
                     tile_3, tile_4, tile_5,
                     tile_6, tile_7, tile_8;
    private Button start, shuffle, next_step, stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridLayout = findViewById(R.id.grid);
        initTextViews();
        setUpGoalList();

        StateComparator stateComparator = new StateComparator();
        priorityQueue = new PriorityQueue<>(1000, stateComparator);
        createCurrentState();

        setGridElements(current_state);

        start = findViewById(R.id.start);
        shuffle = findViewById(R.id.shuffle);
        next_step = findViewById(R.id.hint);
        stop = findViewById(R.id.stop);

        start.setOnClickListener(v->{
            shuffle.setEnabled(false);
            start.setEnabled(false);

            Handler handler = new Handler();
            handler.post(this::solve);
//            AsyncSolver asyncSolver = new AsyncSolver();
//            asyncSolver.doInBackground(null);
        });
        stop.setOnClickListener(n->{
//            if(timer!=null)
//                timer.cancel();
            shuffle.setEnabled(true);
            start.setEnabled(true);
        });
        next_step.setOnClickListener(w-> Toast.makeText(this, solveForHint().split("&")[0], Toast.LENGTH_LONG).show());
        shuffle.setOnClickListener(b->{
            createCurrentState();
            setGridElements(current_state);
        });
    }

    private void setUpGoalList() {
        goal_list.add(0);
        goal_list.add(1);
        goal_list.add(2);
        goal_list.add(3);
        goal_list.add(4);
        goal_list.add(5);
        goal_list.add(6);
        goal_list.add(7);
        goal_list.add(8);
    }

    private void setGridElements(State current_state){
        for (int i = 0; i<9; i++){
            TextView textView = (TextView)gridLayout.getChildAt(i);
            textView.setText(String.valueOf(current_state.getCurrent_state()[(int)i/3][i%3]));
            if (textView.getText().equals("0")) {
                textView.setBackground(getDrawable(R.drawable.zero_border));
                textView.setText("");
            }
            else
                textView.setBackground(getDrawable(R.drawable.border));
        }
    }

    public void createCurrentState() {
        int [][] current;
        do {
            ArrayList<Integer> numbers = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                numbers.add(i);
            }
            Collections.shuffle(numbers);

            current = new int[3][3];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    // init_state[i][j] = numbers.get(i * 3 + j);
                    current[i][j] = numbers.get(i * 3 + j);
                    init[i][j] = current[i][j];
                }
            }
        }while (!isSolvable(current));

        current_state = new State(current);
        current_state.setExplored(true);
        states = new ArrayList<>();
        open = new ArrayList<>();
        close = new ArrayList<>();

        states.add(current_state);
        open.add(current_state);
        Collections.sort(open, new StateComparator());

        printArray(current_state.getCurrent_state(), "This is starting state");
    }

    private boolean isSolvable(int[][] puzzle) {
        // Count inversions in given 8 puzzle
        int inv_count = 0;
        for (int i = 0; i < 3 - 1; i++) {
            for (int j = i + 1; j < 3; j++) // Value 0 is used for empty space
            {
                if (puzzle[j][i] > 0 && puzzle[j][i] > 0
                        && puzzle[j][i] > puzzle[i][j]) {
                    inv_count++;
                }
            }
        }

        // return true if inversion count is even.
        return (inv_count % 2 == 0);
    }

    private void printArray(int[][] array, String message){
//        System.out.println(message);
//        for (int i = 0; i < 3; i++) {
//            System.out.print("[ ");
//            for (int j = 0; j < 3; j++) {
//                System.out.printf("%d%s", array[i][j], " ");
//            }
//            System.out.println("]");
//        }
    }

    private int[][] expandRight(int[][] array, int n, int m) {
        int[][] expand_right = new int[3][3];
        copyArray(array, expand_right);

        int tmp = expand_right[n][m];
        expand_right[n][m] = expand_right[n][m + 1];
        expand_right[n][m + 1] = tmp;
        //manhattanFunction(expand_right);
        sol += "Go Right&";
        return expand_right;
    }

    private int[][] expandLeft(int[][] array, int n, int m) {
        int[][] expand_left = new int[3][3];
        copyArray(array, expand_left);

        int tmp = expand_left[n][m];
        expand_left[n][m] = expand_left[n][m - 1];
        expand_left[n][m - 1] = tmp;
        sol += "Go Left&";
        //manhattanFunction(expand_left);
        return expand_left;
    }

    private int[][] expandUp(int[][] array, int n, int m) {
        int[][] expand_up = new int[3][3];
        copyArray(array, expand_up);

        int tmp = expand_up[n][m];
        expand_up[n][m] = expand_up[n - 1][m];
        expand_up[n - 1][m] = tmp;
        sol += "Go Up&";
        //manhattanFunction(expand_up);
        return expand_up;
    }

    private int[][] expandDown(int[][] array, int n, int m) {
        int[][] expand_down = new int[3][3];
        copyArray(array, expand_down);

        int tmp = expand_down[n][m];
        expand_down[n][m] = expand_down[n + 1][m];
        expand_down[n + 1][m] = tmp;
        sol += "Go Down&";
        //manhattanFunction(expand_down);
        return expand_down;
    }

    private void initTextViews(){
        tile_0 = findViewById(R.id.tile_0);
        tile_0.setOnClickListener(a->{
            if(tile_1.getText().equals("")){
                tile_1.setText(tile_0.getText());
                tile_0.setText("");
            }else if (tile_3.getText().equals("")){
                tile_3.setText(tile_0.getText());
                tile_0.setText("");
            }
            decorateTextView();
        });
        tile_1 = findViewById(R.id.tile_1);
        tile_1.setOnClickListener(b->{
            if(tile_0.getText().equals("")){
                tile_0.setText(tile_1.getText());
                tile_1.setText("");
            }else if (tile_2.getText().equals("")){
                tile_2.setText(tile_1.getText());
                tile_1.setText("");
            }else if (tile_4.getText().equals("")){
                tile_4.setText(tile_1.getText());
                tile_1.setText("");
            }
            decorateTextView();
        });
        tile_2 = findViewById(R.id.tile_2);
        tile_2.setOnClickListener(c->{
            if(tile_1.getText().equals("")){
                tile_1.setText(tile_2.getText());
                tile_2.setText("");
            }else if (tile_5.getText().equals("")){
                tile_5.setText(tile_2.getText());
                tile_2.setText("");
            }
            decorateTextView();
        });
        tile_3 = findViewById(R.id.tile_3);
        tile_3.setOnClickListener(d->{
            if(tile_0.getText().equals("")){
                tile_0.setText(tile_3.getText());
                tile_3.setText("");
            }else if (tile_4.getText().equals("")){
                tile_4.setText(tile_3.getText());
                tile_3.setText("");
            }else if (tile_6.getText().equals("")){
                tile_6.setText(tile_3.getText());
                tile_3.setText("");
            }
            decorateTextView();
        });
        tile_4 = findViewById(R.id.tile_4);
        tile_4.setOnClickListener(e->{
            if(tile_1.getText().equals("")){
                tile_1.setText(tile_4.getText());
                tile_4.setText("");
            }else if (tile_3.getText().equals("")){
                tile_3.setText(tile_4.getText());
                tile_4.setText("");
            }else if (tile_5.getText().equals("")){
                tile_5.setText(tile_4.getText());
                tile_4.setText("");
            }
            else if (tile_7.getText().equals("")){
                tile_7.setText(tile_4.getText());
                tile_4.setText("");
            }
            decorateTextView();
        });
        tile_5 = findViewById(R.id.tile_5);
        tile_5.setOnClickListener(f->{
            if(tile_2.getText().equals("")){
                tile_2.setText(tile_5.getText());
                tile_5.setText("");
            }else if (tile_4.getText().equals("")){
                tile_4.setText(tile_5.getText());
                tile_5.setText("");
            }else if (tile_8.getText().equals("")){
                tile_8.setText(tile_5.getText());
                tile_5.setText("");
            }
            decorateTextView();
        });
        tile_6 = findViewById(R.id.tile_6);
        tile_6.setOnClickListener(g->{
            if(tile_3.getText().equals("")){
                tile_3.setText(tile_6.getText());
                tile_6.setText("");
            }else if (tile_7.getText().equals("")){
                tile_7.setText(tile_6.getText());
                tile_6.setText("");
            }
            decorateTextView();
        });
        tile_7 = findViewById(R.id.tile_7);
        tile_7.setOnClickListener(h->{
            if(tile_6.getText().equals("")){
                tile_6.setText(tile_7.getText());
                tile_7.setText("");
            }else if (tile_4.getText().equals("")){
                tile_4.setText(tile_7.getText());
                tile_7.setText("");
            }else if (tile_8.getText().equals("")){
                tile_8.setText(tile_7.getText());
                tile_7.setText("");
            }
            decorateTextView();
        });
        tile_8 = findViewById(R.id.tile_8);
        tile_8.setOnClickListener(i->{
            if(tile_5.getText().equals("")){
                tile_5.setText(tile_8.getText());
                tile_8.setText("");
            }else if (tile_7.getText().equals("")){
                tile_7.setText(tile_8.getText());
                tile_8.setText("");
            }
            decorateTextView();
        });

        textViews.add(tile_0);
        textViews.add(tile_1);
        textViews.add(tile_2);
        textViews.add(tile_3);
        textViews.add(tile_4);
        textViews.add(tile_5);
        textViews.add(tile_6);
        textViews.add(tile_7);
        textViews.add(tile_8);
    }

    private void decorateTextView(){
        ArrayList<Integer> checker = new ArrayList<>();
        for(TextView textView: textViews) {
            if (textView.getText().equals("0") || textView.getText().equals("")) {
                textView.setBackground(getDrawable(R.drawable.zero_border));
                textView.setText("");
            } else
                textView.setBackground(getDrawable(R.drawable.border));

            if(textView.getText().equals(""))
                checker.add(0);
            else
                checker.add(Integer.valueOf(textView.getText().toString()));
        }

        setCurrentStateFromBoard(checker);

        for (int i=0; i<checker.size()-1; i++){
            if (checker.get(i) != i){
                return;
            }
            //Log.i("PLEASE", String.valueOf(checker.get(i)));
        }showYouWonDialog();
    }

    private void showYouWonDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Congrats")
                .setMessage("You have completed 8 puzzle! Great work!")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.cancel())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void setCurrentStateFromBoard(ArrayList<Integer> numbers){

        int [][] current;


        current = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                // init_state[i][j] = numbers.get(i * 3 + j);
                current[i][j] = numbers.get(i * 3 + j);
                init[i][j] = current[i][j];
            }
        }

        current_state = new State(current);
        current_state.setExplored(true);
        states = new ArrayList<>();
        open = new ArrayList<>();
        close = new ArrayList<>();

        states.add(current_state);
        open.add(current_state);
        Collections.sort(open, new StateComparator());

        printArray(current_state.getCurrent_state(), "This is starting state");
    }

    private int manhattan(int[][] expanded) {
        int manhattan_value = 0;
        int to_find;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                to_find = first_goal[i][j];
                for (int q = 0; q < 3; q++) {
                    for (int w = 0; w < 3; w++) {
                        if (to_find == expanded[q][w]) {
                            manhattan_value = manhattan_value + Math.abs(i - q) + Math.abs(j - w);
                        }
                    }
                }
            }
        }

        return manhattan_value;

        /*
         State new_state = new State(expanded);
         ////new_state.setCost(manhattan_value);
         for (State state : states) {
         if (state.equals(new_state)) {
         new_state.setExplored(true);
         ////if(new_state.getCost()<state.getCost()){
         // // state.setCost(manhattan_value);
         ////state.setExplored(false);
         ////}

         break;
         }
         }
         new_state.setCost(manhattan_value);
         if (!new_state.isExplored()) {
         open.add(new_state);
         states.add(new_state);
         }
         */
    }

    private int cost(int[][] expanded) {
        int cost = 0;
        int to_find;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                to_find = init[i][j];
                for (int q = 0; q < 3; q++) {
                    for (int w = 0; w < 3; w++) {
                        if (to_find == expanded[q][w]) {
                            cost = cost + Math.abs(i - q) + Math.abs(j - w);
                        }
                    }
                }
            }
        }

        return cost;
    }

    public void copyArray(int[][] from_array, int[][] to_array) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                to_array[i][j] = from_array[i][j];
            }
        }
    }

    private boolean areTheyEqual(int[][] arr1, int[][] arr2) {
        for (int k = 0; k < 3; k++) {
            for (int l = 0; l < 3; l++) {
                if (arr1[k][l] != arr2[k][l]) {
                    return false;
                }
            }
        }
        return true;
    }

    private String solve() {

        int s = 0;
        sol = "";

        int row, column;
        row = column = 0;
        int[][] zero = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};

        //do {

        while (!open.isEmpty()) {

            State expand_left = new State();
            State expand_right = new State();
            State expand_up = new State();
            State expand_down = new State();

                /*for (State state : states) {
                 printArray(state.getCurrent_state(), "states " + s);
                 System.out.println("");
                 }*/
            s++;

            current_state = open.get(0);
            printArray(current_state.getCurrent_state(), "current");
            open.remove(0);
            if (areTheyEqual(current_state.getCurrent_state(), first_goal)) {
                //return sol;
                break;
            }

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (current_state.getCurrent_state()[i][j] == 0) {
                        row = i;
                        column = j;
                        break;
                    }
                }
            }

            if ((column - 1 >= 0)) {
                expand_left.setCurrentState(expandLeft(current_state.getCurrent_state(), row, column));
                expand_left.setCost(manhattan(expand_left.getCurrent_state()) + cost(expand_left.getCurrent_state()));
            }
            if ((column + 1 <= 2)) {
                expand_right.setCurrentState(expandRight(current_state.getCurrent_state(), row, column));
                expand_right.setCost(manhattan(expand_right.getCurrent_state()) + cost(expand_right.getCurrent_state()));

            }
            if ((row - 1 >= 0)) {
                expand_up.setCurrentState(expandUp(current_state.getCurrent_state(), row, column));
                expand_up.setCost(manhattan(expand_up.getCurrent_state()) + cost(expand_up.getCurrent_state()));

            }
            if ((row + 1 <= 2)) {
                expand_down.setCurrentState(expandDown(current_state.getCurrent_state(), row, column));
                expand_down.setCost(manhattan(expand_down.getCurrent_state()) + cost(expand_down.getCurrent_state()));

            }

            //left
            if (!areTheyEqual(expand_left.getCurrent_state(), zero)) {
                A_star(expand_left, "left");
            }

            //right
            if (!areTheyEqual(expand_right.getCurrent_state(), zero)) {
                A_star(expand_right, "right");
            }

            //up
            if (!areTheyEqual(expand_up.getCurrent_state(), zero)) {
                A_star(expand_up, "up");
            }

            //down
            if (!areTheyEqual(expand_down.getCurrent_state(), zero)) {
                A_star(expand_down, "down");
            }

            close.add(current_state);
            setGridElements(current_state);

        }

        setGridElements(current_state);

        //} while (!areTheyEqual(current_state.getCurrent_state(), first_goal));
        printArray(current_state.getCurrent_state(), "Congrats");

        start.setEnabled(true);
        shuffle.setEnabled(true);

        return sol;
    }

    public void A_star(State expand, String s) {

        boolean states_flag, open_flag, close_flag;
        int index;

        states_flag = open_flag = close_flag = false;

        for (State state : states) {
            if (areTheyEqual(state.getCurrent_state(), expand.getCurrent_state())) {
                states_flag = true;
                break;

            }
        }
        if (!states_flag) {//not generated before
            expand.setCost(manhattan(expand.getCurrent_state()) + cost(expand.getCurrent_state()));
            expand.setParent(current_state.getCurrent_state());
            open.add(expand);
            Collections.sort(open, new StateComparator());
            states.add(expand);
        } else if (states_flag) {//generated before

            index = -1;
            for (State state : open) {
                index++;
                if (areTheyEqual(state.getCurrent_state(), expand.getCurrent_state())) {
                    open_flag = true;
                    break;

                }
            }

            if (open_flag) {
                //int index = open.indexOf(expand_left);
                State old_left = open.get(index);
                if (expand.getCost() < old_left.getCost()) {
                    old_left.setCost(expand.getCost());
                    old_left.setParent(current_state.getCurrent_state());
                    open.add(index, old_left);
                    Collections.sort(open, new StateComparator());
                }
            }

            index = -1;
            for (State state : close) {
                index++;
                if (areTheyEqual(state.getCurrent_state(), expand.getCurrent_state())) {
                    close_flag = true;
                    break;

                }
            }

            if (close_flag) {
                //int index = close.indexOf(expand_left);
                State old_left = close.get(index);
                if (expand.getCost() < old_left.getCost()) {
                    close.remove(index);
                    open.add(expand);
                    Collections.sort(open, new StateComparator());
                }
            }

        }

    }

    private String solveForHint() {

        int s = 0;
        sol = "";

        int row, column;
        row = column = 0;
        int[][] zero = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};

        //do {

        while (!open.isEmpty()) {

            State expand_left = new State();
            State expand_right = new State();
            State expand_up = new State();
            State expand_down = new State();

                /*for (State state : states) {
                 printArray(state.getCurrent_state(), "states " + s);
                 System.out.println("");
                 }*/
            s++;

            current_state = open.get(0);
            printArray(current_state.getCurrent_state(), "current");
            open.remove(0);
            if (areTheyEqual(current_state.getCurrent_state(), first_goal)) {
                //return sol;
                break;
            }

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (current_state.getCurrent_state()[i][j] == 0) {
                        row = i;
                        column = j;
                        break;
                    }
                }
            }

            if ((column - 1 >= 0)) {
                expand_left.setCurrentState(expandLeft(current_state.getCurrent_state(), row, column));
                expand_left.setCost(manhattan(expand_left.getCurrent_state()) + cost(expand_left.getCurrent_state()));
            }
            if ((column + 1 <= 2)) {
                expand_right.setCurrentState(expandRight(current_state.getCurrent_state(), row, column));
                expand_right.setCost(manhattan(expand_right.getCurrent_state()) + cost(expand_right.getCurrent_state()));

            }
            if ((row - 1 >= 0)) {
                expand_up.setCurrentState(expandUp(current_state.getCurrent_state(), row, column));
                expand_up.setCost(manhattan(expand_up.getCurrent_state()) + cost(expand_up.getCurrent_state()));

            }
            if ((row + 1 <= 2)) {
                expand_down.setCurrentState(expandDown(current_state.getCurrent_state(), row, column));
                expand_down.setCost(manhattan(expand_down.getCurrent_state()) + cost(expand_down.getCurrent_state()));

            }

            //left
            if (!areTheyEqual(expand_left.getCurrent_state(), zero)) {
                A_star(expand_left, "left");
            }

            //right
            if (!areTheyEqual(expand_right.getCurrent_state(), zero)) {
                A_star(expand_right, "right");
            }

            //up
            if (!areTheyEqual(expand_up.getCurrent_state(), zero)) {
                A_star(expand_up, "up");
            }

            //down
            if (!areTheyEqual(expand_down.getCurrent_state(), zero)) {
                A_star(expand_down, "down");
            }

            close.add(current_state);
            //setGridElements(current_state);

        }

        //setGridElements(current_state);

        //} while (!areTheyEqual(current_state.getCurrent_state(), first_goal));
        printArray(current_state.getCurrent_state(), "Congrats");

        //start.setEnabled(true);
        //shuffle.setEnabled(true);

        return sol;
    }

}
