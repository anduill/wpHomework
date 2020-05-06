package org.wpengine.account.resolver

object DomainObjects {
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
}
