import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.MappedByteBuffer;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.ArrayList;
// the query file which represents binary finger print and database (_DB) files. 

public class FastaQueryFile extends WindowsFile{
	
	String inputIndexPath = null;

	public FastaQueryFile (String inputPath, String inputIndexPath, String OutputFilePath, String OutputDbPath, String discordantPairs, short windowSize, short min_mapq, boolean isReference, short permuteLength) throws Exception{
		
		super(inputPath,OutputFilePath,OutputDbPath,discordantPairs,windowSize,min_mapq,isReference,permuteLength);
		this.inputIndexPath = inputIndexPath;
	}
	
	@Override public void run(){
                File outputFile = new File(OutputFilePath);
                File outputDatabase = new File(OutputDbPath);
                try {
                        fos = new FileOutputStream(outputFile);
                        printerDatabase = new PrintWriter(outputDatabase);
                        CreateWindows(inputPath,inputIndexPath,discordantPairs,windowSize);
                        fos.close();
                        printerDatabase.close();
                } catch (Exception e) {
                        e.printStackTrace();
                }
                System.out.println("Files successfully created!!");
        }


	public void CreateWindows(String inputPath,String inputIndexPath, String discordantPairs, short windowSize) throws IOException{
		
		long recordId = 0L;
		long count = 0;
		// we need both .bam file and its index (.bam.bai) files as input.
		// Also, we need discordant reads file. 		
		File inputFile = new File(inputPath);
		System.out.println("Opened input file: " + inputPath);
		//File inputIndex = new File(inputPath+".bai");
		File inputIndex = new File(inputIndexPath);		
		System.out.println("Opened input index file: " + inputIndexPath);		

		//final SAMFileReader inputBam = new SAMFileReader(inputFile,inputIndex);
		final SAMFileReader inputBam = new SAMFileReader(inputFile);
		BufferedReader reader = new BufferedReader(new FileReader(discordantPairs));
		String line = null;
		String[] parts = null;
		// we go through each line of discordant reads file.
		try {
			if((line = reader.readLine()) != null) 
			{    	
				parts = line.split(" ");
				String rightRef = parts[0]; // the right reference side of breakpoint
				String leftRef = parts[4]; // the left reference side of breakpoint
				//System.out.println("DR_line: " + line);
				// read content of bam file.
				for (final SAMRecord bamRecord : inputBam)
				{
					//System.out.println("reading bam file");
					String readBuffer = bamRecord.getReadString();
					//System.out.println("read buffer: " + readBuffer + " , len: " + readBuffer.length());
					
					if (readBuffer.length() < 15) break; // ignore too small reads. 
					recordId++;
					//if(recordId == 10) break;
					List<Integer> idx_arr = new ArrayList<Integer>();
					if (readBuffer.length() < windowSize){
						readBuffer = readBuffer + Utils.repeat(windowSize - readBuffer.length(), "N");
						idx_arr.add(0);
					}else{
						int last_idx = readBuffer.length() - windowSize;
							//idx_arr.add(last_idx / 8);
							//idx_arr.add(last_idx / 4);
						idx_arr.add(last_idx);
							//idx_arr = {0, last_idx /8, last_idx / 4, last_idx};
					}
					// we need to pick i values from a list of values. For example, for read length 50; we might a list of [0, 8, 16, 18]
					//int last_idx = readBuffer.length() - windowSize;
					//int idx_arr[] = {0, last_idx /8, last_idx / 4, last_idx};
					//for (int i = 0; i <= (bamRecord.getReadLength()-windowSize); i++) {
					for (int ii =0; ii < idx_arr.size(); ii++ ) {
						int i = idx_arr.get(ii);
						short readStartPos = (short) (i+1);
						String window = "";
						//short posWindow = 0;
						boolean allNwindow = true;
						for (int j=i; j< i+windowSize;j++){
							char currentChar = Character.toUpperCase((char) readBuffer.charAt(j));
								// we replace ambiguous codes with its equevalent
								// ref: http://www.boekhoff.info/?pid=data&dat=fasta-codes
							switch(currentChar){
								case 'U':
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
							}
									
								//allNwindow = false;
							window = window + currentChar;
								//posWindow++;
							if (window.length() == windowSize){
									//if (! allNwindow)
								if(i == 0 || i == readBuffer.length() - windowSize)//Catch only first and last window
										SeqToBinaryWindow(window, windowSize, bamRecord.getAlignmentStart(), readStartPos, recordId, bamRecord.getReadName(), bamRecord.getReferenceName(),bamRecord.getReadString(),isReference, rightRef, leftRef);											
											// convert sequence to the binary window									
							}
								//}
						}
						//}
						if ((recordId % 1000)==0){
							//printerFile.flush();
							fos.flush();
							printerDatabase.flush();
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//printerFile.flush();
		fos.flush();
		printerDatabase.flush();
		inputBam.close();
		reader.close();
	}
}
