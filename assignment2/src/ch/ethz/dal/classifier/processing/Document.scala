package ch.ethz.dal.classifier.processing

import scala.util.Try
import assignment2.StopWords
import com.github.aztek.porterstemmer.PorterStemmer

abstract class Document {
  def title  : String 
  def body   : String
  def name   : String 
  def ID     : Int = Try(name.toInt).getOrElse(-1)  
  def date   : String
  def codes  : Set[String] = Set()  
  def topics : Set[String] = Set()  
  def content: String  
  def tokens : List[String] = StopWords.filterNot(Tokenizer.tokenize(PorterStemmer.stem(content))).toList
}
