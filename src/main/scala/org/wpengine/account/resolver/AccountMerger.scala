package org.wpengine.account.resolver

import org.wpengine.account.resolver.Domain.{
  CombinedAccountRecord,
  CompositeRecord,
  CsvAccountRecord,
  RemoteAccountRecord
}

import scala.util.Try

object AccountMerger {
  def joinAccounts(
      csvAccount: Try[CsvAccountRecord],
      remoteAccount: Try[RemoteAccountRecord]): Try[CombinedAccountRecord] = {
    val result = for {
      csvAcct <- csvAccount
      remoteAcct <- remoteAccount
    } yield {
      if (csvAcct.accountId != remoteAcct.account_id) {
        throw new IllegalArgumentException(
          s"csvAccountId ${csvAcct.accountId} does not match remoteAcctId ${remoteAcct.account_id}")
      } else {
        CombinedAccountRecord(
          accountId = csvAcct.accountId,
          accountName = csvAcct.accountName,
          firstName = csvAcct.firstName,
          createdOn = csvAcct.createdOn,
          status = remoteAcct.status,
          statusOn = remoteAcct.created_on
        )
      }
    }
    result
  }
  def streamJoinedAccounts(csvStream: Seq[CompositeRecord[CsvAccountRecord]],
                           fetcher: RemoteAccountFetcher): Seq[CompositeRecord[CombinedAccountRecord]] = {
    csvStream.map {compRecord =>
      val remoteRecord = compRecord._2.flatMap {csvRec => fetcher.fetchAccount(csvRec.accountId)}
      (compRecord._1, joinAccounts(compRecord._2, remoteRecord))
    }
  }

}
