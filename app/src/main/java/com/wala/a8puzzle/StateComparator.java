package com.wala.a8puzzle;

import java.util.Comparator;

class StateComparator implements Comparator<State> {
    @Override
    public int compare(State x, State y) {
        // Assume neither string is null. Real code should
        // probably be more robust
        // You could also just return x.length() - y.length(),
        // which would be more efficient.
        if (x.getCost() < y.getCost()) {
            return -1;
        }
        if (x.getCost() > y.getCost()) {
            return 1;
        }
        return 0;
    }
}
