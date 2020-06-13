/*
 * The base calss for the QueryFile and DataFile classes. This class contains method for creating the fingerprint files for QueryFile (Input bam file)
 * and the DataFile(Chromosem file)
 * 
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class WindowsFile {
	
	short permuteLength = 0;
	//PrintWriter printerFile = null; // because other parts of the code, use text files, we using text files too
	FileOutputStream fos = null; // we are using binary files in future to reduce the file sizes
	PrintWriter printerDatabase = null;
	// The RNA query data input contains chimeric data which shows fusion between two right and left references, like chr1 and chr4 
	short min_mapq = 10; //default minimum
	short windowSize;
	long countWindowId=0;
	long nWindowsPrinted = 0L;
	long nValuesPrinted = 0L;
	boolean isReference;
	byte[] subseqBytes; // the reference to the subsequence byte array
	String OutputFilePath = null;
	String OutputDbPath = null;
	String inputPath = null ;
	String discordantPairs = null;
	
	public WindowsFile(String inputPath,String OutputFilePath,String OutputDbPath,String discordantPairs,short windowSize,short min_mapq,boolean isReference,short permuteLength) throws Exception{
		this.inputPath = inputPath;
		this.OutputFilePath = OutputFilePath;
                this.OutputDbPath = OutputDbPath;
                this.discordantPairs = discordantPairs;
                this.windowSize = windowSize;
		this.min_mapq = min_mapq;
		this.isReference = isReference;
		this.permuteLength = permuteLength;
	}
	
	public void run(){		
		File outputFile = new File(OutputFilePath);
		File outputDatabase = new File(OutputDbPath);
		try {			
			fos = new FileOutputStream(outputFile);
			printerDatabase = new PrintWriter(outputDatabase);
			CreateWindows(inputPath,discordantPairs,windowSize);
			fos.close();
			printerDatabase.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Files successfully created!!");
	}
	
	public void CreateWindows(String inputPath, String discordantPairs, short windowSize)throws Exception {
	
		
	}
	
	// This method convert the sequence into binary finger print and write down necessary info into the DB file.
	public void SeqToBinaryWindow(String subseq, short windowSize, long referenceStartPos, short readStartPos, long recordId, String readName, String referenceName, String readString, boolean isReference, String rightRef, String leftRef){
			
		if (isReference)
			//printerDatabase.println(countWindowId + "\t" + referenceChr + "\t" + referenceStartPos);
			printerDatabase.println(countWindowId + "\t" + referenceName + "\t" + referenceStartPos);
		else
			printerDatabase.println(countWindowId + "\t" + recordId + "\t" + readName + "\t" + readStartPos + "\t" + referenceName + "\t" + referenceStartPos + "\t" + readString + "\t" + rightRef + "\t" + leftRef );
	
		
		subseqBytes = Utils.binaryCodingJaccardWindow(subseq,permuteLength,isReference);
		
		try{
			fos.write(subseqBytes); // writing to a binary file
		}catch(IOException e){
			System.out.println(e.toString());
		}
		countWindowId++;
		nWindowsPrinted++;
		
	}
	
	/*public void SeqToWindow(String subseq, short windowSize, long referenceStartPos, short readStartPos, long recordId, String readName, String referenceName, String readString, boolean isReference){
		
		boolean	print = true;
		
		if (print){
			if (isReference)
				printerDatabase.println(countWindowId + "\t" + referenceChr + "\t" + referenceStartPos);
			else
				printerDatabase.println(countWindowId + "\t" + recordId + "\t" + readName + "\t" + readStartPos + "\t" + referenceName + "\t" + referenceStartPos + "\t" + readString);
		
			if (isReference)
				printerFile.print("1 ");
			else
				printerFile.print("2 ");
			String printed =Utils.jaccardWindow(subseq,permuteLength);
			printerFile.println(printed);
			countWindowId++;
			nWindowsPrinted++;
		}
	}*/
}
	
