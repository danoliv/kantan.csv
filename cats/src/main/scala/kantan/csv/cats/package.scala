package kantan.csv

import _root_.cats._
import _root_.cats.data.Xor
import _root_.cats.functor.Contravariant
import kantan.csv

/** Declares various type class instances for bridging `kantan.csv` and `cats`. */
package object cats extends kantan.codecs.cats.CatsInstances {
  implicit def xorCellDecoder[A, B](implicit da: CellDecoder[A], db: CellDecoder[B]): CellDecoder[Xor[A, B]] =
      CellDecoder(s ⇒ da.decode(s).map(a ⇒ Xor.Left(a)).orElse(db.decode(s).map(b ⇒ Xor.Right(b))))

  implicit def xorCellEncoder[A, B](implicit ea: CellEncoder[A], eb: CellEncoder[B]): CellEncoder[Xor[A, B]] =
      CellEncoder(eab ⇒ eab match {
        case Xor.Left(a)  ⇒ ea.encode(a)
        case Xor.Right(b) ⇒ eb.encode(b)
      })

  implicit def xorRowDecoder[A, B](implicit da: RowDecoder[A], db: RowDecoder[B]): RowDecoder[Xor[A, B]] =
      RowDecoder(row ⇒ da.decode(row).map(a ⇒ Xor.Left(a)).orElse(db.decode(row).map(b ⇒ Xor.Right(b))))

  implicit def xorRowEncoder[A, B](implicit ea: csv.RowEncoder[A], eb: csv.RowEncoder[B]): csv.RowEncoder[Xor[A, B]] =
    csv.RowEncoder(xab ⇒ xab match {
      case Xor.Left(a)  ⇒ ea.encode(a)
      case Xor.Right(b) ⇒ eb.encode(b)
    })

  implicit def foldableRowEncoder[A, F[_]](implicit ea: CellEncoder[A], F: Foldable[F]): RowEncoder[F[A]] =
    csv.RowEncoder(as ⇒ F.foldLeft(as, Seq.newBuilder[String])((acc, a) ⇒ acc += ea.encode(a)).result())



  // - CSV input / output ----------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  /** `Contravariant` instance for `CsvInput`. */
  implicit val csvInput: Contravariant[CsvInput] = new Contravariant[CsvInput] {
    override def contramap[A, B](r: CsvInput[A])(f: B ⇒ A) = r.contramap(f)
  }

  /** `Contravariant` instance for `CsvOutput`. */
  implicit val csvOutput: Contravariant[CsvOutput] = new Contravariant[CsvOutput] {
    override def contramap[A, B](r: CsvOutput[A])(f: B ⇒ A) = r.contramap(f)
  }


  // - CsvError --------------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------------------------
  implicit val csvErrorEq: Eq[CsvError] = new Eq[CsvError] {
    override def eqv(x: CsvError, y: CsvError): Boolean = x == y
  }

  implicit val decodeErrorEq: Eq[DecodeError] = new Eq[DecodeError] {
      override def eqv(x: DecodeError, y: DecodeError): Boolean = x == y
    }
}