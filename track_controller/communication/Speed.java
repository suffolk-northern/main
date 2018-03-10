/*

 * Ryan Matthews

 *

 * Inter-module communication

 */



package track_controller.communication;



// Speed at which a train is allowed to move



public class Speed

{

	public static final int VALUE_LENGTH = 10;



	// Units: miles per hour

	//

	// Representation: unary, e.g,

	//

	//   [ 0, 0, 0, 0, 0, ... 0 ] = 0

	//   [ 1, 0, 0, 0, 0, ... 0 ] = 1

	//   [ 1, 1, 0, 0, 0, ... 0 ] = 2

	//   [ 1, 1, 1, 0, 0, ... 0 ] = 3

	//   ...

	//

	// Mapping: multiples of 5, e.g.,

	//

	//   unary 0 ->  0 mph

	//   unary 1 ->  5 mph

	//   unary 2 -> 10 mph

	//   unary 3 -> 15 mph

	//   ...

	//

	// Length specified by VALUE_LENGTH.

	public final boolean[] value;



	// Returns a boolean array of size VALUE_LENGTH, with the first length

	// elements set to true, and the remaining elements set to false.

	//

	// Makes it easier to convert an integer to a Speed.

	//

	// Throws IllegalArgumentException if length not in range

	// [0, VALUE_LENGTH].

	public static boolean[] run(int length)

	{

		if (length < 0 || length > VALUE_LENGTH)

			throw new IllegalArgumentException("length bounds");



		boolean[] array = new boolean[VALUE_LENGTH];



		for (int i = 0; i < length; ++i)

			array[i] = true;



		return array;

	}



	// Constructs a Speed as a copy of another.

	public Speed(Speed other)

	{

		this.value = other.value.clone();

	}



	// Initializes all members.

	//

	// Throws IllegalArgumentException if any member initializer is

	// invalid.

	public Speed(boolean[] value)

		throws IllegalArgumentException

	{

		if (value.length != VALUE_LENGTH)

			throw new IllegalArgumentException("value length");



		this.value = value;

	}
        
        //Determines whether the speed of a train on a certain block exceeds
        //the speed limit of the track block which it is on.
        
        //Throws IllegalArgumentExcepiton if speedLimit is outside allowed range of > 0 mph and < VALUE_LENGTH*5.
        
        public boolean SpeedExceeded(int speedLimit)
               
                throws IllegalArgumentException
                
        {
            if(speedLimit <= 0 || speedLimit > VALUE_LENGTH*5)
            {
                throw new IllegalArgumentException("Speed Limit outside allowed range");
            }
            if((speedLimit/5) * 5 != VALUE_LENGTH*5)
            {
                for(int i = 0; i <= speedLimit/5; i++)
                {
                    if(!value[i])
                    {
                        return false;
                    }
                }
            }
            else
            {
                for(int i = 0; i < speedLimit/5; i++)
                {
                    if(!value[i])
                    {
                        return false;
                    }
                }
            }
            return true;      
            
        }

}  
