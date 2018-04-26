package train_controller;

public class Vitality
{
    TrainController controller;
    Control ctrl1;
    Control ctrl2;
    double pow1 = 0;
    double pow2 = 0;
    double brake1 = 0;
    double brake2 = 0;
    
    public Vitality(TrainController tc)
    {
        controller = tc;
        ctrl1 = new Control(tc);
        ctrl2 = new Control(tc);
    }
    
    public double[] decision(double setSpeed)
    {
        double [] ret = {-1,-1};
        while(true)  
        {    
            ctrl1.updateLoop(setSpeed);
            ctrl2.updateLoop(setSpeed);
            
            pow1 = ctrl1.getCommands()[0];
            pow2 = ctrl2.getCommands()[0];
            brake1 = ctrl1.getCommands()[1];
            brake2 = ctrl2.getCommands()[1];
            
            if(pow1 == pow2 & brake1 == brake2)
            {
                ret[0] = pow1;
                ret[1] = brake1;
                return ret;
            }
            else
            {
                ctrl1.restart();
                ctrl2.restart();
            }
        }    
    } 
}
