import java.io.{File, FileWriter}

import scala.util.Random
import java.util
object GenFile {
  def main(args: Array[String]): Unit = {

    val file=new File("D:\\WorkSpace\\Git\\LowMemoryBytesKeyMap\\src\\main\\resources\\data\\sid_len15.csv");
    val writer=new FileWriter(file)

    val maxLen = 59
    val str = "1234567890abcdefghigklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ-"
    var i = 0
    val map=new util.HashSet[String]();
    while (i < 2000000) {

//      val len = Random.nextInt(maxLen)+5
      val len = 15
      val uuid = Range(0, len).map(t => {
        str.charAt(Random.nextInt(str.length))
      }).mkString("")
      val sid=Math.abs(Random.nextLong())
      if(!map.contains(uuid)){
        map.add(uuid)
      val res=uuid + "\t" + sid
      writer.write(res+"\n")
      i +=1
      }
    }
    println("success write ,"+i)
    writer.close()
  }
}
