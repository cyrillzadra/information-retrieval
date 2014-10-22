import java.nio.file.Path
import java.nio.file.Files
import java.nio.file.FileSystems
import scala.util.Try
import scala.collection.JavaConversions._
import java.io.InputStream
import java.io.File
import ch.ethz.dal.tinyir.io.DocStream
import ch.ethz.dal.tinyir.io.DirStream
import java.nio.file.Paths
import java.nio.file.DirectoryStream
import scala.io.Source
import scala.io.BufferedSource
import scala.io.BufferedSource
import scala.xml.XML
import ch.ethz.dal.tinyir.processing.Tokenizer

object StreamTest extends App {

  val px = "C:/dev/projects/eth/information-retrieval/course-material/assignment1/zips/zips-1-old/"

  protected def getFileTree(f: File): Stream[File] =
    f #:: (if (f.isDirectory) f.listFiles().toStream
    else Stream.empty)

  var nr = 0;
  for (doc <- getFileTree(new File(px))) {
    if (doc.isFile()) {
      nr += 1
      val bs: BufferedSource = Source.fromFile(doc)
      val xml = XML.loadFile(doc)
      
      val docNo = xml \ "DOCNO"
      val text = xml \ "TEXT"
      
      println(docNo.text.trim())
      println(text)
      val tokens = text.flatMap{ t => Tokenizer.tokenize(t.text) }
      println(tokens)
    }
  }
  println(nr)
}

