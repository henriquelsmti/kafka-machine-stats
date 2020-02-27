package ti.lsm.henrique.io

import oshi.SystemInfo
import oshi.hardware.CentralProcessor
import oshi.hardware.ComputerSystem
import oshi.hardware.HardwareAbstractionLayer
import oshi.software.os.OperatingSystem
import ti.lsm.henrique.kafka.KafkaClient
import ti.lsm.henrique.model.ComputerRecord
import java.math.BigInteger
import java.security.MessageDigest
import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ComputerIdentifier {

    @Inject
    lateinit var kafkaClient: KafkaClient

    private val computerRecord: ComputerRecord by lazy {
        val systemInfo = SystemInfo()
        val operatingSystem: OperatingSystem = systemInfo.operatingSystem
        val hardwareAbstractionLayer: HardwareAbstractionLayer = systemInfo.hardware
        val centralProcessor: CentralProcessor = hardwareAbstractionLayer.processor
        val computerSystem: ComputerSystem = hardwareAbstractionLayer.computerSystem
        val vendor: String = operatingSystem.manufacturer
        val processorSerialNumber: String = computerSystem.serialNumber
        val processorIdentifier: String = centralProcessor.identifier
        val processors: Int = centralProcessor.logicalProcessorCount


        val info = "$vendor#$processorSerialNumber#${computerSystem.baseboard}#$processorIdentifier#$processors"
        val md = MessageDigest.getInstance("MD5")
        val key = BigInteger(1, md.digest(info.toByteArray())).toString(16).padStart(32, '0')

        ComputerRecord(
                key = key,
                vendor = vendor,
                processorSerialNumber = processorSerialNumber,
                baseboard = computerSystem.baseboard.toString(),
                processorIdentifier = processorIdentifier,
                processors = processors.toString()
        )
    }

    val id by lazy { computerRecord.key }

    @PostConstruct
    private fun init() {
        kafkaClient.sendRecord(computerRecord.topic, computerRecord.key, computerRecord)
    }
}