package train_controller;

import train_model.communication.ControllerLink;


public class Control
{
    TrainController controller;
    ControllerLink link;
    double [] queue = new double[5000];
    int queueInsert = 0;
    int queueFill = 0;
    double outgoingPowerCmd;
    double outgoingBrakeCmd;
    double currentSpeed;
    double mass = 50 * 907.185;
    double MAXPOWER = 120000;
    double loopPower = 0;
    double MAX_SDECEL = 1.2;
    
    public Control(TrainController tc)
    {
        controller = tc;
        link = tc.getLink();
    }
    
    public void restart()
    {
        queue = new double[5000];
        queueInsert = 0;
        queueFill = 0;
    }
    
    public double[] getCommands()
    {
        double [] cmds = {outgoingPowerCmd, outgoingBrakeCmd};
        return cmds;
    }
    
    public void updateLoop(double setSpeed)
    {
        // Update power
        currentSpeed = link.speed();
        double error = setSpeed - currentSpeed;
	queue[queueInsert++] = error;
	if(queueInsert % queue.length == 0)
            queueInsert = 0;
        double averageError = 0;
	for(int i=0; i < queueFill; i++)
	{
            averageError += queue[i] / queueFill;            
	}
	if(++queueFill > queue.length)
            queueFill = queue.length;
        
        loopPower = controller.getKs()[0] * error * currentSpeed * mass;
        loopPower += controller.getKs()[1] * averageError * currentSpeed * mass;
        outgoingPowerCmd = loopPower / MAXPOWER;
        double preOutgoingPowerCmd = outgoingPowerCmd;
        if(preOutgoingPowerCmd == 0)
            preOutgoingPowerCmd = 0.01;
        if(currentSpeed == 0)
            outgoingPowerCmd = 0.01;
        if(outgoingPowerCmd > 1)
            outgoingPowerCmd = 1;
        if(outgoingPowerCmd < 0)
            outgoingPowerCmd = 0;

        // Update brake
        outgoingBrakeCmd = -1 * preOutgoingPowerCmd * MAXPOWER / (mass * currentSpeed * MAX_SDECEL);
        if(outgoingBrakeCmd > 1)
            outgoingBrakeCmd = 1;
        if(outgoingBrakeCmd < 0)
            outgoingBrakeCmd = 0;
    }
}
