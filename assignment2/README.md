information-retrieval (assignment2)
=====================

TODO TODO

Please write your code such that :
** you do not leave commented code in your final submissions 
** you include clarifying comments wherever the code is hard to understand. 
** your code follows this Scala style guide and this formatting: Scala_Twitter_style_guide 

• A readme that summarizes your approach and the assumptions that you've made. Don't forget to describe the feature space you are using. Also mention your main class and constants (such as paths) in your code that we have to change in order to run your system.
• Predictions for both test sets as well as performance measures on the labelled test set.
• Your complete source code (the complete src folder). For the correct submission format, please refer to the slides. Each of you has to individually hand in a submission by Nov. 26nd, 23:59:59.


# Main pogram

assignment2.Main requires following arguments:

-trainData [directory]
-testData  [directory]
-labeled [true|false]
-type [NB|LR|SVM]

For instance:

-trainData C:/IR/trainData/ -testData C:/IR/test-with-labels/ -labeled true -type SVM

It's important to set following VM Arguments:

-Xss400m -Xms2g -Xmx4g -XX:-UseGCOverheadLimit

# General Classification Information

All 3 classification techniques are one-vs-all approach.
All 3 classification are using StopWords (assignment2.StopWords.scala) and Stemming (com.github.aztek.porterstemmer.PortStemmer.scala)

# Naive Bayse

Class assignment2.naivebayse.NaiveBayseClassification

Output 3 Topics

P= 0.7194131709337228 , R= 0.7333289634183215 , F1= 0.7020213093418058

# Logistic Regression

Class assignment2.regression.LogisticRegressionClassification

P= 0.1592772146319149 , R= 0.27407038396564426 , F1= 0.19102499514718105
P= 0.13322248487513225 , R= 0.18784543859165262 , F1= 0.1502050493620021

# SVM - Support Vector Machines

Class assignment2.svm.SvmClassification

P= NaN , R= 0.41359754506214796 , F1= 0.3096332806014363


