package br.com.passella.httpserver.adapter.output

import br.com.passella.fastlogger.FastLogger
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 * Pool de ByteBuffers para reutilização.
 * Mantém três tamanhos de buffer para diferentes necessidades.
 */
class BufferPool {
    private companion object {
        private val logger = FastLogger.getLogger(BufferPool::class.java)
        private const val SMALL_BUFFER_SIZE = 4 * 1024      // 4KB
        private const val MEDIUM_BUFFER_SIZE = 16 * 1024    // 16KB
        private const val LARGE_BUFFER_SIZE = 64 * 1024     // 64KB
        private const val MAX_POOL_SIZE_PER_BUCKET = 32     // Máximo de buffers por tamanho
    }

    private val smallBuffers = ConcurrentLinkedQueue<ByteBuffer>()
    private val mediumBuffers = ConcurrentLinkedQueue<ByteBuffer>()
    private val largeBuffers = ConcurrentLinkedQueue<ByteBuffer>()

    private val smallCount = AtomicInteger(0)
    private val mediumCount = AtomicInteger(0)
    private val largeCount = AtomicInteger(0)

    fun acquire(requiredSize: Int): ByteBuffer {
        val buffer = when {
            requiredSize <= SMALL_BUFFER_SIZE -> getFromQueue(smallBuffers, smallCount, SMALL_BUFFER_SIZE)
            requiredSize <= MEDIUM_BUFFER_SIZE -> getFromQueue(mediumBuffers, mediumCount, MEDIUM_BUFFER_SIZE)
            requiredSize <= LARGE_BUFFER_SIZE -> getFromQueue(largeBuffers, largeCount, LARGE_BUFFER_SIZE)
            else -> {
                logger.debug { "Criando buffer grande sob demanda: $requiredSize bytes" }
                ByteBuffer.allocate(requiredSize)
            }
        }

        buffer.clear()
        return buffer
    }

    fun release(buffer: ByteBuffer) {
        val capacity = buffer.capacity()

        when {
            capacity == SMALL_BUFFER_SIZE && smallCount.get() < MAX_POOL_SIZE_PER_BUCKET -> {
                buffer.clear()
                smallBuffers.offer(buffer)
                smallCount.incrementAndGet()
            }

            capacity == MEDIUM_BUFFER_SIZE && mediumCount.get() < MAX_POOL_SIZE_PER_BUCKET -> {
                buffer.clear()
                mediumBuffers.offer(buffer)
                mediumCount.incrementAndGet()
            }

            capacity == LARGE_BUFFER_SIZE && largeCount.get() < MAX_POOL_SIZE_PER_BUCKET -> {
                buffer.clear()
                largeBuffers.offer(buffer)
                largeCount.incrementAndGet()
            }
        }
    }

    private fun getFromQueue(
        queue: ConcurrentLinkedQueue<ByteBuffer>, counter: AtomicInteger, size: Int
    ): ByteBuffer {
        val buffer = queue.poll()
        if (buffer != null) {
            counter.decrementAndGet()
            return buffer
        }
        return ByteBuffer.allocate(size)
    }
}