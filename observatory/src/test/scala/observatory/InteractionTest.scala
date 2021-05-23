package observatory

import java.io.File
import java.nio.file.Paths

trait InteractionTest extends UnitSuite {

  test("tile") {
    val image = Interaction.tile(stationYearlyTemperatures, temperatureColors, Tile(1, 3, 2))

    image.output(Paths.get(s"src/test/resources/test-$year-2-1-3-tile.png"))

    assert(image.width == 256)
    assert(image.height == 256)
  }

  ignore("generate images") {
    def generateImage(year: Year, tile: Tile, data: Iterable[(Location, Temperature)]): Unit = {
      val dir = new File(s"target/temperatures/$year/${tile.zoom}")

      val image = Interaction.tile(data, temperatureColors, tile)

      dir.mkdirs()
      val out = image.output(new File(s"${dir.getPath}/${tile.x}-${tile.y}.png"))
      println(out.getPath)
    }

    Interaction.generateTiles(Set((year, stationYearlyTemperatures)), generateImage)
  }

}
