package me.thevipershow.connectionprotocol.packets.crypt

interface PacketEncryption {
    fun getDecryptOutputSize(length: Int): Int

    fun getEncryptOutputSize(length: Int): Int

    @Throws(Exception::class)
    fun decrypt(input: ByteArray, inputOffset: Int, inputLength: Int, output: ByteArray, outputOffset: Int): Int

    @Throws(Exception::class)
    fun encrypt(input: ByteArray, inputOffset: Int, inputLength: Int, output: ByteArray, outputOffset: Int): Int
}