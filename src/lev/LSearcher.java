package lev;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedInputStream;

/**
 *
 * @author Justin Swanson
 */
public class LSearcher {

    String[] targets;
    int[] found;
    Boolean mark;

    public LSearcher(String... targets_) {
        targets = targets_;
        found = new int[targets.length];
    }

    public String next(int in) {
        return next((char) in);
    }

    public String next(char in) {
        for (int i = 0; i < found.length; i++) {
            if (in == targets[i].charAt(found[i])) {
                found[i]++;
                if (found[i] == targets[i].length()) {
                    return targets[i];
                }
            } else if (in == targets[i].charAt(0)) {
                found[i] = 1;
            } else {
                found[i] = 0;
            }
        }

        return "";
    }

    public String next(int in, BufferedInputStream input) {
        return next((char) in, input);
    }

    public String next(char in, BufferedInputStream input) {
        mark = true;
        for (int i = 0; i < found.length; i++) {
            if (in == targets[i].charAt(found[i])) {
                found[i]++;
                if (found[i] == targets[i].length()) {
                    return targets[i];
                }
                mark = false;
            } else if (in == targets[i].charAt(0)) {
                found[i] = 1;
            } else {
                found[i] = 0;
            }
        }
        if (mark) {
            input.mark(25);
        }

        return "";
    }
}
