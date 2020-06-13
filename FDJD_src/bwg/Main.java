/*
 * TDJD - Translocation Detector using computing jaccard distance.
 * This program implements part of an algorithm to detect translocation in genome using a window technique that splits
 * the reads in pre-determined size windows and uses jaccard distance to find the nearest neighboor.  
 * Version 1.0 by Hamidreza Mohebbi
 * October 2016
 */

import java.util.List;
import java.util.Set;

public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		int i = 0, j;
		String input="", inputIndex = "", output="", outputDB="", discordantPairs="";
		
		//Default parameters:
		// task: change the code that divide the read with length l into two parts with length 25.
		// Then padd 7 zeros with each part to build window size of 32 !!!
		short windowSize=32; // In our case, read length is 50 and widnow size should be 32?? Yes
		// 64, having a big window of size 64 and load the whole read into one is wrong. 
		short permuteLength = 4;
		Boolean isReference=false;
		short min_mapq=32;		
		long wgChunkSize = 20000000;
		int jumpStep = 7; // we jump k step for creating reference finger print		

		while (i < args.length && args[i].startsWith("-")) {
		    	String arg = args[i++];
	    		for (j = 1; j < arg.length(); j++) {
				char flag = arg.charAt(j);
				//System.err.println("flag: " + flag);
				switch (flag) {
				case 'i':
				input= args[i++];
				break;
				case 'x':
				inputIndex = args[i++];
				break;
				case 'o':
				output= args[i++];
				outputDB= output+"_DB";	
				break;
				case 'd':
				discordantPairs= args[i++];
				break;
				case 'w':
				windowSize= Short.parseShort(args[i++]);
				break;
				case 'g':
				permuteLength= Short.parseShort(args[i++]);
				break;
				case 'c':
				//System.err.println(" args[i++]: " + args[i++]);
				isReference = true;							
				break;
				case 'k':
				jumpStep = Integer.parseInt(args[i++]);
				break;
				case 's':
				wgChunkSize= Long.parseLong(args[i++]);				
				break;
				default:
				System.err.println("ParseCmdLine: illegal option " + flag + " Usage:< -i input_file, -x input_index_file,  -o output_file -d discordant_reads_file -w windows_length -g gram_length -c is_ref -k jumpStep -s wgChunkSize>");
				break;
				}
		        }
		}
		
		//Windows generation	
		System.out.println("Started at: "+ System.nanoTime() + "(nanotime)");
		//System.out.println("Input: " + input + " , output: " + output + " , c: "+ referenceChr);
		System.out.println("Input: " + input + " , output: " + output );
		//Utils.permute("ACGNT".toCharArray(), permuteLength); 
		//List<Set> words = Utils.permute("ACGT".toCharArray(), permuteLength);
		Utils.permute("ACGT".toCharArray(), permuteLength);
		System.out.println("words len: " + Utils.words.size());
		//System.out.println(Utils.words);
		// we don't have the two below functions on utils
		//Utils.permute_wc("ACGNT".toCharArray(), permuteLength); // change ACGT to ACGNT
		//Utils.builLookupTbl();
						
		if (isReference){
			//DataFile df = new DataFile(input,output,outputDB,discordantPairs,windowSize,referenceChr,min_mapq,isReference,permuteLength, wgChunkSize, jumpStep);
			//DataFile df = new DataFile(input,output,outputDB,discordantPairs,windowSize,min_mapq,isReference,permuteLength, wgChunkSize, jumpStep);
			//df.run();
			
                	//System.out.println("Main.java, output: "+ output);
			//System.out.println("Main.java, input: "+ input);
			// reference file. 
			FastaRefFile fdf = new FastaRefFile(input,output,outputDB,discordantPairs,windowSize,min_mapq,isReference,permuteLength, wgChunkSize, jumpStep);
			fdf.run();
		}else{
			//QueryFile qf = new QueryFile(input,output,outputDB,discordantPairs,windowSize,referenceChr,min_mapq, isReference,permuteLength);
			//QueryFile qf = new QueryFile(input,output,outputDB,discordantPairs,windowSize,min_mapq, isReference,permuteLength);
			//qf.run();

			FastaQueryFile fqf = new FastaQueryFile(input,inputIndex,output,outputDB,discordantPairs,windowSize,min_mapq,isReference,permuteLength);
                        fqf.run();
		}
			
		System.out.println("Finished at: "+ System.nanoTime());
	}
}
