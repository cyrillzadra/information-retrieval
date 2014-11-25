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

# General Classification Information

All 3 classification techniques are one-vs-all approach.
All 3 classification techniques are using StopWords (assignment2.StopWords.scala) and Stemming (com.github.aztek.porterstemmer.PortStemmer.scala)

# Naive Bayse

Class assignment2.naivebayse.NaiveBayseClassification

F1 Avg = 0.18959501932990883

# Logistic Regression

Class assignment2.regression.LogisticRegressionClassification

# SVM - Support Vector Machines

Class assignment2.svm.SvmClassification

F1 Avg = 0.3002290216193687


