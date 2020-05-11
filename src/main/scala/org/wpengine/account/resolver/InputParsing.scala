package org.wpengine.account.resolver

import java.io.InputStream

import io.circe.Json
import io.circe.generic.auto._
import org.wpengine.account.resolver.Domain.{CompositeRecord, CsvAccountRecord, RemoteAccountRecord}

import scala.io.BufferedSource
import scala.util.Try

object InputParsing {
  val csvDelimiter = ","

  /**
   *
   * @param line is the CSV string for the record
   * @return return deserialized record of CSV string
   */
  def parseCsvLine(line: String): Try[CsvAccountRecord] = Try{
    val tokens = line.split(csvDelimiter).map(Option(_))
    if(tokens.length == 4){
      CsvAccountRecord(tokens(0).get.toLong, tokens(1), tokens(2), tokens(3))
    } else {
      throw new IllegalArgumentException(s"Line: ($line) does not have 4 comma-delimited values")
    }
  }

  /**
   *
   * @param source is the stream of CSV strings
   * @return a tuple that consists of (original-string, Try[CsvAccountRecord]).  The Try indicates that parsing may have failed.
   */
  def parseCsvInputStream(source: BufferedSource): Seq[CompositeRecord[CsvAccountRecord]] = {
    source.getLines().toStream.tail.map {line =>
      (line, parseCsvLine(line))
    }//Done to skip header
  }

  /**
   *
   * @param json is an object returned by the fetcher
   * @return a Try[RemoteAccountRecord].  Try indicates that parsing may have failed
   */
  def parseRemoteJsonObject(json: Json): Try[RemoteAccountRecord] = {
    json.as[RemoteAccountRecord].toTry
  }
}
