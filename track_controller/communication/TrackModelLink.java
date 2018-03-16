/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package track_controller.communication;

/**
 * Link from Track Controller to Track Model.
 * 
 * Yasaswi Duvvuru
 */

import track_controller.TrackController;
import track_model.*;
public class TrackModelLink {
    private TrackController controller;
    
    public TrackModelLink(TrackController controller){
        this.controller = controller;
    }
    
    public void occupancies(Branch branch, boolean[][] blockNumbers)
            throws IllegalArgumentException
    {
        controller.occupancies(branch, blockNumbers);
    }
    
    public void crossings(Branch branch, boolean[][] blockNumbers)
            throws IllegalArgumentException
    {
        controller.crossings(branch, blockNumbers);
    }
    
    public int sendSpeed(boolean[] speed)
    {
        int speedCount = 0;
        for(int i = 0; i < speed.length; i++)
        {
            if(speed[i])
            {
                speedCount++;
            }
            else
            {
                break;
            }
        }
        
        return speedCount*5;
    }
    
    public boolean[][] sendAuthority(boolean[] commonAuth, boolean[] leftAuth, boolean[] rightAuth)
    {
        boolean[][] authority = new boolean[3][TrackController.MAX_BRANCH_SIZE];
        
        authority[0] = commonAuth;
        authority[1] = leftAuth;
        authority[2] = rightAuth;
    }
}
