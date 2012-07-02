package com.aceevo.riak.driver.impl

import com.basho.riak.client.{IRiakObject, IRiakClient}
import com.basho.riak.client.bucket.Bucket
import com.basho.riak.client.query.indexes.BinIndex
import collection.mutable.ListBuffer
import com.codahale.logula.Logging
import com.basho.riak.client.convert.Converter
import collection.JavaConversions._
import com.aceevo.riak.driver.RiakStorageDriver

/**
 * Created with IntelliJ IDEA.
 * User: rjenkins
 * Date: 5/17/12
 * Time: 7:48 PM
 * To change this template use File | Settings | File Templates.
 */

class RiakDriver[T](bucket: String,
                    riakClient: IRiakClient) extends RiakStorageDriver[String, T] with Logging {

  def getByKey(key: String, converter: Converter[T]): Option[T] = {
    val data: IRiakObject = getBucket.fetch(key).execute()
    if (data != null)
      new Some(converter.toDomain(data))
    else
      None

  }

  def persist(key: String, t: T, converter: Converter[T]): T = {
    getBucket.store(key, t).withConverter(converter).returnBody(true).execute()
  }

  def delete(t: T) {
    getBucket.delete(t).execute()
  }

  def deleteByKey(key: String) {
    getBucket.delete(key).execute()
  }

  // Find all records of Type T for Matching 2i query
  def findFor2i(index: (String, String), converter: Converter[T]): List[T] = {

    val keys = getBucket.fetchIndex(BinIndex.named(index._1)).withValue(index._2).execute()
    val listBuffer = new ListBuffer[T]

    for (key <- keys) {
      listBuffer.prepend(converter.toDomain(getBucket.fetch(key).execute()))
    }

    listBuffer.toList
  }

  def deleteFor2i(index: (String, String)) {

    val keys = getBucket.fetchIndex(BinIndex.named(index._1)).withValue(index._2).execute()

    for (key <- keys) {
      getBucket.delete(key).execute()
    }
  }

  // Utility method to retrieve a bucket
  private def getBucket: Bucket = {
    riakClient.fetchBucket(bucket).execute();
  }

}
