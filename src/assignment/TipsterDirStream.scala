package assignment

import ch.ethz.dal.tinyir.io.DirStream
import ch.ethz.dal.tinyir.io.ParsedXMLStream
import ch.ethz.dal.tinyir.processing.XMLDocument
import ch.ethz.dal.tinyir.io.ZipDirStream

class TipsterDirStream (path: String, ext: String = "") 
extends ParsedXMLStream(new DirStream(path, "")){
  def stream : Stream[XMLDocument] = unparsed.stream.map(is => new MyTipsterParse(is))
  def length = unparsed.length 
}