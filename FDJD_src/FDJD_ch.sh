#$ -S /bin/bash
#
#$ -l h_rt=144:00:00,num_proc=64
#$ -q scavenger.smp
#$ -P SCAVENGER
#$ -N FDJD
#$ -pe smp 32
#$ -cwd
#$ -R yes
# mail options send mail on a=abort, b=begin, e=end, s=suspend, uncomment to use
#$ -m aeb
#$ -M mohebbi@cs.umb.edu
#
# specify stdout and stderr files.  Will default to jobname.[oe]<job_id> otherwise.
#$ -o FDJD_ch.out
#$ -e FDJD_ch.err
#
#$ -cwd
# Diagnostic/Logging Information
echo "using $NSLOTS CPUs"
echo `date`

#######################################################################
# Fusion Detection using Jaccard Distance
# Version 1.0 by Hamidreza Mohebbi
# May, 2019
#######################################################################
#Requires installation:
#Bowtie2,picard,samtools,hadoop,jdk,blast+
#######################################################################
#######################################################################
#Parameters List:
#base: input fastq files of RNA reads.
#ncores: number of cores to use
#picardPath: picard absolute path
#bowtiePath: bowtie path
#rawInput: raw file absolute path
#fastq1: fastq1 absolute path
#fastq2: fastq2 absolute path
#alignedInputPath: aligned sam file (bowtie output) folder
#index: hg19 index folder # we don't need it
# sample run: bash FDJD_ch.sh -left_fq data/simulated/sim_101_fastq/sim1_reads_1.fq.renamed.fq -right_fq data/simulated/sim_101_fastq/sim1_reads_2.fq.renamed.fq -genome_lib_dir genome_lib/GRCh38_gencode_v29_CTAT_lib_Mar272019.plug-n-play/ctat_genome_lib_build_dir/ -ref_fp genome_lib/NFPBin/ -CPU $NSLOTS
#######################################################################
#######################################################################
#Machine parameters
totalstart=`date +%s`

rootPath=$(pwd)/
#index=${rootPath}bowtie2-index/hg19 # we need to replace bowtie2 with STAR alignment.
hadoopPath=/home/hamidreza.mohebbi001/bin/hadoop-2.7.3/
#bowtiePath=/opt/bowtie2/
starPath=/home/hamidreza.mohebbi001/bin/STAR-2.7.0f/
picardPath=${rootPath}/picard-tools-1.92/

## new inputs
left_fq = ''
right_fq = ''
genome_lib_dir = ''
numthread=24
jaccardTH=0.7

raw=0
c1=2
c2=3

while getopts i:g:abrpxh option
do
    case "${option}"
    in
    left_fq) left_fq=${OPTARG};;
    right_fq) right_fq=${OPTARG};;
    genome_lib_dir) genome_lib_dir=${OPTARG};;
    ref_fp) reference=${OPTARG};;
    CPU) numthread=${OPTARG};;
    i) input=${OPTARG};;
    g) inputGlobal=${OPTARG};;
    r) raw=1;;
    a) c1=${OPTARG};;
    b) c2=${OPTARG};;
    p) nCores=${OPTARG};;
    x) index=$OPTARG;;
    h) hadoopPath=$OPTARG;;


esac
done
base=${input}
base=${base%.*}
#mkdir ${rootPath}Data/
inputWpath=${rootPath}Data/${base}/
mkdir ${inputWpath}
mkdir ${inputWpath}mr2
mkdir ${inputWpath}mr2/NFP
#reference=${rootPath}HumanGenome/NFP-bin/step1/

#binary finger print generation.
wgJar=${rootPath}bwg/*
wgWindowSize=32
wgGRAM=4
wgBamOutput=${inputWpath}wgOutput/NFP-bin/
echo "$wgBamOutput"
mkdir ${wgBamOutput}
wgBamReference=${rootPath}bamRefNFPBin/
echo "$wgBamReference"
#mkdir ${wgBamReference}
wgchuncksize=20000000
#SSE jaccard version that works with binary files
ssejaccard=${rootPath}SSE-jaccard-b/src/ssejaccard
#ssejaccardtmp=${inputWpath}jaccardDistTmp/
#mkdir ${ssejaccardtmp}
ssejaccardoutput=${inputWpath}jaccardDist/
mkdir ${ssejaccardoutput}
jaccardPP=${rootPath}jaccardPP-b/ssejaccardPP.jar # works with binary files
jaccardPPOutput=${inputWpath}ssejaccardPP_Out/
mkdir ${jaccardPPOutput}
binCat=${rootPath}bincat/bincat.jar
inputPlusChr=${inputWpath}inputPlusChr/
mkdir ${inputPlusChr}
underline="_"
readfn=READS
readfn=$base$underline$readfn
output=${inputWpath}${readfn}
hitsfn=hits.txt
#hitsfn=$base$underline$hitsfn
# clean up old files and directories
echo "clean up old files and directories"
rm ${wgBamOutput}MP-out*
rm ${inputPlusChr}*
rm ${ssejaccardoutput}*
rm -rf ${jaccardPPOutput}*
rm -rf ${inputWpath}mr2/NFP/*
rm ${inputWpath}${readfn}
cat > ${inputWpath}${readfn} &
rm ${inputWpath}${hitsfn}
cat > ${inputWpath}${hitsfn} &
username=$USER
hadoop fs -rmr /user/${username}/${base}
echo "end of cleaning"

if [ $raw -eq 1 ]; then

start=`date +%s`
export PATH=$PATH:${bowtiePath}
export PATH=$PATH:${picardPath}
fastq1=${inputWpath}${base}.f1.fastq
fastq2=${inputWpath}${base}.f2.fastq
########################################################################
########################################################################
# Create the 2 fastq inputs for the aligner
#echo "Creating fastq files with picard-tools-1.92..."
#java -jar ${picardPath}picard.jar SamToFastq I=${input} FASTQ=${fastq1} SECOND_END_FASTQ=${fastq2} VALIDATION_STRINGENCY=SILENT
#echo "Fastq files created."
########################################################################
########################################################################
#echo "Aligning file with bowtie2-2.1.0 (End-to-end alignment)..."
#${bowtiePath}bowtie2 -p ${nCores} -x ${index} -1 ${fastq1} -2 ${fastq2} -S ${inputWpath}${base}.sam
#echo "Aligning file with bowtie2-2.1.0 (local alignment)..."
#${bowtiePath}bowtie2 -p ${nCores} -x ${index} --local -1 ${fastq1} -2 ${fastq2} -S ${inputWpath}${base}.local.sam
#echo "Alignments complete."
########################################################################
########################################################################
# Convert sam files to bam using samtools (in parallel)
#echo "Converting sam files to bam format using samtools-0.1.19..."
#samtools view -b -S ${inputWpath}${base}.sam > ${inputWpath}${base}.bam
#samtools view -b -S ${inputWpath}${base}.local.sam > ${inputWpath}${base}.local.bam
#echo "Bam files created."
# Sort bam files
#echo "Sorting bam files using samtools-0.1.19..."
#samtools sort -o ${inputWpath}${base}.sorted ${inputWpath}${base}.bam
#samtools sort -o ${inputWpath}${base}.local.sorted ${inputWpath}${base}.local.bam
#echo "Sorting complete."
# Index bam files
#echo "Indexing sorted bam files using samtools-0.1.19..."
#samtools index ${inputWpath}${base}.sorted.bam
#samtools index ${inputWpath}${base}.local.sorted.bam
#echo "Indexing complete."
############################################################################
############################################################################
input=${inputWpath}${base}.local.sorted.bam
inputGlobal=${inputWpath}${base}.sorted.bam

end=`date +%s`
echo "Execution time of alignment preprocessing: $((end-start)) secs"
fi
#COMMENT1
############################################################################
############################################################################
#Isolate discordant reads with flag 30 and option -F: excludes read mapped in proper pair, read unmapped, mate unmapped, reverse strand

start=`date +%s`
echo "Isolating discordant reads with flag -F 30..."
samtools view -h -F 30 ${inputGlobal} | awk  '{if (NR<87) print $0}; {if($3!=$7 && $7!="=" && ($3=="chr2" || $3=="chr3") && ($7=="chr2" || $7=="chr3") && $5!="0") print $0}' | samtools view -bS - > ${inputWpath}${base}.df.bam

samtools sort -o ${inputWpath}${base}.df.sorted.bam ${inputWpath}${base}.df.bam
samtools index ${inputWpath}${base}.df.sorted.bam
samtools view  ${inputWpath}${base}.df.sorted.bam > ${inputWpath}${base}.df.sorted.sam
samtools view -q 30 ${inputWpath}${base}.df.sorted.bam > ${inputWpath}${base}.df.sorted.filter.sam
awk '{print $3, $4}' ${inputWpath}${base}.df.sorted.filter.sam > ${inputWpath}${base}.df.filter.txt
echo "Isolation complete."
end=`date +%s`
echo "Execution time of Isolation: $((end-start)) secs"
#COMMENT1
############################################################################
############################################################################
#<<"COMMENT1"
start=`date +%s`

echo "Creating query fingerprints..."

java -cp $(for i in ${wgJar}.jar ; do echo -n $i: ; done):picard-tools-1.92/picard-1.92.jar:picard-tools-1.92/sam-1.92.jar Main -i ${input} -d ${inputWpath}${base}.df.filter.txt -w ${wgWindowSize} -g ${wgGRAM} -o ${wgBamOutput}NFPMP-out

echo "Fingerprints created."
end=`date +%s`
echo "Execution time of fingerprints creation, without concatination: $((end-start)) secs"

#COMMENT1
############################################################################
############################################################################
start=`date +%s`

echo "Preparing inputs for jaccard dist..."
for f in $wgBamReference*
do
echo $f
reffile=$(basename $f)
(java -jar $binCat -i ${wgBamOutput}NFPMP-out -j $f -o $inputPlusChr$reffile ) & wait
done
wait
echo "jaccard dist preprocessing done."

#COMMENT1

#Run SSE-Jaccard
#<<"COMMENT1"

echo "Computing the jaccard distance of pairs..."
#for f in $inputPlusChr*
#do
#       reffile=$(basename $f)
#(exec "${ssejaccard}" "$f" "$ssejaccardoutput$reffile") & wait
(exec "${ssejaccard}" "$f" "$ssejaccardoutput") & wait
#done
echo "Jaccard Distance complete."

end=`date +%s`
echo "Execution time of jaccard distance computation: $((end-start)) secs"
#COMMENT1
#<<"COMMENT1"
echo "Processing distance files Preparing for Map-Reduce..."
start=`date +%s`
array=($(wc -l ${wgBamOutput}NFPMP-out_DB))
nquerypoints=${array[0]}
java -jar $jaccardPP $ssejaccardoutput $jaccardPPOutput $nquerypoints $wgchuncksize
echo "Process complete."
end=`date +%s`
echo "Execution time of preparing distance files for Map-Reduce: $((end-start)) secs"
#COMMENT1
#<<"COMMENT1"
############################################################################
############################################################################
#Run Map-Reduce
start=`date +%s`

echo "module load hadoop"
hadoop fs -mkdir /user/${username}/${base}/NFP/
hadoop fs -put ${jaccardPPOutput}*  /user/${username}/${base}/NFP/
eval $cmd0
eval $cmd1
eval $cmd2
mpJar=${rootPath}mp/mp.jar
mp_exec=mp
echo "Mapping and reducing..."
MR_HADOOPJAR=/opt/hadoop-1.2.1/hadoop-core-1.2.1.jar
MR_MANIFEST=${rootPath}mp/manifest.txt
echo "Compiling mp source code.."
javac -classpath $MR_HADOOPJAR ${rootPath}mp/*.java
echo "compile command: javac -classpath $MR_HADOOPJAR ${rootPath}org/myorg/*.java"
echo "Creating mp jar.."
jar cvf $mpJar $MR_MANIFEST ${rootPath}mp/*.class
echo "Givi ng full permission to mp.jar"
chmod 777 $mpJar
export HADOOP_CLASSPATH=$mpJar
export HADOOP_CLASSPATH=$mpJar:$HADOOP_CLASSPATH
export HADOOP_INSTALL=${hadoopPath}
export PATH=$PATH:${HADOOP_INSTALL}bin:${HADOOP_INSTALL}sbin
echo "Running Map-Reduce"
hadoop jar $mpJar mnt.miczfs.tide.mp.src.mp /user/${username}/${base}/NFP/
echo "copy files from HDFS to local"
hadoop fs -get /user/${username}/${base}/NFP/mr2/* ${inputWpath}mr2/NFP/
echo "Map-Reduce complete."
end=`date +%s`
echo "Execution time of Map-Reduce: $((end-start)) secs"
#COMMENT1
############################################################################
############################################################################
start=`date +%s`

mpOutput=${inputWpath}mr2/NFP/part-r-00000
refiner=${rootPath}tide2refiner/Results.jar
echo "Refining results"
java -jar $refiner $mpOutput ${wgBamOutput}NFPMP-out_DB ${output} ${hitsfn}
echo "Refinement complete."

end=`date +%s`
echo "Execution time of Refinement: $((end-start)) secs"

totalend=`date +%s`
echo "Total execution time : $((totalend-totalstart)) secs"

exit 0
