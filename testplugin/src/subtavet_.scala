
import ij.{IJ, ImagePlus}
import ij.plugin.{ImageCalculator => ICAL, ZProjector => ZP}

/**
  * Created by kawasaki on 2016/02/01.
  */
class subtavet_ {
  val imp = IJ.getImage
  val ZPimg = new ZP(imp)
  ZPimg.setMethod(ZP.MEDIAN_METHOD)
  ZPimg.setImage(imp)
  ZPimg.doProjection
  val avest = ZPimg.getProjection
  val ic = new ICAL
  ic.run("Subtract stack", imp, avest)
}
