package com.wala.a8puzzle;

//package com.example.lib;
//package lib;

class State {

    private int[][] current_state;
    private boolean explored = false;
    private int cost;
    private int[][] parent = new int[3][3];

    boolean isExplored() {
        return explored;
    }

    int getCost() {
        return cost;
    }

    void setCost(int cost) {
        this.cost = cost;
    }

    int[][] getParent() {
        return parent;
    }

    void setParent(int[][] parent) {
        this.parent = parent;
    }

    State() {
        this.current_state = new int[3][3];
        /*for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.current_state[i][j] = 0;
            }
        }*/
    }

    State(int[][] current_state) {
        this.current_state = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.current_state[i][j] = current_state[i][j];
            }
        }

    }

    void setExplored(boolean explored) {
        this.explored = explored;
    }

    int[][] getCurrent_state() {
        return current_state;
    }

    void setCurrentState(int[][] current_state) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.current_state[i][j] = current_state[i][j];
            }
        }

    }

    boolean equals(State state) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (this.current_state[i][j] != state.getCurrent_state()[i][j]) {
                    return false;
                }
            }
        }

        return true;
    }
}

