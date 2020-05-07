// Copyright 2020 TheViperShow
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0
//
package me.thevipershow.connectionprotocol.crypt

interface PacketEncryption {
    fun getDecryptOutputSize(length: Int): Int

    fun getEncryptOutputSize(length: Int): Int

    @Throws(Exception::class)
    fun decrypt(input: ByteArray, inputOffset: Int, inputLength: Int, output: ByteArray, outputOffset: Int): Int

    @Throws(Exception::class)
    fun encrypt(input: ByteArray, inputOffset: Int, inputLength: Int, output: ByteArray, outputOffset: Int): Int
}