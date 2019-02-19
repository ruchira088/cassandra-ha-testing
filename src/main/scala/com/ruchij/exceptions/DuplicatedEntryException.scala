package com.ruchij.exceptions

case class DuplicatedEntryException[A](values: List[A]) extends Exception {
  override def getMessage: String =
    s"Duplicate entries found: [${values.mkString(",")}]"
}
