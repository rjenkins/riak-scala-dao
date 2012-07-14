//
// Copyright 2012, Ray Jenkins
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package com.aceevo.riak.driver.impl

import com.basho.riak.client.{IRiakObject, IRiakClient}
import com.basho.riak.client.bucket.Bucket
import com.codahale.logula.Logging
import com.basho.riak.client.convert.Converter
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import com.aceevo.riak.driver.RiakStorageDriver
import com.basho.riak.client.query.indexes.{IntIndex, BinIndex}

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

    val data: IRiakObject = fetchBucket.fetch(key).execute()
    if (data != null)
      new Some(converter.toDomain(data))
    else
      None

  }

  def persist(key: String, t: T, converter: Converter[T]): T = {
    fetchBucket.store(key, t).withConverter(converter).returnBody(true).execute()
  }

  def delete(t: T) {
    fetchBucket.delete(t).execute()
  }

  def deleteByKey(key: String) {
    fetchBucket.delete(key).execute()
  }

  def findFor2i(index: String, value: String, converter: Converter[T]): List[T] = {
    get2iResults(getStringKeys(index, value), converter)
  }

  def findFor2i(index: String, value: Int, converter: Converter[T]): List[T] = {
    get2iResults(getIntKeys(index, value), converter)
  }

  def deleteFor2i(index: String, value: String) {
    delete2iResults(getStringKeys(index, value))
  }

  def deleteFor2i(index: String, value: Int) {
    delete2iResults(getIntKeys(index, value))
  }

  // Find all records of Type T for Matching 2i query
  private def getIntKeys(index: String, value: Int): List[String] = {
    fetchBucket.fetchIndex(IntIndex.named(index)).withValue(value).execute().toList
  }

  // Find all records of Type T for Matching 2i query
  private def getStringKeys(index: String, value: String): List[String] = {
    fetchBucket.fetchIndex(BinIndex.named(index)).withValue(value).execute().toList
  }

  private def get2iResults(keys: List[String], converter: Converter[T]): List[T] = {

    val listBuffer = new ListBuffer[T]
    keys.foreach({ key => listBuffer.prepend(converter.toDomain(fetchBucket.fetch(key).execute()))})
    listBuffer.toList
  }

  private def delete2iResults(keys: List[String]) {
    keys.foreach(fetchBucket.delete(_).execute())
  }

  def getBucket = bucket

  // Utility method to retrieve a bucket
  private def fetchBucket: Bucket = {
    riakClient.fetchBucket(bucket).execute()
  }

}
