/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myctc;

/**
 *
 * @author missm
 */

import java.util.ArrayDeque;

/**
 *
 * @author missm
 */
public class MyCtc {
    
    public static MyCtc ctc;
    public static MyCtcUI ui;
    
    public static ArrayDeque<Train> trains = new ArrayDeque<Train>();
    public static ArrayDeque<Block> blueline = new ArrayDeque<Block>();
    public static ArrayDeque<Block> switches = new ArrayDeque<Block>();
    
    public static void showUI()
    {
        ui.showUI();
    }
    
    /*
    public MyCtc()
    {
        ctc = this;
        ui = new MyCtcUI(ctc);
    }
    */
    
    
    //public static void main(String[] args)
    public MyCtc()
    {
        ctc = this;
        ui = new MyCtcUI(ctc);
        showUI();
        
        blueline.add(new Block("blue",true));
        blueline.add(new Block("blue", 'A', 1, 100, null, null, false));
        blueline.add(new Block("blue", 'A', 2, 100, null, null, false, 1, null, null, null, null));
        blueline.add(new Block("blue", 'A', 3, 100, null, null, false, true, "Cathy"));
        blueline.add(new Block("blue", 'A', 4, 100, null, null, false));
        blueline.add(new Block("blue", 'A', 5, 100, null, null, true));
        blueline.add(new Block("blue", 'A', 6, 100, null, null, false, 2, null, null, null, null));
        blueline.add(new Block("blue", 'A', 7, 100, null, null, false));
        blueline.add(new Block("blue", 'A', 8, 100, null, null, false));
        
        Block block = getBlock("blue",'\0',0);
        block.setNext(getBlock("blue",'A',1));
        block.setPrev(getBlock("blue",'A',8));
        
        block = getBlock("blue",'A',1);
        block.setNext(getBlock("blue",'A',2));
        block.setPrev(getBlock("blue",'\0',0));
        
        block = getBlock("blue",'A',2);
        ArrayDeque<Block> from = new ArrayDeque<Block>();
        ArrayDeque<Block> to = new ArrayDeque<Block>();
        from.add(getBlock("blue",'A',1));
        from.add(getBlock("blue",'A',7));
        to.add(getBlock("blue",'A',2));
        block.setSwitch(1, from, to);
        block.setNext(getBlock("blue",'A',3));
        switches.add(block);
    
        block = getBlock("blue",'A',3);
        block.setNext(getBlock("blue",'A',4));
        block.setPrev(getBlock("blue",'A',2));
        
        block = getBlock("blue",'A',4);
        block.setNext(getBlock("blue",'A',5));
        block.setPrev(getBlock("blue",'A',3));
        
        block = getBlock("blue",'A',5);
        block.setNext(getBlock("blue",'A',6));
        block.setPrev(getBlock("blue",'A',4));
    
        block = getBlock("blue",'A',6);
        from = new ArrayDeque<Block>();
        to = new ArrayDeque<Block>();
        from.add(getBlock("blue",'A',6));
        to.add(getBlock("blue",'A',7));
        to.add(getBlock("blue",'A',8));
        block.setSwitch(2, from, to);
        block.setPrev(getBlock("blue",'A',5));
        switches.add(block);
        
        block = getBlock("blue",'A',7);
        block.setNext(getBlock("blue",'A',1));
        block.setPrev(getBlock("blue",'A',6));
        
        block = getBlock("blue",'A',8);
        block.setNext(getBlock("blue",'\0',0));
        block.setPrev(getBlock("blue",'A',6));
        
        Train train = new Train(1,getBlock("blue",'\0',0));
        trains.add(train);
        
        Train train2 = new Train(2,getBlock("blue",'\0',0));
        trains.add(train2);
        
        updateTrack();
        updateTrains();
        
        /*
        
        routeTrain(train,getBlock("blue",'A',3),0);
        
        train.setLoc(getBlock("blue",'A',3));
        getBlock("blue",'A',3).setOccupied(true);
        
        routeTrain(train,getBlock("blue",'\0',0),0);
        
        getTrain(1).setLoc(getBlock("blue",'\0',0));
        getTrain(2).setLoc(getBlock("blue",'A',3));
        getBlock("blue",'A',3).setOccupied(true);
        getBlock("blue",'A',2).setSwitchPos(getBlock("blue",'A',1), getBlock("blue",'A',2));
        
        routeTrain(getTrain(1),getBlock("blue",'A',5),0);

        
        getTrain(1).setLoc(getBlock("blue",'\0',0));
        getTrain(2).setLoc(getBlock("blue",'\0',0));
        getBlock("blue",'A',3).setOccupied(false);
        */
    
    }
    
    private static Train getTrain(int ID)
    {
        ArrayDeque<Train> temp = trains.clone();
        Train mytrain = temp.poll();
        
        while(!temp.isEmpty() && mytrain.getID() != ID)
        {
            mytrain = temp.poll();
        }
        
        if (mytrain.getID() == ID)
            return mytrain;
        else
            return null;
    }
    
    protected boolean routeTrain(String ID, String l, String bl, double sp)
    {
        Train train = getTrain(Integer.parseInt(ID));
        
        char sec;
        int num;
        
        if(bl.equalsIgnoreCase("YARD"))
        {
            sec = '\0';
            num = 0;
        }
        else
        {
            sec = bl.charAt(0);
            num = Integer.parseInt(bl.substring(1));
        }
        
        Block block = getBlock(l,sec,num);
        
        routeTrain(train,block,sp);
        
        return true;
    }
    
    private static void routeTrain(Train train, Block dest, double sp)
    {
        ArrayDeque<Block> route = findRoute(train,train.getLoc(),dest);
        
        printRoute(route);
        
        train.setRoute(route);
        
        ArrayDeque<SwitchAndPos> swpos = getSwitches(route);
        
        printSwitchesOnRoute(swpos);
        
        ArrayDeque<SwitchAndPos> temp = swpos.clone();
        SwitchAndPos curr = null;
        Block f;
        Block t;
        int swID;
        Block block;
        while(!temp.isEmpty())
        {
            curr = temp.poll();
            f = curr.getFrom();
            t = curr.getTo();
            swID = curr.getBlock().getSwID();
            block = curr.getBlock();
            if(!block.getSwitchCurrFrom().equals(f) || !block.getSwitchCurrTo().equals(t))
                setSwitch(block,f,t);
            
        }
        
        double auth = calcAuth(route,route.getFirst(),route.getLast());
        double speed = sp; // from ui
        
        train.setSpeed(speed);
        train.setAuth(auth);
        sendSpeedAuth(train,speed,auth);
    }
    
    private static void printSwitchesOnRoute(ArrayDeque<SwitchAndPos> swpos)
    {
        System.out.println("Switches");
        
        Block block;
        
        ArrayDeque<SwitchAndPos> temp2 = swpos.clone();
        SwitchAndPos curr = null;
        int swID;
        Block f;
        Block t;
        while(!temp2.isEmpty())
        {
            curr = temp2.poll();
            f = curr.getFrom();
            t = curr.getTo();
            swID = curr.getBlock().getSwID();
            
            System.out.println(f.display()+" "+t.display());            
        }
    }
    
    private static void printRoute(ArrayDeque<Block> route)
    {
        System.out.println("Route");
        
        ArrayDeque<Block> temp = route.clone();
        while(!temp.isEmpty())
        {
            System.out.println(temp.poll().display());
        }
    }
    
    private static boolean isForwardSwitch(Block block)
    {
        if(!block.hasSwitch())
            return false;
        
        if(block.getSwitchFrom().contains(block))
            return true;
        
        return false;
    }
    
    private static boolean isBackwardSwitch(Block block)
    {
        if(!block.hasSwitch())
            return false;
        
        if(block.getSwitchTo().contains(block))
            return true;
        
        return false;
    }
        
    
    private static double calcAuth(ArrayDeque<Block> route, Block start, Block end)
    {
        double auth = 0;
        
        ArrayDeque<Block> temp = route.clone();
        Block block = temp.poll();
        Block prev = null;
        
        // authority does not include our current block
        while(!block.equals(start))
        {
            prev = block;
            block = temp.poll(); 
        }
        
        while(!temp.isEmpty())
        {
            prev = block;
            block = temp.poll();
            
            // check for trains in the way
            if(block.isOccupied())
                return auth;
            // check switches are in correct position
            else if(block.hasSwitch())
                if(isForwardSwitch(block) && temp.peek() != null && (!block.getSwitchCurrTo().equals(temp.peek())))
                    return auth;
                else if(isBackwardSwitch(block) && prev != null && !block.getSwitchCurrFrom().equals(prev))
                    return auth;
                else
                    auth += block.getLength();
            else
                auth += block.getLength();
        }
        
        return auth;
    }
    
    private static Block getBlock(String line, char sec, int num)
    {
        ArrayDeque<Block> temp = blueline.clone();
        Block block;
        while(!temp.isEmpty())
        {
            block = temp.poll();
            if(block.getLine().equals("blue") && block.getSec() == sec && block.getNum() == num)
                return block;
        }
        
        return null;
    }
    
    private static void updateTrack()
    {
        // for all track block in blue line
        ArrayDeque<Block> temp = blueline.clone();
        Block bl;
        String str = "";
        
        while(!temp.isEmpty())
        {
            bl = temp.poll();
            str += bl.display();
            
        }
    }
    
    private static void updateTrains()
    {
        // for all trains in trains
    }
    
    protected void tellMBOSwitches()
    {
        // for all blocks in switches
        System.out.println("To MBO");
        ArrayDeque<Block> temp = switches.clone();
        Block sw;
        
        while(!temp.isEmpty())
        {
            sw = temp.poll();
            System.out.println("Switch in block "+sw.display());
            System.out.println("Current position: from "+sw.getSwitchCurrFrom().display()+", to "+sw.getSwitchCurrTo().display());
        }
    }
    
    private static void loadSched()
    {
        
    }
    
    private static void readInTrack()
    {
        
    }
    
    private static void sendSpeedAuth(Train train, double speed, double auth)
    {
        Block loc = train.getLoc();
        
        SwitchAndPos swpos = getSwitches(train.getRoute()).peek();
        Block sw = swpos.getBlock();
        Block from = swpos.getFrom();
        Block to = swpos.getTo();
        
        System.out.println("To Track Controller");
        System.out.println("Train "+train.getID()+" at location "+loc.display());
        System.out.println("Send speed = " + speed + ", authority = "+ auth);
        System.out.println("Next switch position, in block: "+sw.display()+" from: "+from.display()+" to: "+to.display());
    }
    
    private static void setSwitch(Block swBlock, Block from, Block to)
    {
        System.out.println("To Track Controller");
        System.out.println("Set switch at block "+swBlock.display());
        System.out.println("To configuration from = "+from.display()+", to = "+to.display());
    }
    
    private static void getTrackConUpdate()
    {
        
    }
    
    private static void getTicketInfo()
    {
        
    }
    
    private static void calcThroughput()
    {
        
    }
    
    private static ArrayDeque<Block> explored;
    
    private static ArrayDeque<Block> findRoute(Train train, Block start, Block dest)
    {
        ArrayDeque<Block> route = new ArrayDeque<Block>();
        //explored = new ArrayDeque<>();
        
        routes = new ArrayDeque<ArrayDeque<Block>>();
        
        
        findRouteRec(start, dest, new ArrayDeque<Block>());
        
        
        int minsize = Integer.MAX_VALUE;
        ArrayDeque<Block> curr;
        ArrayDeque<ArrayDeque<Block>> temp = routes.clone();
        while(!temp.isEmpty())
        {
            curr = temp.poll();
            if(curr.size() < minsize)
            {
                minsize = curr.size();
                route = curr;
            }
            
        }
       
        return route;
    }
    
    private static ArrayDeque<ArrayDeque<Block>> routes;
    
    private static void findRouteRec(Block start, Block dest, ArrayDeque<Block> route)
    {        
        route.add(start);
        //explored.add(start);
        
        if(route.size() > blueline.size())
            return;
        
        if(start.display().equals(dest.display()))
        {
            routes.add(route);
            return;
        }
        
        Block block = start;
        ArrayDeque<Block> neighbors = new ArrayDeque<Block>();
        
        if(block.hasSwitch() && isForwardSwitch(block))
        {
            neighbors = block.getSwitchTo();
        }
        else
        {
            neighbors.add(block.getNext());
        }
        
        while(!neighbors.isEmpty())
        {
            block = neighbors.poll();
            //if(!explored.contains(block))
                findRouteRec(block,dest,route.clone());
            
        }
        
    }
    
    
    
    
    private static ArrayDeque<SwitchAndPos> getSwitches(ArrayDeque<Block> route)
    {
        if (route.getFirst().equals(route.getLast()))
            return null;
        
        ArrayDeque<SwitchAndPos> switches = new ArrayDeque<SwitchAndPos>();
        
        SwitchAndPos swpos;
        Block from = null;
        Block to = null;
        
        ArrayDeque<Block> temp = route.clone();
        Block prev = null;
        Block curr = temp.poll();
        
        do
        {
            if(curr.hasSwitch())
            {
                if(curr.getSwitchFrom().contains(curr))
                {
                    from = curr;
                    if (temp.peek() != null)
                        to = temp.peek();
                    else
                        to = null;
                }
                else if(curr.getSwitchTo().contains(curr))
                {
                    to = curr;
                    if(prev!=null)
                        from = prev;
                    else
                        from = null;
                }
                
                if(from != null && to != null)
                {
                    swpos = new SwitchAndPos(curr,from,to);
                    switches.add(swpos);
                }
            }
            
            prev = curr;
            curr = temp.poll();
                
            
        }while(!temp.isEmpty());
        
        return switches;
    }
    
    private static class SwitchAndPos
    {
        private Block block;
        private Block from;
        private Block to;
        
        public SwitchAndPos()
        {
            block = null;
            from = null;
            to = null;
        }
        
        public SwitchAndPos(Block b, Block f, Block t)
        {
            block = b;
            from = f;
            to = t;
        }
        
        public Block getBlock()
        {
            return block;
        }
        
        public Block getFrom()
        {
            return from;
        }
        
        public Block getTo()
        {
            return to;
        }
    }
    
    private static class Train
    {
        private int ID;
        private Block location;
        private double authority;
        private double setpoint_speed;
        private ArrayDeque<Block> route;
        private Block dest;
        private double deadline;
        private int passengers;
        
        public Train()
        {
            ID = 0;
            location = null;
            authority = 0;
            setpoint_speed = 0;
            route = null;
            dest = null;
            deadline = 0;
            passengers = 0;
        }
        
        public Train(int id, Block loc)
        {
            ID = id;
            location = loc;
            authority = 0;
            setpoint_speed = 0;
            route = null;
            dest = null;
            deadline = 0;
            passengers = 0;
        
        }
        
        public Train(int id, Block loc, double auth, double speed, ArrayDeque<Block> r)
        {
            ID = id;
            location = loc;
            authority = auth;
            setpoint_speed = speed;
            route = r;
            dest = route.getLast();
            deadline = 0;
            passengers = 0;
        
        }
        
        private ArrayDeque<Block> getRoute()
        {
            return route;
        }
        
        private void setLoc(Block newLoc)
        {
            location = newLoc;
        }
        
        public Block getLoc()
        {
            return location;
        }
        
        public void setRoute(ArrayDeque<Block> r)
        {
            route = r;
        }
        
        public void setSpeed(double s)
        {
            setpoint_speed = s;
        }
        
        public void setAuth(double a)
        {
            authority = a;
        }
        
        public int getID()
        {
            return ID;
        }
    }
    
    private static class Block
    {
        private boolean yard;
        private String line;
        private char section;
        private int num;
        private boolean occupied;
        private double length;
        private Block next;
        private Block prev;
        private boolean sw;
        private int switchID;
        private boolean rrxing;
        private ArrayDeque<Block> sw_from;
        private ArrayDeque<Block> sw_to;
        private boolean rrxing_status;
        private boolean signal;
        private boolean broken;
        private Block sw_curr_from;
        private Block sw_curr_to;
        private boolean hasStation;
        private String station;
        
        public Block()
        {
            yard = false;
            line = null;
            section = 0;
            num = 0;
            occupied = false;
            length = 0;
            next = null;
            prev = null;
            sw = false;
            switchID = 0;
            rrxing = false;
            sw_from = null;
            sw_to = null;
            rrxing_status = false;
            signal = false;
            broken = false;
            sw_curr_from = null;
            sw_curr_to = null;
            hasStation = false;
            station = "";
        }
        
        public Block(String l, boolean isYard)
        {
            yard = isYard;
            line = l;
            section = '\0';
            num = 0;
            occupied = false;
            length = 0;
            next = null;
            prev = null;
            sw = false;
            switchID = 0;
            rrxing = false;
            sw_from = null;
            sw_to = null;
            rrxing_status = false;
            signal = false;
            broken = false;
            sw_curr_from = null;
            sw_curr_to = null;
            hasStation = false;
            station = "";
        }
        
        public Block(String l, char sec, int n, double len, Block nextb, Block prevb, boolean rr)
        {
            yard = false;
            line = l;
            section = sec;
            num = n;
            occupied = false;
            length = len;
            next = nextb;
            prev = prevb;
            sw = false;
            switchID = 0;
            rrxing = rr;
            sw_from = null;
            sw_to = null;
            rrxing_status = false;
            signal = false;
            broken = false;
            sw_curr_from = null;
            sw_curr_to = null;
            hasStation = false;
            station = "";
        }
        
        public Block(String l, char sec, int n, double len, Block nextb, Block prevb, boolean rr, int swID, ArrayDeque<Block> swf, ArrayDeque<Block> swt, Block currf, Block currt)
        {
            yard = false;
            line = l;
            section = sec;
            num = n;
            occupied = false;
            length = len;
            next = nextb;
            prev = prevb;
            sw = true;
            switchID = swID;
            rrxing = rr;
            sw_from = swf;
            sw_to = swt;
            rrxing_status = false;
            signal = false;
            broken = false;
            sw_curr_from = currf;
            sw_curr_to = currt;
            hasStation = false;
            station = "";
        }
        
        public Block(String l, char sec, int n, double len, Block nextb, Block prevb, boolean rr, boolean stat, String statID)
        {
            yard = false;
            line = l;
            section = sec;
            num = n;
            occupied = false;
            length = len;
            next = nextb;
            prev = prevb;
            sw = false;
            switchID = 0;
            rrxing = rr;
            sw_from = null;
            sw_to = null;
            rrxing_status = false;
            signal = false;
            broken = false;
            sw_curr_from = null;
            sw_curr_to = null;
            hasStation = stat;
            station = statID;
        }
        
        private boolean isOccupied()
        {
            return occupied;
        }
        
        private void setOccupied(boolean stat)
        {
            occupied = stat;
        }
        
        private double getLength()
        {
            return length;
        }
        
        private String display()
        {
            return line + " " + section + " " + num;
        }
        
        private String getLine()
        {
            return line;
        }
        
        private char getSec()
        {
            return section;
        }
        
        private int getNum()
        {
            return num;
        }
        
        private void setPrev(Block p)
        {
            prev = p;
        }
        
        private void setNext(Block n)
        {
            next = n;
        }
        
        private int getSwID()
        {
            return switchID;
        }
        
        private void setSwitch(int id, ArrayDeque<Block> from, ArrayDeque<Block> to)
        {
            sw = true;
            switchID = id;
            sw_from = from;
            sw_to = to;
            sw_curr_from = from.peekFirst();
            sw_curr_to = to.peekFirst();
        }
        
        private void setSwitchPos(Block currf, Block currt)
        {
            sw_curr_from = currf;
            sw_curr_to = currt;
        }
        
        private Block getNext()
        {
            return next;
        }
        
        private boolean hasSwitch()
        {
            return sw;
        }
        
        private ArrayDeque<Block> getSwitchTo()
        {
            return sw_to;
        }
        
        private ArrayDeque<Block> getSwitchFrom()
        {
            return sw_from;
        }
        
        private Block getSwitchCurrFrom()
        {
            return sw_curr_from;
        }
        
        private Block getSwitchCurrTo()
        {
            return sw_curr_to;
        }
        
        private ArrayDeque<Block> getSwitchPos()
        {
            if (sw == false)
                return null;
            
            ArrayDeque<Block> pos = new ArrayDeque<Block>();
            
            pos.add(sw_curr_from);
            pos.add(sw_curr_to);
            
            return pos;
        }
                
    }
}




