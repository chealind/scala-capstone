package observatory

trait Interaction2Test extends UnitSuite {

  test("year selection") {
    val layer = Signal(Layer(LayerName.Temperatures, temperatureColors.toSeq, 1970 to 2007))

    assert(Interaction2.yearSelection(layer, Signal(2001)).apply() == 2001)
    assert(Interaction2.yearSelection(layer, Signal(1970)).apply() == 1970)
    assert(Interaction2.yearSelection(layer, Signal(2007)).apply() == 2007)

    assert(Interaction2.yearSelection(layer, Signal(1965)).apply() == 1970)
    assert(Interaction2.yearSelection(layer, Signal(2011)).apply() == 2007)
  }
}
