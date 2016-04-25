/**
  * Created by kawasaki on 2016/01/15.
  */

import ij._
import ij.plugin.{Duplicator => DC, Filters3D => F3D, ImageCalculator => ICAL, ContrastEnhancer => CE, GaussianBlur3D => GB3D}
import ij.process.{ImageProcessor, ImageConverter => ICON}
import org.bytedeco.javacpp.opencv_core._
import org.bytedeco.javacpp.opencv_highgui._
import org.bytedeco.javacpp.opencv_video._

class testscala1_ extends plugin.PlugIn {
  // プラグイン起動時に ImageJ 本体から呼ばれるメソッド．
  def run(arg: String) = {

    val gd = new gui.GenericDialog("parms")
    val imp1 = IJ.getImage
    gd.addNumericField("rx:", 2.0, 1)
    gd.addNumericField("ry:", 5.0, 1)
    gd.addNumericField("rz:", 5.0, 1)
    gd.addChoice("threshold method:", Array("Default", "Huang", "Intermodes", "IsoData", "Li", "MaxEntropy", "Mean", "MinError(I)", "Minimum", "Moments", "Otsu", "Percentile", "RenyiEntropy", "Shanbhag", "Triangle", "Yen"), "Shanbhag")
    gd.addChoice("filter method:", Array("MEAN", "MEDIAN", "MIN", "MAX", "VAR", "MAXLOCAL", "GAUSSIAN"), "MEDIAN")
    gd.addCheckbox("create new window", false)
    gd.addNumericField("slices:", 8.0, 1)
    gd.showDialog

    if (!gd.wasCanceled) {

      if (gd.getNextBoolean) imp1.duplicate.show

      val start = java.lang.System.currentTimeMillis.toDouble

      Gray(imp1)

      dellight(imp1, gd.getNextChoice)

      enhancecont(imp1)

      filter3D(imp1, gd.getNextNumber, gd.getNextNumber, gd.getNextNumber, gd.getNextChoiceIndex)

      //val imp4 = enhancecont(imp3)
      imp1.show
      //new ImagePlus(imp1.getTitle, imp1.getStack).show
      val delta = java.lang.System.currentTimeMillis.toDouble - start
      IJ.error(delta + "ms")
    }
  }

  // GRAY scaleじゃないなら変換する
  def Gray(imp: ImagePlus) = {
    if (imp.getBitDepth != 8) {
      val ic = new ICON(imp)
      ic.convertToGray8
    }
  }

  // 細胞体を除去する
  private def dellight(imp: ImagePlus, methods: String) = {
    val subtimp = new DC().run(imp)
    val stack = subtimp.getStack()
    for (i <- 1 to subtimp.getStackSize) {
      val ip = stack.getProcessor(i)
      ip.setAutoThreshold(methods + " dark")
      val lower = ip.getMinThreshold()
      //val upper = ip.getMaxThreshold()
      ip.threshold(lower toInt)
      //subtimp.updateImage
    }
    val ic = new ICAL
    val newimp = ic.run("Subtract stack", imp, subtimp)
    //newimp.show
    IJ.getImage
  }

  // 三次元フィルター
  private def filter3D(imp: ImagePlus, rx: Double, ry: Double, rz: Double, mindex: Int) = {
    val stack = imp.getStack
    if (mindex == 6) GB3D.blur(imp, rx, ry, rz)
    else {
      val filterindex = mindex + 10
      val F3Dimg = F3D.filter(stack, filterindex, rx toFloat, ry toFloat, rz toFloat)
      for (i <- 1 to stack.size) {
        stack.setProcessor(F3Dimg.getProcessor(i), i)
        imp.updateAndDraw
      }
    }
  }

  private def enhancecont(imp: ImagePlus) = {
    val ce = new CE
    ce.setNormalize(true)
    ce.setProcessStack(true)
    ce.stretchHistogram(imp, 0.4)
    imp.updateAndDraw()
    IJ.getImage
  }

  //  def(){
  //   }
}