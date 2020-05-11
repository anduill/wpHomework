package org.wpengine.account.resolver

import java.io.InputStream

import io.circe.Json
import io.circe.generic.auto._
import org.wpengine.account.resolver.Domain.{CompositeRecord, CsvAccountRecord, RemoteAccountRecord}

import scala.io.BufferedSource
import scala.util.Try

object InputParsing {
  val csvDelimiter = ","
  def parseCsvLine(line: String): Try[CsvAccountRecord] = Try{
    val tokens = line.split(csvDelimiter).map(Option(_))
    if(tokens.length == 4){
      CsvAccountRecord(tokens(0).get.toLong, tokens(1), tokens(2), tokens(3))
    } else {
      throw new IllegalArgumentException(s"Line: ($line) does not have 4 comma-delimited values")
    }
  }
  def parseCsvInputStream(source: BufferedSource): Seq[CompositeRecord[CsvAccountRecord]] = {
    source.getLines().toStream.tail.map {line =>
      (line, parseCsvLine(line))
    }//Done to skip header
  }
  def parseRemoteJsonObject(json: Json): Try[RemoteAccountRecord] = {
    json.as[RemoteAccountRecord].toTry
  }
}
