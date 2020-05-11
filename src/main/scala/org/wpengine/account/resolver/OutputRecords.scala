package org.wpengine.account.resolver

import java.io.BufferedWriter

import cats.effect.IO
import com.typesafe.scalalogging.LazyLogging
import org.wpengine.account.resolver.Domain.{CombinedAccountRecord, CompositeRecord}

import scala.util.{Failure, Success}



object OutputRecords extends LazyLogging{
  /**
   *
   * @param recs are combined records that also include each original input string
   * @param writer is for writing the new CSV to the file
   * @return IO instance so that running the routine is delayed until explicitly calling run.
   */
  def writeOutCsvRecords(recs: Seq[CompositeRecord[CombinedAccountRecord]], writer: BufferedWriter): IO[Unit] = IO {
    writer.write(Domain.outputHeaderString)
    writeOutCsvRecordsHelper(recs, writer)
  }

  /**
   *
   * @param recs are combined records that also include each original input string
   * @param writer is for writing the new CSV to the file
   *               NOTE: this is a tail-recursive routine that terminates once the stream is emptied
   */
  def writeOutCsvRecordsHelper(recs: Seq[CompositeRecord[CombinedAccountRecord]], writer: BufferedWriter): Unit = {
    if(recs.isEmpty){
      writer.flush()
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
