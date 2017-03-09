package com.peach

import com.peach.arvo.CustomerAddress
import org.apache.avro.mapred.{AvroInputFormat, AvroWrapper}
import org.apache.hadoop.io.NullWritable
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by peach on 2017/3/4 0004.
  * 使用的是spark2.1.0版本，在spark1.6.3版本，无法创建dataset，报错：
  * Exception:org.apache.spark.sql.AnalysisException: Unable to generate an encoder for inner class `....` without access to the scope that this class was defined in. Try moving this class out of its parent class.;
  *
  */
object ScalaReadAvro {

  case class CustomerAddressData(ca_address_sk: Long,
                                 ca_address_id: String,
                                 ca_street_number: String,
                                 ca_street_name: String,
                                 ca_street_type: String,
                                 ca_suite_number: String,
                                 ca_city: String,
                                 ca_county: String,
                                 ca_state: String,
                                 ca_zip: String,
                                 ca_country: String,
                                 ca_gmt_offset: Double,
                                 ca_location_type: String
                                )
//  org.apache.spark.sql.catalyst.encoders.OuterScopes.addOuterScope(this)

  def main(args: Array[String]): Unit = {
    val path = "/Users/zoulihan/Desktop/customeraddress.avro"
    val conf = new SparkConf().setAppName("test").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    import sqlContext.implicits._

    val _rdd = sc.hadoopFile[AvroWrapper[CustomerAddress], NullWritable, AvroInputFormat[CustomerAddress]](path)
    val ddd = _rdd.map(line => new CustomerAddressData(
      line._1.datum().getCaAddressSk,
      line._1.datum().getCaAddressId.toString,
      line._1.datum().getCaStreetNumber.toString,
      line._1.datum().getCaStreetName.toString,
      line._1.datum().getCaStreetType.toString,
      line._1.datum().getCaSuiteNumber.toString,
      line._1.datum().getCaCity.toString,
      line._1.datum().getCaCounty.toString,
      line._1.datum().getCaState.toString,
      line._1.datum().getCaZip.toString,
      line._1.datum().getCaCountry.toString,
      line._1.datum().getCaGmtOffset,
      line._1.datum().getCaLocationType.toString
    ))

    val ds = sqlContext.createDataset(ddd)
    ds.show()
  }
}
