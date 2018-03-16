/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package track_controller.communication;

/**
 *
 * @author Yasaswi Duvvuru
 */
import track_controller.TrackController;
        
public class Switch {
    
    private Authority[] authorities;
    private boolean commonLeft;
    private boolean commonRight;
    
    public Switch(Authority[] authorities)
    {
       this.authorities = authorities; 
    }
    
    public boolean[] canFlipSwitch() throws IllegalArgumentException
    {
        boolean[] flipCheck = new boolean[2];
        flipCheck[0] = false;
        flipCheck[1] = false;
        if(authorities[0].blocks[0] && authorities[1].blocks[0])
        {
            flipCheck[0] = true;
        }
        else if(authorities[0].blocks[0] && authorities[2].blocks[0])
        {
            flipCheck[1] = true;
        }
        else if(authorities[0].blocks[0] && authorities[1].blocks[0] && authorities[2].blocks[0])
        {
            throw new IllegalArgumentException("Invalid Tristate Authority");
        }
    }
    
    public void flipSwitch(Branch branch) throws IllegalArgumentException
    {
       int index = TrackController.branchToIndex(branch);
       switch(index)
       {
           case 0:
               throw new IllegalArgumentException("Cannot Switch common branch. Choose left or right.");
           case 1:
               commonLeft = true;
               commonRight = false;
               break;
           case 2:
               commonLeft = false;
               commonRight = true;
       }
    }
    
    
    
}
