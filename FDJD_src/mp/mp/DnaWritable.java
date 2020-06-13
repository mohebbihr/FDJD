//package mnt.miczfs.tide.mp.src;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

public class DnaWritable implements Writable, WritableComparable<DnaWritable> {
 	
	//LongWritable bamWindow;
	//LongWritable read;
    //LongWritable refWindow;
    private Long bamWindow;
    private Long read;
    private Long refWindow;
	//private Integer chr;
    private Double dist;
    //private Long rightChrNum = 0;
    //private Long leftChrNum = 0;
    private String rightChr = "";
    private String leftChr = "";
	//IntWritable chr;
	//DoubleWritable dist;
	
    public DnaWritable(){
	super();
    }
	  
	/*public DnaWritable(){
		super();
		this.bamWindow = new Long();
		this.read = new Long();
		this.refWindow = new Long();
		
		this.bamWindow = new LongWritable();
    	this.read = new LongWritable();
    	this.refWindow = new LongWritable();
    	this.chr = new IntWritable();
    	this.dist = new DoubleWritable();
    }*/
	
    //public DnaWritable(LongWritable bamWindow, LongWritable read, LongWritable refWindow, IntWritable chr, DoubleWritable dist){
    //public DnaWritable(Long bamWindow, Long read, Long refWindow, Integer chr, Double dist){
    //public DnaWritable(Long bamWindow, Long read, Long refWindow, Double dist, Long rightChrNum, Long leftChrNum){
    public DnaWritable(Long bamWindow, Long read, Long refWindow, Double dist, String rightChr, String leftChr){
	super();	
	this.bamWindow = bamWindow;
	this.read = read;
    	this.refWindow = refWindow;
    	//this.chr = chr;
    	this.dist = dist;
	//this.rightChrNum = rightChrNum;
	//this.leftChrNum = leftChrNum;
	this.rightChr = rightChr;
	this.leftChr = leftChr;
    }
	
    	/*public DnaWritable(long bamWindow, long read, long refWindow, int chr, double dist){
		super();
		this.bamWindow = new Long(bamWindow);
        	this.read = new Long(read);
        	this.refWindow = new Long(refWindow);
		this.chr = chr;
		this.dist = dist;*/		
		/*this.bamWindow = new LongWritable(bamWindow);
    		this.read = new LongWritable(read);
    		this.refWindow = new LongWritable(refWindow);
    		this.chr = new IntWritable(chr);
    		this.dist = new DoubleWritable(dist);*/
    	//}
    
    @Override
    public void write(DataOutput out) throws IOException {
	out.writeLong(this.bamWindow);
        out.writeLong(this.read);
        out.writeLong(this.refWindow);
        //out.writeInt(chr);
        out.writeDouble(this.dist);
	//out.writeLong(this.rightChrNum);
	//out.writeLong(this.leftChrNum);	
	out.writeChars(this.rightChr);
	out.writeChars(this.leftChr);
	/*bamWindow.write(dataOutput);
	read.write(dataOutput);
	refWindow.write(dataOutput);
	chr.write(dataOutput);
	dist.write(dataOutput);*/

    }
	
    @Override
    public void readFields(DataInput in) throws IOException {
    	this.bamWindow = in.readLong();
        this.read = in.readLong();
        this.refWindow = in.readLong();
        //this.chr = in.readInt();
        this.dist = in.readDouble();
	//this.rightChrNum = in.readLong();
	//this.leftChrNum = in.readLong();
	// we need to read the next four characters. chr1
	String rc = "";
	for (int i=0; i<4; i++)
		rc += in.readChar();
	this.rightChr = rc;

        String lc = "";
        for (int i=0; i<4; i++)
                lc += in.readChar();
        this.leftChr = lc;

	/*bamWindow.readFields(dataInput);
    	read.readFields(dataInput);
    	refWindow.readFields(dataInput);
    	chr.readFields(dataInput);
    	dist.readFields(dataInput);*/
    }
	
    @Override
    public String toString(){
	//String rightChr = Long.toString(this.rightChrNum); // "chr" + 
	//String leftChr = Long.toString(this.leftChrNum); // "chr" + 
	return bamWindow + "\t" + read + "\t" + refWindow + "\t" + dist + "\t" + this.rightChr + "\t" + this.leftChr;
	//return bamWindow + "\t" + read + "\t" + refWindow + "\t" + dist;
	//return bamWindow + "\t" + read + "\t" + refWindow + "\t" + chr + "\t" + dist;
    }
    
    //public LongWritable getBamWindow() {
    public Long getBamWindow() {
    	return bamWindow;
    }
    
    //public LongWritable getRead() {
    public Long getRead() {
    	return read;
    }
    
    //public LongWritable getRefWindow() {
    public Long getRefWindow() {    
	return refWindow;
    }
    
    //public IntWritable getChr() {
    //public Integer getChr(){    
	//return chr;
    //}
    
    //public DoubleWritable getDist() {
    public Double getDist(){    
	return dist;
    }
    
/*    public Long getRightChrNum(){
    	return this.rightChrNum;
    }

    public Long getLeftChrNum(){
        return this.leftChrNum;
    }*/
    public String getRightChr(){
        return this.rightChr;
    }

    public String getLeftChr(){
        return this.leftChr;
    }

    //public void setBamWindow(LongWritable bamWindow) {
    public void setBamWindow(Long bamWindow) {    
	this.bamWindow = bamWindow;
    }
    
    //public void setRead(LongWritable read) {
    public void setRead(Long read){
	this.read = read;
    }
    
    //public void setRefWindow(LongWritable refWindow) {
    public void setRefWindow(Long refWindow){
	this.refWindow = refWindow;
    }
    
    //public void setChr(IntWritable chr) {
    //public void setChr(Integer chr) {
    //	this.chr = chr;
    //}
    
    //public void setDist(DoubleWritable dist) {
    public void setDist(Double dist) {   
	this.dist = dist;
    }

    /*public void setRightChrNum(Long rightChr){
        this.rightChrNum = rightChr;
    }

    public void setLeftChrNum(Long leftChr){
        this.leftChrNum = leftChr;
    }*/
    public void setRightChr(String rightChr){
        this.rightChr = rightChr;
    }

    public void setLeftChr(String leftChr){
        this.leftChr = leftChr;
    }


      //Add this static method as well
      public static DnaWritable read(DataInput in) throws IOException {
      		DnaWritable dnaWritable = new DnaWritable();
      		dnaWritable.readFields(in);
      		return dnaWritable;
      }

    @Override
    public int compareTo(DnaWritable dnaWritable) {
	return 1;
    }    
  
   
    @Override
    public int hashCode() {
    // This is used by HashPartitioner, so it will hash based on read number
    	return read.hashCode();
    }
}
