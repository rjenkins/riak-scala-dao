package com.aceevo.riak.dao.impl

import com.codahale.logula.Logging
import com.basho.riak.client.convert.Converter
import com.aceevo.riak.driver.RiakStorageDriver
import com.aceevo.riak.dao.GenericRiakEntityDAO


/**
 * Created with IntelliJ IDEA.
 * User: rjenkins
 * Date: 5/16/12
 * Time: 10:45 AM
 * To change this template use File | Settings | File Templates.
 */


abstract class RiakEntityDAO[K, T](storageDriver: RiakStorageDriver[K, T]) extends GenericRiakEntityDAO[K, T]
with Logging with Converter[T] {

  // Retrieve Entity by Key
  def getByKey(key: K): Option[T] = {
    storageDriver.getByKey(key, this)
  }

  // For a given key and type persist entity
  def persist(key: K, t: T): T = {
    storageDriver.persist(key, t, this)
  }

  def findFor2i(index: (String, String)): List[T] = {
    storageDriver.findFor2i(index, this)
  }

  def delete(t: T): Unit = {
    storageDriver.delete(t)
  }

  def deleteByKey(key: K) = {
    storageDriver.deleteByKey(key)
  }

  def deleteAllFor2i(index: (String, String)) {
    storageDriver.deleteFor2i(index)
  }
}
