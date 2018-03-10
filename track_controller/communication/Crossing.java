/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package track_controller.communication;

/**
 * Railroad Crossing Data
 * 
 * For one branch
 * 
 * Yasaswi Duvvuru
 */
public class Crossing {
    public boolean[] crossingPresent;
    public boolean barDown;
    public boolean crossingLightGreen;
    
    public Crossing(boolean[] crossingPresent)
            
            throws IllegalArgumentException
    {
        this.crossingPresent = crossingPresent;
        barDown = true;
        crossingLightGreen = false;
    }
    
    public void setCrossing(boolean[] occupancies)
            throws IllegalArgumentException
    {
        int j = -1;
        for(int i = 0; i < crossingPresent.length; i++)
        {
            if(crossingPresent[i])
            {
                j = i;
            }
        }
        if(j > 0 && j < crossingPresent.length)
        {
            barDown = occupancies[j] && occupancies[j - 1] && occupancies[j + 1];
            crossingLightGreen = !barDown;
        }
    }
}
