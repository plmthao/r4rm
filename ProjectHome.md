A small plug-in that integrates R into RapidMiner. It allows to
  1. ... embed R-snippets into a RapidMiner process
  1. ... visualize data with R within RM (with the option to include the plots into reports)
  1. ... do rapid-prototyping in R by means of a OpenInR operator which converts all its inputs into R-dataframes, creates  a temporary workspace containing those, and finally opens the workspace in a new instance of R.

Furthermore the plug-in provides a set of utility functions to ease the development of parametrized R-operators.

Currently, the plug-in requires Rserve to run in the background (either locally or on a remote location).

This project has been supported by the Max Planck Institute of Molecular Cell Biology and Genetics, Germany.
http://mpi-cbg.de/