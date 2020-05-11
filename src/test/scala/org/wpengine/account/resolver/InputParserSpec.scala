package org.wpengine.account.resolver

import java.io.{BufferedWriter, FileWriter}

import org.scalatest.{FlatSpec, Matchers}
import InputParsing._
import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto._
import org.http4s.circe._
import io.circe.syntax._
import org.http4s.HttpService
import org.http4s.client.Client
import org.http4s.client.blaze.Http1Client
import org.http4s.dsl.impl.Root
import org.http4s.dsl.io._

import scala.io.{BufferedSource, Source}
import scala.io.Source._

class InputParserSpec extends FlatSpec with Matchers {
  val app = HttpService[IO] {
    case GET -> Root / "v1" / "accounts" / account =>
      Ok(Json.obj(
        "account_id" -> account.toLong.asJson,
        "status" -> "cool cucumber".asJson,
        "created_on" -> "2015-08-08".asJson
      ))
  }
  val client: Client[IO] = Client.fromHttpService(app)

  "parsing test one" should "succeed" in {
    val testFetcher = SimpleRemoteFetcher(client, "http://nowhere:8080/v1/accounts")
    val fetched = testFetcher.fetchAccount(12345)
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
