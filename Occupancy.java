/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package track_controller.communication;

import track_controller.TrackController;

/**
 *Blocks that are considered occupied for any reason
 * 
 * for one branch
 * 
 * Yasaswi Duvvuru
 */
public class Occupancy {
    // true/false for occupied/not occupied

	//

	// Each element maps to a block. Indexed by distance from the inside of

	// the branch, e.g.,

	//

	//   for a track controller with 3, 4, and 5 blocks at common,

	//   left, and right branches:

	//

	//                      = = = =

	//                    /

	//      = = = - switch

	//                    \

	//                      = = = = =

	//

	//   [1, 1, 0] for the common branch maps to:

	//

	//                      = = = =

	//                    /

	//      0 1 1 - switch

	//                    \

	//                      = = = = =

	//

	//   and [0, 1, 0, 1, 1] for the right branch maps to:

	//

	//                      = = = =

	//                    /

	//      = = = - switch

	//                    \

	//                      0 1 0 1 1
    
    public boolean occupancies[];
    public boolean lights[];
    
    //Constructs an occupancy as a clone of another
    
    public Occupancy (Occupancy other)
    {
        this.occupancies = other.occupancies.clone();
    }
    
    public Occupancy (boolean[] occupancies) throws IllegalArgumentException{
        if(occupancies.length < 1 || occupancies.length > TrackController.MAX_BRANCH_SIZE){
            throw new IllegalArgumentException("length bounds");
        }
        
        this.occupancies = new boolean[occupancies.length];
        this.lights = new boolean[occupancies.length];
        
        this.occupancies = occupancies;
        
        for(int i = 0; i < occupancies.length; i++)
        {
         this.lights[i] = !this.occupancies[i];   
        }
    }
    

}
