#$ -S /bin/bash
#
#$ -l h_rt=144:00:00,num_proc=4,h_vmem=4G 
#$ -q scavenger.smp
#$ -P SCAVENGER
#$ -N fpchr
#$ -pe smp 1
#$ -cwd
#$ -R yes
# mail options send mail on a=abort, b=begin, e=end, s=suspend, uncomment to use
#
# specify stdout and stderr files.  Will default to jobname.[oe]<job_id> otherwise.
#$ -o fpchr_ch.out
#$ -e fpchr_ch.err
#
#$ -cwd
# Diagnostic/Logging Information
echo "using $NSLOTS CPUs"
echo `date`
#Machine parameters
rootPath=$(pwd)/
input=/hpcstor1/scratch02/p/pprgamma/FDJD/genome_lib/GRCh38_Gencode26/GRCh38.primary_assembly.genome.fa
output=/hpcstor1/scratch02/p/pprgamma/FDJD/genome_lib/FPBin/BFP_GRCH38_ref_genome

wgJar=${rootPath}bwg/*
wgWindowSize=32 # make it 30 for reference genome!!!!
wgGRAM=4

echo "Creating RNA reference genome fingerprints..."

echo "input: $input"
echo "output: ${output}"

java -cp $(for i in ${wgJar}.jar ; do echo -n $i: ; done):picard-tools-1.92/picard-1.92.jar:picard-tools-1.92/sam-1.92.jar Main -i ${input} -o ${output} -w ${wgWindowSize} -c true -g ${wgGRAM}

echo "Fingerprints created."
#Diagnostic/Logging Information
echo "Finish Run"
echo "end time is `date`"

exit 0 
