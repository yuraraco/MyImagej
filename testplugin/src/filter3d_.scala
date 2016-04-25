/**
  * Created by kawasaki on 2016/01/29.
  */

import ij._
import ij.plugin.{GaussianBlur3D, Filters3D}

class filter3d_ {
  val imp = IJ.getImage
  val stack = imp.getStack
  val F3D = GaussianBlur3D.blur(imp, 2, 2, 2)
  //val F3D = Filters3D.filter(stack, Filters3D.MEDIAN, 2, 2, 2)
  //new ImagePlus(imp.getTitle, F3D).show
  imp.show
}
