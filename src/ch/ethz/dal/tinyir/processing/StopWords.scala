package ch.ethz.dal.tinyir.processing

object StopWords {
  val stopWords = Set("the", "a", "is", "it", "are")
  def filter(tokens: Seq[String]) = tokens.filter(stopWords)
  def filterNot(tokens: Seq[String]) = tokens.filterNot(stopWords)
}

object StopWordsMain {
  def main(args: Array[String]) = {

    val terms = Seq("the", "cyrill")

    println(StopWords.filter(terms))
    println(StopWords.filterNot(terms))

  }
}