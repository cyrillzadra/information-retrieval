package assignment

import ch.ethz.dal.tinyir.io.ZipDirStream
import ch.ethz.dal.tinyir.processing.XMLDocument
import ch.ethz.dal.tinyir.io.ParsedXMLStream
import ch.ethz.dal.tinyir.processing.TipsterParse
import ch.ethz.dal.tinyir.io.DirStream

class Tipster3Stream (path: String, ext: String = "") 
extends ParsedXMLStream(new DirStream(path, "")){
  def stream : Stream[XMLDocument] = unparsed.stream.map(is => new TipsterParse(is))
  def length = unparsed.length 
}