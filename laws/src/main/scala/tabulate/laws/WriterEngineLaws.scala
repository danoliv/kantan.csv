package tabulate.laws

import tabulate.engine.WriterEngine
import tabulate.ops._

trait WriterEngineLaws {
  implicit def engine: WriterEngine

  def roundTrip(csv: List[List[Cell]]): Boolean = csv.asCsv(',').unsafeReadCsv[List, List[Cell]](',', false) == csv

  def noTrailingSeparator(csv: List[List[Cell.NonEscaped]]): Boolean =
    csv.asCsv(',').split("\n").map(_.trim).forall(!_.endsWith(","))

  // This test is slightly dodgy, but works: we're assuming that the data is properly serialized (this is checked by
  // roundTrip), an want to make sure that we get the right number of rows. The `trim` bit is to allow for the optional
  // empty row.
  def crlfAsRowSeparator(csv: List[List[Cell.NonEscaped]]): Boolean =
    csv.asCsv(',').trim.split("\r\n").length == csv.length
}

object WriterEngineLaws {
  def apply(e: WriterEngine): WriterEngineLaws = new WriterEngineLaws {
    override implicit val engine = e
  }
}