package com.aceevo.riak.driver

import com.basho.riak.client.convert.Converter

/**
 * Created with IntelliJ IDEA.
 * User: rjenkins
 * Date: 5/18/12
 * Time: 9:18 AM
 * To change this template use File | Settings | File Templates.
 */

trait RiakStorageDriver[K, T] {
  def delete(t: T)
  def deleteByKey(key: K)
  def getByKey(key: K, converter: Converter[T]): Option[T]
  def persist(key: K, t: T, converter: Converter[T]): T
  def findFor2i(index: (String, String), converter: Converter[T]): List[T]
  def deleteFor2i(index: (String, String))
}
