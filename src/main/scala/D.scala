import cats.Monad
import cats.effect.Ref
import cats.effect.kernel.Async
import cats.syntax.all._
import cats.instances.list._


trait Dtrait[A, Self[_]] {
  def insertRow(rowIndex: Int): Self[A]

  def insertColumn(columnIndex: Int): Self[A]

  def setValue(x: Int, y: Int, value: A): Self[A]

  def deleteRow(rowIndex: Int): Self[A]

  def deleteColumn(columnIndex: Int): Self[A]

  def getValue(x: Int, y: Int): Option[A]

  def clearValue(x: Int, y: Int): Self[A]

  def f[M[_] : Monad, A, B]: (A => M[B]) => Self[A] => M[Self[B]]
}

class D[A](val doc: List[List[Option[A]]], val width: Int, val height: Int) extends Dtrait[A, D] {
  def insertRow(rowIndex: Int): D[A] = {
    val newDoc: List[List[Option[A]]] =
      doc.take(rowIndex) ::: (List.fill(width)(Option.empty[A]) :: doc.drop(rowIndex))
    new D[A](newDoc, width, height + 1)
  }

  def insertColumn(columnIndex: Int): D[A] = {
    val newDoc: List[List[Option[A]]] = doc.map(row =>
      row.take(columnIndex) ::: (Option.empty[A] :: row.drop(columnIndex)))
    new D[A](newDoc, width + 1, height)
  }

  override def setValue(x: Int, y: Int, value: A): D[A] = {
    val updatedRow = doc(y).updated(x, Some(value))
    val newDoc = doc.updated(y, updatedRow)
    new D[A](newDoc, width, height)
  }

  override def deleteRow(rowIndex: Int): D[A] = {
    val newDoc = doc.take(rowIndex) ::: doc.drop(rowIndex + 1)
    new D[A](newDoc, width, height - 1)
  }

  override def deleteColumn(columnIndex: Int): D[A] = {
    val newDoc = doc.map(row => row.take(columnIndex) ::: row.drop(columnIndex + 1))
    new D[A](newDoc, width - 1, height)
  }

  override def getValue(x: Int, y: Int): Option[A] = doc(y)(x)

  override def clearValue(x: Int, y: Int): D[A] = {
    val updatedRow = doc(y).updated(x, Option.empty[A])
    val newDoc = doc.updated(y, updatedRow)
    new D[A](newDoc, width, height)
  }

  override def f[M[_] : Monad, A, B]: (A => M[B]) => D[A] => M[D[B]] =
    f =>
      d => {
        val newDocF = d.doc.traverse(_.traverse(_.traverse(f)))
        newDocF.map(newDoc => new D[B](newDoc, d.width, d.height))
      }
}

object D {
  def apply[A](width: Int, height: Int): D[A] = {
    val doc: List[List[Option[A]]] = List.fill(height)(List.fill(width)(Option.empty[A]))
    new D[A](doc, width, height)
  }
}

class DWrapper[A, F[_] : Async](dRef: Ref[F, D[A]]) {

  def insertRow(rowIndex: Int): F[Unit] = dRef.update(_.insertRow(rowIndex))

  def insertColumn(columnIndex: Int): F[Unit] = dRef.update(_.insertColumn(columnIndex))

  def setValue(x: Int, y: Int, value: A): F[Unit] = dRef.update(_.setValue(x, y, value))

  def deleteRow(rowIndex: Int): F[Unit] = dRef.update(_.deleteRow(rowIndex))

  def deleteColumn(columnIndex: Int): F[Unit] = dRef.update(_.deleteColumn(columnIndex))

  def getValue(x: Int, y: Int): F[Option[A]] = dRef.get.map(_.getValue(x, y))

  def clearValue(x: Int, y: Int): F[Unit] = dRef.update(_.clearValue(x, y))

}

object Dwrapper {
  def apply[F[_] : Async, A](width: Int, height: Int): F[DWrapper[A, F]] =
    Ref.of[F, D[A]](D(width, height)).map(ref =>
      new DWrapper[A, F](ref)
    )
}
