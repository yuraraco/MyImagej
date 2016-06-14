import ij.IJ
import ij.plugin.{Duplicator => DC, ImageCalculator => ICAL}

/**
  * Created by kawasaki on 2016/06/14.
  */
class diffstack_ {
  val imp = IJ.getImage
  val ss = imp.getStackSize()
  val imp1 = new DC().run(imp, 2, ss)
  val imp2 = new DC().run(imp, 1, ss - 1)
  val ic = new ICAL
  ic.run("Subtract stack", imp1, imp2)
  imp1.show
}
