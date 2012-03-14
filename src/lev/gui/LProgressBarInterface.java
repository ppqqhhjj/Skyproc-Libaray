/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lev.gui;

/**
 *
 * @author Justin Swanson
 */
public interface LProgressBarInterface {
    /**
     * 
     * @param in Value to set as the max unit value of the progress bar.
     */
    void setMax(int in);
    /**
     * 
     * @param in Value to set as the max unit value of the progress bar.
     * @param status String to set as the status of the progress bar.
     */
    void setMax(int in, String status);
    /**
     * 
     * @param status String to set as the status of the progress bar.
     */
    void setStatus(String status);
    /**
     * Increments the progress bar one unit.
     */
    void incrementBar();
    /**
     * Resets the progress bar to zero of max.
     */
    void reset();
    /**
     * 
     * @param in value to set the progress bar at.
     */
    void setBar(int in);
}
