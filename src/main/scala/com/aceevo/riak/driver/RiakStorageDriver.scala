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
  def findFor2iString(index: (String, String), converter: Converter[T]): List[T]
  def findFor2iInt(index: (String, Int), converter: Converter[T]): List[T]
  def deleteFor2iString(index: (String, String))
  def deleteFor2iInt(index: (String, Int))
  def getBucket : String
}
