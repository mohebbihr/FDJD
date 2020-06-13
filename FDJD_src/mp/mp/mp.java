//package mnt.miczfs.tide.mp.src;
import java.io.File;
import java.net.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;

public class mp{
	
	public static void main(String[] args) throws Exception {
		
		if (args.length != 2){
			System.err.println("Usage: <input directory>");
		}
		
		Job job1 = new Job();
		job1.setJarByClass(mp.class);
		//File folder = new File(args[0]);
		//File parent = folder.getParentFile();
		//File[] listOfFiles = folder.listFiles(); 
		Configuration conf = new Configuration();
    		FileSystem fs = FileSystem.get(new URI(args[0]), conf);
    		FileStatus[] listOfFiles = fs.listStatus(new Path(args[0]));
		if(listOfFiles == null) System.err.println("There is no files in this path: "+ args[0]);
		System.err.println("Number of files in path: "+ listOfFiles.length);
		for (int i = 0; i < listOfFiles.length; i++) 
		{
		 	 if ((!listOfFiles[i].isDir()) && !listOfFiles[i].getPath().toString().endsWith("~"))
			 //if ((listOfFiles[i].isFile()) && !listOfFiles[i].getName().endsWith("~"))
			 {
				//String path = args[1] + "/" + listOfFiles[i].getName();
				System.err.println("path: " + listOfFiles[i].getPath().toString());
				String path = listOfFiles[i].getPath().toString().substring(21);
				System.err.println("path after substring: "+ path);
				FileInputFormat.addInputPath(job1, new Path(path)); 
				System.out.println("path: " + path);
			  }
		}
		String oFolder = args[0] + "/mr1";		
		FileOutputFormat.setOutputPath(job1, new Path(oFolder));
		job1.setMapperClass(TransMapper1.class);
		job1.setReducerClass(TransReducer1.class);
		job1.setMapOutputKeyClass(LongWritable.class);
		job1.setMapOutputValueClass(DnaWritable.class);
		job1.setOutputKeyClass(LongWritable.class);
		job1.setOutputValueClass(Text.class);
		
		System.err.println("End of job1");

		if (job1.waitForCompletion(true)){
			System.err.println("Start of job2");
			//
			Job job2 = new Job();
			job2.setJarByClass(mp.class);
			//folder = new File(oFolder);
			//listOfFiles = folder.listFiles(); 
			Configuration conf2 = new Configuration();
			//String oFolder = args[1] + "/mr1";
 	                FileSystem fs2 = FileSystem.get(new URI(oFolder), conf2);
        	        FileStatus[] listOfFiles2 = fs2.listStatus(new Path(oFolder));

                	//FileSystem fs = FileSystem.get(new URI(oFolder), conf);
                	//FileStatus[] listOfFiles = fs.listStatus(new Path(oFolder));
			//fs = FileSystem.get(new URI(oFolder), conf);
                        //listOfFiles = fs.listStatus(new Path(oFolder));
			for (int i = 0; i < listOfFiles2.length; i++) 
			{
			 	 if ((!listOfFiles2[i].isDir()) && listOfFiles2[i].getPath().toString().contains("part-r") && !listOfFiles2[i].getPath().toString().endsWith("~"))
				 {
					 //String path = oFolder + "/" + listOfFiles[i].getName();
					 String path = listOfFiles2[i].getPath().toString().substring(21);
					 System.err.println("Added to Job2: "+ path);
					 FileInputFormat.addInputPath(job2, new Path(path));
					 System.out.println(path);
				  }
			}
			oFolder = args[0] + "/mr2";
			System.out.println("mr2 path: "+ oFolder);
			FileOutputFormat.setOutputPath(job2, new Path(oFolder));
			job2.setMapperClass(TransMapper2.class);
			job2.setReducerClass(TransReducer2.class);
			job2.setMapOutputKeyClass(LongWritable.class);
			job2.setMapOutputValueClass(DnaWritable.class);
			job2.setOutputKeyClass(LongWritable.class);
			job2.setOutputValueClass(NullWritable.class);
						
			System.exit(job2.waitForCompletion(true) ? 0 : 1);
			
		}
	}
}
