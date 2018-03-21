/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package track_model;

import java.util.ArrayList;

/**
 *
 * @author Gowest
 */
public class Yard extends TrackBlock {

    protected String line = "YARD";
    protected int block = -1;
    
    protected String message;
//    protected ArrayList<Driver> drivers = new ArrayList<>();

    public Yard() {
        super("YARD", -1);
    }
}
