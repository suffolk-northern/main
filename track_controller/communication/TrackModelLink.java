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
}
