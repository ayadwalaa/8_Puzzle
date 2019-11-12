package com.wala.a8puzzle;

class State {
    private int[][] current_state = new int[3][3];
    private boolean explored = false;
    private int cost, level;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    boolean isExplored() {
        return explored;
    }

    int getCost() {
        return cost;
    }

    void setCost(int cost) {
        this.cost = cost;
    }

    State(int[][] current_state){
        for(int i=0;i<3;i++)
            for (int j =0;j<3;j++)
                this.current_state[i][j] = current_state[i][j];

    }
    void setExplored(boolean explored) {
        this.explored = explored;
    }

    int[][] getCurrent_state() {
        return current_state;
    }

    boolean equals(State state){
        for(int i=0;i<3;i++)
            for (int j=0;j<3;j++)
                if (this.current_state[i][j] != state.getCurrent_state()[i][j])
                    return false;

        return true;
    }
}
