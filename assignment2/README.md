information-retrieval (assignment2)
=====================

TODO TODO

Please write your code such that :
** you do not leave commented code in your final submissions 
** you include clarifying comments wherever the code is hard to understand. 
** your code follows this Scala style guide and this formatting: Scala_Twitter_style_guide 

# Main pogram

assignment2.Main requires following arguments:

-trainData [directory]
-testData  [directory]
-labeled [true|false]
-type [NB|LR|SVM]

For instance:

-trainData C:/IR/trainData/ -testData C:/IR/test-with-labels/ -labeled true -type SVM

# Naive Bayse

Class assignment2.naivebayse.NaiveBayseClassification

Used: one-vs-all

F1 Avg = 0.18959501932990883

# Logistic Regression

Class assignment2.regression.LogisticRegressionClassification

Used: one-vs-all

# SVM - Support Vector Machines

Class assignment2.svm.SvmClassification

Used: one-vs-all

F1 Avg = 0.3002290216193687


