package assignment.util

import ch.ethz.dal.tinyir.processing.Document

class TestDocument(val id: String, val s: String) extends Document {
  def title = ""
  def body = s
  def name = ""
  override def ID = id.hashCode()
  def date = ""
  def content = body
}