package org.wpengine.account.resolver

import java.io.BufferedWriter

import cats.effect.IO
import com.typesafe.scalalogging.LazyLogging
import org.wpengine.account.resolver.Domain.{CombinedAccountRecord, CompositeRecord}

import scala.util.{Failure, Success}



object OutputRecords extends LazyLogging{
  def writeOutCsvRecords(recs: Seq[CompositeRecord[CombinedAccountRecord]], writer: BufferedWriter): IO[Unit] = IO {
    writer.write(Domain.outputHeaderString)
    writeOutCsvRecordsHelper(recs, writer)
  }
  def writeOutCsvRecordsHelper(recs: Seq[CompositeRecord[CombinedAccountRecord]], writer: BufferedWriter): Unit = {
    if(recs.isEmpty){
      writer.close()
    } else {
      writer.newLine()
      val (originalString, combinedRec) = recs.head
      combinedRec match {
        case Failure(exception) =>
          logger.error(s"Exception for CSV Record: $originalString. Writing original CSV to output ** Reason: ${exception.getMessage}")
          writer.write(originalString)
        case Success(combinedRec) => writer.write(Domain.combinedRecToCsv(combinedRec))
      }
      writeOutCsvRecordsHelper(recs.tail, writer)
    }
  }
}
