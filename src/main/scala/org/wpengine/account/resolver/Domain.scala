package org.wpengine.account.resolver

import scala.util.Try

object Domain {
  case class CsvAccountRecord(accountId: Long,
                              accountName: Option[String],
                              firstName: Option[String],
                              createdOn: Option[String])

  case class RemoteAccountRecord(account_id: Long,
                                 status: Option[String],
                                 created_on: Option[String])
  case class CombinedAccountRecord(accountId: Long,
                                   accountName: Option[String],
                                   firstName: Option[String],
                                   createdOn: Option[String],
                                   status: Option[String],
                                   statusOn: Option[String])

  /**
   *
   * @param comb is the combined record
   * @return CSV string for record
   */
  def combinedRecToCsv(comb: CombinedAccountRecord): String = {
    s"${comb.accountId.toString},${comb.firstName.getOrElse("")},${comb.createdOn.getOrElse(
      "")},${comb.status.getOrElse("")},${comb.statusOn.getOrElse("")}"
  }
  type OriginalInput = String
  type CompositeRecord[R] = (OriginalInput, Try[R])

  val inputHeaderString = "Account ID,Account Name,First Name,Created On"
  val outputHeaderString =
    "Account ID,First Name,Created On,Status,Status Set On"
}
