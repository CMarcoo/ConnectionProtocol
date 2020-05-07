/*
 *  Copyright 2020 TheViperShow
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 */
@file:Suppress("JoinDeclarationAndAssignment")

package me.thevipershow.connectionprotocol.crypt

import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

class AESEncryption(key: Key) : PacketEncryption {
    private val inputCipher: Cipher
    private val outputCipher: Cipher

    init {
        this.inputCipher = Cipher.getInstance("AES/CFB8/NoPadding")
        this.inputCipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(key.encoded))
        this.outputCipher = Cipher.getInstance("AES/CFB8/NoPadding")
        this.outputCipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(key.encoded))
    }

    override fun getDecryptOutputSize(length: Int): Int {
        return this.inputCipher.getOutputSize(length)
    }

    override fun getEncryptOutputSize(length: Int): Int {
        return this.outputCipher.getOutputSize(length)
    }

    override fun decrypt(
        input: ByteArray,
        inputOffset: Int,
        inputLength: Int,
        output: ByteArray,
        outputOffset: Int
    ): Int {
        return this.inputCipher.update(input, inputOffset, inputLength, output, outputOffset)
    }

    override fun encrypt(
        input: ByteArray,
        inputOffset: Int,
        inputLength: Int,
        output: ByteArray,
        outputOffset: Int
    ): Int {
        return this.outputCipher.update(input, inputOffset, inputLength, output, outputOffset)
    }
}