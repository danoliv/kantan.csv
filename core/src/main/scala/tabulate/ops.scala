package tabulate

import java.io.StringWriter

import tabulate.engine.WriterEngine

object ops extends CsvInput.ToCsvInputOps
                   with CsvOutput.ToCsvOutputOps
                   with RowEncoder.ToRowEncoderOps
                   with CellEncoder.ToCellEncoderOps {
  implicit class CellDecoderOps(val str: String) extends AnyVal {
    def decodeCsvCell[A](implicit da: CellDecoder[A]): DecodeResult[A] = da.decode(str)
    def unsafeDecodeCsvCell[A](implicit da: CellDecoder[A]): A = da.unsafeDecode(str)
  }

  implicit class RowDecoderOps(val row: Seq[String]) extends AnyVal {
    def decodeCsvRow[A](implicit da: RowDecoder[A]): DecodeResult[A] = da.decode(row)
    def unsafeDecodeCsvRow[A](implicit da: RowDecoder[A]): A = da.unsafeDecode(row)
  }

  implicit class TraversableOps[A: RowEncoder](val rows: Traversable[A]) {
    def asCsv(sep: Char, header: Seq[String] = Seq.empty)(implicit engine: WriterEngine): String = {
      val out = new StringWriter()
      CsvWriter(out, sep, header).write(rows)
      out.toString
    }
  }
}
