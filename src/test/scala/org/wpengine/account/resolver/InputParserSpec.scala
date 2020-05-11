package org.wpengine.account.resolver

import java.io.{BufferedWriter, FileWriter}

import org.scalatest.{FlatSpec, Matchers}
import InputParsing._
import cats.effect.IO
import org.http4s.client.blaze.Http1Client

import scala.io.{BufferedSource, Source}
import scala.io.Source._

class InputParserSpec extends FlatSpec with Matchers {
  "parsing test one" should "succeed" in {
    val test_file: BufferedSource = fromResource("test_input.csv")
    val fetcher = SimpleRemoteFetcher(Http1Client[IO]().unsafeRunSync())
    val writer = new BufferedWriter(new FileWriter("test_output.csv"))
    val csvRecords = parseCsvInputStream(test_file)
    val joinedRecords = AccountMerger.streamJoinedAccounts(csvRecords, fetcher)
    val ioRunner = OutputRecords.writeOutCsvRecords(joinedRecords, writer)
    ioRunner.unsafeRunSync()
    test_file.close()
    println("done")
  }

}
