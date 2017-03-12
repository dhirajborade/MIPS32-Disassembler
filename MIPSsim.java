import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class MIPSsim {

	static String outputFile = "";
	
	public static void main(String[] inputArguments) {
		
		/*
		 * inputArguments[0] = Input File
		 * inputArguments[1] = Output File
		 * inputArguments[2] = Command "dis"
		 */
		
		//Get the Arguments in the form -> java MIPSsim inputFile outputFile Command
		
		if (inputArguments.length == 3 && inputArguments[2].equals("dis") && !inputArguments[1].equals(null) && !inputArguments[0].equals(null)) {
			try {
				
				MIPSsim instructionDecoder = new MIPSsim();
				PrintWriter writer = new PrintWriter(inputArguments[1], "UTF-8");
				writer.print(instructionDecoder.disassembleInstruction(inputArguments[0]));
				writer.close();
				
			} catch (IOException exceptionio) {
				
				exceptionio.printStackTrace();
			}
		}
		
		else {
			System.out.println("Please Enter Valid Arguments");
		}
	}
	
	public String disassembleInstruction(String inputFileArgs) throws IOException {
		
		FileInputStream inputBinaryFile = new FileInputStream(inputFileArgs);
		
        byte[] byteStream = new byte[4]; //to store the first 32 bits of instruction 
        int bytescounter = 4;
        boolean broken = false; //to check the 32 bits binary string of the instruction
        String[] instructionPart; //to store the binary in 6 groups of digits
        /*
        instruction[0] -> 1st 6 bits of data
        instruction[1] to instruction[5] -> each group has 5 bits of data
        instruction[5] -> last 6 bits of data       
        */
        int MemoryCounter = 600; //initialize the memory counter
        
        while(!broken){
        	
        	bytescounter = inputBinaryFile.read(byteStream);
        
        	if(bytescounter<4){
        		System.out.println("Word is Incomplete");
        		inputBinaryFile.close();
        		return "error";
        	}
        	
        	 /*
            instruction[0] -> 1st 6 bits of data
            instruction[1] to instruction[5] -> each group has 5 bits of data
            instruction[5] -> last 6 bits of data       
            */
        	
        	String byteString = "";
        	
        	for(int i=0;  i<4; i++){
    			
    			int unsigned = byteStream[i] & 0xFF;
    			String binaryString = Integer.toBinaryString(unsigned);
    			
    			/* To take care of the missing zero, which occurs due to direct conversion from Hex to Binary
    			*  Example: After converting "8" to binary, JAVA by default considers it as "1000", thus preceding 3 zeros are missed
    			*/
    			while(binaryString.length()<8)
    				binaryString = new StringBuilder(binaryString).insert(0, "0").toString();
    		
    			byteString += binaryString;
    		}
        	
			instructionPart = new String[6];
			instructionPart[0] = byteString.substring(0,6);
			instructionPart[1] = byteString.substring(6,11);
			instructionPart[2] = byteString.substring(11,16);
			instructionPart[3] = byteString.substring(16,21);
			instructionPart[4] = byteString.substring(21,26);
			instructionPart[5] = byteString.substring(26);
    	
        	for(int i = 0 ; i < 6 ; i++)
        		outputFile += instructionPart[i] + " ";
        	outputFile += MemoryCounter + " ";
        	MemoryCounter += 4;
      
        	broken = disassembleInstruction(instructionPart);
        	outputFile += "\n";
        			
       }
        
        //For printing the data section of the code
        
        while(true){
        	
        	bytescounter = inputBinaryFile.read(byteStream);
        	if(bytescounter != 4)
        		break;
        	
        	String wordString = "";
        	
        	for(int i = 0 ; i < 4 ; i++){
    			
    			int unsigned = byteStream[i] & 0xFF;
    			String binaryString = Integer.toBinaryString(unsigned);
    			

    			/* To take care of the missing zero, which occurs due to direct conversion from Hex to Binary
    			*  Example: After converting "8" to binary, JAVA by default considers it as "1000", thus preceding 3 zeros are missed
    			*/
    			
    			while(binaryString.length()<8)
    				binaryString = new StringBuilder(binaryString).insert(0, "0").toString();
    		
    			wordString += binaryString;
    		}
        	
        	outputFile += wordString + " " + MemoryCounter + " " + Integer.parseInt(wordString, 2);
        	MemoryCounter += 4;
        	outputFile += "\n";
        	
        }
        inputBinaryFile.close();
       return (outputFile);
	}
	
	//Case Switch for decoding the Instruction Set as per the Opcode
	
	public static boolean disassembleInstruction(String[] instructionPart){
		boolean broken = false;
		int sourceRegisterString, targetRegisterString, destinationRegisterString = 0;
		String immediateDataString;
		String opcodeDataString = instructionPart[0];
		switch (opcodeDataString){
		
        case "000000":
        	
        	if(instructionPart[5].equals("001101")){
        		broken = true;
        		outputFile += "BREAK";
        	}
        	
        	if(instructionPart[5].equals("000000") && instructionPart[2].equals("00000")){
        		destinationRegisterString = Integer.parseInt(instructionPart[3],2);
        		if(destinationRegisterString == 0)
        			outputFile += "NOP";
        	}
        	
        	if(instructionPart[5].equals("100001")){
        		outputFile += "ADDU ";
        		sourceRegisterString = Integer.parseInt(instructionPart[1], 2);
            	targetRegisterString = Integer.parseInt(instructionPart[2], 2);
            	destinationRegisterString = Integer.parseInt(instructionPart[3], 2);
            	outputFile += "R" + destinationRegisterString + ", R" + sourceRegisterString + ", R" + targetRegisterString;
        	}
        	
        	if(instructionPart[5].equals("100000")){
        		outputFile += "ADD ";
        		sourceRegisterString = Integer.parseInt(instructionPart[1], 2);
            	targetRegisterString = Integer.parseInt(instructionPart[2], 2);
            	destinationRegisterString = Integer.parseInt(instructionPart[3],2);
            	outputFile += "R" + destinationRegisterString + ", R" + sourceRegisterString + ", R" + targetRegisterString;
        	}
        	
        	if(instructionPart[5].equals("100100")){
        		outputFile += "AND ";
        		sourceRegisterString = Integer.parseInt(instructionPart[1], 2);
            	targetRegisterString = Integer.parseInt(instructionPart[2], 2);
            	destinationRegisterString = Integer.parseInt(instructionPart[3],2);
            	outputFile += "R" + destinationRegisterString + ", R" + sourceRegisterString + ", R" + targetRegisterString;
        	}
        	
        	if(instructionPart[5].equals("100101")){
        		outputFile += "OR ";
        		sourceRegisterString = Integer.parseInt(instructionPart[1], 2);
            	targetRegisterString = Integer.parseInt(instructionPart[2], 2);
            	destinationRegisterString = Integer.parseInt(instructionPart[3],2);
            	outputFile += "R" + destinationRegisterString + ", R" + sourceRegisterString + ", R" + targetRegisterString;
        	}
        	
        	if(instructionPart[5].equals("100110")){
        		outputFile += "XOR ";
        		sourceRegisterString = Integer.parseInt(instructionPart[1], 2);
            	targetRegisterString = Integer.parseInt(instructionPart[2], 2);
            	destinationRegisterString = Integer.parseInt(instructionPart[3],2);
            	outputFile += "R" + destinationRegisterString + ", R" + sourceRegisterString + ", R" + targetRegisterString;
        	}
        	
        	if(instructionPart[5].equals("100111")){
        		outputFile += "NOR ";
        		sourceRegisterString = Integer.parseInt(instructionPart[1], 2);
            	targetRegisterString = Integer.parseInt(instructionPart[2], 2);
            	destinationRegisterString = Integer.parseInt(instructionPart[3],2);
            	outputFile += "R" + destinationRegisterString + ", R" + sourceRegisterString + ", R" + targetRegisterString;
        	}
        	
        	if(instructionPart[5].equals("100010")){
        		outputFile += "SUB ";
        		sourceRegisterString = Integer.parseInt(instructionPart[1], 2);
            	targetRegisterString = Integer.parseInt(instructionPart[2], 2);
            	destinationRegisterString = Integer.parseInt(instructionPart[3],2);
            	outputFile += "R" + destinationRegisterString + ", R" + sourceRegisterString + ", R" + targetRegisterString;
        	}
        	
        	if(instructionPart[5].equals("100011")){
        		outputFile += "SUBU ";
        		sourceRegisterString = Integer.parseInt(instructionPart[1], 2);
            	targetRegisterString = Integer.parseInt(instructionPart[2], 2);
            	destinationRegisterString = Integer.parseInt(instructionPart[3],2);
            	outputFile += "R" + destinationRegisterString + ", R" + sourceRegisterString + ", R" + targetRegisterString;
        	}
        		
        	if(instructionPart[5].equals("000000")){
        		sourceRegisterString = Integer.parseInt(instructionPart[4], 2);
            	targetRegisterString = Integer.parseInt(instructionPart[2], 2);
            	destinationRegisterString = Integer.parseInt(instructionPart[3],2);
            	if(targetRegisterString != 0){
            		outputFile += "SLL "; 
            		outputFile += "R" + destinationRegisterString + ", R" + targetRegisterString + ", #" + sourceRegisterString;
            	}
        	}
        	
        	if(instructionPart[5].equals("000010")){
        		outputFile += "SRL ";
        		sourceRegisterString = Integer.parseInt(instructionPart[4], 2);
            	targetRegisterString = Integer.parseInt(instructionPart[2], 2);
            	destinationRegisterString = Integer.parseInt(instructionPart[3],2);
            	outputFile += "R" + destinationRegisterString + ", R" + targetRegisterString + ", #" + sourceRegisterString;
        	}
        	
        	if(instructionPart[5].equals("000011")){
        		outputFile += "SRA ";
        		sourceRegisterString = Integer.parseInt(instructionPart[4], 2);
            	targetRegisterString = Integer.parseInt(instructionPart[2], 2);
            	destinationRegisterString = Integer.parseInt(instructionPart[3],2);
            	outputFile += "R" + destinationRegisterString + ", R" + targetRegisterString + ", #" + sourceRegisterString;
        	}
        	
        	if(instructionPart[5].equals("101010")){
        		outputFile += "SLT ";
        		sourceRegisterString = Integer.parseInt(instructionPart[1], 2);
            	targetRegisterString = Integer.parseInt(instructionPart[2], 2);
            	destinationRegisterString = Integer.parseInt(instructionPart[3],2);
            	outputFile += "R" + destinationRegisterString + ", R" + sourceRegisterString + ", R" + targetRegisterString;
        	}
        	
        	if(instructionPart[5].equals("101011")){
        		outputFile += "SLTU ";
        		sourceRegisterString = Integer.parseInt(instructionPart[1], 2);
            	targetRegisterString = Integer.parseInt(instructionPart[2], 2);
            	destinationRegisterString = Integer.parseInt(instructionPart[3],2);
            	outputFile += "R" + destinationRegisterString + ", R" + sourceRegisterString + ", R" + targetRegisterString;
        	}
        	
        	break;
        	
        case "001000":
        	
        	outputFile += "ADDI ";
        	sourceRegisterString = Integer.parseInt(instructionPart[1], 2);
        	targetRegisterString = Integer.parseInt(instructionPart[2], 2);
        	immediateDataString = instructionPart[3]+instructionPart[4]+instructionPart[5];
        	destinationRegisterString = Integer.parseInt(immediateDataString, 2);
        	if(immediateDataString.startsWith("1"))
        		destinationRegisterString = destinationRegisterString-65536;
        	outputFile += "R"+ targetRegisterString + ", R" + sourceRegisterString + ", #" + destinationRegisterString;
        	
        	break;
        	
        case "001010":
        	
        	outputFile += "SLTI ";
        	sourceRegisterString = Integer.parseInt(instructionPart[1], 2);
        	targetRegisterString = Integer.parseInt(instructionPart[2], 2);
        	immediateDataString = instructionPart[3] + instructionPart[4] + instructionPart[5];
        	destinationRegisterString = Integer.parseInt(immediateDataString, 2);
        	if(immediateDataString.startsWith("1"))
        		destinationRegisterString = destinationRegisterString - 65536; //to negate the overflow
        	outputFile += "R" + targetRegisterString + ", R" + sourceRegisterString + ", #" + destinationRegisterString;
        	
        	break;
        	
        case "001001":
        	
        	outputFile += "ADDIU ";
			sourceRegisterString = Integer.parseInt(instructionPart[1], 2);
			targetRegisterString = Integer.parseInt(instructionPart[2], 2);
			immediateDataString = instructionPart[3] + instructionPart[4] + instructionPart[5];
			destinationRegisterString = Integer.parseInt(immediateDataString, 2);
			if(immediateDataString.startsWith("1"))
				destinationRegisterString = destinationRegisterString - 65536; //to negate the overflow
			outputFile += "R" + targetRegisterString + ", R" + sourceRegisterString + ", #" + destinationRegisterString;
			
			break;
			
        case "000100":
        	
        	outputFile += "BEQ ";
        	sourceRegisterString = Integer.parseInt(instructionPart[1], 2);
        	targetRegisterString = Integer.parseInt(instructionPart[2], 2);
        	immediateDataString = instructionPart[3]+instructionPart[4]+instructionPart[5];
        	immediateDataString += "00";
        	outputFile += "R" + sourceRegisterString + ", R" + targetRegisterString + ", #" + Integer.parseInt(immediateDataString, 2);
        	
        	break;
        
        case "000101": 
        	
        	outputFile += "BNE ";
			sourceRegisterString = Integer.parseInt(instructionPart[1], 2);
			targetRegisterString = Integer.parseInt(instructionPart[2], 2);
			immediateDataString = instructionPart[3]+instructionPart[4]+instructionPart[5];
			immediateDataString += "00";
			outputFile += "R" + sourceRegisterString + ", R" + targetRegisterString + ", #" + Integer.parseInt(immediateDataString, 2);
			
			break;
			
        case "000001":
        	
        	sourceRegisterString = Integer.parseInt(instructionPart[1], 2);
			targetRegisterString = Integer.parseInt(instructionPart[2], 2);
			immediateDataString = instructionPart[3] + instructionPart[4] + instructionPart[5];
			if(targetRegisterString == 1){
				immediateDataString += "00";
				outputFile += "BGEZ ";
				outputFile += "R" + sourceRegisterString + ", #" + Integer.parseInt(immediateDataString, 2);
			}
			if(targetRegisterString == 0){
				immediateDataString += "00";
				outputFile += "BLTZ ";
				outputFile += "R" + sourceRegisterString + ", #" + Integer.parseInt(immediateDataString, 2);
			}
			
			break;
        
        case "000110": 
    		
			sourceRegisterString = Integer.parseInt(instructionPart[1], 2);
			targetRegisterString = Integer.parseInt(instructionPart[2], 2);
			immediateDataString = instructionPart[3]+instructionPart[4]+instructionPart[5];
			immediateDataString += "00";
			outputFile += "BLEZ ";
			if(immediateDataString.startsWith("0"))
				outputFile += "R" + sourceRegisterString + ", #" + Integer.parseInt(immediateDataString, 2);
			else
				outputFile += "R" + sourceRegisterString + ", #" + (Integer.parseInt(immediateDataString, 2) - 262144);
			
			break;
		
        case "000111": 
    		
			sourceRegisterString = Integer.parseInt(instructionPart[1], 2);
			targetRegisterString = Integer.parseInt(instructionPart[2], 2);
			immediateDataString = instructionPart[3]+instructionPart[4]+instructionPart[5];
			immediateDataString += "00";
			outputFile += "BGTZ ";
			outputFile += "R" + sourceRegisterString + ", #" + Integer.parseInt(immediateDataString, 2);
			
			break;
			
        case "000010":
        	
        	outputFile += "J #";
			sourceRegisterString = Integer.parseInt(instructionPart[1], 2);
			targetRegisterString = Integer.parseInt(instructionPart[2], 2);
			immediateDataString = instructionPart[1] + instructionPart[2] + instructionPart[3] + instructionPart[4] + instructionPart[5];
			immediateDataString += "00";
			outputFile += Integer.parseInt(immediateDataString, 2);
			
			break;
			
        case "101011":
        	
        	outputFile += "SW ";
        	sourceRegisterString = Integer.parseInt(instructionPart[1], 2);
        	targetRegisterString = Integer.parseInt(instructionPart[2], 2);
        	immediateDataString = instructionPart[3] + instructionPart[4] + instructionPart[5];
        	outputFile += "R" + targetRegisterString + ", " + Integer.parseInt(immediateDataString, 2) + "(R" + sourceRegisterString + ")";
        	
        	break;
        	
        case "100011":
        	
        	outputFile += "LW ";
        	sourceRegisterString = Integer.parseInt(instructionPart[1], 2);
        	targetRegisterString = Integer.parseInt(instructionPart[2], 2);
        	immediateDataString = instructionPart[3] + instructionPart[4] + instructionPart[5];
        	outputFile += "R" + targetRegisterString + ", " + Integer.parseInt(immediateDataString, 2) + "(R" + sourceRegisterString + ")";
        	
        	break;
        	
        default: 
        	
        	System.out.println("Unknown Opcode");
        	
        	break;
		}
		
		if(instructionPart[0].equals("000000") && instructionPart[5].equals("001101"))
			broken = true;
			
		return broken;
	}
}