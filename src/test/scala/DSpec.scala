import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class DSPec extends AsyncFreeSpec with AsyncIOSpec {

  "Document" - {
    "Should be properly created" in {
      val d = D[Int](4, 5)
      d.doc shouldBe List.fill(5)(List.fill(4)(Option.empty[Int]))
      d.height shouldBe 5
      d.width shouldBe 4
    }

    "Should properly create and delete rows and columns" in {
      val d = D[Int](4, 5)
      val dNewRow = d.insertRow(1)
      val dNewColumn = d.insertColumn(1)
      val dDelRow = d.deleteRow(1)
      val dDelColumn = d.deleteColumn(1)

      dNewRow.doc shouldBe List.fill(6)(List.fill(4)(Option.empty[Int]))
      dNewRow.width shouldBe 4
      dNewRow.height shouldBe 6

      dNewColumn.doc shouldBe List.fill(5)(List.fill(5)(Option.empty[Int]))
      dNewColumn.width shouldBe 5
      dNewColumn.height shouldBe 5

      dDelRow.doc shouldBe List.fill(4)(List.fill(4)(Option.empty[Int]))
      dDelRow.width shouldBe 4
      dDelRow.height shouldBe 4

      dDelColumn.doc shouldBe List.fill(5)(List.fill(3)(Option.empty[Int]))
      dDelColumn.width shouldBe 3
      dDelColumn.height shouldBe 5
    }

    "Should properly set, give and clear value" in {
      val d = D[Int](4, 5)
      val setValueD = d.setValue(0, 0, 4)
      val clearValue = setValueD.clearValue(0, 0)
      val value = setValueD.getValue(0, 0)
      setValueD.getValue(0, 0) shouldBe Some(4)
      clearValue.getValue(0, 0) shouldBe Option.empty[Int]
      value shouldBe Some(4)
    }
  }
}
