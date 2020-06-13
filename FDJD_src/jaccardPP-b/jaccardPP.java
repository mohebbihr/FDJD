
/*MapReduce application 
*/
import java.io.File;
import java.io.IOException;

public class jaccardPP {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	
	static int readSize = 50;
	static int windowSize = 64;
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
				
		if (args.length != 5){
			System.err.println("Usage: <input jaccard distance directory> <input query DB file> <output directory> <number of query points> <number of data points>");
		}
		String inputBJFile = args[0];
		String inputDBFile = args[1];
		String outputDir = args[2];
		int nWindowsRead = 2;//Changed here because we are getting only the first and last window of the read
		int nQueryPoints = Integer.parseInt(args[3]);
		int nDataPoints = Integer.parseInt(args[4]);
		
		System.out.println();
		System.out.println("**Starting MPPP**");
		System.out.println();
		System.out.println("Pre-Defined max number of query points in input file: "+ String.valueOf(nQueryPoints));
		System.out.println("Pre-Defined max number of data points in input file: "+ String.valueOf(nDataPoints));
		System.out.println();
		
		File folder = new File(inputBJFile);
		File parent = folder.getParentFile();
		File[] listOfFiles = folder.listFiles();
		System.out.println("Number of input files: " + listOfFiles.length); 
		for (int i = 0; i < listOfFiles.length; i++) 
		{
			if ((listOfFiles[i].isFile()) && !listOfFiles[i].getName().endsWith("~") && listOfFiles[i].getName().length() > 2)
			{
				String fileName = listOfFiles[i].getName();
				String inFile = folder.getAbsolutePath()+ "/" + fileName;
				int posRoot = inFile.indexOf("./");
				int dataFileId = 1;
				if (posRoot != -1)
					inFile = inFile.substring(0,posRoot)+inFile.substring(posRoot+2,inFile.length());				
				String outFilePath = outputDir + fileName;
				posRoot = outFilePath.indexOf("./");
				if (posRoot != -1)
					outFilePath = outFilePath.substring(0,posRoot)+outFilePath.substring(posRoot+2,outFilePath.length());
				//int pos = listOfFiles[i].getName().indexOf('_');
				//System.out.println("list of file: " + listOfFiles[i].getName() + " ,inFile: " + inFile + ", outFile: "+ outFile);
				//if(listOfFiles[i].getName().indexOf(".bam") != -1)
				//	dataFileId = Integer.parseInt(listOfFiles[i].getName().substring(pos+1, listOfFiles[i].getName().length() -4 ));
				//else
				//	dataFileId = Integer.parseInt(listOfFiles[i].getName().substring(pos+1, listOfFiles[i].getName().length() ));
				
				//int posChr = listOfFiles[i].getName().indexOf("chr");
				//String referenceChr = listOfFiles[i].getName().substring(posChr+3,pos);
				//System.out.println("Pre-processing "+ referenceChr + "_" + String.valueOf(dataFileId));
				// we need a way to read referenceChar !!??? how???
				// We don't have the chromosome reference info (chr2,chr3), in our reference fasta file. 
				// but we have left and right chromosome in breakpoint form the query file (discordantPair file)
				// solution: we need to read 
				dataFileId = Integer.parseInt(listOfFiles[i].getName().substring(listOfFiles[i].getName().length() - 1 ));
				
				System.out.println("Pre-processing " + inFile + " ..");
				jaccardPPAction mp_pp = new jaccardPPAction(inFile,inputDBFile,outFilePath,dataFileId,nWindowsRead,nQueryPoints,nDataPoints);
				//jaccardPPAction mp_pp = new jaccardPPAction(inFile,outFile,/*queryFileId,*/dataFileId,nWindowsRead,referenceChr,nQueryPoints,nDataPoints);
				System.out.println("Output File: "+ outFilePath);
				System.out.println("Pre-processing completed.");
				System.out.println();
			 }
		}
	}
}
