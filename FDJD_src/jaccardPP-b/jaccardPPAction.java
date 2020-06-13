import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.io.*;
import java.util.HashMap;


public class jaccardPPAction {

	BufferedReader dbReader = null;
	PrintWriter pr = null;
	PrintWriter pr2 = null; // test
	HashMap<Integer, Integer> hmap = new HashMap<Integer, Integer>();	
	File inputFile = null;
	File inputDBFile = null; 
	FileInputStream fis = null; // reading from binary file
	DataInputStream dis = null;
	//FileOutputStream fos = null; // we used only binary files for input, if we use binary file for output, we need to use binary files for hadoop which is difficult
	//BufferedOutputStream outBuff = null;
	//DataOutputStream dos = null;
	
	long fileLen = 0L;
	int queryFileId;
	int dataFileId;
	int nWindowsRead;
	//String referenceChr; // we don't have reference chromosome info in fasta reference file. 
	int nQPoints;
	int nDPoints;
		
	public jaccardPPAction(String inFile,String inDBFile,String outFile,int dataFileId,int nWindowsRead, int nQPoints, int nDPoints) throws IOException{
	//public jaccardPPAction(String inFile,String outFile,int dataFileId,int nWindowsRead, int nQPoints, int nDPoints) throws IOException{			
			this.inputFile = new File(inFile);
			this.inputDBFile = new File(inDBFile);
			this.fileLen = inputFile.length();
			this.fis = new FileInputStream(inputFile);
			this.dis = new DataInputStream(fis);
			// read _DB txt file
			this.dbReader = new BufferedReader(new FileReader(this.inputDBFile));
			this.pr = new PrintWriter(new File(outFile));
			//for test purposes
			String outFile2 = outFile.substring(0, outFile.indexOf("jaccardPP_Out")) + "mp/fpart-000" + inFile.substring(inFile.length() - 1);
			System.out.println("foutput: " + outFile2);
			this.pr2 = new PrintWriter(new File(outFile2));
			
			//ouput file objects
			//fos = new FileOutputStream(outFile);
			//outBuff =new BufferedOutputStream(fos);
		    	//dos =new DataOutputStream(outBuff);
			
			this.nWindowsRead = nWindowsRead;
			//this.referenceChr = referenceChr;
			this.nQPoints = nQPoints;
			this.nDPoints = nDPoints;
			this.dataFileId = dataFileId;
			try{			
				process();
			}catch(Exception e){
				System.out.println(e.toString());
			}

			if(fis != null)
				fis.close();
			if(dis != null)
				dis.close();
			
			/*if(dos != null){
				dos.flush();
				dos.close();
			}
			if(outBuff != null){
				outBuff.flush();
				outBuff.close();
			}
			if(fos != null){
				fos.flush();
				fos.close();
			}	*/			
			
			/*if (br != null)
				br.close();*/
			if (pr != null){
				pr.flush();
				pr.close();
			}
			if (this.pr2 != null){
				this.pr2.flush();
				this.pr2.close();
			}
		}
	
	public String findChrString(int idx) throws Exception{
		if( this.dbReader == null ){
			// we need to initialize reader first.
			return "";
		}
		String st = "";
		// we search inside the _DB file of query, not reference. 
		while((st = this.dbReader.readLine()) != null){		
			String[] parts = st.split("\t");
			int parts_len = parts.length;
			int countWId = Integer.parseInt(parts[0]);
			int index = Integer.parseInt(parts[1]);
			if (idx == index || idx == countWId ){
				return parts[parts_len - 2] + "_" + parts[parts_len - 1];
			}
		}
		return "";
	}

	public void process() throws Exception,IOException{
		long readOffset =0L;
		String currentLine;
		//while ((currentLine = br.readLine()) != null) {
		while( readOffset < fileLen ){
			// reading from binary input file.		
			int qWindow = Integer.reverseBytes(dis.readInt());
			int query_idx = qWindow;
			readOffset += (Integer.SIZE / 8);
			int dWindow = Integer.reverseBytes(dis.readInt());
			readOffset += (Integer.SIZE / 8);
			
			if (qWindow >= nQPoints){
				int temp = qWindow;
				qWindow = dWindow;
				dWindow = temp;
			}
			
			dWindow += (nDPoints*dataFileId);			
			int read = (int) Math.floor((double)qWindow/(double)nWindowsRead)+1;			
			float distance = dis.readFloat();
			readOffset += (Float.SIZE / 8);	
			// how we can write left and right chromosome at breakpoint ??
			// we need to search the query database file. We need to load the _DB file!! 
			String chr_str = null;
			try{
			//	chr_str = findChrString(query_idx);
				chr_str = this.dbReader.readLine();
			}catch(Exception e){
				System.out.println(e.toString());
				break;
			}
			if (chr_str != null){
				//String[] chr_parts = chr_str.split("_");
				//String right_chr = chr_parts[0];
				//String left_chr = chr_parts[1];
				String[] chr_parts = chr_str.split("\t");
				String tid = chr_parts[0];
				String right_chr = chr_parts[chr_parts.length - 2];
				String left_chr = chr_parts[chr_parts.length - 1];
				this.pr.println(String.valueOf(qWindow) + " "+ String.valueOf(read) + " " + String.valueOf(dWindow) + " " + String.valueOf(distance) + " " + right_chr + " " + left_chr);
				// for test purposes
				// insert qWindow and dWindow into a hashmap and if they inserted for the first time, print it into output file. 
				// we insert each intery two times, (qWindow, dWindow) and (dWindow, qWindow)
				if (this.hmap.containsKey(qWindow) == false){
					this.hmap.put(qWindow, dWindow);
					this.pr2.println(String.valueOf(qWindow));
				}				
				if (this.hmap.containsKey(dWindow) == false){
                                        this.hmap.put(dWindow, qWindow);
                                        this.pr2.println(String.valueOf(dWindow));
                                }

			        //this.pr2.println(tid + " " + String.valueOf(qWindow) + " " + String.valueOf(dWindow) + " "+ String.valueOf(read));
			}
			//pr.println(String.valueOf(qWindow) + " "+ String.valueOf(read) + " " + String.valueOf(dWindow) + " " + referenceChr + " " + String.valueOf(distance));

		}
	}
	
}
