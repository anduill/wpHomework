package org.wpengine.account.resolver

import org.wpengine.account.resolver.DomainObjects.RemoteAccountRecord

import scala.util.Try

trait RemoteAccountFetcher {
  def fetchAccount(accountId: Long): Try[RemoteAccountRecord]
}

