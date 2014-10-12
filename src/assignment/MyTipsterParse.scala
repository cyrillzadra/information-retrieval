package assignment

import java.io.InputStream

import org.w3c.dom.{Document => XMLDoc}

import ch.ethz.dal.tinyir.processing.XMLDocument

class MyTipsterParse(is: InputStream) extends XMLDocument(is) {
  override def title: String = ""
  override def ID = name.hashCode()
  override def body: String = read(doc.getElementsByTagName("TEXT"))
  override def name: String = read(doc.getElementsByTagName("DOCNO")).filter(_.isLetterOrDigit)
  override def date: String = ""
  override def content: String = body
}
