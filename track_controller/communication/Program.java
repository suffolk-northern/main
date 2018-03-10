/*

 * Ryan Matthews

 *

 * Inter-module communication

 */



package track_controller.communication;



import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import track_controller.TrackController;

import track_controller.communication.Branch;



// Track controller program

//

// Simulates a "file" that a programmer loads onto the device.



public class Program

{

	// Common, left, and right branch lengths

	//

	// Representation: unary "tally marks"

	//

	// Each array SHOULD be TrackController.MAX_BRANCH_SIZE in length. The

	// "syntax" check for that occurs at load time, NOT at the time of

	// creation of this object.

	//

	// The integer length is represented as a run of 1s followed by a run

	// of 0s, e.g.,

	//

	//   length 2 = [ 1, 1, 0, 0, 0, 0, 0, 0, ... 0 ]

	//   length 5 = [ 1, 1, 1, 1, 1, 0, 0, 0, ... 0 ]

	//

	// Length 0 is illegal and results in an error at load time.

	public boolean[] commonBranchLength;

	public boolean[]   leftBranchLength;

	public boolean[]  rightBranchLength;
        
        
        //Common. Left, and Right branch block numbers
        //
        //Representation: Binary block number values
        //
        //Each 2D array should have an inner dimension of MAX_BINARY_BLOCK_ADDRESS_LENGTH in length.
        //Also the outer dimension should be MAX_BRANCH_SIZE in length. The
        // "syntax" check for these occurs at load time, NOT at the time of
        // creation of this object.
        //
        //The integer block number is represented as follows wiht the 0th array block being the LSB:
        //
        //
        // Block Number 7 = [1, 1, 1, 0, 0, 0, 0, 0, 0, 0]
        // Block number 19 = [1, 1, 0, 0, 1, 0, 0, 0, 0, 0]
        //
        //An array of the binary block numbers comprises a 2D boolean array of block numbers for a certain branch.
        
        public boolean[][] commonBranchBlocks;
        public boolean[][]   leftBranchBlocks;
        public boolean[][]  rightBranchBlocks;


	// Returns a boolean array of size TrackController.MAX_BRANCH_SIZE,

	// with the first length elements set to true, and the remaining

	// elements set to false.

	//

	// Makes it easier to write a Program.

	//

	// Throws IllegalArgumentException if length not in range

	// [0, TrackController.MAX_BRANCH_SIZE].

	public static boolean[] run(int length)

		throws IllegalArgumentException

	{

		if (length < 0 || length > TrackController.MAX_BRANCH_SIZE)

			throw new IllegalArgumentException("length bounds");



		boolean[] array = new boolean[TrackController.MAX_BRANCH_SIZE];



		for (int i = 0; i < length; ++i)

			array[i] = true;



		return array;

	}
        
        //Returns a 2D boolean array with inner dimension of length MAX_BINARY_BLOCK_ADDRESS_LENGTH
        //and outer dimension MAX_BRANCH_SIZE with an array of binary representations of block numbers
        //with LSB being the 0th index.
        //
        //Throws FileNotFoundException if file is not found or IllegalArgumentException if one of the
        //values in the file is not 0 or 1.
        
        public static boolean[][] runBlocks(int length, String branchFile) throws FileNotFoundException, IllegalArgumentException
        {
            Scanner bF = new Scanner(new File(branchFile));
            boolean[][] branchBlocks = new boolean[TrackController.MAX_BRANCH_SIZE][TrackController.MAX_BINARY_BLOCK_ADDRESS_LENGTH];
            
            for(int i = 0; i < TrackController.MAX_BRANCH_SIZE ; i++)
            {
                if (i < length) {
                    String[] line = bF.nextLine().split(",");
                    for (int j = 0; j < TrackController.MAX_BINARY_BLOCK_ADDRESS_LENGTH; j++) {
                        switch (Integer.parseInt(line[j])) {
                            case 0:
                                branchBlocks[i][j] = false;
                                break;
                            case 1:
                                branchBlocks[i][j] = true;
                                break;
                            default:
                                throw new IllegalArgumentException("Bad Block File Value: Not 0 or 1");
                        }
                    }
                }
                else {
                    for (int j = 0; j < TrackController.MAX_BINARY_BLOCK_ADDRESS_LENGTH; j++) {
                        branchBlocks[i][j] = false;
                    }
                }
                
            }
            return branchBlocks;
        }



	// Returns the number of leading ones in a boolean array.

	//

	// Throws IllegalArgumentException if array is not length

	// TrackController.MAX_BRANCH_SIZE, or if it does not contain a run

	// leading ones (of any length) followed by all zeros.

	//

	// Makes it easier to parse a Program.

	public static int parseRun(boolean[] array)

		throws IllegalArgumentException

	{

		if (array.length != TrackController.MAX_BRANCH_SIZE)

			throw new IllegalArgumentException("length");



		int i = 0;



		for (; i < array.length; ++i)

			if (array[i] == false)

				break;



		for (int j = i; j < array.length; ++j)

			if (array[j] == true)

				throw new IllegalArgumentException("format");



		return i;

	}
        
        //Returns all the block numbers in a certain branch path
        //
        //Throws IllegalArgumentException if array is not inner dimension
        //TrackController.MAX_BINARY_BLOCK_ADDRESS_LENGTH and outer dimension
        //TrackController.MAX_BRANCH_SIZE
        //
        //Makes it easier to parse Block Numbers for occupancy and other purposes
        
        public static int[] parseRunBlocks(boolean[][] array)
        {
            if(array.length != TrackController.MAX_BRANCH_SIZE && array[0].length != TrackController.MAX_BINARY_BLOCK_ADDRESS_LENGTH)
            {
                throw new IllegalArgumentException("Block Array Dimensions incorrect");
                
            }
            int[] blockNumbers = new int[TrackController.MAX_BRANCH_SIZE];
            
            for(int i = 0; i < TrackController.MAX_BRANCH_SIZE; i++) {
                int blockNumber = 0;
                for(int j = 0; j < TrackController.MAX_BINARY_BLOCK_ADDRESS_LENGTH; j++) {
                    if(array[i][j])
                    {
                        blockNumber += Math.pow(2, j);
                    }
                }
                blockNumbers[i] = blockNumber;
            }
            return blockNumbers;
        }



	// Initializes all members.

	//

	// Throws IllegalArgumentException if any member is null.

	public Program(boolean[] commonBranchLength,

	               boolean[]   leftBranchLength,

	               boolean[]  rightBranchLength,
                       
                       String      commonBranchFile,
                       
                       String        leftBranchFile,
                       
                       String       rightBranchFile)

		throws IllegalArgumentException, FileNotFoundException

	{

		if (commonBranchLength == null ||

		      leftBranchLength == null ||

		     rightBranchLength == null)

			throw new IllegalArgumentException("member is null");



		this.commonBranchLength = commonBranchLength;

		this.  leftBranchLength =   leftBranchLength;

		this. rightBranchLength =  rightBranchLength;
                
                this.commonBranchBlocks =  runBlocks(parseRun(commonBranchLength), commonBranchFile);
                this.  leftBranchBlocks =      runBlocks(parseRun(leftBranchLength), leftBranchFile);
                this. rightBranchBlocks =    runBlocks(parseRun(rightBranchLength), rightBranchFile);

	}

}
