/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package levgui;

/**
 *
 * @author Justin Swanson
 */
public interface ProgressBar {
    void setMax(int in);
    void setMax(int in, String status);
    void setStatus(String reason);
    void incrementBar();
    void reset();
    void setBar(int in);
}
