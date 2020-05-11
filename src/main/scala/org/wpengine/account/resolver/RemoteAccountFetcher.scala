package org.wpengine.account.resolver

import org.wpengine.account.resolver.Domain.RemoteAccountRecord

import scala.util.Try

trait RemoteAccountFetcher {
  def fetchAccount(accountId: Long): Try[RemoteAccountRecord]
}

