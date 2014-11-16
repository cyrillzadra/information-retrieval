package ch.ethz.dal.tinyir.processing

import util.Try
import util.Success
import javax.xml.parsers._
import org.w3c.dom.{ Document => XMLDoc }
import java.io.InputStream
import ch.ethz.dal.tinyir.io.DocStream

class TipsterParse(is: InputStream) extends XMLDocument(is) {
  override def title: String = ""
  override def body: String = read(doc.getElementsByTagName("TEXT"))
  override def name: String = read(doc.getElementsByTagName("DOCNO")).filter(_.isLetterOrDigit)
  override def date: String = ""
  override def content: String = body
}

object TipsterParse {
  def main(args: Array[String]) {
    val dirname = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/"
    val fname = dirname + "/AP880212-0001"
    val parse = new TipsterParse(DocStream.getStream(fname))
    val name = parse.name
    println(name)
    val content = parse.content
    println(content.take(100) + "..." + content.takeRight(100))
  }
}
