# FDJD
In this research we present a parallel method to convert inefficient categorical space into a compact binary array and therefore, reduce the dimensionality of the data and speed up the computation.  FDJD pipeline contains three steps: general alignment, fusion candidate generation, and refinement. In our research, Jaccard distance is used as a similarity measure to find the nearest neighbors of a given query binary fingerprint alongside a fast KNN implementation.  We benchmarked our fusion prediction accuracy using both simulated and genuine RNA-Seq data sets.  Fusion detection results are compared with the state-of-the-art-methods STAR-Fusion, InFusion and TopHat-Fusion. We verified our conclusion by testing various fusion detection methods on genuine paired-end Illumina RNA-Seq data that were obtained from 60 publicly available cancer cell line data sets. 




