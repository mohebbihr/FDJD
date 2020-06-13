import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.MappedByteBuffer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.*;
import java.util.Scanner;

// The reference file which contains both database (_DB) and binary finger print files. 
public class FastaRefFile extends WindowsFile {
	
	static final int SIZE = 4 * 1024; 
	static byte[] buffer = new byte[SIZE];
	static byte[] printBuffer = new byte[SIZE];
	long wgChunkSize = 20000000; 
	int fileCount = 0;		
	int jumpStep = 3; // if the jumpStep is k, we need to put k-1 here

	public FastaRefFile(String inputPath,String OutputFilePath,String OutputDbPath,String discordantPairs,short windowSize,short min_mapq,boolean isReference,short permuteLength, long wgChunkSize, int jumpStep) throws Exception{
		
		super(inputPath,OutputFilePath,OutputDbPath,discordantPairs,windowSize,min_mapq,isReference,permuteLength);
		this.wgChunkSize = wgChunkSize;		
		this.jumpStep = jumpStep - 1;
	}
	
	@Override public void run(){
		//System.out.println("OutputFilePath: " + OutputFilePath);
		//System.out.println("OutputDbPath: " + OutputDbPath);

		if (OutputFilePath != null && OutputDbPath != null ){
		File outputFile = new File(OutputFilePath + "_" + fileCount);
		File outputDatabase = new File(OutputDbPath);
		try {			
			fos = new FileOutputStream(outputFile);
			System.out.println("output file is created");
			printerDatabase = new PrintWriter(outputDatabase);
			System.out.println("output database file is created!!");
			CreateWindows(inputPath,discordantPairs,windowSize);
			fos.close();
			printerDatabase.close();
			System.out.println("Files successfully created!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("End of run.");
		}
	}
	
	@Override public void CreateWindows(String inputPath,String discordantPairs,short windowSize) throws Exception{
	
		System.out.println("createwindows started!");	
		long pos =0L;
		long referenceStartPos = 1L;
		short posWindow = 0;
		String window = "";
		char currentChar;
		boolean allNwindow = true;
		long fileLen = 0L;
		boolean posWindowChange = false;
		String readName = ""; // 		
		String refName = ""; // ex: char1, char2, ....

		// reading the input fasta file (.fa)
		try(Scanner sc = new Scanner(new File(inputPath))){
			while (sc.hasNextLine()){
				String line = sc.nextLine().trim();
				if(line.charAt(0) == '>'){
					refName = line.substring(1, line.indexOf(" "));
					System.out.println("refName: " + refName);
					referenceStartPos = 0;		
				}else {
					referenceStartPos += 1;
					// if the line is less than windowSize, then we need to padd it with what? 
					if ( line.length() < windowSize && window == ""){
						line = line + Utils.repeat(windowSize - line.length(), "N");
					}
					// process line, char by char
					for (int charidx = 0; charidx < line.length(); charidx ++ ){
						
						currentChar = line.charAt(charidx);
						int charType = Character.getType(currentChar);
						if( charType == Character.LOWERCASE_LETTER)
							currentChar = Character.toUpperCase(currentChar);
						//if( currentChar != '\n' && currentChar != 'N' ){
						if(currentChar != '\n'){
							// we replace ambiguous codes with its equevalent
							// ref: http://www.boekhoff.info/?pid=data&dat=fasta-codes
							switch(currentChar){
                                                	case 'U' : 
								currentChar = 'T';
								break;
							case 'Y' :
                                                        	currentChar = 'T';
                                                        	break;
                                                	case 'R':
                                                        	currentChar = 'G';
                                                        	break;
                                                	case 'K':
                                                        	currentChar = 'T';
                                                        	break;
                                                	case 'M':
                                                        	currentChar = 'A';
                                                        	break;
                                                	case 'S':
                                                        	currentChar = 'C';
                                                        	break;
                                                	case 'W':
                                                        	currentChar = 'A';
                                                        	break;
                                                	case 'B':
                                                        	currentChar = 'C';
                                                        	break;
                                                	case 'V':
                                                        	currentChar = 'G';
                                                        	break;
                                                	case 'H':
                                                        	currentChar = 'T';
                                                        	break;
							default :
								if (currentChar != 'A' && currentChar != 'G' && currentChar != 'C' && currentChar != 'T' )
									currentChar = 'N';
								break;	
								
                                        		}// end of switch
							window = window + currentChar;
							// if we have a window with windowSize characters in it
							if ( window.length() == windowSize){
								if(nValuesPrinted == (fileCount + 1) * wgChunkSize){
									// create a new output file and write content to it
									fos.flush();
									fos.close();
									fileCount ++;
									File outputFile = new File(OutputFilePath + "_" + fileCount);
									try {
										fos = new FileOutputStream(outputFile);
									} catch (FileNotFoundException e) {
										e.printStackTrace();
									}
								}
								//System.out.println("w: " + window);
								// convert window into binary format and save it.
								
								//System.out.println("w len: " + window.length() + " ,w: " + window); 
								// in case of a reference file, we need to pass empty string as some of the arguments.
								SeqToBinaryWindow(window,windowSize,referenceStartPos,(short) 0,0,readName,refName,"",isReference,"", "");
                                                        
                                                        	nValuesPrinted ++;
								// empty the window
								window = "";
							} // end of windowSize if
                                        	} // end of compare with N if statement.	
					
					}// end of for	
					fos.flush();
                                	printerDatabase.flush();		
				}	
			}
		}
                System.out.println("Windows printed: " + nValuesPrinted);

	}
}

