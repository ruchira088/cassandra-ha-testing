package com.ruchij.models
import java.util.UUID

import com.github.javafaker.Faker
import org.joda.time.DateTime

object Random {

  val faker: Faker = Faker.instance()

  import faker._

  def uuid(): UUID = UUID.randomUUID()

  def boolean(): Boolean = faker.bool().bool()

  def user(): User =
    User(
      uuid(),
      DateTime.now(),
      name().firstName(),
      name().lastName(),
      number().numberBetween(0, 99),
      internet().emailAddress(),
      boolean()
    )
}
