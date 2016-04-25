import ij.IJ
import ij.plugin.{ContrastEnhancer=>CE}

/**
  * Created by yurayura on 2016/04/23.
  */
class enhcont_ {
  val imp = IJ.getImage
  val stack = imp.getStack
  val ce = new CE
  //ce.setNormalize(true)
  //ce.setProcessStack(true)
  //ce.equalize(imp)
  ce.stretchHistogram(imp, 0.4)
  imp.updateAndDraw
  //imp.show
}