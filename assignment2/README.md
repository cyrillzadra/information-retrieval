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

For all 3 classification top 3 topics are returned. 

# Naive Bayse

Class assignment2.naivebayse.NaiveBayseClassification

In a first pass a assignment2.index.IndexBuilder collects all relevant information from train data, such as nr of documents, topic counts, topic length (total number of tokens for each topic) and topicTfIndex ( collection frequency for each topic ), and puts it in Memory.

In a second pass NaiveBayseClassification goes over test data 

naive bayse

Best result using Naive Bayse:

P= 0.7194131709337228 , R= 0.7333289634183215 , F1= 0.7020213093418058

# Logistic Regression

Class assignment2.regression.LogisticRegressionClassification

Best result using Logistic Regression:

P= 0.13322248487513225 , R= 0.18784543859165262 , F1= 0.1502050493620021

# SVM - Support Vector Machines

Class assignment2.svm.SvmClassification

Best result using SVM:

In a first pass SvmClassification uses assignment2.index.FeatureBuilder to collect seperately all features (term frequencies) from train and test data. 






P= 0.5904149471800447 , R= 0.601059109400312 , F1= 0.5744665782352627

