package assignment.io

import ch.ethz.dal.tinyir.processing.Document
import java.io.File
import scala.xml.XML
import scala.xml.Elem
import ch.ethz.dal.tinyir.processing.Tokenizer
import scala.annotation.tailrec

class MyXMLDocument(f: File) extends Document {

  val isFile: Boolean = f.isFile()

  private def xml: Elem = {
    if (isFile) {
      XML.loadFile(f)
    } else {
      null
    }
  }

  override def title: String = ""
  override def body: String = {
    if (isFile) {
      val text = xml \ "TEXT"
      text.map{ t => t.text }.mkString(" ")
    } else {
      ""
    }
  }
  override def name: String = {
    if (isFile) {
      (xml \ "DOCNO").text.filter(_.isLetterOrDigit)
    } else {
      ""
    }
  }
  override def date: String = ""
  override def content: String = body

}

object MyXMLDocument { 
    def main(args: Array[String]) {
      val path = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/zips/ap880315/AP880315-0009"
      val doc = new MyXMLDocument(new File(path))
      println("TITLE=" + doc.title)
      println("BODY=" + doc.body)
      println("NAME=" + doc.name)
      println("TOKENS=" + doc.tokens)
    }
}