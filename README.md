# FDJD
Here, we describe a fusion detection method using RNA-Seq data which takes advantage of both these two approaches. This method aligns RNA-Seq input data to the reference genome to identify discordantly mapping reads like a mapping-first approach. Additionally, it converts discordant reads into a novel binary fingerprint format which makes it possible to quickly and accurately search the reference human genome for fusions. This fusion detection pipeline contains three steps: general alignment, fusion candidate generation, and refinement.

The BAM and chimeric disjoint output alignment files are then processed at the second step to generate fusion candidates. 
We first converted the RNA query reads into a compact binary fingerprint format, which is then inputted to a parallel tiling search algorithm that uses an SIMD multi-thread architecture. This enables us to find the location of fusion transcripts in an accurate and fast fashion. We concluded that although this method is approximately comparable to other methods in execution time, it achieved the highest accuracy. We verified our conclusion by testing various fusion detection methods on genuine paired-end Illumina RNA-Seq data that were obtained from 60 publicly available cancer cell line data sets. 


