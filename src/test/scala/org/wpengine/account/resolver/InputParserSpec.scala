package org.wpengine.account.resolver

import java.io.{BufferedWriter, FileWriter, StringWriter}

import org.scalatest.{FlatSpec, Matchers}
import InputParsing._
import cats.effect.IO
import io.circe.generic.auto._
import org.http4s.circe._
import io.circe.syntax._
import org.http4s.HttpService
import org.http4s.client.Client
import org.http4s.dsl.impl.Root
import org.http4s.dsl.io._
import org.wpengine.account.resolver.Domain.RemoteAccountRecord

import scala.io.BufferedSource
import scala.io.Source._

class InputParserSpec extends FlatSpec with Matchers {
  val sampleData: Map[Long, RemoteAccountRecord] =
    Map[Long, RemoteAccountRecord](
      222222l -> RemoteAccountRecord(222222,
                                     Some("active"),
                                     Some("2012-03-01")),
      48213l -> RemoteAccountRecord(48213, Some("closed"), Some("2012-03-02")),
      918299l -> RemoteAccountRecord(918299,
                                     Some("active"),
                                     Some("2012-03-03")),
      88888l -> RemoteAccountRecord(88888, Some("closed"), Some("2012-03-04"))
    )
  val app = HttpService[IO] {
    case GET -> Root / "v1" / "accounts" / account =>
      sampleData.get(account.toLong) match {
        case Some(value) => Ok(value.asJson)
        case None        => NotFound()
      }
  }
  val client: Client[IO] = Client.fromHttpService(app)
  val expectedString1 = """Account ID,First Name,Created On,Status,Status Set On
                                 |222222,Ra's,2012-03-01,active,2012-03-01
                                 |48213,Wilson,2015-07-07,closed,2012-03-02
                                 |918299,Norman,2014-04-29,active,2012-03-03
                                 |88888,Otto,2013-08-08,closed,2012-03-04
                                 |6666,honeybooboo,dontExist,2012-03-14""".stripMargin
  val expectedString2 = """Account ID,First Name,Created On,Status,Status Set On
                          |222222,Ra's,2012-03-01,active,2012-03-01
                          |48213,Wilson,2015-07-07,closed,2012-03-02
                          |918299,Norman,2014-04-29,active,2012-03-03
                          |88888,Otto,2013-08-08,closed,2012-03-04
                          |88888,bad,csv,weird,,Exist,2012-03-14""".stripMargin
  val expectedString3 = """Account ID,First Name,Created On,Status,Status Set On
                          |222222,Ra's,2012-03-01,active,2012-03-01
                          |48213,Wilson,2015-07-07,closed,2012-03-02
                          |918299,Norman,2014-04-29,active,2012-03-03
                          |88888,Otto,2013-08-08,closed,2012-03-04
                          |notaNumber,honeybooboo,dontExist,2012-03-14""".stripMargin

  "parsing test one" should "succeed" in {
    val test_file: BufferedSource = fromResource("test_input1.csv")
    val fetcher = SimpleRemoteFetcher(client, "http://nowhere:8080")
    val stringWriter = new StringWriter()
    val writer = new BufferedWriter(stringWriter)
    val csvRecords = parseCsvInputStream(test_file)
    val joinedRecords = AccountMerger.streamJoinedAccounts(csvRecords, fetcher)
    val ioRunner = OutputRecords.writeOutCsvRecords(joinedRecords, writer)
    ioRunner.unsafeRunSync()
    val actualString = stringWriter.toString
    actualString shouldBe expectedString1
    test_file.close()
  }

  "parsing test two" should "succeed" in {
    val test_file: BufferedSource = fromResource("test_input2.csv")
    val fetcher = SimpleRemoteFetcher(client, "http://nowhere:8080")
    val stringWriter = new StringWriter()
    val writer = new BufferedWriter(stringWriter)
    val csvRecords = parseCsvInputStream(test_file)
    val joinedRecords = AccountMerger.streamJoinedAccounts(csvRecords, fetcher)
    val ioRunner = OutputRecords.writeOutCsvRecords(joinedRecords, writer)
    ioRunner.unsafeRunSync()
    val actualString = stringWriter.toString
    actualString shouldBe expectedString2
    test_file.close()
  }

  "parsing test three" should "succeed" in {
    val test_file: BufferedSource = fromResource("test_input3.csv")
    val fetcher = SimpleRemoteFetcher(client, "http://nowhere:8080")
    val stringWriter = new StringWriter()
    val writer = new BufferedWriter(stringWriter)
    val csvRecords = parseCsvInputStream(test_file)
    val joinedRecords = AccountMerger.streamJoinedAccounts(csvRecords, fetcher)
    val ioRunner = OutputRecords.writeOutCsvRecords(joinedRecords, writer)
    ioRunner.unsafeRunSync()
    val actualString = stringWriter.toString
    actualString shouldBe expectedString3
    test_file.close()
  }

}
