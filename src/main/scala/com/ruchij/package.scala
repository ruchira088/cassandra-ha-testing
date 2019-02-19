package com
import io.getquill.{CassandraAsyncContext, SnakeCase}
import scalaz.OptionT

import scala.concurrent.Future
import scala.language.higherKinds

package object ruchij {
  type FutureOpt[A] = OptionT[Future, A]

  type AsyncCassandraContext = CassandraAsyncContext[SnakeCase]
}
