package org.wpengine.account.resolver

import java.io.{BufferedWriter, FileWriter}

import cats.effect.IO
import com.typesafe.scalalogging.LazyLogging
import org.http4s.client.blaze.Http1Client
import org.wpengine.account.resolver.InputParsing.parseCsvInputStream
import pureconfig.ConfigReader.Result
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import scala.io.{BufferedSource, Source}

/**
 * This is the entry point for the program.  It first grabs configuration and then attempts to run the routine.
 */
object WPMerge extends App with LazyLogging {
  val config: Result[WPMergeConfig] = ConfigSource.default.load[WPMergeConfig]
  config match {
    case Left(failures) =>
      logger.error(s"Account merging configuration failures: ${failures.toList}")
      System.exit(1)
    case Right(config) =>
      val fetcher = SimpleRemoteFetcher(Http1Client[IO]().unsafeRunSync(), config.serviceLocation)
      val inputFile: BufferedSource = Source.fromFile(config.inputFile, "UTF-8")
      val fileWriter = new BufferedWriter(new FileWriter(config.outputFile))
      val csvRecords = parseCsvInputStream(inputFile)
      val joinedRecords = AccountMerger.streamJoinedAccounts(csvRecords, fetcher)
      val ioRunner = OutputRecords.writeOutCsvRecords(joinedRecords, fileWriter)
      ioRunner.unsafeRunSync()
      inputFile.close()
      System.exit(0)
  }
}
