/**
  * Created by kawasaki on 2016/01/15.
  */

import ij._
import ij.plugin.{ContrastEnhancer => CE, Duplicator => DC, Filters3D => F3D, GaussianBlur3D => GB3D, ImageCalculator => ICAL, ZProjector => ZP}
import ij.process.{ImageConverter => ICON}
import ij.plugin.frame.RoiManager
import org.bytedeco.javacpp.opencv_core._
import org.bytedeco.javacpp.opencv_core.IplImage
import org.bytedeco.javacpp.opencv_imgproc._
import org.bytedeco.javacpp.opencv_highgui._
import org.bytedeco.javacpp.opencv_video._
import org.bytedeco.javacv._
import org.bytedeco.javacpp.opencv_video._
import org.bytedeco.javacpp.opencv_imgcodecs._


class testscala1_ extends plugin.PlugIn {
  // プラグイン起動時に ImageJ 本体から呼ばれるメソッド．
  def run(arg: String) = {

    val gd = new gui.GenericDialog("parms")
    val imp1 = IJ.getImage

    gd.addNumericField("rx:", 10.0, 1)
    gd.addNumericField("ry:", 10.0, 1)
    gd.addNumericField("rz:", 5.0, 1)
    val tmethod = Array("Default", "Huang", "Intermodes", "IsoData", "Li", "MaxEntropy", "Mean", "MinError(I)", "Minimum", "Moments", "Otsu", "Percentile", "RenyiEntropy", "Shanbhag", "Triangle", "Yen")
    gd.addChoice("threshold method:", tmethod, "Otsu")
    val fmethod = Array("MEAN", "MEDIAN", "MIN", "MAX", "VAR", "MAXLOCAL", "GAUSSIAN")
    gd.addChoice("filter method:", fmethod, "GAUSSIAN")
    gd.addCheckbox("create new window", false)
    gd.addNumericField("Number of Cols:", 1, 0)
    gd.addNumericField("Number of Rows:", 10, 0)
    gd.showDialog

    if (!gd.wasCanceled) {

      if (gd.getNextBoolean) imp1.duplicate.show

      val start = java.lang.System.currentTimeMillis.toDouble

      Gray(imp1)
      //val imp2 = fcon(imp1)
      dellight(imp1, gd.getNextChoice)
      subtavet(imp1)
      difstack(imp1)
      filter3D(imp1, gd.getNextNumber, gd.getNextNumber, gd.getNextNumber, gd.getNextChoiceIndex)
      enhancecont(imp1)
      cint(imp1, gd.getNextNumber toInt, gd.getNextNumber toInt)

      imp1.show

      val delta = java.lang.System.currentTimeMillis.toDouble - start
      IJ.error(delta + "ms")
    }
  }

  // GRAY scaleじゃないなら変換する
  def Gray(imp: ImagePlus): ImagePlus = {
    if (imp.getBitDepth != 8) {
      val ic = new ICON(imp)
      ic.convertToGray8
    }
    imp
  }

  // 細胞体を除去する
  private def dellight(imp: ImagePlus, methods: String): ImagePlus = {
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
    ic.run("Subtract stack", imp, subtimp)
    //newimp.show
    //IJ.getImage
  }

  // 背景を引く
  def subtavet(imp: ImagePlus) = {
    val ZPimg = new ZP(imp)
    ZPimg.setMethod(ZP.MEDIAN_METHOD)
    ZPimg.setImage(imp)
    ZPimg.doProjection
    val avest = ZPimg.getProjection
    val ic = new ICAL
    ic.run("Subtract stack", imp, avest)
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

  // コントラスト強調
  private def enhancecont(imp: ImagePlus) = {
    val ce = new CE
    val stack = imp.getStack
    ce.setNormalize(true)
    //ce.setUseStackHistogram(true)
    ce.stretchHistogram(imp, 0.4)
    for (i <- 1 to stack.size) {
      ce.equalize(stack.getProcessor(i))
      imp.updateAndDraw
    }
  }

  //  def fcon(imp: ImagePlus) = {
  //    val ip = imp.getProcessor
  //    val ipb = ip.getBufferedImage
  //    val OCVFC = new OpenCVFrameConverter.ToIplImage
  //    val J2DFC = new Java2DFrameConverter
  //    val IPlimg = OCVFC.convert(J2DFC.convert(ipb))
  //    val frame = new CanvasFrame(imp.getTitle)
  //    //return IPlimg
  //    cvShowImage("test",IPlimg)
  //  }

  //ROIを計測する
  def cint(imp: ImagePlus, c: Int, r: Int) = {
    val RW = imp.getWidth / c
    val RH = imp.getHeight / r
    val RM = new RoiManager
    for (i <- 1 to c ; j <- 1 to r) {
      imp.setRoi(RW * (i - 1), RH * (j - 1), RW, RH)
      RM.addRoi(imp.getRoi)
    }
    val Res = RM.multiMeasure(imp)
    Res.show("results")
  }

  def difstack(imp: ImagePlus) = {
    val stack = imp.getStack
    val imp1 = new DC().run(imp, 2, stack.size)
    val imp2 = new DC().run(imp, 1, stack.size - 1)
    val ic = new ICAL
    val newimp = ic.run("Subtract stack", imp1, imp2)
    IJ.getImage
  }

}