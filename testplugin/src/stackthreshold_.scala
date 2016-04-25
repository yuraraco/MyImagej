/**
  * Created by kawasaki on 2016/04/08.
  */

import ij._
import ij.plugin.{Duplicator => DC, ImageCalculator => ICAL}
import ij.process.{AutoThresholder => AT}

class stackthreshold_ {
  val methods = AT.getMethods()
  val imp1 = IJ.getImage()
  val imp2 = new DC().run(imp1)
  val stack = imp2.getStack()
  for(i <- 1 to imp2.getStackSize) {
    val ip = stack.getProcessor(i)
    ip.setAutoThreshold("Shanbhag dark")
    val lower = ip.getMinThreshold()
    //val upper = ip.getMaxThreshold()
    ip.threshold(lower toInt)
    //imp2.updateImage
  }
  //imp2.updateAndDraw()
  val ic = new ICAL
  val imp3 = ic.run("Subtract stack", imp1, imp2)

  //imp3.show()

}
