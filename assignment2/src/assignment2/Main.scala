package assignment2

import assignment2.naivebayse.NaiveBayseClassification
import assignment2.regression.LogistictRegressionClassification
import assignment2.svm.SvmClassification

object Main extends App {

  /**
   *  -trainData [directory]
   *  -testData  [directory]
   *  -labeled [true|false]
   *   -type [NB|LR|SVM]
   */
  if (args.length == 0) throw new Exception("-trainData [directory] -testData [directory] -labeled [true|flase] -type [NB|LR|SVM]")
  val trainDataPath: String = { if (args(0) == "-trainData" && args(1) != null) args(1) else throw new Exception("no trainData argument defined : -trainData [directory]") }
  val testDataPath: String = { if (args(2) == "-testData" && args(3) != null) args(3) else throw new Exception("no testData argument defined : -testData [directory] ") }
  val labeled: String = { if (args(4) == "-labeled" && args(5) != null) args(5) else throw new Exception("no labeled argument defined: -labeled [true|flase]") }
  val classType: String = { if (args(6) == "-type" && args(7) != null) args(7) else throw new Exception("no type argument defined: -type [NB|LR|SVM]") }

  println("trainDataPath = " + trainDataPath)
  println("testDataPath  = " + testDataPath)
  println("labeled = " + labeled)
  println("classType     = " + classType)

  val l = {
    if (labeled.equals("true"))
      true
    else if (labeled.equals("false"))
      false
    else
      throw new Exception("labeled argument should be [true|flase] but is : " + labeled)
  }

  val c = initClassificationType(classType);

  c.process();

  def initClassificationType(t: String): Classification = t match {
    case "NB"  => new NaiveBayseClassification(trainDataPath, testDataPath, l)
    case "LR"  => new LogistictRegressionClassification(trainDataPath, testDataPath, l)
    case "SVM" => new SvmClassification(trainDataPath, testDataPath, l)
    case _     => throw new Exception("-type should be [NB|LR|SVM] but is : " + t)
  }

}




