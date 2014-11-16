package assignment.io

import java.io.File
import ch.ethz.dal.tinyir.processing.TipsterParse
import scala.annotation.tailrec

class MyStream(path: String)  {
  
	private val f : File = new File(path)

    private def unparsed: Stream[File] = 
      f #:: (if (f.isDirectory) f.listFiles().toStream
    else Stream.empty)

    def stream : Stream[MyXMLDocument] = unparsed.map(file => new MyXMLDocument(file))

}